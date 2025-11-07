package com.hiruna.dm2_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.hiruna.dm2_backend.data.model.AppUser;
import com.hiruna.dm2_backend.data.repo.AppUser.SQLite.AppUserRepo;
import com.hiruna.dm2_backend.service.sync_service.AppUserSyncService;
import com.hiruna.dm2_backend.service.sync_service.GenericSyncService;


@Service
public class AppUserService {
    private AppUserRepo appUserRepo;
    private AppUserSyncService appUserSyncService;
    private GenericSyncService genericSyncService;

    public AppUserService(AppUserRepo appUserRepo, AppUserSyncService appUserSyncService, GenericSyncService genericSyncService){
        this.appUserRepo=appUserRepo;        
        this.appUserSyncService=appUserSyncService;
        this.genericSyncService=genericSyncService;
    }    

    //create user
    public AppUser createAppUser(AppUser user){        
        AppUser saved_user = appUserRepo.save(user);       
        // appUserSyncService.syncInsertToOracle(saved_user, success -> {markUserAsSynced(saved_user);System.out.println("Synced with Oracle");});
        genericSyncService.syncInsertToOracle(user, user.getUserID(), "/api/appuser", success -> {markUserAsSynced(saved_user);}, null);

        return saved_user;
    }

    //update user
    public Boolean updateAppUser(AppUser user){
        try{
            Optional<AppUser> retrieved_user = appUserRepo.findById(user.getUserID());
            if (retrieved_user.isPresent()){
                    AppUser got_user = retrieved_user.get();
                    got_user.setAccountName(user.getAccountName());
                    got_user.setPassword(user.getPassword());
                    got_user.setIsSynced(user.getIsSynced());

                    appUserRepo.save(got_user);
                    // appUserSyncService.syncUpdateToOracle(got_user, failure -> {
                    //     markUserAsUnsynced(got_user);
                    // }, success-> markUserAsSynced(got_user));
                    genericSyncService.syncUpdateToOracle(got_user, "/api/appuser", success-> markUserAsSynced(got_user), failure -> markUserAsUnsynced(got_user));
                    return true;
            } else {
                System.out.println("ERROR: User ID ("+user.getUserID()+") not found in the database.");
                return false;
            }            
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }        
    }

    //delete user
    public Boolean deleteAppUser(long id){
        try{
            Optional<AppUser> user = appUserRepo.findById(id);
            if (user.isPresent()){
                AppUser got_user = user.get();
                got_user.setIsDeleted(1);
                appUserRepo.save(got_user); 

                // appUserSyncService.syncDeleteToOracle(got_user.getUserID(), success->{
                //     appUserRepo.deleteById(got_user.getUserID());
                // });
                genericSyncService.syncDeleteToOracle(got_user.getUserID(), "/api/appuser", success->appUserRepo.deleteById(got_user.getUserID()));
                return true;               
            } else {
                System.err.println("ERROR: Could not mark record as deleted");
                return false;
            }
        } catch (Exception e){
            System.err.println("ERROR: Could not mark record as deleted");
            e.printStackTrace();            
            return false;
        }
    }

    //mark sync
    public void markUserAsSynced(AppUser user){
        try{
            AppUser u = appUserRepo.findById(user.getUserID()).get();
            u.setIsSynced(1);
            appUserRepo.save(u);
        } catch (Exception ex){
            ex.printStackTrace();
        }        
    }

    //mark unsync
    public void markUserAsUnsynced(AppUser user){
        try{
            Optional<AppUser> u = appUserRepo.findById(user.getUserID());
            if (u.isPresent()){
                AppUser got_user = u.get();
                got_user.setIsSynced(0);
                appUserRepo.save(got_user);
            } else {
                System.err.println("ERROR: User ("+user.getUserID()+") not found for updating");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //sync all
    public void syncAll(){
        //syncing inserts and updates
        List<AppUser> list = appUserRepo.findByIsSynced(0);

        if (!list.isEmpty()) {
            // appUserSyncService.syncAllInsertUpdate(list, user->{
            //         appUserSyncService.syncInsertToOracle(user , success -> {
            //             markUserAsSynced(user);
            //             System.out.println("SYNC INSERT: User ID ("+user.getUserID()+") has been synced");
            //         });
            //     }, user -> {
            //         appUserSyncService.syncUpdateToOracle(user, failure -> {
            //                     markUserAsUnsynced(user);
            //                 }, success-> {
            //                     markUserAsSynced(user);
            //                     System.out.println("SYNC UPDATE: User ID ("+user.getUserID()+") has been synced");
            //                 });
            //     });
            genericSyncService.syncAllInsertUpdate(list, "/api/appuser" , user->{
                    // appUserSyncService.syncInsertToOracle(user , success -> {
                    //     markUserAsSynced(user);
                    //     System.out.println("SYNC INSERT: User ID ("+user.getUserID()+") has been synced");
                    // });
                    genericSyncService.syncInsertToOracle(user, user.getUserID(), "/api/appuser", success->markUserAsSynced(user), null);
                }, user -> {
                    // appUserSyncService.syncUpdateToOracle(user, failure -> {
                    //             markUserAsUnsynced(user);
                    //         }, success-> {
                    //             markUserAsSynced(user);
                    //             System.out.println("SYNC UPDATE: User ID ("+user.getUserID()+") has been synced");
                    //         });
                    genericSyncService.syncUpdateToOracle(user, "/api/appuser", success->markUserAsSynced(user), failure->markUserAsUnsynced(user));
                });                    
        };

        
        //syncing deletions
        List<AppUser> list_to_delete = appUserRepo.findByIsDeleted(1);        
        // if (!list_to_delete.isEmpty()){
        //     appUserSyncService.syncAllDeletions(list_to_delete, user -> {
        //         appUserSyncService.syncDeleteToOracle(user.getUserID(), delete -> {
        //             appUserRepo.deleteById(user.getUserID());
        //             System.out.println("SYNC DELETE: User ID ("+user.getUserID()+") has been synced");
        //         });
        //     });
        // }
        if (!list_to_delete.isEmpty()){
            genericSyncService.syncAllDelete(list_to_delete, user->{
                genericSyncService.syncDeleteToOracle(user.getId(), "/api/appuser", delete->{
                    appUserRepo.deleteById(user.getId());
                    System.out.println("Delete Synced.");
                });
            });
        }        
    }
}