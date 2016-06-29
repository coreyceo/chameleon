package com.bidhee.chameleon;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bidhee.chameleon.communication.CommunicationAPIManager;
import com.bidhee.chameleon.global.Define;
import com.bidhee.chameleon.global.Globals;
import com.bidhee.custom.CircularImageView;
import com.bidhee.dao.FAQHelpDao;
import com.bidhee.fragment.EditProfileFragment;
import com.bidhee.fragment.FAQHelpFragment;
import com.bidhee.fragment.HelpFragment;
import com.bidhee.model.HelpFaqModel;
import com.bidhee.model.UserModel;
import com.bidhee.model.comm.ResponseDelegate;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private TextView tvName;
	@SuppressWarnings("unused")
	private UserModel userData;
	private CircularImageView ivUserImage;

	@SuppressWarnings("unused")
	private LinearLayout btnAccount, btnScanner, btnFaq, btnHelp,btnHistory;

	private Toolbar toolbarMain;
	public Handler mHandler = new Handler();


	@SuppressWarnings("unused")
	private RequestQueue rq;
	@SuppressWarnings("unused")
	private HelpFaqModel helpFaqmodel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		rq = Volley.newRequestQueue(getApplicationContext());

		findView();
		getFaqHelp();
		
		if (Globals.gCurrentUser != null){
			String[] fullName = Globals.gCurrentUser.user_name.split(" ");
	
			if(Globals.gCurrentUser.user_name !=null && !Globals.gCurrentUser.user_name.isEmpty() && !Globals.gCurrentUser.user_name.equals("null")){
				if(fullName.length==2){
					tvName.setText("Welcome! "+capString(fullName[0])+" "+capString(fullName[1]));
				}
				else if(fullName.length==3){
					tvName.setText("Welcome! "+capString(fullName[0])+" "+capString(fullName[1])+" "+capString(fullName[2]));
				}
				else{
					tvName.setText("Welcome! "+capString(Globals.gCurrentUser.user_name));
				}
			}else{
				tvName.setText("Welcome! " + Globals.gCurrentUser.user_email);
			}	
	
			if(Globals.gCurrentUser.user_avatar !=null && !Globals.gCurrentUser.user_avatar.isEmpty()){
				Picasso.with(getApplicationContext()).load(Globals.gCurrentUser.user_avatar).placeholder(R.drawable.user_load).into(ivUserImage);
			}
			else{
				Picasso.with(getApplicationContext()).load(R.drawable.user_load).into(ivUserImage);
			}
		}
		
//		UserTableDAO usertabledao = new UserTableDAO(getApplicationContext());
//		DAOSessionTable daosessiontable = new DAOSessionTable(getApplicationContext());
//		long id = daosessiontable.getSessionUserId();
//		userData = usertabledao.getDataProfile(id);
//		String[] fullName = userData.getUserName().split(" ");
//
//		if(userData.getUserName()!=null&&!userData.getUserName().isEmpty()){
//			if(fullName.length==2){
//				tvName.setText("Welcome! "+capString(fullName[0])+" "+capString(fullName[1]));
//			}
//			else if(fullName.length==3){
//				tvName.setText("Welcome! "+capString(fullName[0])+" "+capString(fullName[1])+" "+capString(fullName[2]));
//			}
//			else{
//				tvName.setText("Welcome! "+capString(userData.getUserName()));
//			}
//		}
//		else{
//			tvName.setText("Welcome! "+capString(userData.getUserEmail()));
//		}
//
//
//		if(userData.getUserImage()!=null&&!userData.getUserImage().isEmpty()){
//			Picasso.with(getApplicationContext()).load(userData.getUserImage()).placeholder(R.drawable.user_load).into(ivUserImage);
//		}
//		else{
//			Picasso.with(getApplicationContext()).load(R.drawable.user_load).into(ivUserImage);
//		}

		btnAccount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "This feature is coming soon!", Toast.LENGTH_SHORT).show();
