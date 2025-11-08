package com.hiruna.dm2_backend.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mapping.AccessOptions.SetOptions.Propagation;
import org.springframework.stereotype.Service;

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
    private AccountService self;

    public AccountService(GenericSyncService genericSyncService, AccountRepo accountRepo, ExpenseAccountRepo expenseAccountRepo, FundAccountRepo fundAccountRepo, @Lazy AccountService accountService){
        this.genericSyncService=genericSyncService;
        this.accountRepo=accountRepo;
        this.expenseAccountRepo=expenseAccountRepo;
        this.fundAccountRepo=fundAccountRepo;
        this.self=accountService;
        this.syncUrl="/api/account";
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

    //delete
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

    // //update
    // public Boolean updateAccount(Account acc){
    //     return genericEntityService.updateRecord(accountRepo, acc, syncUrl, (entity, updated)->{
    //         entity.setAccName(updated.getAccName());
    //         entity.setDescription(updated.getDescription());
    //         entity.setBalance(updated.getBalance());
    //         // entity.setCreatedDate(updated.getCreatedDate());
    //         // entity.setStatus(updated.getStatus());
    //         entity.setInitialAmount(updated.getInitialAmount());
    //         entity.setIsSynced(updated.getIsSynced());
    //         entity.setIsDeleted(updated.getIsDeleted());
    //     });
    // }

    // //delete
    // public Boolean deleteAccount(Long id){
    //     return genericEntityService.deleteRecord(accountRepo, id, syncUrl);
    // }

    // //sync all
    // public void syncAll(){
    //     genericEntityService.syncAll(accountRepo, syncUrl);
    // }        
}
