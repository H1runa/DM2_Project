package com.hiruna.dm2_backend.service.sync_service;

import java.util.List;
import java.util.function.Consumer;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.hiruna.dm2_backend.data.model.BillReminder;

@Service
public class BillReminderSyncService {
    private WebClient webClient;

    public BillReminderSyncService(WebClient.Builder builder){
        this.webClient=builder
                            .baseUrl("http://localhost:8002/oracle")
                            .build();
    }

    public void syncInsertToOracle(BillReminder rem, Consumer<ResponseEntity<?>> success, Consumer<Long> constraint_failed){
        webClient.post()
            .uri("/api/billreminder")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(rem)
            .retrieve()
            .toBodilessEntity()
            .doOnSuccess(resp -> {
                success.accept(resp);
            }).doOnError(err -> {
                if (err instanceof WebClientResponseException) {
                    WebClientResponseException error = (WebClientResponseException) err;
                    if (error.getStatusCode().value() == 409){
                        System.err.println("ERROR: Constraint Faliure");
                        constraint_failed.accept(rem.getRemindID()); //deleting the sqlite record because constraint failed in oracle
                    } else if (error.getStatusCode().value() == 500){
                        System.err.println("ERROR: Internal Server Error");
                    }
                }
                System.err.println("ERROR: While sycing Bill reminder insert to oracle");     
                                        
            }).block();

        
    }

    public void syncUpdateToORacle(BillReminder rem, Consumer<Boolean> success, Consumer<Void> failure){
        webClient.put()
            .uri("/api/billreminder")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(rem)
            .retrieve()
            .bodyToMono(Boolean.class)
            .doOnSuccess(resp -> {
                success.accept(resp);
            }).doOnError(err -> {                
                failure.accept(null);
            })
            .block();
    }
    
    public void syncDeleteToOracle(long id, Consumer<Boolean> success){
        webClient.delete()
            .uri("/api/billreminder/"+id)
            .retrieve()
            .bodyToMono(Boolean.class)
            .doOnSuccess(resp -> {
                System.out.println("BillReminder deletion synced with Oracle");
                success.accept(resp);
            }).doOnError(err-> {
                System.err.println("ERROR: Could not delete");
            })
            .block();
    }

    //function to check if a user exists in oracle
    public Boolean checkIfExists(long id){
        Boolean check = webClient.get()
                            .uri("/api/billreminder/"+id+"/exists")
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .block();
        return check;            
    }

    @Async
    public void syncAllInsertUpdate(List<BillReminder> list, Consumer<BillReminder> insert, Consumer<BillReminder> update){
        for (BillReminder billReminder : list) {
            //update
            if (checkIfExists(billReminder.getRemindID())){
                update.accept(billReminder);
            } else {
                //insert
                insert.accept(billReminder);
            }
        }
    }

    @Async
    public void syncAllDelete(List<BillReminder> list, Consumer<BillReminder> delete){
        for (BillReminder billReminder : list) {
            delete.accept(billReminder);
        }
    }
}