//				Fragment editFragment = new EditProfileFragment(MainActivity.this);
//				FragmentManager fragmentManager = getSupportFragmentManager();
//				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//				fragmentTransaction.replace(android.R.id.content, editFragment);
//				fragmentTransaction.addToBackStack(null);
//				fragmentTransaction.commit();
			}
		});

		btnFaq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle faqHelpBundle = new Bundle();
				faqHelpBundle.putString("type", "faq");
				Fragment faqHelpFrag = new FAQHelpFragment();
				faqHelpFrag.setArguments(faqHelpBundle);
				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(android.R.id.content, faqHelpFrag);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});

		btnHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Fragment faqHelpFrag = new HelpFragment();
				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(android.R.id.content, faqHelpFrag);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();			
			}
		});
	}

	public void scanBarcode(View view) {
		if (!isCameraAvailable()) {
			Toast.makeText(this, "No Camera to scan ticket", Toast.LENGTH_LONG).show();
		} else {
			new IntentIntegrator(this).initiateScan();
		}
	}

	private void findView() {
		tvName = (TextView) findViewById(R.id.tvName);
		ivUserImage = (CircularImageView) findViewById(R.id.ivUserImage);
		btnAccount = (LinearLayout) findViewById(R.id.btnAccount);
		btnScanner = (LinearLayout) findViewById(R.id.btnScanner);
		btnFaq = (LinearLayout) findViewById(R.id.btnFaq);
		btnHelp = (LinearLayout) findViewById(R.id.btnHelp);
		btnHistory =(LinearLayout) findViewById(R.id.btnHistory);

		toolbarMain = (Toolbar) findViewById(R.id.toolbarMain);
		toolbarMain.setTitle("Dashboard");
		toolbarMain.setBackgroundColor(getResources().getColor(R.color.primaryColor));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode,	resultCode, data);
		if (result != null) {
			if (result.getContents() == null) {
				Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
			} else {
				Intent checkIntent = new Intent(this, CheckTicketActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("scanned", result.getContents());  
				checkIntent.putExtras(bundle);
				startActivity(checkIntent);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public boolean isCameraAvailable() {
		PackageManager pm = getPackageManager();
		return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	public String capString(String cap){
		StringBuilder sb = new StringBuilder(cap);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));  
		return sb.toString();
	}

	private void getFaqHelp() {

		mHandler.post(new Runnable(){
			@Override
			public void run(){
				new CommunicationAPIManager(MainActivity.this).sendGetFaqHelpRequest(new ResponseDelegate() {																
							@Override
							public void succeed(final JSONObject responseObj) {
								try{
									runOnUiThread(new Runnable(){
										@Override
										public void run(){
											try{
				                                if (responseObj != null){
				                                    if (responseObj.optBoolean(Define.TAG_API_RESULT, false) == true){
				                                    	if (responseObj.has(Define.TAG_API_VALUES)){
				                                    		JSONObject valuesObj = responseObj.optJSONObject(Define.TAG_API_VALUES);
				                                    		if (valuesObj != null){
				                                    			JSONArray faqHelpsArray = valuesObj.optJSONArray(Define.TAG_FHS_OBJECTS);
				                                    			if (faqHelpsArray != null){
				    												ArrayList<HelpFaqModel> helpFaqArray = new ArrayList<HelpFaqModel>();
				                                    				for (int i = 0; i < faqHelpsArray.length(); i++){
				                                    					HelpFaqModel fhModel = new HelpFaqModel(faqHelpsArray.optJSONObject(i));
				                                    					helpFaqArray.add(fhModel);
				                                    				}
				                            						FAQHelpDao faqDao = new FAQHelpDao(getApplicationContext());
				                            						faqDao.deletAllData();
				                            						faqDao.createFaqArray(helpFaqArray);
				                                    			}
				                                    		}
				                                    	}
				                                    }
				                                }
				                            }catch(Exception e){
				                                e.printStackTrace();
				                            }
										}
									});
								}catch(Exception e){
									e.printStackTrace();
								}
							}
							
							@Override
							public void failed(final JSONObject errorObj) {
							}
						});
			}
		});		
//		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
//				APIUrls.URL_FAQ_HELP, null,
//				new Response.Listener<JSONObject>() {
//			@Override
//			public void onResponse(JSONObject response) {
//				try {
//					ArrayList<HelpFaqModel> helpFaqArray = new ArrayList<HelpFaqModel>();
//					if (response.length() != 0 && response != null) {
//						JSONArray jsonArray = response
//								.getJSONArray("faqHelpData");
//						for (int i = 0; i < jsonArray.length(); i++) {
//							JSONObject c = jsonArray.getJSONObject(i);
//							String faq = c.getString("faq");
//							String help = c.getString("help");
//							helpFaqmodel = new HelpFaqModel(faq, help);
//							helpFaqArray.add(helpFaqmodel);
//						}
//						FAQHelpDao faqDao = new FAQHelpDao(getApplicationContext());
//						faqDao.deletAllData();
//						faqDao.createFaqArray(helpFaqArray);
//
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//
//			}
//		}, new Response.ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//
//			}
//		}) {
//		};
//		rq.add(jsonObjReq);
	}

}
