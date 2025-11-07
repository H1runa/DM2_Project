package com.hiruna.dm2_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.hiruna.dm2_backend.data.model.BillReminder;
import com.hiruna.dm2_backend.data.repo.BillReminder.SQLite.BillReminderRepo;
import com.hiruna.dm2_backend.service.sync_service.BillReminderSyncService;
import com.hiruna.dm2_backend.service.sync_service.GenericSyncService;

@Service
public class BillReminderService {
    private BillReminderRepo billReminderRepo;
    private BillReminderSyncService billReminderSyncService;
    private GenericSyncService genericSyncService;
    private GenericEntityService genericEntityService;

    public BillReminderService(BillReminderRepo billReminderRepo, BillReminderSyncService billReminderSyncService, GenericSyncService genericSyncService, GenericEntityService genericEntityService){
        this.billReminderRepo=billReminderRepo;
        this.billReminderSyncService=billReminderSyncService;
        this.genericSyncService=genericSyncService;
        this.genericEntityService=genericEntityService;
    }

    public BillReminder createReminder(BillReminder reminder){
        return genericEntityService.insertRecord(billReminderRepo, reminder, "/api/billreminder");
    }

    //updating reminder
    // public Boolean updateReminder(BillReminder reminder){
    //     Optional<BillReminder> rem = billReminderRepo.findById(reminder.getRemindID());
    //     if (rem.isPresent()){
    //         BillReminder got_rem = rem.get();
    //         got_rem.setRemindName(reminder.getRemindName());
    //         got_rem.setDeadline(reminder.getDeadline());
    //         got_rem.setStatus(reminder.getStatus());            
    //         //no need to update user id
    //         got_rem.setIsSynced(0);
    //         got_rem.setIsDeleted(reminder.getIsDeleted());

    //         billReminderRepo.save(got_rem);
    //         billReminderSyncService.syncUpdateToORacle(got_rem, resp -> {
    //             markAsSynced(got_rem.getRemindID());
    //         }, err -> {
    //             markAsUnsynced(got_rem.getRemindID());
    //         });
    //         genericSyncService.syncUpdateToOracle(got_rem,"/api/billreminder" ,resp -> markAsSynced(got_rem.getRemindID()), err -> markAsUnsynced(got_rem.getRemindID()));
    //         return true;
    //     } else {
    //         System.out.println("ERROR: Failed to update BillReminder");            
    //         return false;
    //     }
    // }
    public Boolean updateReminder(BillReminder reminder){
        return genericEntityService.updateRecord(billReminderRepo, reminder, "/api/billreminder", (entity, updated)-> {
            entity.setRemindName(updated.getRemindName());
            entity.setDeadline(updated.getDeadline());
            entity.setStatus(updated.getStatus());
            entity.setIsSynced(0);
            entity.setIsDeleted(updated.getIsDeleted());
        });
    }

   
    public Boolean deleteReminder(Long id){
        return genericEntityService.deleteRecord(billReminderRepo, id, "/api/billreminder");
    }

    //sync all function
    public void syncAll(){       
        genericEntityService.syncAll(billReminderRepo, "/api/billreminder");
    }

    //marking the synced reminder
    // @Retryable(value = {CannotAcquireLockException.class}, maxAttempts = 5, backoff = @Backoff(delay = 500))
    public void markAsSynced(long id){
        Optional<BillReminder> rem = billReminderRepo.findById(id);
        try{
            BillReminder got_rem = rem.get();
            got_rem.setIsSynced(1);
            billReminderRepo.save(got_rem);
        } catch(Exception e){            
            System.err.println("ERROR: Failed to mark as synced");
            e.printStackTrace();
        }
    }

    //marking the unsynced reminder
    // @Retryable(value = {CannotAcquireLockException.class}, maxAttempts = 3, backoff = @Backoff(delay = 100))
    public void markAsUnsynced(long id){
        Optional<BillReminder> rem = billReminderRepo.findById(id);
        try{
            BillReminder got_rem = rem.get();
            got_rem.setIsSynced(0);
            billReminderRepo.save(got_rem);
        } catch(Exception e){            
            System.err.println("ERROR: Failed to mark as synced");
            e.printStackTrace();
        }
    }

    //deleting reminder by id
    public void deleteReminderById(long id){
        billReminderRepo.deleteById(id);
        System.out.println("Bill Reminder deleted from local");
    }
}
