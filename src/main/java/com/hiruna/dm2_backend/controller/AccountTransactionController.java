package com.hiruna.dm2_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiruna.dm2_backend.data.model.AccountTransaction;
import com.hiruna.dm2_backend.service.AccountTransactionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/accounttransaction")
public class AccountTransactionController {
    private AccountTransactionService accountTransactionService;
    
    public AccountTransactionController(AccountTransactionService accountTransactionService){
        this.accountTransactionService=accountTransactionService;
    }

    //create
    @PostMapping("")
    public ResponseEntity<Boolean> createAccTransaction(@RequestBody AccountTransaction entity) {
        try{
            return ResponseEntity.ok(accountTransactionService.createAccountTransaction(entity));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body(false);
        }
    }

    //viewAccountTransaction
    @GetMapping("/{id}/viewtransaction")
    public ResponseEntity<?> viewAccountTransaction(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(accountTransactionService.viewTransaction(id));
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(505).body(e.getMessage());
        }
    }
    
    
}
