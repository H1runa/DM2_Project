package com.hiruna.dm2_backend.data.model;

import com.hiruna.dm2_backend.interfaces.SyncModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SAVINGGOAL")
public class SavingGoal implements SyncModel{
    @Id
    @Column(name = "goalID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goalID;

    @Column(name = "goalName")
    private String goalName;

    @Column(name = "targetAmount")
    private Double targetAmount;

    @Column(name = "createdDate", insertable = false)
    private String createdDate;

    @Column(name = "deadline")
    private String deadline;

    @Column(name = "accID")
    private Long accID;

    @Column(name = "isSynced")
    private int isSynced;

    @Column(name = "isDeleted")
    private int isDeleted;

    public SavingGoal() {
    }

    public SavingGoal(Long goalID, String goalName, Double targetAmount, String createdDate, String deadline,
            Long accID, int isSynced, int isDeleted) {
        this.goalID = goalID;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.createdDate = createdDate;
        this.deadline = deadline;
        this.accID = accID;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
    }

    public Long getGoalID() {
        return goalID;
    }

    public void setGoalID(Long goalID) {
        this.goalID = goalID;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public Double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(Double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
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
        return this.goalID;
    }

}
