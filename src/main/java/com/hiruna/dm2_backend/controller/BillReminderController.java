package com.hiruna.dm2_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiruna.dm2_backend.data.model.BillReminder;
import com.hiruna.dm2_backend.service.BillReminderService;
import com.hiruna.dm2_backend.service.sync_service.BillReminderSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/billreminder")
public class BillReminderController {
    
    private BillReminderService billReminderService;

    public BillReminderController(BillReminderService billReminderService){
        this.billReminderService=billReminderService;              
    }

    @PostMapping("")
    public ResponseEntity<?> createReminder(@RequestBody BillReminder reminder) {
        try{            
            return ResponseEntity.ok(billReminderService.createReminder(reminder));
        } catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("")
    public ResponseEntity<Boolean> updateReminder(@RequestBody BillReminder reminder) {
        try{
            return ResponseEntity.ok(billReminderService.updateReminder(reminder));
        } catch (Exception e){
            return ResponseEntity.status(500).body(false);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteReminder(@PathVariable long id){
        try{
            return ResponseEntity.ok(billReminderService.deleteReminder(id));
        } catch (Exception e){
            return ResponseEntity.status(500).body(false);
        }
    }

    @PostMapping("/sync")
    public void syncAll() {
        billReminderService.syncAll();
    }
    
    
}
