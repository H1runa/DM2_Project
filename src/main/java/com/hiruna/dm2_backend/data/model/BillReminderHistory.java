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
@Table(name = "BILLREMINDERHISTORY")
public class BillReminderHistory implements SyncModel {
    @Id
    @Column(name = "historyID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)    
    private Long historyID;

    @Column(name = "remindID")
    private Long remindID;

    @Column(name = "remindName")
    private String remindName;

    @Column(name = "deadline")
    private String deadline;

    @Column(name = "status")
    private String status;

    @Column(name = "userID")
    private Long userID;

    @Column(name = "isSynced")
    private int isSynced;

    @Column(name = "isDeleted")
    private int isDeleted;

    public BillReminderHistory() {
    }

    public BillReminderHistory(Long historyID, Long remindID, String remindName, String deadline, String status,
            Long userID, int isSynced, int isDeleted) {
        this.historyID = historyID;
        this.remindID = remindID;
        this.remindName = remindName;
        this.deadline = deadline;
        this.status = status;
        this.userID = userID;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
    }

    public Long getHistoryID() {
        return historyID;
    }

    public void setHistoryID(Long historyID) {
        this.historyID = historyID;
    }

    public Long getRemindID() {
        return remindID;
    }

    public void setRemindID(Long remindID) {
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
        return this.historyID;
    }
}
