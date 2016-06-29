package com.bidhee.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.bidhee.chameleon.global.Define;

public class User {
	public int 		user_id;
	public String 	user_name;
	public String 	user_avatar;
	public String 	user_email;
	public String 	user_password;
	public String 	user_gender;
	public String 	user_birthday;
	public String 	user_social_id;
	public int 		user_social_type;
	public String 	user_device_type;
	public String 	user_device_token;
	public int		user_status;
	public String 	user_verification_code;
	public String	user_verification_code_created;
	
	public User(){
		user_id = -1;
		user_name = "";
		user_avatar = "";
		user_email = "";
		user_password = "";
		user_gender = "";
		user_birthday = "";
		user_social_id = "";
		user_social_type = 0;
		user_device_type = "android";
		user_device_token = "";
		user_status = 0;
		user_verification_code = "";
		user_verification_code_created = "";
	}
	
	public User(JSONObject obj){
		this();
		if (obj == null) return;

		user_id 							= obj.optInt(Define.TAG_USER_ID, -1);
		user_name							= obj.optString(Define.TAG_USER_NAME, "");
		user_avatar							= obj.optString(Define.TAG_USER_AVATAR, "");
		user_email							= obj.optString(Define.TAG_USER_EMAIL, "");
		user_password						= obj.optString(Define.TAG_USER_PASSWORD, "");
		user_gender							= obj.optString(Define.TAG_USER_GENDER, "");
		user_birthday						= obj.optString(Define.TAG_USER_BIRTHDAY, "");
		user_social_id						= obj.optString(Define.TAG_USER_SOCIAL_ID, "");
		user_social_type					= obj.optInt(Define.TAG_USER_SOCIAL_TYPE, 0);
		user_device_type					= obj.optString(Define.TAG_USER_DEVICE_TYPE, "");
		user_device_token					= obj.optString(Define.TAG_USER_DEVICE_TOKEN, "");
		user_status							= obj.optInt(Define.TAG_USER_STATUS);
		user_verification_code				= obj.optString(Define.TAG_USER_VERIFICATION_CODE, "");
		user_verification_code_created 		= obj.optString(Define.TAG_USER_VERIFICATION_CODE_CREATED, "");
	}
	
	public JSONObject getUserObjectAsJSON(){
		JSONObject retVal = new JSONObject();
		
		try{
			retVal.put(Define.TAG_USER_ID, user_id);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		try{
			retVal.put(Define.TAG_USER_NAME, user_name);
		}catch(JSONException e){
			e.printStackTrace();
		}

		try{
			retVal.put(Define.TAG_USER_AVATAR, user_avatar);
		}catch(JSONException e){
			e.printStackTrace();
		}

		try{
			retVal.put(Define.TAG_USER_EMAIL, user_email);
		}catch(JSONException e){
			e.printStackTrace();
		}

		try{
			retVal.put(Define.TAG_USER_PASSWORD, user_password);
		}catch(JSONException e){
			e.printStackTrace();
		}

		try{
			retVal.put(Define.TAG_USER_GENDER, user_gender);
		}catch(JSONException e){
			e.printStackTrace();
		}

		try{
			retVal.put(Define.TAG_USER_BIRTHDAY, user_birthday);
		}catch(JSONException e){
			e.printStackTrace();
		}

		try{
			retVal.put(Define.TAG_USER_SOCIAL_ID, user_social_id);
		}catch(JSONException e){
			e.printStackTrace();
		}

		try{
			retVal.put(Define.TAG_USER_SOCIAL_TYPE, user_social_type);
		}catch(JSONException e){
			e.printStackTrace();
		}

		try{
			retVal.put(Define.TAG_USER_DEVICE_TYPE, user_device_type);
		}catch(JSONException e){
			e.printStackTrace();
		}
		
		try{
			retVal.put(Define.TAG_USER_DEVICE_TOKEN, user_device_token);
		}catch(JSONException e){
			e.printStackTrace();
		}

		return retVal;
	}
}
