package com.hiruna.dm2_backend.data.model;

import org.hibernate.annotations.GenericGenerator;

import com.hiruna.dm2_backend.interfaces.SyncModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "EXPENSEACCOUNT")
public class ExpenseAccount implements SyncModel {
    @Id
    @Column(name = "accID")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "manual-id")
    @GenericGenerator(name = "manual-id", strategy = "assigned")
    private Long accID;

    @Column(name = "expenseCategory")
    private String expenseCategory;

    @Column(name = "spendingLimit")
    private Double spendingLimit;

    @Column(name = "isSynced")
    private int isSynced;

    @Column(name = "isDeleted")
    private int isDeleted;

    public ExpenseAccount() {
    }

    public ExpenseAccount(Long accID, String expenseCategory, Double spendingLimit, int isSynced, int isDeleted) {
        this.accID = accID;
        this.expenseCategory = expenseCategory;
        this.spendingLimit = spendingLimit;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
    }

    public Long getAccID() {
        return accID;
    }

    public void setAccID(Long accID) {
        this.accID = accID;
    }

    public String getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public Double getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(Double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }
    @Override
    public int getIsSynced() {
        return isSynced;
    }
    @Override
    public void setIsSynced(int isSynced) {
        this.isSynced = isSynced;
    }
    @Override
    public int getIsDeleted() {
        return isDeleted;
    }
    @Override
    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }
    @Override
    public Long getId(){
        return this.accID;
    }

}
