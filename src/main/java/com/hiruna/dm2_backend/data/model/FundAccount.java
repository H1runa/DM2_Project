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
@Table(name = "FUNDACCOUNT")
public class FundAccount implements SyncModel {
    @Id
    @Column(name = "accID")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "manual-id")
    @GenericGenerator(name = "manual-id", strategy = "assigned")
    private Long accID;

    @Column(name = "fundType")
    private String fundType;

    @Column(name = "minimumLimit")
    private Double minimumLimit;

    @Column(name = "isSynced")
    private int isSynced;

    @Column(name = "isDeleted")
    private int isDeleted;

    public FundAccount() {
    }

    public FundAccount(Long accID, String fundType, Double minimumLimit, int isSynced, int isDeleted) {
        this.accID = accID;
        this.fundType = fundType;
        this.minimumLimit = minimumLimit;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
    }

    public Long getAccID() {
        return accID;
    }

    public void setAccID(Long accID) {
        this.accID = accID;
    }

    public String getFundType() {
        return fundType;
    }

    public void setFundType(String fundType) {
        this.fundType = fundType;
    }

    public Double getMinimumLimit() {
        return minimumLimit;
    }

    public void setMinimumLimit(Double minimumLimit) {
        this.minimumLimit = minimumLimit;
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
