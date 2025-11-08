package com.hiruna.dm2_backend.data.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hiruna.dm2_backend.data.model.AppUser;
import com.hiruna.dm2_backend.data.model.BillReminder;
import com.hiruna.dm2_backend.interfaces.SyncRepo;

@Repository
public interface BillReminderRepo extends SyncRepo<BillReminder>{
    @Override
    List<BillReminder> findByIsSynced(int isSynced);
    @Override    
    List<BillReminder> findByIsDeleted(int isDeleted);
}
