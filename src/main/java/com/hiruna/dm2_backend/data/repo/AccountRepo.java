package com.hiruna.dm2_backend.data.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hiruna.dm2_backend.data.model.Account;
import com.hiruna.dm2_backend.interfaces.SyncRepo;

@Repository
public interface AccountRepo extends SyncRepo<Account> {
    @Override
    List<Account> findByIsSynced(int isSynced);
    @Override
    List<Account> findByIsDeleted(int isDeleted);
}
