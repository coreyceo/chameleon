package com.bidhee.model.comm;

/**
 * Created by JinjinLee on 1/6/16.
 */
public class BaseResponseData {

    private String action;
    private boolean result;
    private String messages;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return messages;
    }

    public void setMessage(String message) {
        this.messages = message;
    }
}
