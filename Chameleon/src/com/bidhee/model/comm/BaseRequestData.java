package com.bidhee.model.comm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.RequestParams;

import android.content.Context;



/**
 * Created by JinjinLee on 1/6/16.
 */
public abstract class BaseRequestData {

    protected Context context;
    protected ResponseDelegate responseDelegate;

    private String url;
    private String REQUEST_ACTION;
    private Class<?> responseType;
    private RequestParams requestParams;
    private JSONObject requestedParameter;

    public BaseRequestData(Context context, ResponseDelegate delegate) {

        this.context = context;
        this.responseDelegate = delegate;
    }

    public Context getContext() {

        return context;
    }

    protected String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAction(){

        return REQUEST_ACTION;
    }

    public void setRequestAction(String requestAction){
        this.REQUEST_ACTION = requestAction;
    }

    protected ResponseDelegate getResponseDelegate() {

        return responseDelegate;
    }

    public Class<?> getResponseType() {
        return responseType;
    }

    public void setResponseType(Class<?> responseType) {
        this.responseType = responseType;
    }

    public RequestParams getRequestParams() {
        return requestParams;
    }

    public JSONObject getParamBody() {
        return requestedParameter;
    }

    public void setRequestParams(RequestParams requestParams) {
        this.requestParams = requestParams;
    }

    public void setJsonArray(String tagName, JSONArray jArray) throws JSONException {

        requestedParameter.putOpt(tagName, jArray);
    }
}
