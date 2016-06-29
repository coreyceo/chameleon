package com.bidhee.chameleon.communication;

import com.bidhee.chameleon.global.Define;
import com.bidhee.chameleon.global.WebConfig;
import com.bidhee.model.comm.RequestData;
import com.bidhee.model.comm.ResponseDelegate;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("DefaultLocale")
public class CommunicationAPIManager {
	Context mContext;
	private RequestData requestData;

	public CommunicationAPIManager(Context ctx){
		mContext = ctx;
	}

	public void sendSignupRequest(final String 	user_email,
			final String 		user_password,
			final String		user_device_token,
			final ResponseDelegate callback){		
		try{
			final String  strURL = WebConfig.BASE_URL;
			RequestParams params = new RequestParams();

			params.put(Define.TAG_API_ACTION,			Define.TAG_API_ACTION_SIGNUP);
			params.put(Define.TAG_USER_EMAIL, 			user_email);
			params.put(Define.TAG_USER_PASSWORD, 		user_password);
			params.put(Define.TAG_USER_DEVICE_TYPE, 	Define.PLATFORM_ANDROID);
			params.put(Define.TAG_USER_DEVICE_TOKEN, 	user_device_token);

			requestData = new RequestData(mContext, callback);
			requestData.setUrl(strURL);
			requestData.setResponseType(null);

			requestData.setRequestParams(params);
			requestData.executePostWithoutProgressBar();
		}catch(Exception e){
			e.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Error err){
			err.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Throwable th){
			th.printStackTrace();
			if (callback != null) callback.failed(null);
		}finally{}
	}

	public void sendVerifyUserRequest(final int user_id,
			final String verification_code,
			final ResponseDelegate callback){		
		try{
			final String  strURL = WebConfig.BASE_URL;
			RequestParams params = new RequestParams();

			params.put(Define.TAG_API_ACTION,					Define.TAG_API_ACTION_VERIFY_USER);
			params.put(Define.TAG_USER_ID, 						user_id);
			params.put(Define.TAG_USER_VERIFICATION_CODE, 		verification_code);

			requestData = new RequestData(mContext, callback);
			requestData.setUrl(strURL);
			requestData.setResponseType(null);

			requestData.setRequestParams(params);
			requestData.executePostWithoutProgressBar();
		}catch(Exception e){
			e.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Error err){
			err.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Throwable th){
			th.printStackTrace();
			if (callback != null) callback.failed(null);
		}finally{}
	}

	public void sendResendVerificationCodeRequest(final int user_id,
			final ResponseDelegate callback){		
		try{
			final String  strURL = WebConfig.BASE_URL;
			RequestParams params = new RequestParams();

			params.put(Define.TAG_API_ACTION,					Define.TAG_API_ACTION_RESEND_CODE);
			params.put(Define.TAG_USER_ID, 						user_id);

			requestData = new RequestData(mContext, callback);
			requestData.setUrl(strURL);
			requestData.setResponseType(null);

			requestData.setRequestParams(params);
			requestData.executePostWithoutProgressBar();
		}catch(Exception e){
			e.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Error err){
			err.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Throwable th){
			th.printStackTrace();
			if (callback != null) callback.failed(null);
		}finally{}
	}

	public void sendForgotPasswordRequest(final String 	user_email,
			final ResponseDelegate callback){		
		try{
			final String  strURL = WebConfig.BASE_URL;
			RequestParams params = new RequestParams();

			params.put(Define.TAG_API_ACTION,			Define.TAG_API_ACTION_FORGOT_PASSWORD);
			params.put(Define.TAG_USER_EMAIL, 			user_email);

			requestData = new RequestData(mContext, callback);
			requestData.setUrl(strURL);
			requestData.setResponseType(null);

			requestData.setRequestParams(params);
			requestData.executePostWithoutProgressBar();
		}catch(Exception e){
			e.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Error err){
			err.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Throwable th){
			th.printStackTrace();
			if (callback != null) callback.failed(null);
		}finally{}
	}

	public void sendSigninSocialRequest(String 		user_email,
										String 		user_social_id,
										int			user_social_type,
										String 		user_name,
										String		user_device_token,
										String		user_avatar,
										String		user_gender,
										final ResponseDelegate callback){
		try{
			final String  strURL = WebConfig.BASE_URL;
			RequestParams params = new RequestParams();

			params.put(Define.TAG_API_ACTION,			Define.TAG_API_ACTION_SIGNIN_SOCIAL);
			params.put(Define.TAG_USER_EMAIL, 			user_email);
			params.put(Define.TAG_USER_SOCIAL_ID, 		user_social_id);
			params.put(Define.TAG_USER_SOCIAL_TYPE, 	user_social_type);
			params.put(Define.TAG_USER_NAME, 			user_name);
			params.put(Define.TAG_USER_AVATAR, 			user_avatar);
			params.put(Define.TAG_USER_GENDER, 			user_gender);
			params.put(Define.TAG_USER_DEVICE_TYPE, 	Define.PLATFORM_ANDROID);
			params.put(Define.TAG_USER_DEVICE_TOKEN, 	user_device_token);

			requestData = new RequestData(mContext, callback);
			requestData.setUrl(strURL);
			requestData.setResponseType(null);

			requestData.setRequestParams(params);
			requestData.executePostWithoutProgressBar();
		}catch(Exception e){
			e.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Error err){
			err.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Throwable th){
			th.printStackTrace();
			if (callback != null) callback.failed(null);
		}finally{}
	}

	public void sendSigninRequest(String 	user_email,
			String 		user_password,
			String		user_device_token,
			final ResponseDelegate callback){
		try{
			final String  strURL = WebConfig.BASE_URL;
			RequestParams params = new RequestParams();

			params.put(Define.TAG_API_ACTION,			Define.TAG_API_ACTION_SIGNIN);
			params.put(Define.TAG_USER_EMAIL, 			user_email);
			params.put(Define.TAG_USER_PASSWORD, 		user_password);
			params.put(Define.TAG_USER_DEVICE_TYPE, 	Define.PLATFORM_ANDROID);
			params.put(Define.TAG_USER_DEVICE_TOKEN, 	user_device_token);

			requestData = new RequestData(mContext, callback);
			requestData.setUrl(strURL);
			requestData.setResponseType(null);

			requestData.setRequestParams(params);
			requestData.executePostWithoutProgressBar();
		}catch(Exception e){
			e.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Error err){
			err.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Throwable th){
			th.printStackTrace();
			if (callback != null) callback.failed(null);
		}finally{}
	}

	public void sendGetFaqHelpRequest(final ResponseDelegate callback)
	{
		try{
			final String  strURL = WebConfig.BASE_URL;
			RequestParams params = new RequestParams();

			params.put(Define.TAG_API_ACTION,			Define.TAG_API_ACTION_GET_FAQ_HELP);

			requestData = new RequestData(mContext, callback);
			requestData.setUrl(strURL);
			requestData.setResponseType(null);

			requestData.setRequestParams(params);
			requestData.executePostWithoutProgressBar();
		}catch(Exception e){
			e.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Error err){
			err.printStackTrace();
			if (callback != null) callback.failed(null);
		}catch(Throwable th){
			th.printStackTrace();
			if (callback != null) callback.failed(null);
		}finally{}
	}
}