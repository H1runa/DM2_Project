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
import com.hiruna.dm2_backend.interfaces.SyncModel;

@Service
public class GenericSyncService {
    private WebClient webclient;

    public GenericSyncService(WebClient.Builder builder){
        this.webclient=builder.baseUrl("http://localhost:8002/oracle").build();
    }

    public <T> void syncInsertToOracle(T entity, long entityId,  String syncUrl, Consumer<ResponseEntity<?>> success, Consumer<Long> constraint_failed){
        webclient.post()
            .uri(syncUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(entity)
            .retrieve()
            .toBodilessEntity()
            .doOnSuccess(resp -> {
                System.out.println("Synced insert to Oracle");
                success.accept(resp);
            }).doOnError(err -> {
                if (err instanceof WebClientResponseException) {
                    WebClientResponseException error = (WebClientResponseException) err;
                    if (error.getStatusCode().value() == 409){
                        System.err.println("ERROR: Constraint Faliure");
                        constraint_failed.accept(entityId); //deleting the sqlite record because constraint failed in oracle
                    } else if (error.getStatusCode().value() == 500){
                        System.err.println("ERROR: Internal Server Error");
                    }
                }
                System.err.println("ERROR: While sycing insert to oracle");     
                                        
            }).block();

        
    }

    public <T> void syncUpdateToOracle(T entity, String syncUrl, Consumer<Boolean> success, Consumer<Void> failure){
        webclient.put()
            .uri(syncUrl)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(entity)
            .retrieve()
            .bodyToMono(Boolean.class)
            .doOnSuccess(resp -> {
                System.out.println("Synced update to Oracle");
                success.accept(resp);
            }).doOnError(err -> {                
                failure.accept(null);
            })
            .block();
    }

    public void syncDeleteToOracle(long id, String syncUrl,  Consumer<Boolean> success){
        webclient.delete()
            .uri(syncUrl+"/"+id)
            .retrieve()
            .bodyToMono(Boolean.class)
            .doOnSuccess(resp -> {
                System.out.println("Synced deletion to Oracle");
                System.out.println(resp);
                success.accept(resp);
            }).doOnError(err-> {
                System.err.println("ERROR: Could not delete");
            })
            .block();
    }

     //function to check if a user exists in oracle
    public Boolean checkIfExists(long id, String syncUrl){
        Boolean check = webclient.get()
                            .uri(syncUrl+"/"+id+"/exists")
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .block();
        return check;            
    }

    public <T extends SyncModel> void syncAllInsertUpdate(List<T> list, String syncUrl ,Consumer<T> insert, Consumer<T> update){
        for (T item : list) {
            //update
            if (checkIfExists(item.getId(), syncUrl)){
                update.accept(item);
            } else {
                //insert
                insert.accept(item);
            }
        }
    }

    public <T> void syncAllDelete(List<T> list, Consumer<T> delete){
        for (T item : list) {
            delete.accept(item);
        }
    }
    
}
