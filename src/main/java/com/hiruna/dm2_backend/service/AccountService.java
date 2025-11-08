package com.hiruna.dm2_backend.service;

import org.springframework.stereotype.Service;

import com.hiruna.dm2_backend.data.model.Account;
import com.hiruna.dm2_backend.data.repo.AccountRepo;

@Service
public class AccountService {
    private GenericEntityService genericEntityService;
    private AccountRepo accountRepo;
    private String syncUrl;

    public AccountService(GenericEntityService genericEntityService, AccountRepo accountRepo){
        this.genericEntityService=genericEntityService;
        this.accountRepo=accountRepo;
        this.syncUrl="/api/account";
    }

    //insert
    public Account createAccount(Account acc){
        return genericEntityService.insertRecord(accountRepo, acc, syncUrl);
    }

    //update
    public Boolean updateAccount(Account acc){
        return genericEntityService.updateRecord(accountRepo, acc, syncUrl, (entity, updated)->{
            entity.setAccName(updated.getAccName());
            entity.setDescription(updated.getDescription());
            entity.setBalance(updated.getBalance());
            entity.setCreatedDate(updated.getCreatedDate());
            entity.setStatus(updated.getStatus());
            entity.setInitialAmount(updated.getInitialAmount());
            entity.setIsSynced(updated.getIsSynced());
            entity.setIsDeleted(updated.getIsDeleted());
        });
    }

    //delete
    public Boolean deleteAccount(Long id){
        return genericEntityService.deleteRecord(accountRepo, id, syncUrl);
    }

    //sync all
    public void syncAll(){
        genericEntityService.syncAll(accountRepo, syncUrl);
    }
    
}
