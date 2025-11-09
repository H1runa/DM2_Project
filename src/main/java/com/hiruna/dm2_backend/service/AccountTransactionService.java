package com.hiruna.dm2_backend.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.hiruna.dm2_backend.data.model.Account;
import com.hiruna.dm2_backend.data.model.AccountTransaction;
import com.hiruna.dm2_backend.data.model.BillReminder;
import com.hiruna.dm2_backend.data.model.BillReminderHistory;
import com.hiruna.dm2_backend.data.repo.AccountRepo;
import com.hiruna.dm2_backend.data.repo.AccountTransactionRepo;
import com.hiruna.dm2_backend.data.repo.BillReminderHistoryRepo;
import com.hiruna.dm2_backend.data.repo.BillReminderRepo;
import com.hiruna.dm2_backend.service.sync_service.GenericSyncService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class AccountTransactionService {
    private AccountTransactionRepo accountTransactionRepo;
    private BillReminderHistoryRepo billReminderHistoryRepo;
    private GenericSyncService genericSyncService;
    private AccountRepo accountRepo;
    private BillReminderRepo billReminderRepo;
    private GenericEntityService genericEntityService;
    @PersistenceContext
    private EntityManager entityManager;
    private AccountTransactionService self;
    private String syncUrl;

    public AccountTransactionService(AccountTransactionRepo accountTransactionRepo, BillReminderHistoryRepo billReminderHistoryRepo, @Lazy AccountTransactionService self, GenericSyncService genericSyncService, AccountRepo accountRepo, BillReminderRepo billReminderRepo, GenericEntityService genericEntityService){
        this.accountTransactionRepo=accountTransactionRepo;
        this.billReminderHistoryRepo=billReminderHistoryRepo;
        this.genericSyncService=genericSyncService;
        this.accountRepo=accountRepo;
        this.billReminderRepo=billReminderRepo;
        this.genericEntityService=genericEntityService;
        this.self=self;
        this.syncUrl="/api/accounttransaction";
    }

    public Boolean createAccountTransaction(AccountTransaction transaction){
        AtomicBoolean bool = new AtomicBoolean(true);
        self.insertAccountTransactionSQLite(transaction);
        genericSyncService.syncInsertToOracle(transaction, transaction.getTransID(), syncUrl, success->{
            genericEntityService.markAsSynced(transaction, accountTransactionRepo);// marking as synced
            if (transaction.getTransType().equals("Expense")){
                //expense
                Optional<Account> opt_acc = accountRepo.findById(transaction.getAccID());
                if(opt_acc.isPresent()){
                    Account acc = opt_acc.get();
                    acc.setBalance(acc.getBalance()-transaction.getAmount()); //deducting balance
                    accountRepo.save(acc);
                } else {
                    System.err.println("ERROR: Account not found for transaction");
                }
            } else if (transaction.getTransType().equals("Fund")){
                //fund
                Optional<Account> opt_acc = accountRepo.findById(transaction.getAccID());
                if(opt_acc.isPresent()){
                    Account acc = opt_acc.get();
                    acc.setBalance(acc.getBalance()+transaction.getAmount());
                    accountRepo.save(acc);
                } else {
                    System.err.println("ERROR: Account not found for transaction");
                }
            }

            //inserting to billreminderhistory
            if (billReminderRepo.existsByRemindName(transaction.getTransName())){
                BillReminder reminder = billReminderRepo.findByRemindName(transaction.getTransName()).get();
                BillReminderHistory history = new BillReminderHistory();
                history.setRemindID(reminder.getRemindID());
                history.setRemindName(reminder.getRemindName());
                history.setDeadline(reminder.getDeadline());
                history.setStatus(reminder.getStatus());
                history.setUserID(reminder.getUserID());
                history.setIsSynced(reminder.getIsSynced());
                history.setIsDeleted(reminder.getIsDeleted());

                billReminderHistoryRepo.save(history); //saving to billreminderhistory

                billReminderRepo.deleteById(reminder.getRemindID()); //deleting from billreminder
            }

        }, failure -> {
            accountTransactionRepo.deleteById(transaction.getTransID());
            bool.set(false);
            System.out.println("ERROR: Account transaction cleaned up after constraint failure");
        });

        return bool.get();
        
    }

    @Transactional
    public AccountTransaction insertAccountTransactionSQLite(AccountTransaction transaction){
        entityManager.persist(transaction);
        entityManager.flush();
        entityManager.refresh(transaction);

        return transaction;
    }

}
