package com.bidhee.model.comm;


import org.json.JSONObject;

/**
 * Created by JinjinLee on 1/6/16.
 */
public interface ResponseDelegate{
    public void succeed(final JSONObject responseObj);
    public void failed(final JSONObject errorObj);
}
