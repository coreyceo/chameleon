package com.bidhee.chameleon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.bidhee.chameleon.global.Globals;
import com.bidhee.metadata.DAOSessionTable;
import com.bidhee.model.DTOSession;

public class SplashActivity extends ActionBarActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();// will help you to identify which method causes the bug.
				} finally {
					if (FacebookApplication.config.checkIfLoggedIn()){
						Globals.gCurrentUser = FacebookApplication.config.getLoginUserInfo();
						if (Globals.gCurrentUser != null){
							Intent openStart = new Intent(getApplicationContext(), MainActivity.class);
							startActivity(openStart);
						}else{
							Intent openStart = new Intent(getApplicationContext(), SignInActivity.class);
							startActivity(openStart);								
						}
					}else{
						Intent openStart = new Intent(getApplicationContext(), SignInActivity.class);
						startActivity(openStart);
					}
				}
			}
		};
		timer.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}	
}


