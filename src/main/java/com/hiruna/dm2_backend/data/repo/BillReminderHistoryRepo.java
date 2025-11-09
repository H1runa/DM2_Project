package com.hiruna.dm2_backend.data.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hiruna.dm2_backend.data.model.AccountTransaction;
import com.hiruna.dm2_backend.data.model.BillReminderHistory;
import com.hiruna.dm2_backend.interfaces.SyncRepo;

@Repository
public interface BillReminderHistoryRepo extends SyncRepo<BillReminderHistory> {
    @Override
    List<BillReminderHistory> findByIsSynced(int isSynced);
    @Override    
    List<BillReminderHistory> findByIsDeleted(int isDeleted); 
}
