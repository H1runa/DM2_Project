package com.hiruna.dm2_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hiruna.dm2_backend.data.model.AppUser;
import com.hiruna.dm2_backend.service.AppUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;




@RestController
@RequestMapping("/appuser")
public class AppUserController {
    private AppUserService appUserService;

    public AppUserController(AppUserService appUserService){
        this.appUserService=appUserService;
    }

    //create request
    @PostMapping("")
    public String createAppUser(@RequestBody AppUser user) {
        try{
            appUserService.createAppUser(user);
            return "App User Created";
        } catch (Exception ex){
            return "ERROR: App User Not Created";
        }
    }
    //update request
    @PutMapping("")
    public ResponseEntity<?> updateAppUser(@RequestBody AppUser user) {
        try{
            Boolean saved_user = appUserService.updateAppUser(user);
            return ResponseEntity.ok(saved_user);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected Error");
        }                
    }
    //delete request
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppUser(@PathVariable long id){
        try{
            Boolean deleted_user = appUserService.deleteAppUser(id);
            return ResponseEntity.ok(deleted_user);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected Error");            
        }
    }

    //sync all request
    @PostMapping("/sync")
    public void syncAll() {
        appUserService.syncAll();        
    }

    //userAccountView
    @GetMapping("/{id}/accountnames")
    public ResponseEntity<?> userAccountView(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(appUserService.userAccountView(id));
        } catch(Exception e){
            if (e.getMessage().equals("NOT_FOUND")){
                return ResponseEntity.status(404).body("NOT_FOUND");
            } else {
                return ResponseEntity.status(505).body(e.getMessage());
            }
        }
    }

    //user login
    @GetMapping("/login")
    public ResponseEntity<Boolean> getMethodName(@RequestParam String accountName, @RequestParam String password) {
        try{
            return ResponseEntity.ok(appUserService.userLogin(accountName, password));
        } catch (Exception e){
            return ResponseEntity.ok(false);
        }
    }
    
    
    
    
}
