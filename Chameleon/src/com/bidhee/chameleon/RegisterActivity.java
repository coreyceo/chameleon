package com.bidhee.chameleon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
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
import com.bidhee.metadata.APIUrls;
import com.bidhee.metadata.DAOSessionTable;
import com.bidhee.model.DTOSession;
import com.bidhee.model.UserModel;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegisterActivity extends ActionBarActivity {
	private EditText etRegisterEmail,etRegisterPassword,etRegisterRePassword;
	private ProgressBarCircularIndeterminate pbRegister;
	private Button btnRegister;
	private Toolbar toolbarRegister;
	private LinearLayout llRegisterForm;

	//GCM 

	private String registrationID = "";
	GoogleCloudMessaging gcm;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String SENDER_ID = "1030302571509";

	private boolean error;
	private String message;
	private String fullName="";
	private String dateOfBirth=""; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		findView();
		setListner();
	}

	private void findView() {
		pbRegister = (ProgressBarCircularIndeterminate) findViewById(R.id.pbRegister);
		llRegisterForm = (LinearLayout) findViewById(R.id.llRegisterForm);
		etRegisterEmail = (EditText) findViewById(R.id.etRegisterEmail);
		etRegisterPassword = (EditText) findViewById(R.id.etRegisterPassword);
		etRegisterRePassword = (EditText) findViewById(R.id.etRegisterRePassword);
		btnRegister = (Button) findViewById(R.id.btnRegister);
		etRegisterEmail.setHintTextColor(getResources().getColor(R.color.hint_color));
		etRegisterPassword.setHintTextColor(getResources().getColor(R.color.hint_color));
		etRegisterRePassword.setHintTextColor(getResources().getColor(R.color.hint_color));
		toolbarRegister = (Toolbar) findViewById(R.id.toolbarRegister);
		toolbarRegister.setTitle("Register");
		toolbarRegister.setBackgroundColor(getResources().getColor(R.color.primaryColor));

		toolbarRegister.setNavigationIcon(R.drawable.ic_arrow_back_white_36dp);

		toolbarRegister.setNavigationOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}


	private void setListner() {
		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!etRegisterEmail.getText().toString().isEmpty()){
					if(etRegisterEmail.getText().toString().contains("@")){
						if(!etRegisterPassword.getText().toString().isEmpty()){
							if(!etRegisterRePassword.getText().toString().isEmpty()){
								if(etRegisterPassword.getText().toString().equals(etRegisterRePassword.getText().toString())){
									checkGCM();
								}
								else{
									Toast.makeText(getApplicationContext(), "Password din't match", Toast.LENGTH_SHORT).show();
								}
							}
							else{
								Toast.makeText(getApplicationContext(), "Re-enter password to continue", Toast.LENGTH_SHORT).show();
							}
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
				userLogin(etRegisterEmail.getText().toString(),etRegisterPassword.getText().toString());
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


	//GCM TASK

	private class RegisterBackground extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			llRegisterForm.setVisibility(View.GONE);
			pbRegister.setVisibility(View.VISIBLE);
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
				userLogin(etRegisterEmail.getText().toString(),etRegisterPassword.getText().toString());
				System.out.println(msg);
			} catch (IOException ex) {
				ex.printStackTrace();
				userLogin(etRegisterEmail.getText().toString(),etRegisterPassword.getText().toString());
			}
			return msg;
		}

		@Override
		protected void onPostExecute(String msg) {

		}
	}

	public void userLogin(final String email, final String password) {
		llRegisterForm.setVisibility(View.GONE);
		pbRegister.setVisibility(View.VISIBLE);

		RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
		final ArrayList<UserModel> userArray = new ArrayList<UserModel>();
		StringRequest postReq = new StringRequest(Request.Method.POST, APIUrls.URL_SIGNUP, new Response.Listener<String>() {
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
							if(c.has("dateOfBirth")){
								dateOfBirth = c.getString("dateOfBirth");
							}

							if(c.has("fullName")){
								fullName = c.getString("fullName");
							}

							String profileImage = c.getString("profileImage");
							userArray.add(new UserModel(userId, fullName, profileImage, userEmail, userGender, secretKey, "0", "Normal",dateOfBirth));

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
						llRegisterForm.setVisibility(View.VISIBLE);
						pbRegister.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					llRegisterForm.setVisibility(View.VISIBLE);
					pbRegister.setVisibility(View.GONE);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				System.out.println("Error [" + error + "]");
				llRegisterForm.setVisibility(View.GONE);
				pbRegister.setVisibility(View.VISIBLE);
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("email", email);
				params.put("pass", password);
				params.put("type", "gcm");
				params.put("device", "Android");
				if(registrationID!=null&&!registrationID.isEmpty()){
					params.put("push_reg_id",registrationID );
				}
				return params;
			}
		};
		rq.add(postReq);
	}
}
