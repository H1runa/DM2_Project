package com.hiruna.dm2_backend.data.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hiruna.dm2_backend.data.model.AccountTransaction;
import com.hiruna.dm2_backend.data.model.BillReminder;
import com.hiruna.dm2_backend.interfaces.SyncRepo;

@Repository
public interface AccountTransactionRepo extends SyncRepo<AccountTransaction> {
    @Override
    List<AccountTransaction> findByIsSynced(int isSynced);
    @Override    
    List<AccountTransaction> findByIsDeleted(int isDeleted);    
}
