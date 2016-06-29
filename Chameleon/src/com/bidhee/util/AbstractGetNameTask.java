/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bidhee.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.bidhee.chameleon.SignInActivity;
import com.google.android.gms.auth.GoogleAuthUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Display personalized greeting. This class contains boilerplate code to
 * consume the token but isn't integral to getting the tokens.
 */
public abstract class AbstractGetNameTask extends AsyncTask<Void, Void, Void> {
	private static final String TAG = "TokenInfoTask";
	protected Activity mActivity;
	public static String GOOGLE_USER_DATA = "No_data";
	protected String mScope;
	protected String mEmail;
	protected int mRequestCode;
	ProgressDialog pdialog ;

	AbstractGetNameTask(Activity activity, String email, String scope) {
		this.mActivity = activity;
		this.mScope = scope;
		this.mEmail = email;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pdialog = new ProgressDialog(mActivity);
		pdialog.setMessage("Login....");
		if (pdialog != null) {
			pdialog.show();
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			fetchNameFromProfileServer();

		} catch (IOException ex) {
			onError("Following Error occured, please try again. "
					+ ex.getMessage(), ex);
		} catch (JSONException e) {
			onError("Bad response: " + e.getMessage(), e);
		}
		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		pdialog.dismiss();

	}
	protected void onError(String msg, Exception e) {
		if (e != null) {
			Log.e(TAG, "Exception: ", e);
		}
	}

	/**
	 * Get a authentication token if one is not available. If the error is not
	 * recoverable then it displays the error message on parent activity.
	 */
	protected abstract String fetchToken() throws IOException;

	private void fetchNameFromProfileServer() throws IOException, JSONException {
		String token = fetchToken();
		URL url = new URL(
				"https://www.googleapis.com/oauth2/v1/userinfo?access_token="
						+ token);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		int sc = con.getResponseCode();
		if (sc == 200) {
			InputStream is = con.getInputStream();
			GOOGLE_USER_DATA = readResponse(is);
			is.close();
			String userName = null, userGender = null, userImageUrl = null,socialId = null;
			try {
				System.out.println("On Home Page***"+ AbstractGetNameTask.GOOGLE_USER_DATA);
				JSONObject profileData = new JSONObject(AbstractGetNameTask.GOOGLE_USER_DATA);


				System.out.println("Google Data::" + profileData);

				if (profileData.has("picture")) {
					userImageUrl = profileData.getString("picture");

				}
				if (profileData.has("name")) {
					userName = profileData.getString("name");

				}
				if (profileData.has("gender")) {
					userGender = profileData.getString("gender");

				}
				if (profileData.has("id")) {
					socialId = profileData.getString("id");

				}

				Intent intent = new Intent(mActivity, SignInActivity.class);
				intent.putExtra("userName", userName);
				intent.putExtra("pic", userImageUrl);
				intent.putExtra("Gender", userGender);
				intent.putExtra("userId", socialId);
				intent.putExtra("type", "Google");
				mActivity.startActivity(intent);
				mActivity.finish();


				//				UserModel user = new UserModel(0, userName, userImageUrl, "i_kashif72@yahoo.com", userGender, "", socialId, "Google");
				//	        	          
				//				
				//				DTOSession session = DTOSession.getInstance();
				//				DAOSessionTable daoSession = new DAOSessionTable(mActivity);
				//				session.setSessionId(1);
				//				
				//				session.setSessionUserId(user.getUserId());
				//				
				//				session.setSessionActive(true);
				//				daoSession.deletAllData();
				//				daoSession.createSession(session);
				//
				//				UserTableDAO usertableDao = new UserTableDAO(mActivity);
				//				usertableDao.deletAllData();
				//				usertableDao.createUser(user);



			} catch (JSONException e) {
				e.printStackTrace();
			}

			return;
		} else if (sc == 401) {
			GoogleAuthUtil.invalidateToken(mActivity, token);
			onError("Server auth error, please try again.", null);
			return;
		} else {
			onError("Server returned the following error code: " + sc, null);
			return;
		}
	}

	/**
	 * Reads the response from the input stream and returns it as a string.
	 */
	private static String readResponse(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] data = new byte[2048];
		int len = 0;
		while ((len = is.read(data, 0, data.length)) >= 0) {
			bos.write(data, 0, len);
		}
		return new String(bos.toByteArray(), "UTF-8");
	}

}
