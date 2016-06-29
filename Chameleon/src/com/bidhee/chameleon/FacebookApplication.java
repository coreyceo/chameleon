package com.bidhee.chameleon;

import com.bidhee.chameleon.global.Configuration;
import com.bidhee.util.SharedObjects;
import com.facebook.SessionDefaultAudience;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.utils.Logger;

import android.app.Application;
import android.content.Context;

public class FacebookApplication extends Application {
    
	private static final String APP_ID = "487235344767209";
    private static final String APP_NAMESPACE = "thechameleon";
    public static Configuration config;

    @Override
	public void onCreate() {
		super.onCreate();
		SharedObjects.context = this;
        config = new Configuration(getSharedPreferences("riverside_driver", Context.MODE_PRIVATE));
		// set log to true
		Logger.DEBUG_WITH_STACKTRACE = true;

		// initialize facebook configuration
		Permission[] permissions = new Permission[] { 
				Permission.PUBLIC_PROFILE,
				Permission.PUBLISH_ACTION,
				Permission.USER_ABOUT_ME
			};

		SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
			.setAppId(APP_ID)
			.setNamespace(APP_NAMESPACE)
			.setPermissions(permissions)
			.setDefaultAudience(SessionDefaultAudience.FRIENDS)
			.setAskForAllPermissionsAtOnce(true)
			.build();

		SimpleFacebook.setConfiguration(configuration);
	}
}







