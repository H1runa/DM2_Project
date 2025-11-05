package com.hiruna.dm2_backend.service;

import java.util.Optional;

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

    //marking the synced reminder
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
        System.out.println("SYNC FAIL: Bill Reminder deleted from local");
    }
}
