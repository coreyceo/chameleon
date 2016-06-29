package com.bidhee.chameleon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bidhee.chameleon.communication.CommunicationAPIManager;
import com.bidhee.chameleon.global.Define;
import com.bidhee.chameleon.global.Globals;
import com.bidhee.dao.UserTableDAO;
import com.bidhee.metadata.APIUrls;
import com.bidhee.metadata.DAOSessionTable;
import com.bidhee.model.DTOSession;
import com.bidhee.model.UserModel;
import com.bidhee.model.comm.ResponseDelegate;
import com.bidhee.util.AbstractGetNameTask;
import com.bidhee.util.GetNameInForeground;
import com.bidhee.util.PConnectionDetectorUtils;
import com.facebook.Session;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class SignInActivity extends ActionBarActivity {

	private ButtonFlat mBtnForgotPassword;
	private ButtonFlat mBtnRegister;

	private Button mBtnSignIn;

	private LinearLayout mLlGooglePlus;
	private LinearLayout mLlFB;
	private LinearLayout mLlTwitter;

	private Toolbar mToolbarSignin;
	private EditText mEdtUsername;
	private EditText mEdtPassword;

	private LinearLayout mLlSigninRoot;
	private ProgressBarCircularIndeterminate mPbSigning;
	private PConnectionDetectorUtils mConnectionDetector;
	private boolean isInternetConnected = false;
	private boolean error;
	private String message;

	/*
	 * Twitter Integration
	 */
	/* Shared preference keys */
	private static final String PREF_NAME = "sample_twitter_pref";
	private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
	private static final String PREF_USER_NAME = "twitter_user_name";

	private String consumerKey = null;
	private String consumerSecret = null;
	private String callbackUrl = null;
	private String oAuthVerifier = null;

	private static Twitter twitter;
	private static RequestToken requestToken;
	private static int RC_SIGN_TWITTER;
	private static SharedPreferences mSharedPreferences;
	public static final int WEBVIEW_REQUEST_CODE = 100;
	private Handler mHandler = new Handler();

	// google +

	AccountManager mAccountManager;
	String token;
	int serverCode;
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";


	//Facebook

	final Session fbsession = Session.getActiveSession();
	SimpleFacebook mSimpleFacebook;
	@SuppressWarnings("unused")
	private List<NameValuePair> params;
	//	private GetFaceBookTask facebookTask;


	//GCM 

	private String registrationID = "";
	GoogleCloudMessaging gcm;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String SENDER_ID = "1030302571509";

	//Social Variable

	private String userName = "";
	private String userSocialId = "";
	private String userEmail = "";
	private String userSocialType ="";
	private String userGender = "";
	private int		mSocialType = 0;
	private String userSocialImage ="";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);


		mConnectionDetector = new PConnectionDetectorUtils(getApplicationContext());
		isInternetConnected = mConnectionDetector.isConnectingToInternet();
		findView();

		clickListner();
		initTwitterConfigs();

		Bundle googlBundle = getIntent().getExtras();
		if(googlBundle!=null){
			String type = googlBundle.getString("type");
			if(type.equals("Google")){
				userName = googlBundle.getString("userName");
				userSocialId = googlBundle.getString("userId");
				userSocialImage = googlBundle.getString("pic");
				userGender = googlBundle.getString("Gender");
				userSocialType = "Google";
				mSocialType = 1;
				checkGCMForSocial();
			}
		}
	}

	private void initTwitterConfigs() {
		consumerKey = getString(R.string.twitter_consumer_key);
		consumerSecret = getString(R.string.twitter_consumer_secret);
		callbackUrl = getString(R.string.twitter_callback);
		oAuthVerifier = getString(R.string.twitter_oauth_verifier);

		/* Enabling strict mode */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		if (TextUtils.isEmpty(consumerKey) || TextUtils.isEmpty(consumerSecret)) {
			Toast.makeText(this, "Twitter key and secret not configured",Toast.LENGTH_SHORT).show();
			return;
		}

		/* Initialize application preferences */
		mSharedPreferences = getSharedPreferences(PREF_NAME, 0);

		boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
		if (isLoggedIn) {
			Editor e = mSharedPreferences.edit();
			e.clear();
			e.commit();
		}
	}

	private void findView() {
		mBtnForgotPassword = (ButtonFlat) findViewById(R.id.btn_signin_forgot_password);
		mBtnRegister = (ButtonFlat) findViewById(R.id.btn_signin_signup);

		mBtnSignIn = (Button) findViewById(R.id.btn_signin);

		mLlGooglePlus = (LinearLayout) findViewById(R.id.ll_signin_google_plus);
		mLlFB = (LinearLayout) findViewById(R.id.ll_signin_fb);
		mLlTwitter = (LinearLayout) findViewById(R.id.ll_signin_twitter);

		mEdtUsername = (EditText) findViewById(R.id.edt_signin_username);
		mEdtPassword = (EditText) findViewById(R.id.edt_signin_password);
		mPbSigning = (ProgressBarCircularIndeterminate) findViewById(R.id.pb_signin);

		mLlSigninRoot =(LinearLayout) findViewById(R.id.ll_signin_root);

		mToolbarSignin = (Toolbar) findViewById(R.id.toolbar_signin);
		mToolbarSignin.setTitle("Login to Chameleon");
	}


	private void clickListner() {
		mLlGooglePlus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				syncGoogleAccount();
			}
		});

		mBtnForgotPassword.setOnClickListener(new OnClickListener() {

			@SuppressLint("InflateParams")
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
				builder.setTitle("Please enter email address!");
				View viewInflated = LayoutInflater.from(SignInActivity.this).inflate(R.layout.alert_dlg_input, null);
				final EditText input = (EditText) viewInflated.findViewById(R.id.edt_alert_dlg_enter);
				builder.setView(viewInflated);

				builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String strEmailAddress = input.getText().toString();
						if (strEmailAddress == null || strEmailAddress.isEmpty()){
							Toast.makeText(SignInActivity.this, R.string.msg_err_enter_email, Toast.LENGTH_SHORT).show();
							return;
						}
						dialog.dismiss();
						performForgotPassword(strEmailAddress);
					}
				});
				builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.show();
				//				Fragment forgetPasswordFrgament = new ForgetPasswordFragment();
				//				FragmentManager fragmentManager = getSupportFragmentManager();
				//				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				//				fragmentTransaction.replace(android.R.id.content, forgetPasswordFrgament);
				//				fragmentTransaction.addToBackStack(null);
				//				fragmentTransaction.commit();
			}
		});

		mBtnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), SignUpActivity.class));				
			}
		});

		mLlFB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isInternetConnected) {
					Toast.makeText(getApplicationContext(),"No internet Connection, can't sign up",Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(),"Please wait loging in", Toast.LENGTH_SHORT).show();
					mSimpleFacebook.login(onLoginListener);
				}
			}
		});

		mLlTwitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginToTwitter();				
			}
		});

		mBtnSignIn .setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!mEdtUsername.getText().toString().isEmpty()){
					if(mEdtUsername.getText().toString().contains("@")){
						if(!mEdtPassword.getText().toString().isEmpty()){							
							checkGCM();							
						}else{
							Toast.makeText(getApplicationContext(), "Enter password to continue", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "Error email pattern", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "Enter email to continue", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	//GCM
	private void checkGCM() {
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			try{
				new RegisterBackground().execute();
			}catch(ExceptionInInitializerError e){
				e.printStackTrace();
				userLogin(mEdtUsername.getText().toString(), mEdtPassword.getText().toString());				
			}
		}
	}

	private void checkGCMForSocial() {
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			try{
				new RegisterSocial().execute();
			}catch(ExceptionInInitializerError e){
				e.printStackTrace();
				userLoginSocial();
			}
		}
	}

	private boolean checkPlayServices() {

		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				try {
					GooglePlayServicesUtil.getErrorDialog(resultCode, this,PLAY_SERVICES_RESOLUTION_REQUEST).show();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			return false;
		}
		return true;
	}

	public void syncGoogleAccount() {
		if (isInternetConnected) {
			String[] accountarrs = getAccountNames();
			if (accountarrs.length > 0) {
				// you can set here account for login
				getTask(SignInActivity.this, accountarrs[0], SCOPE).execute();

			} else {
				Toast.makeText(SignInActivity.this, "No Google Account Sync!", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(SignInActivity.this, "No Network Service!",Toast.LENGTH_SHORT).show();
		}
	}

	private String[] getAccountNames() {
		mAccountManager = AccountManager.get(this);
		Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
		String[] names = new String[accounts.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = accounts[i].name;
		}
		return names;
	}

	private AbstractGetNameTask getTask(SignInActivity activity, String email, String scope) {
		return new GetNameInForeground(activity, email, scope);
	}

	//Facebook
	final OnLoginListener onLoginListener = new OnLoginListener() {

		@Override
		public void onFail(String reason) {
		}

		@Override
		public void onException(Throwable throwable) {
		}

		@Override
		public void onThinking() {
		}

		@Override
		public void onLogin() {
			loggedInUIState();
		}

		@Override
		public void onNotAcceptingPermissions(Permission.Type type) {
		}
	};

	protected void loggedInUIState() {
		mSimpleFacebook= SimpleFacebook.getInstance();
		if (mSimpleFacebook.isLogin()) {
			System.out.println("LOGGED IN");
			SimpleFacebook.getInstance().getProfile(onProfileListener);
		}
	}


	final OnProfileListener onProfileListener = new OnProfileListener() {
		@Override
		public void onThinking() {

		}

		@Override
		public void onException(Throwable throwable) {
		}

		@Override
		public void onFail(String reason) {
		}

		@Override
		public void onComplete(Profile response) {

			userSocialId = response.getId();
			userName = response.getFirstName()+" "+response.getLastName();
			userSocialType = "Facebook";
			mSocialType = 2;			
			userSocialImage = "https://graph.facebook.com/"+ response.getId()+ "/picture?type=large";
			if(!response.getEmail().equals("null")){
				userEmail = response.getEmail();
			}
			checkGCMForSocial();
		}

	};

	private void loginToTwitter() {
		mSharedPreferences.edit().clear().commit();
		boolean isLoggedIn = mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
		if (!isLoggedIn) {
			final ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(consumerKey);
			builder.setOAuthConsumerSecret(consumerSecret);

			final Configuration configuration = builder.build();
			final TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter.getOAuthRequestToken(callbackUrl);

				/**
				 * Loading twitter login page on webview for authorization Once
				 * authorized, results are received at onActivityResult
				 * */
				RC_SIGN_TWITTER = 1;
				final Intent intent = new Intent(this, WebViewActivity.class);
				intent.putExtra(WebViewActivity.EXTRA_URL, requestToken.getAuthenticationURL());
				startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Unable to login via twitter", Toast.LENGTH_LONG).show();
		}
	}

	private void saveTwitterInfo(AccessToken accessToken) {

		long userID = accessToken.getUserId();

		User user;
		try {
			user = twitter.showUser(userID);
			String username = user.getName();

			/* Storing oAuth tokens to shared preferences */
			Editor e = mSharedPreferences.edit();
			e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
			e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
			e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
			e.putString(PREF_USER_NAME, username);
			e.commit();

		} catch (TwitterException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);

		if (RC_SIGN_TWITTER == 1) {
			if (resultCode == Activity.RESULT_OK) {
				String verifier = data.getExtras().getString(oAuthVerifier);
				try {
					AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);

					long userID = accessToken.getUserId();
					final User user = twitter.showUser(userID);
					saveTwitterInfo(accessToken);
					userSocialId = String.valueOf(accessToken.getUserId());
					userName =  user.getName();
					userSocialType = "Twitter";
					mSocialType = 3;
					userSocialImage =  user.getBiggerProfileImageURL();

					checkGCMForSocial();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	//GCM TASK
	private class RegisterBackground extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLlSigninRoot.setVisibility(View.GONE);
			mPbSigning.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... arg0) {
			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
				}
				registrationID = gcm.register(SENDER_ID);
				msg = "Dvice registered, registration ID=" + registrationID;
				userLogin(mEdtUsername.getText().toString(),mEdtPassword.getText().toString());
			} catch (IOException ex) {
				ex.printStackTrace();
				userLogin(mEdtUsername.getText().toString(),mEdtPassword.getText().toString());
			}
			return msg;
		}

		@Override
		protected void onPostExecute(String msg) {

		}
	}

	private class RegisterSocial extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLlSigninRoot.setVisibility(View.GONE);
			mPbSigning.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... arg0) {
			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
				}
				registrationID = gcm.register(SENDER_ID);
				msg = "Dvice registered, registration ID=" + registrationID;
				userLoginSocial();

				System.out.println(msg);
			} catch (IOException ex) {
				ex.printStackTrace();
				userLoginSocial();
			}
			return msg;
		}

		@Override
		protected void onPostExecute(String msg) {
		}
	}

	private void performForgotPassword(final String emailAddress){
		mLlSigninRoot.setVisibility(View.GONE);
		mPbSigning.setVisibility(View.VISIBLE);

		mHandler.post(new Runnable(){
			@Override
			public void run(){
				new CommunicationAPIManager(SignInActivity.this).sendForgotPasswordRequest(emailAddress,
						new ResponseDelegate() {																
					@Override
					public void succeed(final JSONObject responseObj) {
						try{
							runOnUiThread(new Runnable(){
								@Override
								public void run(){
									try{
										mLlSigninRoot.setVisibility(View.VISIBLE);
										mPbSigning.setVisibility(View.GONE);
										if (responseObj != null){
											if (responseObj.has(Define.TAG_API_MESSAGE)){
												Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();				                                		
											}else{
												Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											}
										}else{
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
										}
									}catch(Exception e){
										mLlSigninRoot.setVisibility(View.VISIBLE);
										mPbSigning.setVisibility(View.GONE);
										Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
										e.printStackTrace();
									}
								}
							});
						}catch(Exception e){
							e.printStackTrace();
							mLlSigninRoot.setVisibility(View.VISIBLE);
							mPbSigning.setVisibility(View.GONE);
							Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void failed(final JSONObject errorObj) {
						try{
							runOnUiThread(new Runnable(){
								@Override
								public void run(){
									try{
										mLlSigninRoot.setVisibility(View.VISIBLE);
										mPbSigning.setVisibility(View.GONE);
										if (errorObj != null && errorObj.has(Define.TAG_API_MESSAGE)){
											Toast.makeText(SignInActivity.this, errorObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
										}else{
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
										}
									}catch (Exception e){
										mLlSigninRoot.setVisibility(View.VISIBLE);
										mPbSigning.setVisibility(View.GONE);
										Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
										e.printStackTrace();
									}
								}
							});
						}catch(Exception e){
							e.printStackTrace();
							mLlSigninRoot.setVisibility(View.VISIBLE);
							mPbSigning.setVisibility(View.GONE);
							Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
	}

	public void userLogin(final String email, final String password) {
		mLlSigninRoot.setVisibility(View.GONE);
		mPbSigning.setVisibility(View.VISIBLE);

		mHandler.post(new Runnable(){
			@Override
			public void run(){
				new CommunicationAPIManager(SignInActivity.this).sendSigninRequest(email,
						password, 
						registrationID == null && registrationID.isEmpty() ? "" : registrationID, 
								new ResponseDelegate() {																
					@Override
					public void succeed(final JSONObject responseObj) {
						try{
							runOnUiThread(new Runnable(){
								@Override
								public void run(){
									try{
										if (responseObj != null){
											if (responseObj.optBoolean(Define.TAG_API_RESULT, false) == true){
												JSONObject values = responseObj.optJSONObject(Define.TAG_API_VALUES);
												if (values != null){
													JSONObject userObj = values.optJSONObject(Define.TAG_USER_OBJECT);
													if (userObj != null){
														Globals.gCurrentUser = new com.bidhee.model.User(userObj);
														checkIfVerified();
													}else{
														mLlSigninRoot.setVisibility(View.VISIBLE);
														mPbSigning.setVisibility(View.GONE);
														Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
													}
												}else{
													mLlSigninRoot.setVisibility(View.VISIBLE);
													mPbSigning.setVisibility(View.GONE);
													Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
												}
											}else{
												mLlSigninRoot.setVisibility(View.VISIBLE);
												mPbSigning.setVisibility(View.GONE);
												Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
											}
										}else{
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
										}
									}catch(Exception e){
										mLlSigninRoot.setVisibility(View.VISIBLE);
										mPbSigning.setVisibility(View.GONE);
										Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
										e.printStackTrace();
									}
								}
							});
						}catch(Exception e){
							e.printStackTrace();
							mLlSigninRoot.setVisibility(View.VISIBLE);
							mPbSigning.setVisibility(View.GONE);
							Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void failed(final JSONObject errorObj) {
						try{
							runOnUiThread(new Runnable(){
								@Override
								public void run(){
									try{
										mLlSigninRoot.setVisibility(View.VISIBLE);
										mPbSigning.setVisibility(View.GONE);
										if (errorObj != null && errorObj.has(Define.TAG_API_MESSAGE)){
											Toast.makeText(SignInActivity.this, errorObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
										}else{
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
										}
									}catch (Exception e){
										mLlSigninRoot.setVisibility(View.VISIBLE);
										mPbSigning.setVisibility(View.GONE);
										Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
										e.printStackTrace();
									}
								}
							});
						}catch(Exception e){
							e.printStackTrace();
							mLlSigninRoot.setVisibility(View.VISIBLE);
							mPbSigning.setVisibility(View.GONE);
							Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});		
	}

	@SuppressLint("InflateParams")
	private void checkIfVerified(){
		try{
			if (Globals.gCurrentUser != null){
				FacebookApplication.config.saveLogInUserInfo(Globals.gCurrentUser);
				FacebookApplication.config.saveIfLoggedIn(true);
				if (Globals.gCurrentUser.user_status == Define.USER_STATUS_PENDING){
					mLlSigninRoot.setVisibility(View.VISIBLE);
					mPbSigning.setVisibility(View.GONE);

					AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
					builder.setTitle("Please enter verification code!");
					View viewInflated = LayoutInflater.from(SignInActivity.this).inflate(R.layout.alert_dlg_input_verification_code, null);
					final EditText input = (EditText) viewInflated.findViewById(R.id.edt_alert_dlg_enter_verification_code);
					input.setInputType(InputType.TYPE_CLASS_NUMBER);

					viewInflated.findViewById(R.id.btn_alert_dlg_enter_vc_resend).setOnClickListener(new View.OnClickListener() {						
						@Override
						public void onClick(View v) {
							performResendVerificationCode();
						}
					});

					builder.setView(viewInflated);

					builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String strVerificationCode = input.getText().toString();
							if (strVerificationCode == null || strVerificationCode.isEmpty()){
								Toast.makeText(SignInActivity.this, R.string.msg_err_enter_verification_code, Toast.LENGTH_SHORT).show();
								return;
							}
							dialog.dismiss();
							performVerifyUser(strVerificationCode);
						}
					});
					builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					builder.show();
				}else if(Globals.gCurrentUser.user_status == Define.USER_STATUS_VERIFIED){
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
					finish();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void performVerifyUser(final String strVerificationCode){
		try{

			if (Globals.gCurrentUser == null || Globals.gCurrentUser.user_id == -1) return;
			if (strVerificationCode == null || strVerificationCode.isEmpty()){
				Toast.makeText(getApplicationContext(), "Please enter the verification code!", Toast.LENGTH_SHORT).show();
				return;
			}

			mPbSigning.setVisibility(View.VISIBLE);
			mLlSigninRoot.setVisibility(View.GONE);

			mHandler.post(new Runnable(){
				@Override
				public void run(){
					new CommunicationAPIManager(SignInActivity.this).sendVerifyUserRequest(Globals.gCurrentUser.user_id,
							strVerificationCode, 
							new ResponseDelegate() {																
						@Override
						public void succeed(final JSONObject responseObj) {
							try{
								runOnUiThread(new Runnable(){
									@Override
									public void run(){
										try{
											if (responseObj != null){
												if (responseObj.optBoolean(Define.TAG_API_RESULT, false) == true){
													JSONObject values = responseObj.optJSONObject(Define.TAG_API_VALUES);
													if (values != null){
														JSONObject userObj = values.optJSONObject(Define.TAG_USER_OBJECT);
														if (userObj != null){
															Globals.gCurrentUser = new com.bidhee.model.User(userObj);
															FacebookApplication.config.saveLogInUserInfo(Globals.gCurrentUser);
															FacebookApplication.config.saveIfLoggedIn(true);
															if (Globals.gCurrentUser != null && Globals.gCurrentUser.user_status == Define.USER_STATUS_VERIFIED){
																Intent intent = new Intent(SignInActivity.this, MainActivity.class);
																startActivity(intent);
																finish();
															}else{
																mLlSigninRoot.setVisibility(View.VISIBLE);
																mPbSigning.setVisibility(View.GONE);
																Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();					                                                	
															}
														}else{
															mLlSigninRoot.setVisibility(View.VISIBLE);
															mPbSigning.setVisibility(View.GONE);
															Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
														}
													}else{
														mLlSigninRoot.setVisibility(View.VISIBLE);
														mPbSigning.setVisibility(View.GONE);
														Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
													}
												}else{
													mLlSigninRoot.setVisibility(View.VISIBLE);
													mPbSigning.setVisibility(View.GONE);
													Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
												}
											}else{
												mLlSigninRoot.setVisibility(View.VISIBLE);
												mPbSigning.setVisibility(View.GONE);
												Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											}
										}catch(Exception e){
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											e.printStackTrace();
										}
									}
								});
							}catch(Exception e){
								e.printStackTrace();
								mLlSigninRoot.setVisibility(View.VISIBLE);
								mPbSigning.setVisibility(View.GONE);
								Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void failed(final JSONObject errorObj) {
							try{
								runOnUiThread(new Runnable(){
									@Override
									public void run(){
										try{
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											if (errorObj != null && errorObj.has(Define.TAG_API_MESSAGE)){
												Toast.makeText(SignInActivity.this, errorObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
											}else{
												Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											}
										}catch (Exception e){
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											e.printStackTrace();
										}
									}
								});
							}catch(Exception e){
								e.printStackTrace();
								mLlSigninRoot.setVisibility(View.VISIBLE);
								mPbSigning.setVisibility(View.GONE);
								Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void performResendVerificationCode(){
		try{
			if (Globals.gCurrentUser == null) return;

			mLlSigninRoot.setVisibility(View.GONE);
			mPbSigning.setVisibility(View.VISIBLE);

			mHandler.post(new Runnable(){
				@Override
				public void run(){
					new CommunicationAPIManager(SignInActivity.this).sendResendVerificationCodeRequest(Globals.gCurrentUser.user_id,
							new ResponseDelegate() {																
						@Override
						public void succeed(final JSONObject responseObj) {
							try{
								runOnUiThread(new Runnable(){
									@Override
									public void run(){
										try{
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											if (responseObj != null){
												if (responseObj.has(Define.TAG_API_MESSAGE)){
													Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();					                                		
												}
											}else{
												mLlSigninRoot.setVisibility(View.VISIBLE);
												mPbSigning.setVisibility(View.GONE);
												Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											}
										}catch(Exception e){
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											e.printStackTrace();
										}
									}
								});
							}catch(Exception e){
								e.printStackTrace();
								mLlSigninRoot.setVisibility(View.VISIBLE);
								mPbSigning.setVisibility(View.GONE);
								Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void failed(final JSONObject errorObj) {
							try{
								runOnUiThread(new Runnable(){
									@Override
									public void run(){
										try{
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											if (errorObj != null && errorObj.has(Define.TAG_API_MESSAGE)){
												Toast.makeText(SignInActivity.this, errorObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
											}else{
												Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											}
										}catch (Exception e){
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											e.printStackTrace();
										}catch(Error err){
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											err.printStackTrace();
										}catch (Throwable th){
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											th.printStackTrace();
										}
									}
								});
							}catch(Exception e){
								e.printStackTrace();
								mLlSigninRoot.setVisibility(View.VISIBLE);
								mPbSigning.setVisibility(View.GONE);
								Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
							}catch (Error err){
								err.printStackTrace();
								mLlSigninRoot.setVisibility(View.VISIBLE);
								mPbSigning.setVisibility(View.GONE);
								Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
							}catch(Throwable th){
								th.printStackTrace();
								mLlSigninRoot.setVisibility(View.VISIBLE);
								mPbSigning.setVisibility(View.GONE);
								Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void userLoginSocial() {
		try{
			mLlSigninRoot.setVisibility(View.GONE);
			mPbSigning.setVisibility(View.VISIBLE);

			mHandler.post(new Runnable(){
				@Override
				public void run(){
					new CommunicationAPIManager(SignInActivity.this).sendSigninSocialRequest(userEmail, 
							userSocialId, 
							mSocialType, 
							userName, 
							registrationID, 
							userSocialImage,
							userGender,
							new ResponseDelegate() {																
						@Override
						public void succeed(final JSONObject responseObj) {
							try{
								runOnUiThread(new Runnable(){
									@Override
									public void run(){
										try{
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											if (responseObj != null){
												if (responseObj.optBoolean(Define.TAG_API_RESULT, false) == true){
													JSONObject values = responseObj.optJSONObject(Define.TAG_API_VALUES);
													if (values != null){
														JSONObject userObj = values.optJSONObject(Define.TAG_USER_OBJECT);
														if (userObj != null){
															Globals.gCurrentUser = new com.bidhee.model.User(userObj);
															FacebookApplication.config.saveLogInUserInfo(Globals.gCurrentUser);
															FacebookApplication.config.saveIfLoggedIn(true);
															Intent intent = new Intent(SignInActivity.this, MainActivity.class);
															startActivity(intent);
															finish();
														}else{
															mLlSigninRoot.setVisibility(View.VISIBLE);
															mPbSigning.setVisibility(View.GONE);
															Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
														}
													}else{
														mLlSigninRoot.setVisibility(View.VISIBLE);
														mPbSigning.setVisibility(View.GONE);
														Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
													}
												}else{
													mLlSigninRoot.setVisibility(View.VISIBLE);
													mPbSigning.setVisibility(View.GONE);
													Toast.makeText(SignInActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
												}
											}else{
												mLlSigninRoot.setVisibility(View.VISIBLE);
												mPbSigning.setVisibility(View.GONE);
												Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											}
										}catch(Exception e){
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											e.printStackTrace();
										}
									}
								});
							}catch(Exception e){
								e.printStackTrace();
								mLlSigninRoot.setVisibility(View.VISIBLE);
								mPbSigning.setVisibility(View.GONE);
								Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void failed(final JSONObject errorObj) {
							try{
								runOnUiThread(new Runnable(){
									@Override
									public void run(){
										try{
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											if (errorObj != null && errorObj.has(Define.TAG_API_MESSAGE)){
												Toast.makeText(SignInActivity.this, errorObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
											}else{
												Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											}
										}catch (Exception e){
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											e.printStackTrace();
										}catch(Error err){
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											err.printStackTrace();
										}catch (Throwable th){
											mLlSigninRoot.setVisibility(View.VISIBLE);
											mPbSigning.setVisibility(View.GONE);
											Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
											th.printStackTrace();
										}
									}
								});
							}catch(Exception e){
								e.printStackTrace();
								mLlSigninRoot.setVisibility(View.VISIBLE);
								mPbSigning.setVisibility(View.GONE);
								Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
							}catch (Error err){
								err.printStackTrace();
								mLlSigninRoot.setVisibility(View.VISIBLE);
								mPbSigning.setVisibility(View.GONE);
								Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
							}catch(Throwable th){
								th.printStackTrace();
								mLlSigninRoot.setVisibility(View.VISIBLE);
								mPbSigning.setVisibility(View.GONE);
								Toast.makeText(SignInActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
//
//		RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
//		final ArrayList<UserModel> userArray = new ArrayList<UserModel>();
//		StringRequest postReq = new StringRequest(Request.Method.POST, APIUrls.URL_SOCIAL_LOGIN, new Response.Listener<String>() {
//			@Override
//			public void onResponse(String response) {
//				System.out.println("Social Response::" +response);
//				try {
//					JSONObject jsonObj = new JSONObject(response);
//					error = jsonObj.getBoolean("error");
//					message = jsonObj.getString("message");
//					if (!error) {
//						JSONArray jsonDataArray = jsonObj.getJSONArray("userData");
//						for (int i = 0; i < jsonDataArray.length(); i++) {
//							JSONObject c = jsonDataArray.getJSONObject(i);
//
//							long userId = c.getLong("userId");
//							String userEmail = c.getString("email");
//							String secretKey = c.getString("secretKey");
//							String userGender = c.getString("gender");
//							String fullName = c.getString("fullName");
//							String dateOfBirth = c.getString("dateOfBirth");
//							userArray.add(new UserModel(userId, fullName, userSocialImage, userEmail, userGender, secretKey, "0", "Normal",dateOfBirth));
//
//							DTOSession session = DTOSession.getInstance();
//							DAOSessionTable daoSession = new DAOSessionTable(getApplicationContext());
//							session.setSessionId(1);
//							session.setSessionUserId(userId);
//
//							session.setSessionActive(true);
//							daoSession.deletAllData();
//							daoSession.createSession(session);
//
//							UserTableDAO usertableDao = new UserTableDAO(getApplicationContext());
//							usertableDao.deletAllData();
//							usertableDao.createUserArray(userArray);
//							Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//
//							Intent logedInIntent = new Intent(getApplicationContext(), MainActivity.class);
//							startActivity(logedInIntent);
//							finish();
//						}
//					} else {
//						Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//						mLlSigninRoot.setVisibility(View.VISIBLE);
//						mPbSigning.setVisibility(View.GONE);
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//					mLlSigninRoot.setVisibility(View.VISIBLE);
//					mPbSigning.setVisibility(View.GONE);
//				}
//			}
//		}, new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				System.out.println("Error [" + error + "]");
//				mLlSigninRoot.setVisibility(View.GONE);
//				mPbSigning.setVisibility(View.VISIBLE);
//			}
//		}) {
//			@Override
//			protected Map<String, String> getParams() {
//				Map<String, String> params = new HashMap<String, String>();
//
//				params.put("email", userEmail);
//				params.put("type", "gcm");
//				params.put("device", "Android");
//				params.put("social_id", userSocialId);
//				params.put("social_media_type", userSocialType);
//				params.put("full_name", userName);
//
//				if(registrationID!=null&&!registrationID.isEmpty()){
//					params.put("push_reg_id",registrationID );
//				}
//				return params;
//			}
//		};
//		rq.add(postReq);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
	}
}