package com.tech.api;

public class BaseResponse {
    private String status;
    private String transactionId;
    private String message;
    private int statusCode;

    public BaseResponse() {}

    public BaseResponse(String status, String transactionId, String message, int statusCode) {
        this.setStatus(status);
        this.setTransactionId(transactionId);
        this.setMessage(message);
        this.setStatusCode(statusCode);
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

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

}
