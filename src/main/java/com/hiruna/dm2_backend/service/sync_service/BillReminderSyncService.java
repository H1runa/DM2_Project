package com.hiruna.dm2_backend.service.sync_service;

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

    @Async
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
                                        
            }).subscribe();

        
    }

    @Async
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
            .subscribe();
    }

    @Async
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
            .subscribe();
    }
}
