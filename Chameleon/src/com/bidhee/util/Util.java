package com.bidhee.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.Charset;

/**
 * Created by Han on 2/1/16.
 */
public class Util {
    public static String base64Encode(String stringData) {
        byte[] encodedBytes = Base64.encode(stringData.getBytes(), Base64.DEFAULT);
        return new String(encodedBytes, Charset.forName("UTF-8"));
    }

    public static String base64Encode(byte[] byteData) {
        byte[] encodedBytes = Base64.encode(byteData, Base64.DEFAULT);
        return new String(encodedBytes, Charset.forName("UTF-8"));
    }

    public static String base64Decode(String stringData) {
        try{
            byte[] decodedBytes = Base64.decode(stringData.getBytes(), Base64.DEFAULT);
            return new String(decodedBytes, Charset.forName("UTF-8"));
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.w("Chat content", "Base64 Decode Error,,,");
            return null;
        }
    }

    public static String base64Decode(byte[] byteData) {

        try{
            byte[] decodedBytes = Base64.decode(byteData, Base64.DEFAULT);
            return new String(decodedBytes, Charset.forName("UTF-8"));
        }catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.w("Chat content", "Base64 Decode Error,,,");
            return null;
        }
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();

    }
}
