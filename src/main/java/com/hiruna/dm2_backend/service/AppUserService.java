package com.hiruna.dm2_backend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.hiruna.dm2_backend.data.model.AppUser;
import com.hiruna.dm2_backend.data.repo.AppUserRepo;

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

    public Map<String, Object> userAccountView(Long id){
        Map<String, Object> obj = new HashMap<>();
        Optional<AppUser> opt = appUserRepo.findById(id);
        if (opt.isPresent()){
            AppUser user = opt.get();
            obj.put("accountName", user.getAccountName());
        } else {
            throw new RuntimeException("NOT_FOUND");
        }

        return obj;
    }
}