package com.hiruna.dm2_backend.data.dto;

public class AccountDTO {
    private Long accID;
    private String accName;
    private String description;
    private Double initialAmount;
    private Long userID;
    private String accountType;
    private String category;
    private Double limit;
    
    public AccountDTO() {
    }

    public AccountDTO(Long accID, String accName, String description, Double initialAmount, Long userID, String accountType,
            String category, Double limit) {
        this.accID = accID;
        this.accName = accName;
        this.description = description;
        this.initialAmount = initialAmount;
        this.userID = userID;
        this.accountType = accountType;
        this.category = category;
        this.limit = limit;
    }

    public Long getAccID(){
        return this.accID;
    }

    public void setAccID(Long id){
        this.accID = id;
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
    public String getAccountType() {
        return accountType;
    }
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public Double getLimit() {
        return limit;
    }
    public void setLimit(Double limit) {
        this.limit = limit;
    }  
}
