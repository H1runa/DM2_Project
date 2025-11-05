package com.hiruna.dm2_backend.data.repo.BillReminder.SQLite;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hiruna.dm2_backend.data.model.AppUser;
import com.hiruna.dm2_backend.data.model.BillReminder;

@Repository
public interface BillReminderRepo extends JpaRepository<BillReminder, Long>{
    List<AppUser> findByIsSynced(int isSynced);
    List<AppUser> findByIsDeleted(int isDeleted);
}
