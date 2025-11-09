package com.hiruna.dm2_backend.data.model;

import com.hiruna.dm2_backend.interfaces.SyncModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ACCOUNTTRANSACTION")
public class AccountTransaction implements SyncModel {
    @Id
    @Column(name = "transID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transID;

    @Column(name = "transName")
    private String transName;

    @Column(name = "transType")
    private String transType;

    @Column(name = "transDate", insertable = false)
    private String transDate;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "accID")
    private Long accID;

    @Column(name = "isSynced")
    private int isSynced;

    @Column(name = "isDeleted")
    private int isDeleted;

    public AccountTransaction() {
    }

    public AccountTransaction(Long transID, String transName, String transType, String transDate, Double amount,
            Long accID, int isSynced, int isDeleted) {
        this.transID = transID;
        this.transName = transName;
        this.transType = transType;
        this.transDate = transDate;
        this.amount = amount;
        this.accID = accID;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
    }

    public Long getTransID() {
        return transID;
    }

    public void setTransID(Long transID) {
        this.transID = transID;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getAccID() {
        return accID;
    }

    public void setAccID(Long accID) {
        this.accID = accID;
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
        return this.transID;
    }
}
