package com.hiruna.dm2_backend.service;

import org.springframework.stereotype.Service;
import com.hiruna.dm2_backend.data.model.AppUser;
import com.hiruna.dm2_backend.data.repo.AppUser.SQLite.AppUserRepo;

@Service
public class AppUserService {
    private AppUserRepo appUserRepo;    
    private GenericEntityService genericEntityService;

    public AppUserService(AppUserRepo appUserRepo, GenericEntityService genericEntityService){
        this.appUserRepo=appUserRepo;               
        this.genericEntityService=genericEntityService;        
    }    

    //creating app user
    public AppUser createAppUser(AppUser user){
        return genericEntityService.insertRecord(appUserRepo, user, "/api/appuser");
    }
    
    //updating the app user
    public Boolean updateAppUser(AppUser user){
        return genericEntityService.updateRecord(appUserRepo, user, "/api/appuser", (entity, updated)->{
            entity.setAccountName(updated.getAccountName());
            entity.setPassword(updated.getPassword());
            entity.setIsSynced(0);
            entity.setIsDeleted(updated.getIsDeleted());
        });
    }
    
    //deleting app user
    public Boolean deleteAppUser(Long id){
        return genericEntityService.deleteRecord(appUserRepo, id, "/api/appuser");
    }
    
    //syncing everything
    public void syncAll(){
        genericEntityService.syncAll(appUserRepo, "/api/appuser");
    }
}