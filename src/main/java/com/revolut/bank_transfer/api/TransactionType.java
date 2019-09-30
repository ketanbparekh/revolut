package com.revolut.bank_transfer.api;

public enum TransactionType {

    WITHDRAWL("withdrawl"),
    DEPOSIT("deposit"),
    TRANSFER("transfer");
    
    private final String transactionType;

    public String getStatus() {
        return transactionType;
    }
    
    TransactionType(String transactionType){
        this.transactionType = transactionType;
    }
}
