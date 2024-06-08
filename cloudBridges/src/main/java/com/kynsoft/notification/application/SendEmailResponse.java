package com.kynsoft.notification.application;

public class SendEmailResponse {
    private Boolean status;

    private String message;

    public SendEmailResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
        //this.message = "Message sent successfully!";
    }

    public SendEmailResponse() {
    }

    public Boolean getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
