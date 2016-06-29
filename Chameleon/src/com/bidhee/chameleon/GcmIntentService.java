package com.bidhee.chameleon;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.bidhee.metadata.DAOSessionTable;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService{

	Context context;
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	public static final String TAG = "GCM Demo";
	private String type;
	private String jokeId;
	private long sessionId = 0;
	private Intent notifyIntent;
	private DAOSessionTable sessionLocal;
	private String byUserId;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String msg = intent.getStringExtra("message");
		type = intent.getStringExtra("notificationType");
		jokeId = intent.getStringExtra("jokeId");

		String toUserId = intent.getStringExtra("toUserId");
		String badge = intent.getStringExtra("badge");

		byUserId = intent.getStringExtra("byUserId");

		SharedPreferences preferences = getSharedPreferences("BADGEDATA", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		editor.putString("badge",badge);
		editor.apply();


		sessionLocal = new DAOSessionTable(this);
		sessionId = sessionLocal.getSessionUserId();


		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if(toUserId!=null){
			if (!toUserId.isEmpty()&&toUserId.equals(String.valueOf(sessionId))) {
				if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
					sendNotification("Send error: " + extras.toString());
				} else if (GoogleCloudMessaging. MESSAGE_TYPE_DELETED.equals(messageType)) {
					sendNotification("Deleted messages on server: " + extras.toString());
				} else if (GoogleCloudMessaging. MESSAGE_TYPE_MESSAGE.equals(messageType)) {
					for (int i=0; i<5; i++) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
					}
					SharedPreferences prfs = getSharedPreferences("NOTIFICATION_PREF", Context.MODE_PRIVATE);
					String Astatus = prfs.getString("Notify_Status", "");
					if(Astatus.equals("Hide")){

					}
					else{
						sendNotification(msg);
					}
				}
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
	private void sendNotification(String msg) {
		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);

		if(type.equals("joke")){
			notifyIntent = new Intent(this, MainActivity.class);
			notifyIntent.putExtra("jokeId", jokeId);
			notifyIntent.putExtra("type", "NOTIFY");

		}
		else{

			notifyIntent = new Intent(this, MainActivity.class);
			notifyIntent.putExtra("JokerID", byUserId);
			notifyIntent.putExtra("type", "Notification");


		}
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setColor(getResources().getColor(R.color.text_dark))
				.setContentTitle(getResources().getString(R.string.app_name))
				.setStyle(new NotificationCompat.BigTextStyle()
						.bigText(msg))
				.setAutoCancel(true)
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

}