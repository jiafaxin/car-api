package com.autohome.car.api.common.http;

import lombok.Data;

import java.io.Serializable;

public class ApiPicHotResult implements Serializable {

    private boolean status;
    private String message;

    private Body body;


    public static class Body{

        private String totalStatus;

        public String getTotalStatus() {
            return totalStatus;
        }

        public void setTotalStatus(String totalStatus) {
            this.totalStatus = totalStatus;
        }
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
