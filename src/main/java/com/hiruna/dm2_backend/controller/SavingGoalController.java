package com.hiruna.dm2_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiruna.dm2_backend.data.model.SavingGoal;
import com.hiruna.dm2_backend.service.SavingGoalService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/savinggoal")
public class SavingGoalController {
    private SavingGoalService savingGoalService;

    public SavingGoalController(SavingGoalService savingGoalService){
        this.savingGoalService=savingGoalService;
    }

    @PostMapping("")
    public ResponseEntity<?> createSavingGoal(@RequestBody SavingGoal entity) {
        try{
            return ResponseEntity.ok(savingGoalService.createSavingGoal(entity));
        } catch(Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("")
    public ResponseEntity<Boolean> updateSavingGoal(@RequestBody SavingGoal entity) {
        try{
            return ResponseEntity.ok(savingGoalService.updateSavingGoal(entity));
        } catch(Exception e){
            return ResponseEntity.status(500).body(false);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteSavingGoal(@PathVariable Long id){
        try{
            return ResponseEntity.ok(savingGoalService.deleteSavingGoal(id));
        } catch(Exception e){
            return ResponseEntity.status(500).body(false);
        }
    }

    @PostMapping("/sync")
    public void syncAll() {
        savingGoalService.syncAll();
    }
    
    
    
}
