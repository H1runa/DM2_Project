package com.hiruna.dm2_backend.data.model;

import com.hiruna.dm2_backend.interfaces.SyncModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "BILLREMINDER")
public class BillReminder implements SyncModel {
    @Id
    @Column(name = "remindID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long remindID;

    @Column(name = "remindName")
    private String remindName;

    @Column(name = "deadline")
    private String deadline;

    @Column(name = "status")
    private String status;

    @Column(name = "userID")
    private long userID;

    @Column(name = "isSynced")
    private int isSynced;

    @Column(name = "isDeleted")
    private int isDeleted;

    public BillReminder() {
    }
    
    public BillReminder(long remindID, String remindName, String deadline, String status, long userID, int isSynced,
            int isDeleted) {
        this.remindID = remindID;
        this.remindName = remindName;
        this.deadline = deadline;
        this.status = status;
        this.userID = userID;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
    }

    public long getRemindID() {
        return remindID;
    }

    public void setRemindID(long remindID) {
        this.remindID = remindID;
    }

    public String getRemindName() {
        return remindName;
    }

    public void setRemindName(String remindName) {
        this.remindName = remindName;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public int getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(int isSynced) {
        this.isSynced = isSynced;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public long getId(){
        return this.remindID;
    }            
}
