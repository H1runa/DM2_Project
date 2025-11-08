package com.hiruna.dm2_backend.data.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hiruna.dm2_backend.data.model.ExpenseAccount;

@Repository
public interface ExpenseAccountRepo extends JpaRepository<ExpenseAccount, Long>{
    
}
