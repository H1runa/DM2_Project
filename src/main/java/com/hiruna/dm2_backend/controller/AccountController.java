package com.hiruna.dm2_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiruna.dm2_backend.data.model.Account;
import com.hiruna.dm2_backend.service.AccountService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/account")
public class AccountController {
    private AccountService accountService;

    public AccountController(AccountService accountService){
        this.accountService=accountService;
    }

    @PostMapping("")
    public ResponseEntity<?> createAccount(@RequestBody Account entity) {
        try{
            return ResponseEntity.ok(accountService.createAccount(entity));
        } catch(Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("")
    public ResponseEntity<Boolean> updateAccount(@RequestBody Account entity) {
        try{
            return ResponseEntity.ok(accountService.updateAccount(entity));
        } catch(Exception e){
            return ResponseEntity.status(500).body(false);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteAccount(@PathVariable Long id){
        try{
            return ResponseEntity.ok(accountService.deleteAccount(id));
        } catch(Exception e){
            return ResponseEntity.status(500).body(false);
        }
    }

    @PostMapping("/sync")
    public void syncAll() {
        accountService.syncAll();
    }
    
    
}
