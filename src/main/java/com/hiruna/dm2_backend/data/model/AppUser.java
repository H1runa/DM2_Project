package com.hiruna.dm2_backend.data.model;
import jakarta.persistence.*;

@Entity
@Table(name = "APPUSER")
public class AppUser {
    @Id
    @Column(name = "userID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userID;

    @Column(name = "accountName")
    private String accountName;

    @Column(name = "password")
    private String password;

    @Column(name = "isSynced")
    private int isSynced;   
    
    @Column(name = "isDeleted")
    private int isDeleted;    

    public AppUser() {
    }

    public AppUser(long userID, String accountName, String password, int isSynced, int isDeleted) {
        this.userID = userID;
        this.accountName = accountName;
        this.password = password;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
    
    
}
