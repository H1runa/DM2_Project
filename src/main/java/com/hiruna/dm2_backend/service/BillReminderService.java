package com.hiruna.dm2_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hiruna.dm2_backend.data.model.BillReminder;
import com.hiruna.dm2_backend.data.repo.BillReminderRepo;

@Service
public class BillReminderService {
    private BillReminderRepo billReminderRepo;    
    private GenericEntityService genericEntityService;

    public BillReminderService(BillReminderRepo billReminderRepo, GenericEntityService genericEntityService){
        this.billReminderRepo=billReminderRepo;        
        this.genericEntityService=genericEntityService;
    }

    //creating reminder
    public BillReminder createReminder(BillReminder reminder){
        return genericEntityService.insertRecord(billReminderRepo, reminder, "/api/billreminder");
    }

    //updating reminder
    public Boolean updateReminder(BillReminder reminder){
        return genericEntityService.updateRecord(billReminderRepo, reminder, "/api/billreminder", (entity, updated)-> {
            entity.setRemindName(updated.getRemindName());
            entity.setDeadline(updated.getDeadline());
            entity.setStatus(updated.getStatus());
            entity.setIsSynced(0);
            entity.setIsDeleted(updated.getIsDeleted());
        });
    }

   //deleting reminder
    public Boolean deleteReminder(Long id){
        return genericEntityService.deleteRecord(billReminderRepo, id, "/api/billreminder");
    }

    //sync all function
    public void syncAll(){       
        genericEntityService.syncAll(billReminderRepo, "/api/billreminder");
    }    

    //viewBillReminder
    public List<BillReminder> viewBillReminder(Long userID){
        return billReminderRepo.findByUserID(userID);
    }
}
