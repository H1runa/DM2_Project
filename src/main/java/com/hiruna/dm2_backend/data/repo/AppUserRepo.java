package com.hiruna.dm2_backend.data.repo;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hiruna.dm2_backend.data.model.AppUser;
import com.hiruna.dm2_backend.interfaces.SyncRepo;

import java.util.List;

@Repository
public interface AppUserRepo  extends SyncRepo<AppUser>{
    @Override
    List<AppUser> findByIsSynced(int isSynced);
    @Override
    List<AppUser> findByIsDeleted(int isDeleted);
}
