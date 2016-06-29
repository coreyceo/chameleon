package com.bidhee.chameleon.global;

public class Define {

//Global Constants
	public static final String		PLATFORM_ANDROID			= "android";
	public static final int			USER_STATUS_PENDING			= 0;
	public static final int			USER_STATUS_VERIFIED		= 1;
	
	public static final int			SOCIAL_TYPE_NONE			= 0;
	public static final int			SOCIAL_TYPE_GOOGLE			= 1;
	public static final int			SOCIAL_TYPE_FB				= 2;
	public static final int			SOCIAL_TYPE_TWITTER			= 3;
	
//Tags for API
	public static final String		TAG_API_RESULT			= "result";
	public static final String		TAG_API_ACTION			= "action";
	public static final String		TAG_API_MESSAGE			= "message";
	public static final String		TAG_API_VALUES			= "values";
	
//Tags for API Actions
	public static final String		TAG_API_ACTION_SIGNIN				= "signinUser";
	public static final String		TAG_API_ACTION_SIGNIN_SOCIAL		= "signinSocialUser";
	public static final String		TAG_API_ACTION_SIGNUP				= "signupUser";
	public static final String 		TAG_API_ACTION_FORGOT_PASSWORD 		= "forgotPassword";
	public static final String		TAG_API_ACTION_VERIFY_USER			= "verifyUser";
	public static final String		TAG_API_ACTION_RESEND_CODE			= "resendVerificationCode";
	public static final String		TAG_API_ACTION_GET_FAQ_HELP			= "getAllFaqHelp";
	
//Tags for User
	public static final String		TAG_USER_OBJECT							= "user";
	public static final String 		TAG_USER_ID 							= "user_id";
	public static final String  	TAG_USER_NAME 							= "user_name";
	public static final String  	TAG_USER_AVATAR							= "user_avatar";
	public static final String  	TAG_USER_EMAIL							= "user_email";
	public static final String  	TAG_USER_PASSWORD						= "user_password";
	public static final String  	TAG_USER_GENDER							= "user_gender";
	public static final String  	TAG_USER_BIRTHDAY						= "user_birthday";
	public static final String  	TAG_USER_SOCIAL_ID						= "user_social_id";
	public static final String 		TAG_USER_SOCIAL_TYPE					= "user_social_type";
	public static final String  	TAG_USER_DEVICE_TYPE					= "user_device_type";
	public static final String  	TAG_USER_DEVICE_TOKEN					= "user_device_token";
	public static final String		TAG_USER_STATUS							= "user_status";
	public static final String		TAG_USER_VERIFICATION_CODE				= "user_verification_code";
	public static final String		TAG_USER_VERIFICATION_CODE_CREATED		= "user_verification_code_created";
	
//Tags for FaqHelp
	public static final String		TAG_FHS_OBJECTS			= "faq_helps";
	public static final String		TAG_FH_FAQ_HELP_ID		= "faq_help_id";
	public static final String		TAG_FH_FAQ				= "faq";
	public static final String		TAG_FH_HELP				= "help";
	public static final String		TAG_FH_CREATED_AT		= "created_at";
}