package com.bidhee.chameleon;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class CheckTicketActivity extends ActionBarActivity{

	private String eventName;
	private String date;
	private String venue;
	private String barcode;
	private String row;
	private String seat;
	private String price;
	private String qty;

	private EditText etEventName,etBarcode,etRow,etSeat,etPrice, etQty;
	private EditText etDate,etVenue;
	private Button btnCheck;
	private LinearLayout llCheckForm;
	private ProgressBarCircularIndeterminate pbCheck;
	private Dialog dialogDOB;
	private DatePicker dpEditDob;
	private int currentapiVersion;
//	private String url = "http://www.yarsha.com.np/chameleon/validate.php";
	private String url = "https://api.stubhub.com/inventory/listings/v1/barcodes";
	private String loginUrl = "https://api.stubhub.com/login";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_ticket);
		Bundle b = getIntent().getExtras();
		barcode = b.getString("scanned");
		currentapiVersion = android.os.Build.VERSION.SDK_INT;
		etEventName = (EditText) findViewById(R.id.etEventName);
		etDate = (EditText) findViewById(R.id.etDate);
		etVenue = (EditText) findViewById(R.id.etVenue);
		etBarcode = (EditText) findViewById(R.id.etBarcode);
		etRow = (EditText) findViewById(R.id.etRow);
		etSeat = (EditText) findViewById(R.id.etSeat);
		etPrice = (EditText) findViewById(R.id.etPrice);
		etQty = (EditText) findViewById(R.id.etQty);
		
		btnCheck = (Button) findViewById(R.id.btnCheck);
		llCheckForm = (LinearLayout) findViewById(R.id.llCheckForm);
		pbCheck = (ProgressBarCircularIndeterminate) findViewById(R.id.pbCheck);

		etEventName.setHintTextColor(getResources().getColor(R.color.hint_color));
		etDate.setHintTextColor(getResources().getColor(R.color.hint_color));
		etVenue.setHintTextColor(getResources().getColor(R.color.hint_color));
		etBarcode.setHintTextColor(getResources().getColor(R.color.hint_color));
		etRow.setHintTextColor(getResources().getColor(R.color.hint_color));
		etSeat.setHintTextColor(getResources().getColor(R.color.hint_color));
		etPrice.setHintTextColor(getResources().getColor(R.color.hint_color));
		etQty.setHintTextColor(getResources().getColor(R.color.hint_color));
		etDate.setFocusable(false);
		etBarcode.setFocusable(false);
		etBarcode.setText(barcode);
		

		etDate.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {

				dialogDOB = new Dialog(CheckTicketActivity.this);
				dialogDOB.setContentView(R.layout.dialog_dob);
				dialogDOB.setTitle("Select Date of birth");

				Button btnDobSelect = (Button) dialogDOB.findViewById(R.id.btnokDob);
				Button btnDobCancel = (Button) dialogDOB.findViewById(R.id.btnCancelDob);
				dpEditDob = (DatePicker) dialogDOB.findViewById(R.id.dpEditDob);

				if(currentapiVersion>11){
					try {
						dpEditDob.setCalendarViewShown(false);     
					}
					catch (Exception e) {
						e.printStackTrace(); 	  
					} 
				}
				btnDobCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialogDOB.dismiss();
					}
				});

				btnDobSelect.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int day = dpEditDob.getDayOfMonth();
						int month = dpEditDob.getMonth() + 1;
						int year = dpEditDob.getYear();
						etDate.setText(String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day));
						dialogDOB.dismiss();
					}
				});
				dialogDOB.show();
			}
		});

		btnCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				eventName = etEventName.getText().toString();
				date =etDate.getText().toString();
				venue= etVenue.getText().toString();
				price = etPrice.getText().toString();
				row= etRow.getText().toString();
				seat= etSeat.getText().toString();
				qty = etQty.getText().toString();

				if(!eventName.isEmpty()){
					if(!date.isEmpty()){
						if(!venue.isEmpty()){
							if(!price.isEmpty()){
								if(!row.isEmpty()){
									if(!seat.isEmpty()){
										if (!qty.isEmpty()) {
//											checkTicket();
											getToken();
										} else {
											Toast.makeText(getApplicationContext(), "Provide quantity.", Toast.LENGTH_SHORT).show();
										}
									}else{
										Toast.makeText(getApplicationContext(), "Provide seat no.", Toast.LENGTH_SHORT).show();
									}
								}else{
									Toast.makeText(getApplicationContext(), "Provide event row", Toast.LENGTH_SHORT).show();
								}
							}else{
								Toast.makeText(getApplicationContext(), "Provide ticket price", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "Provide event venue", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "Provide event date", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "Provide event name", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	private void getToken() {
		llCheckForm.setVisibility(View.GONE);
		pbCheck.setVisibility(View.VISIBLE);
		
		RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
		
		StringRequest postReq = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				try {
					JSONObject jsonObj = new JSONObject(response);
					String token = jsonObj.getString("access_token");
					checkTicket(token);
					Log.d("token", token);
//					Toast.makeText(getApplicationContext(), token, Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Can't get access to StubHub", Toast.LENGTH_SHORT).show();
					llCheckForm.setVisibility(View.VISIBLE);
					pbCheck.setVisibility(View.GONE);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				Toast.makeText(getApplicationContext(), "Error connecting to server", Toast.LENGTH_SHORT).show();
				llCheckForm.setVisibility(View.VISIBLE);
				pbCheck.setVisibility(View.GONE);
			}
		}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("grant_type", "password");
				params.put("username", "coreyceo@chameleonverifyapp.com");
				params.put("password", "Corndog18!");
				return params;
			}
			
			@Override
			public Map<String, String> getHeaders() {
			    HashMap<String, String> params = new HashMap<String, String>();
			    params.put("Content-Type", "application/x-www-form-urlencoded");
			    params.put("Authorization", "Basic QmFjc09VbHdyVkkzOE9rU0Roa1htU0dkZFBnYTpmcl84TWk0cWNOeTdqOWFMZk1sekFvbTFiY1Vh");
			    return params;
			}
		};
		
		rq.add(postReq);
		
	}

	public void checkTicket(final String token) {

		try {
			RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
			
			JSONObject event = new JSONObject();
			event.put("name", eventName);
			event.put("date", date);
			event.put("venue", venue);
			
			JSONObject pricePerTicket = new JSONObject();
			pricePerTicket.put("amount", price);
			pricePerTicket.put("currency", "USD");
			
			JSONArray tickets = new JSONArray();
			JSONObject tick = new JSONObject();
			tick.put("row", row);
			tick.put("seat", seat);
			tick.put("barcode", barcode);
			tickets.put(tick);
			
			JSONObject listing = new JSONObject();
			listing.put("event", event);
			listing.put("pricePerTicket", pricePerTicket);
			listing.put("quantity", qty);
			listing.put("splitOption", "NONE");
			listing.put("tickets", tickets);
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("listing", listing);
			
			Log.d("request", jsonObject.toString());
	
			JsonObjectRequest jsonReq = new JsonObjectRequest(url, jsonObject, new Response.Listener<JSONObject>() {
				@Override
				public void onResponse(JSONObject response) {
						Log.d("Response", response.toString());
						startActivity(new Intent(CheckTicketActivity.this, ValidActivity.class));
						finish();
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					if (error.networkResponse.statusCode == 400) {
						startActivity(new Intent(CheckTicketActivity.this, InvalidActivity.class));
						finish();
					} else {
						Toast.makeText(getApplicationContext(), "Please, input valid data", Toast.LENGTH_SHORT).show();
						llCheckForm.setVisibility(View.VISIBLE);
						pbCheck.setVisibility(View.GONE);
					}
				}
			}) {
			    
				@Override
				public Map<String, String> getHeaders() {
				    HashMap<String, String> params = new HashMap<String, String>();
				    params.put("Content-Type", "application/json");
				    params.put("Authorization", "Bearer " + token);
				    return params;
				}
			};
			
			rq.add(jsonReq);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Exception caused", Toast.LENGTH_SHORT).show();
			llCheckForm.setVisibility(View.VISIBLE);
			pbCheck.setVisibility(View.GONE);
		}

	}
}
