package com.hiruna.dm2_backend.interfaces;

public interface SyncModel {
    Long getId();
    void setIsSynced(int value);
    int getIsSynced();
    void setIsDeleted(int value);
    int getIsDeleted();
}