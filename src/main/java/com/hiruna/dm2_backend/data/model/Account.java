package com.hiruna.dm2_backend.data.model;

import com.hiruna.dm2_backend.interfaces.SyncModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ACCOUNT")
public class Account implements SyncModel{
    @Id
    @Column(name = "accID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accID;

    @Column(name = "accName")
    private String accName;

    @Column(name = "description")
    private String description;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "createdDate")
    private String createdDate;

    @Column(name = "status")
    private String status;

    @Column(name = "initialAmount")
    private Double initialAmount;

    @Column(name = "userID")
    private Long userID;

    @Column(name = "isSynced")
    private int isSynced;

    @Column(name = "isDeleted")
    private int isDeleted;

    public Account() {
    }

    public Account(Long accID, String accName, String description, Double balance, String createdDate, String status,
            Double initialAmount, Long userID, int isSynced, int isDeleted) {
        this.accID = accID;
        this.accName = accName;
        this.description = description;
        this.balance = balance;
        this.createdDate = createdDate;
        this.status = status;
        this.initialAmount = initialAmount;
        this.userID = userID;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
    }

    public Long getAccID() {
        return accID;
    }

    public void setAccID(Long accID) {
        this.accID = accID;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
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
