package com.hiruna.dm2_backend.service.sync_service;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.hiruna.dm2_backend.data.model.AppUser;

@Service
public class AppUserSyncService {

    private WebClient webClient;

    public AppUserSyncService(WebClient.Builder builder){
        this.webClient=builder
            .baseUrl("http://localhost:8002/oracle")
            .build();
    }

    @Async
    public void syncInsertToOracle(AppUser user, Consumer<Void> success){
        webClient.post()
            .uri("/api/appuser")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(user)
            .retrieve()
            .toBodilessEntity()
            .doOnSuccess(response -> {                
                success.accept(null);
            })
            .doOnError(error -> System.err.println("ERROR: Syncing: "+error.getMessage()))
            .subscribe();
        System.out.println("Finished syncing");
    }

    @Async
    public void syncUpdateToOracle(AppUser user, Consumer<Throwable> failure, Consumer<Void> success){
        webClient.put()
            .uri("/api/appuser")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(user)
            .retrieve()
            .toBodilessEntity()
            .doOnSuccess(response -> {System.out.println("Synced App User update with Oracle");success.accept(null);})
            .doOnError(error -> failure.accept(error))
            .subscribe();            
    }

    @Async
    public void syncDeleteToOracle(long id, Consumer<ResponseEntity<?>> success){
        webClient.delete()
            .uri("/api/appuser/"+id)
            .retrieve()
            .toBodilessEntity()
            .doOnSuccess(resp -> {
                System.out.println("Synced App User Deletion with Oracle");
                success.accept(resp);
            })
            .doOnError(err -> {
                System.err.println("ERROR: Delete failed on oracle");
            })
            .subscribe();
    }

    //function to check if a user exists in oracle
    public Boolean checkIfExists(long id){
        Boolean check = webClient.get()
                            .uri("/api/appuser/"+id+"/exists")
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .block();
        return check;            
    }

    //function to sync all the records in app user tables
    @Async
    public void syncAllInsertUpdate(List<AppUser> list, Consumer<AppUser> insert, Consumer<AppUser> update){
        for (AppUser user : list) {
            if (checkIfExists(user.getUserID())){                
                update.accept(user);
            } else {
                insert.accept(user);
            }
        }
    }
    @Async
    public void syncAllDeletions(List<AppUser> list, Consumer<AppUser> delete){
        for (AppUser user: list){
            delete.accept(user);
        }
    }
}
