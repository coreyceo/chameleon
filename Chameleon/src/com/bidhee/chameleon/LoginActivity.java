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

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bidhee.dao.UserTableDAO;
import com.bidhee.fragment.ForgetPasswordFragment;
import com.bidhee.metadata.APIUrls;
import com.bidhee.metadata.DAOSessionTable;
import com.bidhee.model.DTOSession;
import com.bidhee.model.UserModel;
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

public class LoginActivity extends ActionBarActivity {

	private ButtonFlat tvForgetPwd;
	private ButtonFlat tvRegister;

	private Button btnLogin;

	private LinearLayout btnGooglePlus;
	private LinearLayout btnFacebook;
	private LinearLayout btnTwitter;

	private Toolbar toolbarLogin;
	private EditText editUserName;
	private EditText editPassword;

	private LinearLayout llLoginForm;
	private ProgressBarCircularIndeterminate pbLogginin;
	private PConnectionDetectorUtils cd;
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
	private String userSocialImage ="";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);


		cd = new PConnectionDetectorUtils(getApplicationContext());
		isInternetConnected = cd.isConnectingToInternet();
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
				userSocialType = "Google";
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
		tvForgetPwd = (ButtonFlat) findViewById(R.id.tvForgetPwd);
		tvRegister = (ButtonFlat) findViewById(R.id.tvRegister);

		btnLogin = (Button) findViewById(R.id.btnLogin);

		btnGooglePlus = (LinearLayout) findViewById(R.id.btnGooglePlus);
		btnFacebook = (LinearLayout) findViewById(R.id.btnFacebook);
		btnTwitter = (LinearLayout) findViewById(R.id.btnTwitter);

		editUserName = (EditText) findViewById(R.id.editUserName);
		editPassword = (EditText) findViewById(R.id.editPassword);
		pbLogginin = (ProgressBarCircularIndeterminate) findViewById(R.id.pbLogginin);

		llLoginForm =(LinearLayout) findViewById(R.id.llLoginForm);

		editUserName.setHintTextColor(getResources().getColor(R.color.hint_color));
		editPassword.setHintTextColor(getResources().getColor(R.color.hint_color));

		toolbarLogin = (Toolbar) findViewById(R.id.toolbarLogin);
		toolbarLogin.setTitle("Login to Chameleon");
		toolbarLogin.setBackgroundColor(getResources().getColor(R.color.primaryColor));
	}


	private void clickListner() {
		btnGooglePlus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				syncGoogleAccount();
			}
		});

		tvForgetPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment forgetPasswordFrgament = new ForgetPasswordFragment();
				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(android.R.id.content, forgetPasswordFrgament);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});

		tvRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
			}
		});

		btnFacebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isInternetConnected) {
					Toast.makeText(getApplicationContext(),"No internet Connection, cant sign up",Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(),"Please wait loging in", Toast.LENGTH_SHORT).show();
					mSimpleFacebook.login(onLoginListener);
				}

			}
		});

		btnTwitter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginToTwitter();				
			}
		});


		btnLogin .setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!editUserName.getText().toString().isEmpty()){
					if(editUserName.getText().toString().contains("@")){
						if(!editPassword.getText().toString().isEmpty()){							
							checkGCM();							
						}
						else{
							Toast.makeText(getApplicationContext(), "Enter password to continue", Toast.LENGTH_SHORT).show();
						}
					}
					else{
						Toast.makeText(getApplicationContext(), "Error email pattern", Toast.LENGTH_SHORT).show();
					}
				}
				else{
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
			}
			catch(ExceptionInInitializerError e){
				e.printStackTrace();
				userLogin(editUserName.getText().toString(),editPassword.getText().toString());				
			}
		}
	}

	private void checkGCMForSocial() {
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			try{
				new RegisterSocial().execute();
			}
			catch(ExceptionInInitializerError e){
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
				} catch (Exception e) {
					e.printStackTrace();

				}
			} else {
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

				getTask(LoginActivity.this, accountarrs[0], SCOPE).execute();

			} else {
				Toast.makeText(LoginActivity.this, "No Google Account Sync!",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(LoginActivity.this, "No Network Service!",Toast.LENGTH_SHORT).show();
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

	private AbstractGetNameTask getTask(LoginActivity activity, String email, String scope) {
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
			System.out.println("LOGED IN");
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
				intent.putExtra(WebViewActivity.EXTRA_URL,
						requestToken.getAuthenticationURL());
				startActivityForResult(intent, WEBVIEW_REQUEST_CODE);

			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Unable to login via twitter", Toast.LENGTH_LONG).show();
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
			llLoginForm.setVisibility(View.GONE);
			pbLogginin.setVisibility(View.VISIBLE);
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
				userLogin(editUserName.getText().toString(),editPassword.getText().toString());
			} catch (IOException ex) {
				ex.printStackTrace();
				userLogin(editUserName.getText().toString(),editPassword.getText().toString());
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
			llLoginForm.setVisibility(View.GONE);
			pbLogginin.setVisibility(View.VISIBLE);
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

	public void userLogin(final String email, final String password) {
		llLoginForm.setVisibility(View.GONE);
		pbLogginin.setVisibility(View.VISIBLE);

		RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
		final ArrayList<UserModel> userArray = new ArrayList<UserModel>();
		StringRequest postReq = new StringRequest(Request.Method.POST, APIUrls.URL_USER_LOGIN, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				System.out.println("LOGIN RESPONSE::" + response);
				try {
					JSONObject jsonObj = new JSONObject(response);
					error = jsonObj.getBoolean("error");
					message = jsonObj.getString("message");
					if (!error) {
						JSONArray jsonDataArray = jsonObj.getJSONArray("userData");
						for (int i = 0; i < jsonDataArray.length(); i++) {
							JSONObject c = jsonDataArray.getJSONObject(i);

							long userId = c.getLong("userId");
							String userEmail = c.getString("email");
							String secretKey = c.getString("secretKey");
							String userGender = c.getString("gender");
							String fullName = c.getString("fullName");
							String profileImage = c.getString("profileImage");
							String dateOfBirth = c.getString("dateOfBirth");

							userArray.add(new UserModel(userId, fullName, APIUrls.URL_IMAGE+profileImage+"&w=200&h=200", userEmail, userGender, secretKey, "0", "Normal",dateOfBirth));

							DTOSession session = DTOSession.getInstance();
							DAOSessionTable daoSession = new DAOSessionTable(getApplicationContext());
							session.setSessionId(1);
							session.setSessionUserId(userId);

							session.setSessionActive(true);
							daoSession.deletAllData();
							daoSession.createSession(session);

							UserTableDAO usertableDao = new UserTableDAO(getApplicationContext());
							usertableDao.deletAllData();
							usertableDao.createUserArray(userArray);
							Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

							Intent logedInIntent = new Intent(getApplicationContext(), MainActivity.class);
							startActivity(logedInIntent);
							finish();

						}

					} else {
						Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
						llLoginForm.setVisibility(View.VISIBLE);
						pbLogginin.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					llLoginForm.setVisibility(View.VISIBLE);
					pbLogginin.setVisibility(View.GONE);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				System.out.println("Error [" + error + "]");
				llLoginForm.setVisibility(View.GONE);
				pbLogginin.setVisibility(View.VISIBLE);
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("email", email);
				params.put("pass", password);
				params.put("type", "gcm");

				if(registrationID!=null&&!registrationID.isEmpty()){
					params.put("push_reg_id",registrationID );

				}
				return params;
			}
		};

		rq.add(postReq);
	}


	public void userLoginSocial() {
		llLoginForm.setVisibility(View.GONE);
		pbLogginin.setVisibility(View.VISIBLE);

		RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
		final ArrayList<UserModel> userArray = new ArrayList<UserModel>();
		StringRequest postReq = new StringRequest(Request.Method.POST, APIUrls.URL_SOCIAL_LOGIN, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				System.out.println("Social Response::" +response);
				try {
					JSONObject jsonObj = new JSONObject(response);
					error = jsonObj.getBoolean("error");
					message = jsonObj.getString("message");
					if (!error) {
						JSONArray jsonDataArray = jsonObj.getJSONArray("userData");
						for (int i = 0; i < jsonDataArray.length(); i++) {
							JSONObject c = jsonDataArray.getJSONObject(i);

							long userId = c.getLong("userId");
							String userEmail = c.getString("email");
							String secretKey = c.getString("secretKey");
							String userGender = c.getString("gender");
							String fullName = c.getString("fullName");
							String dateOfBirth = c.getString("dateOfBirth");
							userArray.add(new UserModel(userId, fullName, userSocialImage, userEmail, userGender, secretKey, "0", "Normal",dateOfBirth));

							DTOSession session = DTOSession.getInstance();
							DAOSessionTable daoSession = new DAOSessionTable(getApplicationContext());
							session.setSessionId(1);
							session.setSessionUserId(userId);

							session.setSessionActive(true);
							daoSession.deletAllData();
							daoSession.createSession(session);

							UserTableDAO usertableDao = new UserTableDAO(getApplicationContext());
							usertableDao.deletAllData();
							usertableDao.createUserArray(userArray);
							Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

							Intent logedInIntent = new Intent(getApplicationContext(), MainActivity.class);
							startActivity(logedInIntent);
							finish();

						}

					} else {
						Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
						llLoginForm.setVisibility(View.VISIBLE);
						pbLogginin.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					llLoginForm.setVisibility(View.VISIBLE);
					pbLogginin.setVisibility(View.GONE);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				System.out.println("Error [" + error + "]");
				llLoginForm.setVisibility(View.GONE);
				pbLogginin.setVisibility(View.VISIBLE);
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();

				params.put("email", userEmail);
				params.put("type", "gcm");
				params.put("device", "Android");
				params.put("social_id", userSocialId);
				params.put("social_media_type", userSocialType);
				params.put("full_name", userName);

				if(registrationID!=null&&!registrationID.isEmpty()){
					params.put("push_reg_id",registrationID );
				}
				return params;
			}
		};
		rq.add(postReq);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSimpleFacebook = SimpleFacebook.getInstance(this);
	}
}