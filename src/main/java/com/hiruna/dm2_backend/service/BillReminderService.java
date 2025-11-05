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

    public BillReminder createReminder(BillReminder reminder){
        BillReminder saved_rem = billReminderRepo.save(reminder);
        billReminderSyncService.syncInsertToOracle(saved_rem, resp -> {markAsSynced(saved_rem.getRemindID());}, id-> {deleteReminderById(id);});
        return saved_rem;
    }

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

    public void deleteReminderById(long id){
        billReminderRepo.deleteById(id);
        System.out.println("SYNC FAIL: Bill Reminder deleted from local");
    }
}
