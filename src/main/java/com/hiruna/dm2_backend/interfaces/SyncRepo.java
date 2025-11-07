package com.hiruna.dm2_backend.interfaces;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SyncRepo<T> extends JpaRepository<T, Long> {
    List<T> findByIsSynced(int isSynced);
    List<T> findByIsDeleted(int isDeleted);
}
