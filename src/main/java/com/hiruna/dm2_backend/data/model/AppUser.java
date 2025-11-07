package com.hiruna.dm2_backend.data.model;
import com.hiruna.dm2_backend.interfaces.SyncModel;

import jakarta.persistence.*;

@Entity
@Table(name = "APPUSER")
public class AppUser implements SyncModel {
    @Id
    @Column(name = "userID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userID;

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

    public AppUser(Long userID, String accountName, String password, int isSynced, int isDeleted) {
        this.userID = userID;
        this.accountName = accountName;
        this.password = password;
        this.isSynced = isSynced;
        this.isDeleted = isDeleted;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
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
        return this.userID;
    }
    
    
}
