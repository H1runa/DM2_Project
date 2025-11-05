package com.hiruna.dm2_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiruna.dm2_backend.data.model.BillReminder;
import com.hiruna.dm2_backend.service.BillReminderService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    
}
