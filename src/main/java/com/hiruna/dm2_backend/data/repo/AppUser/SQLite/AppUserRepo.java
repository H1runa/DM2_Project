package com.hiruna.dm2_backend.data.repo.AppUser.SQLite;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hiruna.dm2_backend.data.model.AppUser;
import java.util.List;


@Repository
public interface AppUserRepo  extends JpaRepository<AppUser, Long>{
    List<AppUser> findByIsSynced(int isSynced);
    List<AppUser> findByIsDeleted(int isDeleted);
}
