package com.bidhee.chameleon.global;

import org.json.JSONObject;

import com.bidhee.model.User;

import android.content.SharedPreferences;

public class Configuration {
    private SharedPreferences prefs;

    private static final String PREF_KEY_GCM_DEVICE_TOKEN   = "devicetoken";
    private static final String PREF_KEY_USER_INFO        	= "user";
    private static final String PREF_KEY_LOGGED_IN          = "loggedin";

    public Configuration(final SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public void saveDeviceToken(String strToken){
        try{
            if (prefs == null) return;
            prefs.edit().putString(PREF_KEY_GCM_DEVICE_TOKEN, strToken).commit();
        }catch(Exception e){
            e.printStackTrace();
        }catch(Error err){
            err.printStackTrace();
        }catch(Throwable th){
            th.printStackTrace();
        }finally{}
    }

    public String getDeviceToken(){
        try{
            if (prefs == null) return "";
            return prefs.getString(PREF_KEY_GCM_DEVICE_TOKEN, "");
        }catch(Exception e){
            e.printStackTrace();
        }catch(Error err){
            err.printStackTrace();
        }catch(Throwable th){
            th.printStackTrace();
        }finally{}

        return "";
    }
    
    public void saveLogInUserInfo(User user){
    	try{
    		if (prefs == null) return;
    		if (user == null) return;
    		prefs.edit().putString(PREF_KEY_USER_INFO, user.getUserObjectAsJSON().toString()).commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public User getLoginUserInfo(){
    	try{
    		if (prefs == null) return null;
    		String strUser = prefs.getString(PREF_KEY_USER_INFO, "");
    		return new User(new JSONObject(strUser));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return null;
    }
    
    public void saveIfLoggedIn(boolean bFlag){
    	try{
    		if (prefs == null) return;
    		prefs.edit().putBoolean(PREF_KEY_LOGGED_IN, bFlag).commit();
    	}catch(Exception e){
    		e.printStackTrace();
    	}    	
    }
    
    public boolean checkIfLoggedIn(){
    	try{
    		if (prefs == null) return false;
    		return prefs.getBoolean(PREF_KEY_LOGGED_IN, false);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return false;
    }
}
