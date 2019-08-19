package com.revolut.bank_transfer.api;

public enum TransactionStatus {

    PENDING(0),
    FAILED(1),
    SUCCESSFUL(2);
    
    private final int status;

    public int getStatus() {
        return status;
    }
    
    TransactionStatus(int status) {
        this.status = status;
    }
}
