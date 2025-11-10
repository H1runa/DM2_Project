package com.hiruna.dm2_backend.data.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hiruna.dm2_backend.data.model.BillReminder;
import com.hiruna.dm2_backend.data.model.SavingGoal;
import com.hiruna.dm2_backend.interfaces.SyncRepo;

@Repository
public interface SavingGoalRepo extends SyncRepo<SavingGoal> {
    @Override
    List<SavingGoal> findByIsSynced(int isSynced);
    @Override    
    List<SavingGoal> findByIsDeleted(int isDeleted);

    List<SavingGoal> findByAccID(Long accID);
}
