package com.tech.api;

public class BaseResponseObject {
    private String status;
    private String transactionId;
    private String message;

    public BaseResponseObject(String status, String transactionId, String message) {
        this.setStatus(status);
        this.setTransactionId(transactionId);
        this.setMessage(message);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
