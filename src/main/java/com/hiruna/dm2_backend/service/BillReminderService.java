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

@Service
public class BillReminderService {
    private BillReminderRepo billReminderRepo;
    private BillReminderSyncService billReminderSyncService;

    public BillReminderService(BillReminderRepo billReminderRepo, BillReminderSyncService billReminderSyncService){
        this.billReminderRepo=billReminderRepo;
        this.billReminderSyncService=billReminderSyncService;
    }

    //inserting new reminder
    public BillReminder createReminder(BillReminder reminder){
        BillReminder saved_rem = billReminderRepo.save(reminder);
        billReminderSyncService.syncInsertToOracle(saved_rem, resp -> {markAsSynced(saved_rem.getRemindID());}, id-> {deleteReminderById(id);});
        return saved_rem;
    }

    //updating reminder
    public Boolean updateReminder(BillReminder reminder){
        Optional<BillReminder> rem = billReminderRepo.findById(reminder.getRemindID());
        if (rem.isPresent()){
            BillReminder got_rem = rem.get();
            got_rem.setRemindName(reminder.getRemindName());
            got_rem.setDeadline(reminder.getDeadline());
            got_rem.setStatus(reminder.getStatus());            
            //no need to update user id
            got_rem.setIsSynced(0);
            got_rem.setIsDeleted(reminder.getIsDeleted());

            billReminderRepo.save(got_rem);
            billReminderSyncService.syncUpdateToORacle(got_rem, resp -> {
                markAsSynced(got_rem.getRemindID());
            }, err -> {
                markAsUnsynced(got_rem.getRemindID());
            });
            return true;
        } else {
            System.out.println("ERROR: Failed to update BillReminder");            
            return false;
        }
    }

    public Boolean deleteReminder(long id){
        try{
            Optional<BillReminder> rem = billReminderRepo.findById(id);
            if (rem.isPresent()){
                BillReminder got_rem = rem.get();
                got_rem.setIsDeleted(1);
                billReminderRepo.save(got_rem);

                billReminderSyncService.syncDeleteToOracle(id, bool -> {
                    deleteReminderById(id);
                });
                return true;
            } else {
                System.err.println("ERROR: BillReminder not deleted (NOT_FOUND)");
                return false;
            }
        } catch (Exception e){
            System.err.println("ERROR: BillReminder not deleted");
            return false;
        }

    }

    //sync all function
    public void syncAll(){
        List<BillReminder> list = billReminderRepo.findByIsSynced(0);

        if (!list.isEmpty()){
            billReminderSyncService.syncAllInsertUpdate(list, record->{
                billReminderSyncService.syncInsertToOracle(record, resp -> {markAsSynced(record.getRemindID());}, id-> {deleteReminderById(id);});
                System.out.println("SYNC INSERT: Bill Reminder creation synced to oracle");
            }, record -> {
                billReminderSyncService.syncUpdateToORacle(record, resp -> {
                        markAsSynced(record.getRemindID());
                    }, err -> {
                        markAsUnsynced(record.getRemindID());
                    });
                System.out.println("SYNC UPDATE: Bill Reminder update synced to oracle");
            });
        }

        List<BillReminder> list_to_delete = billReminderRepo.findByIsDeleted(1);

        if (!list_to_delete.isEmpty()){
            billReminderSyncService.syncAllDelete(list_to_delete, record -> {billReminderSyncService.syncDeleteToOracle(record.getRemindID(), bool -> {
                    deleteReminderById(record.getRemindID());
                });});
        }
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
