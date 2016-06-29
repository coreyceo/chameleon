package com.bidhee.model.comm;

import java.util.HashMap;

import com.bidhee.util.Util;

import android.content.Context;
import android.widget.Toast;


public class RequestData extends BaseRequestData {

    private ServiceRequest serviceRequest;
    @SuppressWarnings("unused")
	private HashMap<String, String> header;

    public RequestData(Context context, ResponseDelegate delegate) {
        super(context, delegate);
    }

    public void executePost() {

        if(Util.isNetworkAvailable(context)) {
            serviceRequest = new ServiceRequest(context, this);
            serviceRequest.executeRequest();

        } else {
            Toast.makeText(context, CommConfig.NETWORK_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
        }

    }

    public void executePostWithoutProgressBar() {

        if(Util.isNetworkAvailable(context)) {
            serviceRequest = new ServiceRequest(context, this);
            serviceRequest.executeRequestWithoutProgressbar();

        } else {
            Toast.makeText(context, CommConfig.NETWORK_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void executeGet() {

        if(Util.isNetworkAvailable(context)) {
            serviceRequest = new ServiceRequest(context, this);
            serviceRequest.executeGetRequest();
        } else {
            Toast.makeText(context, CommConfig.NETWORK_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void executeGetWithoutProgressBar() {

        if(Util.isNetworkAvailable(context)) {
            serviceRequest = new ServiceRequest(context, this);
            serviceRequest.executeGetRequestWithoutProgressBar();
        } else {
            Toast.makeText(context, CommConfig.NETWORK_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void executePut() {

        if(Util.isNetworkAvailable(context)) {
            serviceRequest = new ServiceRequest(context, this);
            serviceRequest.executePutRequest();
        } else {
            Toast.makeText(context, CommConfig.NETWORK_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void executePutWithoutProgressBar() {

        if(Util.isNetworkAvailable(context)) {
            serviceRequest = new ServiceRequest(context, this);
            serviceRequest.executePutRequestWithoutProgressBar();
        } else {
            Toast.makeText(context, CommConfig.NETWORK_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void executePatch() {

        if(Util.isNetworkAvailable(context)) {
            serviceRequest = new ServiceRequest(context, this);
            serviceRequest.executePatchRequest();
        } else {
            Toast.makeText(context, CommConfig.NETWORK_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void executePatchWithoutProgressBar() {

        if(Util.isNetworkAvailable(context)) {
            serviceRequest = new ServiceRequest(context, this);
            serviceRequest.executePatchRequestWithoutProgressBar();
        } else {
            Toast.makeText(context, CommConfig.NETWORK_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void executeDelete() {

        if(Util.isNetworkAvailable(context)) {
            serviceRequest = new ServiceRequest(context, this);
            serviceRequest.executeDeleteRequest();
        } else {
            Toast.makeText(context, CommConfig.NETWORK_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void executeDeleteWithoutProgressBar() {

        if(Util.isNetworkAvailable(context)) {
            serviceRequest = new ServiceRequest(context, this);
            serviceRequest.executeDeleteRequestWithoutProgressBar();
        } else {
            Toast.makeText(context, CommConfig.NETWORK_CONNECTION_ERROR, Toast.LENGTH_LONG).show();
        }
    }
}
