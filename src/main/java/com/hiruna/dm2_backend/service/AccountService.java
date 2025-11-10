package com.hiruna.dm2_backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mapping.AccessOptions.SetOptions.Propagation;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hiruna.dm2_backend.data.dto.AccountDTO;
import com.hiruna.dm2_backend.data.model.Account;
import com.hiruna.dm2_backend.data.model.ExpenseAccount;
import com.hiruna.dm2_backend.data.model.FundAccount;
import com.hiruna.dm2_backend.data.repo.AccountRepo;
import com.hiruna.dm2_backend.data.repo.ExpenseAccountRepo;
import com.hiruna.dm2_backend.data.repo.FundAccountRepo;
import com.hiruna.dm2_backend.interfaces.SyncModel;
import com.hiruna.dm2_backend.service.sync_service.GenericSyncService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class AccountService {
    private GenericSyncService genericSyncService;
    private AccountRepo accountRepo;
    private ExpenseAccountRepo expenseAccountRepo;
    private FundAccountRepo fundAccountRepo;
    private String syncUrl;
    @PersistenceContext
    private EntityManager entityManager;
    private WebClient webclient;
    private AccountService self;

    public AccountService(GenericSyncService genericSyncService, AccountRepo accountRepo, ExpenseAccountRepo expenseAccountRepo, FundAccountRepo fundAccountRepo, @Lazy AccountService accountService, WebClient.Builder builder){
        this.genericSyncService=genericSyncService;
        this.accountRepo=accountRepo;
        this.expenseAccountRepo=expenseAccountRepo;
        this.fundAccountRepo=fundAccountRepo;
        this.self=accountService;
        this.syncUrl="/api/account";
        this.webclient=builder.baseUrl("http://localhost:8002/oracle").build();
    }

    //insert
    public Boolean createAccount(AccountDTO dto){        
        Account acc = self.insertAccountSQLite(dto);
        dto.setAccID(acc.getAccID());
                
        genericSyncService.syncInsertToOracle(dto, acc.getAccID(), "/api/account", ret_entity->{
            markAsSynced(acc.getId(), accountRepo);
            if (dto.getAccountType().equals("Expense")){                           
                markAsSynced(acc.getId(), expenseAccountRepo);
            } else if (dto.getAccountType().equals("Fund")){
                markAsSynced(acc.getId(), fundAccountRepo);
            }
        }, id -> {
            System.err.println("SYNC ERROR: Deleted records because they failed to sync over to Oracle");
            deleteRecord(accountRepo, acc.getId());
            if (dto.getAccountType().equals("Expense")){
                deleteRecord(expenseAccountRepo, acc.getId());
            } else if (dto.getAccountType().equals("Fund")){
                deleteRecord(fundAccountRepo, acc.getId());
            }
        });

        return true;
    }  
    
    //this needs to be a seperate method for it to commit the changes into sqlite db
    @Transactional
    public Account insertAccountSQLite(AccountDTO dto){
        Account acc = new Account();
        acc.setAccName(dto.getAccName());
        acc.setDescription(dto.getDescription());
        acc.setBalance(dto.getInitialAmount());
        acc.setInitialAmount(dto.getInitialAmount());
        acc.setUserID(dto.getUserID());
        acc.setIsSynced(0);
        acc.setIsDeleted(0);

        //saving to sqlite
        entityManager.persist(acc);
        entityManager.flush();
        entityManager.refresh(acc);
        System.out.println("Account created in SQLITE");

        ExpenseAccount expAcc = new ExpenseAccount();
        FundAccount fundAcc = new FundAccount();
        
        if (dto.getAccountType().equals("Expense")){            
            //expense account   
            expAcc.setAccID(acc.getAccID());         
            expAcc.setExpenseCategory(dto.getCategory());
            expAcc.setSpendingLimit(dto.getLimit());
            expAcc.setIsSynced(0);
            expAcc.setIsDeleted(0);

            // expenseAccountRepo.save(expAcc);
            entityManager.persist(expAcc);
            entityManager.flush();
            System.out.println("Expense Account created in SQLITE");            

        } else if (dto.getAccountType().equals("Fund")){
            //fund account      
            fundAcc.setAccID(acc.getAccID());      
            fundAcc.setFundType(dto.getCategory());
            fundAcc.setMinimumLimit(dto.getLimit());
            fundAcc.setIsSynced(0);
            fundAcc.setIsDeleted(0);

            // fundAccountRepo.save(fundAcc);
            entityManager.persist(fundAcc);
            entityManager.flush();
            System.out.println("Fund Account created in SQLITE");

        }      
        
        return acc;
    }

    //mark sync    
    public <T extends SyncModel> void markAsSynced(Long id, JpaRepository<T, Long> repo){
        try{            
            System.out.println("EXPACC: "+id);     
            T ret_entity = repo.findById(id).get();
            ret_entity.setIsSynced(1);
            repo.save(ret_entity);
        } catch (Exception ex){
            System.err.println(ex.getMessage());
        }        
    }

    //mark unsync
    public <T extends SyncModel> void markAsUnsynced(Long id, JpaRepository<T, Long> repo){
        try{
            Optional<T> ret_entity = repo.findById(id);
            if (ret_entity.isPresent()){
                T got_entity = ret_entity.get();
                got_entity.setIsSynced(0);
                repo.save(got_entity);
            } else {
                System.err.println("ERROR: Entity not found");
            }
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    //delete local record
    public <T extends SyncModel> void deleteRecord(JpaRepository<T, Long> repo, Long id){
        repo.deleteById(id);        
    }
    
    //update
    public Boolean updateAccount(AccountDTO dto){
        Optional<Account> opt_acc = accountRepo.findById(dto.getAccID());
        if (opt_acc.isPresent()){
            AtomicInteger accType = new AtomicInteger(0); //holder for type

            Account acc = updateAccountSQLite(opt_acc.get(), dto, accType);
            
            System.out.println("LIMIT: "+ dto.getLimit());
            genericSyncService.syncUpdateToOracle(dto, "/api/account", resp->{
                markAsSynced(acc.getAccID(), accountRepo);
                if (accType.get() == 0){
                    //expense
                    markAsSynced(acc.getAccID(), expenseAccountRepo);
                } else if (accType.get() == 1){
                    //fund
                    markAsSynced(acc.getAccID(), fundAccountRepo);
                }
            }, fail -> {
                markAsUnsynced(acc.getAccID(), accountRepo);
                if (accType.get() == 0){
                    //expense
                    markAsUnsynced(acc.getAccID(), expenseAccountRepo);
                } else if (accType.get() == 1){
                    //fund
                    markAsUnsynced(acc.getAccID(), fundAccountRepo);
                }
            });
        } else {
            System.err.println("ERROR: Account not found for updating");
        }

        return true;

    }

    @Transactional
    public Account updateAccountSQLite(Account acc, AccountDTO dto, AtomicInteger accType){        

            acc.setAccName(dto.getAccName());
            acc.setDescription(dto.getDescription());
            acc.setIsSynced(0);            

            //setting the account category
            if (expenseAccountRepo.existsById(acc.getAccID())){
                ExpenseAccount expAcc = expenseAccountRepo.findById(acc.getAccID()).get();
                dto.setCategory(expAcc.getExpenseCategory());
                accType.set(0); //0 is expense
                expAcc.setSpendingLimit(dto.getLimit()); //updating spending limit
                expenseAccountRepo.save(expAcc); //saving
            } else if (fundAccountRepo.existsById(acc.getAccID())){
                FundAccount fundAcc = fundAccountRepo.findById(acc.getAccID()).get();
                dto.setCategory(fundAcc.getFundType());
                accType.set(1); //1 is fund
                fundAcc.setMinimumLimit(dto.getLimit()); //updating minimum limit
                fundAccountRepo.save(fundAcc); //saving
            }

            return accountRepo.save(acc); //saving entity

    }

    //delete
    public Boolean deleteAccount(Long id){

        // String category = null;
        AtomicReference<String> category = new AtomicReference<String>();

        if (accountRepo.existsById(id)){
            deleteAccountSQLite(id, category);
        } else {
            System.err.println("ERROR: Account not found to delete");
            return false;
        }        

        AtomicBoolean bool = new AtomicBoolean(false);
        self.syncDeleteToOracle(id, category.get() ,"/api/account", success->{            
            if (success){
                deleteRecord(accountRepo, id);
                if (expenseAccountRepo.existsById(id)){
                    //expense
                    deleteRecord(expenseAccountRepo, id);
                } else if (fundAccountRepo.existsById(id)){
                    //fund
                    deleteRecord(fundAccountRepo, id);
                }
                bool.set(true);
                System.out.println("Account deleted");
            } else {
                System.err.println("Failed to delete on Oracle");                
            }            
        });

        return bool.get();

    }

    @Transactional
    public void deleteAccountSQLite(Long id, AtomicReference<String> category){
        Account acc = accountRepo.findById(id).get();
        acc.setIsDeleted(1);
        accountRepo.save(acc);
        if (expenseAccountRepo.existsById(id)){
            //expense
            ExpenseAccount expAcc = expenseAccountRepo.findById(id).get();
            expAcc.setIsDeleted(1);
            expenseAccountRepo.save(expAcc);
            category.set(expAcc.getExpenseCategory());
        } else if (fundAccountRepo.existsById(id)){
            //fund
            FundAccount fundAcc = fundAccountRepo.findById(id).get();
            fundAcc.setIsDeleted(1);
            fundAccountRepo.save(fundAcc);   
            category.set(fundAcc.getFundType());
        }
    }

    //cant use genericsyncservice because we need to pass category only for this specific case
    public void syncDeleteToOracle(Long id, String category , String syncUrl,  Consumer<Boolean> success){
        webclient.delete()
            .uri(syncUrl+"/"+id+"/"+category)
            .retrieve()
            .bodyToMono(Boolean.class)
            .doOnSuccess(resp -> {
                System.out.println("Synced deletion to Oracle");
                System.out.println(resp);
                success.accept(resp);
            }).doOnError(err-> {
                System.err.println("ERROR: Could not delete");
            })
            .block();
    }

    // //sync all
    public void syncAll(){
        List<Account> list = accountRepo.findByIsSynced(0);

        if (!list.isEmpty()){
            for (Account acc : list) {
                AccountDTO dto = new AccountDTO();
                
                dto.setAccID(acc.getAccID());
                dto.setAccName(acc.getAccName());
                dto.setDescription(acc.getDescription());
                dto.setInitialAmount(acc.getInitialAmount());
                dto.setUserID(acc.getUserID());

                if (expenseAccountRepo.existsById(acc.getAccID())){
                    //expense
                    dto.setAccountType("Expense");

                    ExpenseAccount expAcc = expenseAccountRepo.findById(acc.getAccID()).get();
                    dto.setCategory(expAcc.getExpenseCategory());
                    dto.setLimit(expAcc.getSpendingLimit());

                } else if (fundAccountRepo.existsById(acc.getAccID())){
                    //fund
                    dto.setAccountType("Fund");

                    FundAccount fundAcc = fundAccountRepo.findById(acc.getAccID()).get();
                    dto.setCategory(fundAcc.getFundType());
                    dto.setLimit(fundAcc.getMinimumLimit());
                }

                if (genericSyncService.checkIfExists(acc.getAccID(), "/api/account")){
                    //calling update method
                    genericSyncService.syncUpdateToOracle(dto, "/api/account", resp->{
                        markAsSynced(acc.getAccID(), accountRepo);
                        if (dto.getAccountType().equals("Expense")){
                            //expense
                            markAsSynced(acc.getAccID(), expenseAccountRepo);
                        } else if (dto.getAccountType().equals("Fund")){
                            //fund
                            markAsSynced(acc.getAccID(), fundAccountRepo);
                        }
                    }, fail -> {
                        markAsUnsynced(acc.getAccID(), accountRepo);
                        if (dto.getAccountType().equals("Expense")){
                            //expense
                            markAsUnsynced(acc.getAccID(), expenseAccountRepo);
                        } else if (dto.getAccountType().equals("Fund")){
                            //fund
                            markAsUnsynced(acc.getAccID(), fundAccountRepo);
                        }
                    });
                } else {
                    //calling insert method
                    genericSyncService.syncInsertToOracle(dto, acc.getAccID(), "/api/account", ret_entity->{
                        markAsSynced(acc.getId(), accountRepo);
                        if (dto.getAccountType().equals("Expense")){                           
                            markAsSynced(acc.getId(), expenseAccountRepo);
                        } else if (dto.getAccountType().equals("Fund")){
                            markAsSynced(acc.getId(), fundAccountRepo);
                        }
                    }, id -> {
                        System.err.println("SYNC ERROR: Deleted records because they failed to sync over to Oracle");
                        deleteRecord(accountRepo, acc.getId());
                        if (dto.getAccountType().equals("Expense")){
                            deleteRecord(expenseAccountRepo, acc.getId());
                        } else if (dto.getAccountType().equals("Fund")){
                            deleteRecord(fundAccountRepo, acc.getId());
                        }
                    });
                }                

                System.out.println("SYNC: Synced Account");
            }
        }

        List<Account> list_to_delete = accountRepo.findByIsDeleted(1);

        if(!list_to_delete.isEmpty()){
            for (Account acc : list_to_delete){
                String category = null;

                if (expenseAccountRepo.existsById(acc.getAccID())){
                    //expense
                    ExpenseAccount expAcc = expenseAccountRepo.findById(acc.getAccID()).get();
                    category = expAcc.getExpenseCategory();
                } else if (fundAccountRepo.existsById(acc.getAccID())){
                    //fund
                    FundAccount fundAcc = fundAccountRepo.findById(acc.getAccID()).get();
                    category = fundAcc.getFundType();
                }

                self.syncDeleteToOracle(acc.getAccID(), category ,"/api/account", success->{            
                    if (success){
                        deleteRecord(accountRepo, acc.getAccID());
                        if (expenseAccountRepo.existsById(acc.getAccID())){
                            //expense
                            deleteRecord(expenseAccountRepo, acc.getAccID());
                        } else if (fundAccountRepo.existsById(acc.getAccID())){
                            //fund
                            deleteRecord(fundAccountRepo, acc.getAccID());
                        }                        
                        System.out.println("Account deleted");
                    } else {
                        System.err.println("Failed to delete on Oracle");                
                    }            
                });

                System.out.println("SYNC: Synced Account Deleted");

            }
        }


    }

    //viewAccounts
    public List<Map<String, Object>> viewAccounts(String category, Long userID){
        List<Map<String, Object>> list = new ArrayList<>();            
        
        Optional<List<Account>> opt_list = accountRepo.findByUserID(userID);
        if (opt_list.isPresent()){
            List<Account> acc_list = opt_list.get();
            for (Account acc : acc_list){
                Map<String,Object> item = new HashMap<>();

                item.put("accName", acc.getAccName());
                item.put("description", acc.getDescription());
                item.put("balance", acc.getBalance());
                item.put("createdDate", acc.getCreatedDate());
                item.put("status", acc.getStatus());
                item.put("initialAmount", acc.getInitialAmount());

                Optional<ExpenseAccount> opt_exp = expenseAccountRepo.findById(acc.getAccID());
                Optional<FundAccount> opt_fund = fundAccountRepo.findById(acc.getAccID());
                if (opt_exp.isPresent() && opt_exp.get().getExpenseCategory().equals(category)){
                    item.put("spendingLimit", opt_exp.get().getSpendingLimit());
                } else if (opt_fund.isPresent() && opt_fund.get().getFundType().equals(category)){
                    item.put("minimumLimit", opt_fund.get().getMinimumLimit());
                } else {
                    continue;
                }
                
                list.add(new HashMap<>(item));

            }

            return list;

        } else {
            throw new RuntimeException("NOT_FOUND");
        }        
    }


    // public void syncAll(){
    //     genericEntityService.syncAll(accountRepo, syncUrl);
    // }        
}
