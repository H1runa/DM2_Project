package com.hiruna.dm2_backend.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.hiruna.dm2_backend.interfaces.SyncModel;
import com.hiruna.dm2_backend.interfaces.SyncRepo;
import com.hiruna.dm2_backend.service.sync_service.GenericSyncService;

@Service
public class GenericEntityService {
    private GenericSyncService genericSyncService;

    public GenericEntityService(GenericSyncService genericSyncService){
        this.genericSyncService=genericSyncService;
    };

    //inserting records
    public <T extends SyncModel> T insertRecord(JpaRepository<T, Long> repo, T entity, String syncUrl){
        T saved_record = repo.save(entity);
        genericSyncService.syncInsertToOracle(entity, entity.getId(), syncUrl, success -> {markAsSynced(entity, repo);}, id -> {deleteRecord(repo, id);});
        return saved_record;
    }

    //updating reminder
    public <T extends SyncModel> Boolean updateRecord(SyncRepo<T> repo, T entity, String syncUrl , BiConsumer<T,T> entityUpdater){
        Optional<T> ret_entity = repo.findById(entity.getId());
        if (ret_entity.isPresent()){
            T got_entity = ret_entity.get();
            entityUpdater.accept(got_entity, entity);            

            repo.save(got_entity);

            AtomicBoolean state = new AtomicBoolean(true);
            genericSyncService.syncUpdateToOracle(got_entity, syncUrl,resp -> {
                markAsSynced(got_entity, repo);
            }, err -> {
                markAsUnsynced(got_entity, repo);
                state.set(false);
            });           
            return state.get();
        } else {
            System.out.println("ERROR: Failed to update entity");            
            return false;
        }
    }

    //delete records
    public <T extends SyncModel> Boolean deleteRecord(JpaRepository<T, Long> repo, Long id, String syncUrl){
        try{
            Optional<T> ret_entity = repo.findById(id);
            if (ret_entity.isPresent()){
                T got_entity = ret_entity.get();
                got_entity.setIsDeleted(1);
                repo.save(got_entity);

                AtomicBoolean bool_holder = new AtomicBoolean(true);
                genericSyncService.syncDeleteToOracle(got_entity.getId(), syncUrl, bool->{
                    if (bool){
                        deleteRecord(repo, got_entity.getId());
                    } else {
                        System.err.println("ERROR: Failed to delete from Oracle");
                        bool_holder.set(false);
                    }
                });
                return bool_holder.get();
            } else {
                System.err.println("ERROR: BillReminder not deleted (NOT_FOUND)");
                return false;
            }
        } catch (Exception e){
            System.err.println("ERROR: BillReminder not deleted");
            return false;
        }

    }

    //sync all function
    public <T extends SyncModel> void syncAll(SyncRepo<T> repo, String syncUrl){
        List<T> list = repo.findByIsSynced(0);

        if (!list.isEmpty()){
            genericSyncService.syncAllInsertUpdate(list, syncUrl , record->{
                genericSyncService.syncInsertToOracle(record, record.getId(), syncUrl, resp -> {markAsSynced(record, repo);}, id-> {markAsUnsynced(record,repo);});
                System.out.println("SYNC INSERT: Record synchronized");
            }, record -> {
                genericSyncService.syncUpdateToOracle(record, syncUrl , resp -> {
                        markAsSynced(record ,repo);
                    }, err -> {
                        markAsUnsynced(record, repo);
                    });
                System.out.println("SYNC UPDATE: Record synchronized");
            });
        }

        List<T> list_to_delete = repo.findByIsDeleted(1);

        if (!list_to_delete.isEmpty()){
            genericSyncService.syncAllDelete(list_to_delete, record -> {genericSyncService.syncDeleteToOracle(record.getId(), syncUrl ,bool -> {
                    deleteRecord(repo, record.getId());
                });});
        }
    }

    //mark sync    
    public <T extends SyncModel> void markAsSynced(T entity, JpaRepository<T, Long> repo){
        try{            
            T ret_entity = repo.findById(entity.getId()).get();
            ret_entity.setIsSynced(1);
            repo.save(ret_entity);
        } catch (Exception ex){
            ex.printStackTrace();
        }        
    }

    //mark unsync
    public <T extends SyncModel> void markAsUnsynced(T entity, JpaRepository<T, Long> repo){
        try{
            Optional<T> ret_entity = repo.findById(entity.getId());
            if (ret_entity.isPresent()){
                T got_entity = ret_entity.get();
                got_entity.setIsSynced(0);
                repo.save(got_entity);
            } else {
                System.err.println("ERROR: Entity not found");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //delete
    public <T extends SyncModel> void deleteRecord(JpaRepository<T, Long> repo, Long id){
        repo.deleteById(id);        
    }

}
