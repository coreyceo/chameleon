package com.bidhee.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bidhee.chameleon.R;
import com.bidhee.metadata.APIUrls;
import com.gc.materialdesign.views.ButtonFlat;

public class ForgetPasswordFragment extends Fragment {
	private View rootView;
	private EditText etForgetpasswordEmail;
	private Button btnRetrive;
	private String email;
	private ProgressBar pbForgetPassword;
	private ButtonFlat tvForgtetPassswordHaveKey;
	private boolean error;
	private String message;
	private ScrollView svForgetPassword;
	private Dialog dialogChangePassword;
	private ProgressBar pbPasscode;
	private LinearLayout llPasscode;
	private EditText etPasscode,etNewPassword,etReNewPassword;
	private Toolbar toolbarFogetPwd;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_forget_password, container, false);
		etForgetpasswordEmail = (EditText) rootView.findViewById(R.id.etForgetpasswordEmail);
		btnRetrive = (Button) rootView.findViewById(R.id.btnRetrive);
		pbForgetPassword = (ProgressBar) rootView.findViewById(R.id.pbForgetPassword);
		tvForgtetPassswordHaveKey =(ButtonFlat) rootView.findViewById(R.id.tvForgtetPassswordHaveKey);
		svForgetPassword =(ScrollView) rootView.findViewById(R.id.svForgetPassword);
		toolbarFogetPwd = (Toolbar) rootView.findViewById(R.id.toolbarFogetPwd);
				
		toolbarFogetPwd.setTitle("Forget Password");
		toolbarFogetPwd.setBackgroundColor(getResources().getColor(R.color.primaryColor));
		toolbarFogetPwd.setNavigationIcon(R.drawable.ic_arrow_back_white_36dp);
		
		toolbarFogetPwd.setNavigationOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
				 
			}
		});
		
		
		etForgetpasswordEmail.setHintTextColor(getActivity().getResources().getColor(R.color.hint_color));
		
		tvForgtetPassswordHaveKey.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			 	dialogChangePassword = new Dialog(getActivity());
			 	dialogChangePassword.setContentView(R.layout.dialog_pass_code);
			 	dialogChangePassword.setTitle("Change Password");
			 	dialogChangePassword.setCancelable(true);
			 	
			 	etPasscode = (EditText) dialogChangePassword.findViewById(R.id.etPasscode);
			 	etNewPassword = (EditText) dialogChangePassword.findViewById(R.id.etPassCodePassword);
			 	etReNewPassword =(EditText) dialogChangePassword.findViewById(R.id.etPasscodeRePassword);
			 	pbPasscode =(ProgressBar) dialogChangePassword.findViewById(R.id.pbPasscode);
			 	llPasscode =(LinearLayout) dialogChangePassword.findViewById(R.id.llPasscode);
			 	Button btnChange = (Button) dialogChangePassword.findViewById(R.id.btnokPasscode);
			 	Button btnChancel = (Button) dialogChangePassword.findViewById(R.id.btnCancelPasscode);
			 	
			 	dialogChangePassword.setOnKeyListener(new Dialog.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,KeyEvent event) {
						
						 if (keyCode == KeyEvent.KEYCODE_BACK) {
								dialogChangePassword.dismiss();
						}
						 
							return false;
					}
		        });
			 			 	
			 	btnChancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialogChangePassword.dismiss();
					}
				});
			 	
			 	btnChange.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String passcode = etPasscode.getText().toString();
						String passwrod = etNewPassword.getText().toString();
						String rePasswrod = etReNewPassword.getText().toString();
						if(passcode!=null&&!passcode.isEmpty()&&passwrod!=null&&!passwrod.isEmpty()&&!rePasswrod.isEmpty()&&rePasswrod!=null){
							changePassword(passcode, passwrod, rePasswrod);
						}
						
					}
				});
			 	
			 	
		     	dialogChangePassword.show();
				
			}
		});
		
		btnRetrive.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				email = etForgetpasswordEmail.getText().toString();
				if (email != null && !email.isEmpty()) {
					postToApi(email);
				}
			}
		});
		
		
		return rootView;
	
	}
	
	
	public void postToApi(final String email) {
		pbForgetPassword.setVisibility(View.VISIBLE);
		svForgetPassword.setVisibility(View.INVISIBLE);
		RequestQueue rq = Volley.newRequestQueue(getActivity());
		
		StringRequest postReq = new StringRequest(Request.Method.POST, APIUrls.URL_FORGET_PASSWORD, new Response.Listener<String>() {
		    @Override
		    public void onResponse(String response) {
		    	System.out.println("Respnse Changing password::" + response);
		    	try {
					JSONObject jsonObj = new JSONObject(response);
					error = jsonObj.getBoolean("error");
					if(!error){
						message = jsonObj.getString("message");
						Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
					}
					else{
						message = jsonObj.getString("message");
						Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
					}
									
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    	
		    	pbForgetPassword.setVisibility(View.GONE);
		    	svForgetPassword.setVisibility(View.VISIBLE);
		        
		    }
		}, new Response.ErrorListener() {
		    @Override
		    public void onErrorResponse(VolleyError error) {
		        System.out.println("Error ["+error+"]");
		        pbForgetPassword.setVisibility(View.GONE);
		        svForgetPassword.setVisibility(View.VISIBLE);
		         
		    }
		    	        
		}) {     
		    @Override
		    protected Map<String, String> getParams() 
		    {       Map<String, String>  params = new HashMap<String, String>();  
		            params.put("email", email);
		            return params;  
		    }
		};
		
		rq.add(postReq);
	}
	
	public void changePassword(final String passcode,final String passwrod, final String repassword) {
		pbPasscode.setVisibility(View.VISIBLE);
		llPasscode.setVisibility(View.INVISIBLE);
		RequestQueue rq = Volley.newRequestQueue(getActivity());
		
		StringRequest postReq = new StringRequest(Request.Method.POST, APIUrls.URL_FORGET_CHANGE_PASSWORD, new Response.Listener<String>() {
		    @Override
		    public void onResponse(String response) {
		    	System.out.println("Respnse Changing password::" + response);
		    	try {
					JSONObject jsonObj = new JSONObject(response);
					error = jsonObj.getBoolean("error");
					if(!error){
						message = jsonObj.getString("message");
						Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
					}
					else{
						message = jsonObj.getString("message");
						Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
					}
									
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
		    	
		    	pbPasscode.setVisibility(View.GONE);
		    	llPasscode.setVisibility(View.VISIBLE);
		        
		    }
		}, new Response.ErrorListener() {
		    @Override
		    public void onErrorResponse(VolleyError error) {
		        System.out.println("Error ["+error+"]");
		        pbPasscode.setVisibility(View.GONE);
		        llPasscode.setVisibility(View.VISIBLE);
		         
		    }
		    	        
		}) {     
		    @Override
		    protected Map<String, String> getParams() 
		    {       Map<String, String>  params = new HashMap<String, String>();  
		            params.put("pass_code", passcode);
		            params.put("new_pass", passwrod);
		            params.put("pass_retype", repassword);
		            return params;  
		    }
		};
		
		rq.add(postReq);
	}
	

}
