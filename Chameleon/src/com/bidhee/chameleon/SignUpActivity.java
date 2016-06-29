package com.bidhee.chameleon;

import java.io.IOException;

import org.json.JSONObject;

import com.bidhee.chameleon.communication.CommunicationAPIManager;
import com.bidhee.chameleon.global.Define;
import com.bidhee.chameleon.global.Globals;
import com.bidhee.model.User;
import com.bidhee.model.comm.ResponseDelegate;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SignUpActivity extends ActionBarActivity {
	private EditText mTxtEmail;
	private EditText mTxtPassword;
	private EditText mTxtConfirmPassword;
	private ProgressBarCircularIndeterminate mPbSignup;
	private Button mBtnSignup;
	private Toolbar mTbSignup;
	private LinearLayout mLlSingupRoot;

	//GCM
	private String registrationID = "";
	GoogleCloudMessaging gcm;
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	String SENDER_ID = "1030302571509";

	@SuppressWarnings("unused")
	private boolean error;
	@SuppressWarnings("unused")
	private String message;
	@SuppressWarnings("unused")
	private String fullName="";
	@SuppressWarnings("unused")
	private String dateOfBirth="";
	private Handler mHandler = new Handler();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		findView();
		setListner();
	}

	private void findView() {
		mPbSignup 				= (ProgressBarCircularIndeterminate) findViewById(R.id.pb_signup);
		mLlSingupRoot 			= (LinearLayout) findViewById(R.id.ll_signup_root);
		mTxtEmail 				= (EditText) findViewById(R.id.edt_signup_email_address);
		mTxtPassword 			= (EditText) findViewById(R.id.edt_signup_password);
		mTxtConfirmPassword 	= (EditText) findViewById(R.id.edt_signup_confirm_password);
		mBtnSignup 				= (Button) 	findViewById(R.id.btn_signup);
		mTbSignup 				= (Toolbar) findViewById(R.id.toolbar_signup);
		
		mTbSignup.setTitle("Register");
		mTbSignup.setNavigationIcon(R.drawable.ic_arrow_back_white_36dp);
		mTbSignup.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}


	private void setListner() {
		mBtnSignup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!mTxtEmail.getText().toString().isEmpty()){
					if(mTxtEmail.getText().toString().contains("@")){
						if(!mTxtPassword.getText().toString().isEmpty()){
							if(!mTxtConfirmPassword.getText().toString().isEmpty()){
								if(mTxtPassword.getText().toString().equals(mTxtConfirmPassword.getText().toString())){
									getDeviceToken();
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

	private void getDeviceToken() {
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			try{
				new RegisterBackground().execute();
			}catch(ExceptionInInitializerError e){
				e.printStackTrace();
				signupUser(mTxtEmail.getText().toString(), mTxtPassword.getText().toString());
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
			mLlSingupRoot.setVisibility(View.GONE);
			mPbSignup.setVisibility(View.VISIBLE);
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
				signupUser(mTxtEmail.getText().toString(), mTxtPassword.getText().toString());
				System.out.println(msg);
			} catch (IOException ex) {
				ex.printStackTrace();
				signupUser(mTxtEmail.getText().toString(),mTxtPassword.getText().toString());
			}
			return msg;
		}

		@Override
		protected void onPostExecute(String msg) {

		}
	}

	public void signupUser(final String email, final String password) {
		mLlSingupRoot.setVisibility(View.GONE);
		mPbSignup.setVisibility(View.VISIBLE);
		
		mHandler.post(new Runnable(){
			@Override
			public void run(){
				new CommunicationAPIManager(SignUpActivity.this).sendSignupRequest(email,
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
				                                                Globals.gCurrentUser = new User(userObj);
				                                                checkIfVerified();
				                                            }else{
				                        						mLlSingupRoot.setVisibility(View.VISIBLE);
				                        						mPbSignup.setVisibility(View.GONE);
				                                                Toast.makeText(SignUpActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
				                                            }
				                                        }else{
				                    						mLlSingupRoot.setVisibility(View.VISIBLE);
				                    						mPbSignup.setVisibility(View.GONE);
				                                            Toast.makeText(SignUpActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
				                                        }
				                                    }else{
				                						mLlSingupRoot.setVisibility(View.VISIBLE);
				                						mPbSignup.setVisibility(View.GONE);
				                                        Toast.makeText(SignUpActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
				                                    }
				                                }else{
				            						mLlSingupRoot.setVisibility(View.VISIBLE);
				            						mPbSignup.setVisibility(View.GONE);
				                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                                }
				                            }catch(Exception e){
				        						mLlSingupRoot.setVisibility(View.VISIBLE);
				        						mPbSignup.setVisibility(View.GONE);
				                                Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                                e.printStackTrace();
				                            }
										}
									});
								}catch(Exception e){
									e.printStackTrace();
									mLlSingupRoot.setVisibility(View.VISIBLE);
									mPbSignup.setVisibility(View.GONE);
			                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
								}
							}
							
							@Override
							public void failed(final JSONObject errorObj) {
			                    try{
			                        runOnUiThread(new Runnable(){
			                            @Override
			                            public void run(){
			                                try{
			            						mLlSingupRoot.setVisibility(View.VISIBLE);
			            						mPbSignup.setVisibility(View.GONE);
			                                    if (errorObj != null && errorObj.has(Define.TAG_API_MESSAGE)){
			                                        Toast.makeText(SignUpActivity.this, errorObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
			                                    }else{
			                                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
			                                    }
			                                }catch (Exception e){
			            						mLlSingupRoot.setVisibility(View.VISIBLE);
			            						mPbSignup.setVisibility(View.GONE);
			                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
			                                    e.printStackTrace();
			                                }catch(Error err){
			            						mLlSingupRoot.setVisibility(View.VISIBLE);
			            						mPbSignup.setVisibility(View.GONE);
			                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
			                                    err.printStackTrace();
			                                }catch (Throwable th){
			            						mLlSingupRoot.setVisibility(View.VISIBLE);
			            						mPbSignup.setVisibility(View.GONE);
			                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
			                                    th.printStackTrace();
			                                }
			                            }
			                        });
			                    }catch(Exception e){
			                        e.printStackTrace();
									mLlSingupRoot.setVisibility(View.VISIBLE);
									mPbSignup.setVisibility(View.GONE);
			                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
			                    }catch (Error err){
			                        err.printStackTrace();
									mLlSingupRoot.setVisibility(View.VISIBLE);
									mPbSignup.setVisibility(View.GONE);
			                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
			                    }catch(Throwable th){
			                        th.printStackTrace();
									mLlSingupRoot.setVisibility(View.VISIBLE);
									mPbSignup.setVisibility(View.GONE);
			                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
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
					mLlSingupRoot.setVisibility(View.VISIBLE);
					mPbSignup.setVisibility(View.GONE);
					
					AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
	                builder.setTitle("Please enter verification code!");
	                View viewInflated = LayoutInflater.from(SignUpActivity.this).inflate(R.layout.alert_dlg_input_verification_code, null);
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
	                            Toast.makeText(SignUpActivity.this, R.string.msg_err_enter_verification_code, Toast.LENGTH_SHORT).show();
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
			
			mPbSignup.setVisibility(View.VISIBLE);
			mLlSingupRoot.setVisibility(View.GONE);
			
			mHandler.post(new Runnable(){
				@Override
				public void run(){
					new CommunicationAPIManager(SignUpActivity.this).sendVerifyUserRequest(Globals.gCurrentUser.user_id,
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
					                                                Globals.gCurrentUser = new User(userObj);
					                            					FacebookApplication.config.saveLogInUserInfo(Globals.gCurrentUser);
					                            					FacebookApplication.config.saveIfLoggedIn(true);
					                                                if (Globals.gCurrentUser != null && Globals.gCurrentUser.user_status == Define.USER_STATUS_VERIFIED){
					                                                	Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
					                                                	startActivity(intent);
					                                                	finish();
					                                                }else{
						                        						mLlSingupRoot.setVisibility(View.VISIBLE);
						                        						mPbSignup.setVisibility(View.GONE);
						                                                Toast.makeText(SignUpActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();					                                                	
					                                                }
					                                            }else{
					                        						mLlSingupRoot.setVisibility(View.VISIBLE);
					                        						mPbSignup.setVisibility(View.GONE);
					                                                Toast.makeText(SignUpActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
					                                            }
					                                        }else{
					                    						mLlSingupRoot.setVisibility(View.VISIBLE);
					                    						mPbSignup.setVisibility(View.GONE);
					                                            Toast.makeText(SignUpActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
					                                        }
					                                    }else{
					                						mLlSingupRoot.setVisibility(View.VISIBLE);
					                						mPbSignup.setVisibility(View.GONE);
					                                        Toast.makeText(SignUpActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
					                                    }
					                                }else{
					            						mLlSingupRoot.setVisibility(View.VISIBLE);
					            						mPbSignup.setVisibility(View.GONE);
					                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
					                                }
					                            }catch(Exception e){
					        						mLlSingupRoot.setVisibility(View.VISIBLE);
					        						mPbSignup.setVisibility(View.GONE);
					                                Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
					                                e.printStackTrace();
					                            }
											}
										});
									}catch(Exception e){
										e.printStackTrace();
										mLlSingupRoot.setVisibility(View.VISIBLE);
										mPbSignup.setVisibility(View.GONE);
				                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
									}
								}
								
								@Override
								public void failed(final JSONObject errorObj) {
				                    try{
				                        runOnUiThread(new Runnable(){
				                            @Override
				                            public void run(){
				                                try{
				            						mLlSingupRoot.setVisibility(View.VISIBLE);
				            						mPbSignup.setVisibility(View.GONE);
				                                    if (errorObj != null && errorObj.has(Define.TAG_API_MESSAGE)){
				                                        Toast.makeText(SignUpActivity.this, errorObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
				                                    }else{
				                                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                                    }
				                                }catch (Exception e){
				            						mLlSingupRoot.setVisibility(View.VISIBLE);
				            						mPbSignup.setVisibility(View.GONE);
				                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                                    e.printStackTrace();
				                                }catch(Error err){
				            						mLlSingupRoot.setVisibility(View.VISIBLE);
				            						mPbSignup.setVisibility(View.GONE);
				                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                                    err.printStackTrace();
				                                }catch (Throwable th){
				            						mLlSingupRoot.setVisibility(View.VISIBLE);
				            						mPbSignup.setVisibility(View.GONE);
				                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                                    th.printStackTrace();
				                                }
				                            }
				                        });
				                    }catch(Exception e){
				                        e.printStackTrace();
										mLlSingupRoot.setVisibility(View.VISIBLE);
										mPbSignup.setVisibility(View.GONE);
				                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                    }catch (Error err){
				                        err.printStackTrace();
										mLlSingupRoot.setVisibility(View.VISIBLE);
										mPbSignup.setVisibility(View.GONE);
				                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                    }catch(Throwable th){
				                        th.printStackTrace();
										mLlSingupRoot.setVisibility(View.VISIBLE);
										mPbSignup.setVisibility(View.GONE);
				                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
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

			mLlSingupRoot.setVisibility(View.GONE);
			mPbSignup.setVisibility(View.VISIBLE);
			
			mHandler.post(new Runnable(){
				@Override
				public void run(){
					new CommunicationAPIManager(SignUpActivity.this).sendResendVerificationCodeRequest(Globals.gCurrentUser.user_id,
							new ResponseDelegate() {																
								@Override
								public void succeed(final JSONObject responseObj) {
									try{
										runOnUiThread(new Runnable(){
											@Override
											public void run(){
												try{
				            						mLlSingupRoot.setVisibility(View.VISIBLE);
				            						mPbSignup.setVisibility(View.GONE);
					                                if (responseObj != null){
					                                	if (responseObj.has(Define.TAG_API_MESSAGE)){
						                                    Toast.makeText(SignUpActivity.this, responseObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();					                                		
					                                	}
					                                }else{
					            						mLlSingupRoot.setVisibility(View.VISIBLE);
					            						mPbSignup.setVisibility(View.GONE);
					                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
					                                }
					                            }catch(Exception e){
					        						mLlSingupRoot.setVisibility(View.VISIBLE);
					        						mPbSignup.setVisibility(View.GONE);
					                                Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
					                                e.printStackTrace();
					                            }
											}
										});
									}catch(Exception e){
										e.printStackTrace();
										mLlSingupRoot.setVisibility(View.VISIBLE);
										mPbSignup.setVisibility(View.GONE);
				                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
									}
								}
								
								@Override
								public void failed(final JSONObject errorObj) {
				                    try{
				                        runOnUiThread(new Runnable(){
				                            @Override
				                            public void run(){
				                                try{
				            						mLlSingupRoot.setVisibility(View.VISIBLE);
				            						mPbSignup.setVisibility(View.GONE);
				                                    if (errorObj != null && errorObj.has(Define.TAG_API_MESSAGE)){
				                                        Toast.makeText(SignUpActivity.this, errorObj.optString(Define.TAG_API_MESSAGE, ""), Toast.LENGTH_SHORT).show();
				                                    }else{
				                                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                                    }
				                                }catch (Exception e){
				            						mLlSingupRoot.setVisibility(View.VISIBLE);
				            						mPbSignup.setVisibility(View.GONE);
				                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                                    e.printStackTrace();
				                                }catch(Error err){
				            						mLlSingupRoot.setVisibility(View.VISIBLE);
				            						mPbSignup.setVisibility(View.GONE);
				                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                                    err.printStackTrace();
				                                }catch (Throwable th){
				            						mLlSingupRoot.setVisibility(View.VISIBLE);
				            						mPbSignup.setVisibility(View.GONE);
				                                    Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                                    th.printStackTrace();
				                                }
				                            }
				                        });
				                    }catch(Exception e){
				                        e.printStackTrace();
										mLlSingupRoot.setVisibility(View.VISIBLE);
										mPbSignup.setVisibility(View.GONE);
				                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                    }catch (Error err){
				                        err.printStackTrace();
										mLlSingupRoot.setVisibility(View.VISIBLE);
										mPbSignup.setVisibility(View.GONE);
				                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                    }catch(Throwable th){
				                        th.printStackTrace();
										mLlSingupRoot.setVisibility(View.VISIBLE);
										mPbSignup.setVisibility(View.GONE);
				                        Toast.makeText(SignUpActivity.this, R.string.msg_err_an_error_occurred_operation, Toast.LENGTH_SHORT).show();
				                    }
								}
							});
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
