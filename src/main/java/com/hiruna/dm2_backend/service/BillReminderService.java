package com.hiruna.dm2_backend.service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.cglib.core.Local;
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

    //upcoming reminders
    public List<Map<String, Object>> getUpcomingBillReminders(Long userID){
        List<Map<String,Object>> list = new ArrayList<>();

        List<BillReminder> reminderList = billReminderRepo.findByUserID(userID);
        if (!reminderList.isEmpty()){
            for (BillReminder rem : reminderList){
                Map<String, Object> item = new HashMap<>();

                item.put("remindName", rem.getRemindName());
                item.put("deadline", rem.getDeadline());
                item.put("status", rem.getStatus());

                // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy", Locale.ENGLISH);
                // DateTimeFormatter normalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                // LocalDate date = LocalDate.parse(rem.getDeadline(), formatter);
                // LocalDate deadline = LocalDate.parse(date.format(normalFormatter));
                
                String nlsDateFormatPattern = "dd-MMM-yy";
                SimpleDateFormat formatter = new SimpleDateFormat(nlsDateFormatPattern);
                Long days = null;
                try{
                    java.util.Date date = formatter.parse(rem.getDeadline());
                    LocalDate deadline = date.toInstant()       // convert Date → Instant
                                        .atZone(ZoneId.systemDefault()) // apply system time zone
                                        .toLocalDate();

                    LocalDate currenDate = LocalDate.now();                
                    
                    days = ChronoUnit.DAYS.between(currenDate, deadline);
                } catch(Exception e){
                    throw new RuntimeException("Couldn't parse");
                }
                

                if (days == 3){
                    item.put("message", "Only 3 days");
                } else if (days == 2){
                    item.put("message", "Only 2 days");
                } else if (days == 1){
                    item.put("message", "Only 1 days");
                } else if (days == 0){
                    item.put("message", "Due Today");
                } else {
                    item.put("message", "Not Urgent");
                }

                list.add(new HashMap<>(item));
            }
        }

        return list;
    }
}
