package com.bidhee.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bidhee.chameleon.R;
import com.bidhee.custom.CircularImageView;
import com.bidhee.dao.UserTableDAO;
import com.bidhee.metadata.APIUrls;
import com.bidhee.metadata.DAOSessionTable;
import com.bidhee.model.DTOSession;
import com.bidhee.model.UserModel;
import com.bidhee.util.HTTPRequestHandler;
import com.bidhee.util.ImageLoadingUtils;
import com.bidhee.util.PConnectionDetectorUtils;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.squareup.picasso.Picasso;
import com.sromku.simple.fb.SimpleFacebook;


public class EditProfileFragment extends Fragment {

	private View rootView;
	private PConnectionDetectorUtils cd;
	private boolean isInternetConnected = false;
	private Toolbar toolbarEditProfile;

	private Dialog dialogGender;
	private Dialog dialogChangeProfilePicture;
	private Dialog dialogDOB;
	private DatePicker dpEditDob;

	private ImageLoadingUtils utils;
	private final int CameraResult = 1;
	private final int GalleryResult = 2;
	private Uri fileUri;

	private List<NameValuePair> params;
	DTOSession session;
	DAOSessionTable daoSession;
	private UserModel dtoUser;
	public static final String IMAGE_DIRECTORY_NAME = "Android File Upload";
	private TextView tvChangeProfilePicture;
	private EditText etUserName, etGender,eteditUserDob; 
	private CircularImageView ivProfilePicturtureEdit;
	private ScrollView svEditProfile;
	private boolean cancel;
	private ProgressBarCircularIndeterminate pbEditProfile;
	private CompressImageTask compressTask;
	private UserModel dtouser;
	private String userName="";
	private String userGender="";
	private String userdoB="";
	private int currentapiVersion;
	private boolean isImage = false;
	private String userImage;
	private SimpleFacebook mSimpleFacebook;
	private EditProfileTask editTask;
	private RequestQueue rq;
	private UserTableDAO usertabledao;
	private Button btnSaveProfile;
	private String profileImage;

	public EditProfileFragment(ActionBarActivity activity) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rq = Volley.newRequestQueue(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,  Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
		currentapiVersion = android.os.Build.VERSION.SDK_INT;
		findView(rootView);
		cd = new PConnectionDetectorUtils(getActivity());
		isInternetConnected = cd.isConnectingToInternet();
		fillData();
		setUplistner();

		toolbarEditProfile = (Toolbar) rootView.findViewById(R.id.toolbar_frag_profile);
		toolbarEditProfile.setTitleTextColor(getResources().getColor(R.color.white));
		toolbarEditProfile.setTitle("Edit Profile");

		utils = new ImageLoadingUtils(getActivity());

		btnSaveProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try{
					hideSoftKeyBoardOnTabClicked();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				if(isInternetConnected){
					editNow();
				}
				else{
					Toast.makeText(getActivity(), "Please connect to internet", Toast.LENGTH_SHORT).show();
				}
			}
		});

		toolbarEditProfile.setNavigationOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});

		getUserData("");
		return rootView;
	}

	protected void hideSoftKeyBoardOnTabClicked() {
		InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
	}

	private void findView(View rootView) {

		tvChangeProfilePicture =(TextView) rootView.findViewById(R.id.lbl_frag_profile_change_picture);
		etGender =(EditText) rootView.findViewById(R.id.edt_frag_profile_user_gender);
		etUserName =(EditText) rootView.findViewById(R.id.edt_profile_username);
		eteditUserDob =(EditText) rootView.findViewById(R.id.edt_frag_profile_user_dob);
		ivProfilePicturtureEdit =(CircularImageView) rootView.findViewById(R.id.iv_frag_profile_avatar);
		svEditProfile =(ScrollView) rootView.findViewById(R.id.sv_frag_profile);
		pbEditProfile =(ProgressBarCircularIndeterminate) rootView.findViewById(R.id.pb_frag_profile);
		btnSaveProfile =(Button) rootView.findViewById(R.id.btn_frag_profile_save_profile);

		etGender.setHintTextColor(getActivity().getResources().getColor(R.color.hint_color));
		etUserName.setHintTextColor(getActivity().getResources().getColor(R.color.hint_color));
		eteditUserDob.setHintTextColor(getActivity().getResources().getColor(R.color.hint_color));

		mSimpleFacebook = SimpleFacebook.getInstance(getActivity());
		if (mSimpleFacebook.isLogin()) {
			tvChangeProfilePicture.setVisibility(View.GONE);
		} else {
			tvChangeProfilePicture.setVisibility(View.VISIBLE);
		}
	}

	private void fillData() {
		usertabledao = new UserTableDAO(getActivity());
		DAOSessionTable daosessiontable = new DAOSessionTable(getActivity());
		long id = daosessiontable.getSessionUserId();
		dtoUser = usertabledao.getDataProfile(id);
		session = DTOSession.getInstance();
		daoSession = new DAOSessionTable(getActivity());

		if (daoSession.getSessionState() == 1) {
			session.setSessionActive(true);
			if (dtoUser != null) {
				System.out.println("USER IMAGE" + dtoUser.getUserImage());
				if(dtoUser.getUserImage()!=null&& !dtoUser.getUserImage().isEmpty()){
					Picasso.with(getActivity()).load(dtoUser.getUserImage()).placeholder(R.drawable.user_load).into(ivProfilePicturtureEdit);
				}
				else{
					Picasso.with(getActivity()).load(R.drawable.user_load).into(ivProfilePicturtureEdit);
				}

				if(dtoUser.getSocialMediaType()!=null&&dtoUser.getSocialMediaType().equals("Facebook")){
					Picasso.with(getActivity()).load("https://graph.facebook.com/" + dtoUser.getSocialID()+ "/picture?type=large").placeholder(R.drawable.user_load)
					.into(ivProfilePicturtureEdit);
				}

				if(dtoUser.getUserName()!=null&& !dtoUser.getUserName().isEmpty()){
					etUserName.setText(dtoUser.getUserName());
				}
				if(dtoUser.getUserGender()!=null&& !dtoUser.getUserGender().isEmpty()){
					etGender.setText(dtoUser.getUserGender());
				}
				if(dtoUser.getUserDob()!=null&& !dtoUser.getUserDob().isEmpty()){
					eteditUserDob.setText(dtoUser.getUserDob());
				}			
			}
		} else {
			Toast.makeText(getActivity(), "Error occured while fetching user information", Toast.LENGTH_SHORT).show();			
		}			
	}

	private void setUplistner() {

		/*
		 * Focusable false to editText
		 */
		etGender.setFocusable(false);
		eteditUserDob.setFocusable(false);	

		tvChangeProfilePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				dialogChangeProfilePicture = new Dialog(getActivity());
				dialogChangeProfilePicture.setContentView(R.layout.dialog_change_profile_pic);
				dialogChangeProfilePicture.setTitle("Select option");
				dialogChangeProfilePicture.setCancelable(true);

				Button btnCamera = (Button) dialogChangeProfilePicture.findViewById(R.id.btnCPPCamera);
				Button btnGallery = (Button) dialogChangeProfilePicture.findViewById(R.id.btnCPPGallery);
				Button btnPicCancel =(Button) dialogChangeProfilePicture.findViewById(R.id.btnCPPCancel);

				btnCamera.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						takepicfromcamera();
					}

					private void takepicfromcamera() {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						fileUri = getOutputMediaFileUri();
						intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
						startActivityForResult(intent, CameraResult);						
					}

					public Uri getOutputMediaFileUri() {
						return Uri.fromFile(getOutputMediaFile());
					}

					private File getOutputMediaFile() {
						File mediaStorageDir = new File(
								Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
								IMAGE_DIRECTORY_NAME);

						if (!mediaStorageDir.exists()) {
							mediaStorageDir.mkdirs();

						}

						File mediaFile;
						mediaFile = new File(mediaStorageDir.getPath() + File.separator+ "IMG_"+".jpg");

						return mediaFile;
					}					 
				});

				btnGallery.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						takepicfromgallery();
						Toast.makeText(getActivity(), "Gallery", Toast.LENGTH_SHORT).show();
					}

					private void takepicfromgallery() {
						Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						intent.setType("image/*");
						startActivityForResult(intent, GalleryResult);
					}
				});

				btnPicCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialogChangeProfilePicture.dismiss();
					}
				});
				dialogChangeProfilePicture.show();
			}
		});

		etGender.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				dialogGender = new Dialog(getActivity());
				dialogGender.setContentView(R.layout.dialog_gender);
				dialogGender.setTitle("Select your gender");
				dialogGender.setCancelable(true);

				Button btnUnspecified = (Button) dialogGender.findViewById(R.id.btnDUnspecified);
				Button btnMale = (Button) dialogGender.findViewById(R.id.btnDMale);
				Button btnFemale = (Button) dialogGender.findViewById(R.id.btnDFemale);
				Button btnCancel =(Button) dialogGender.findViewById(R.id.btnDCancel);

				btnCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialogGender.dismiss();
					}
				});
				btnMale.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						etGender.setText("Male");
						dialogGender.dismiss();
					}
				});
				btnUnspecified.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						etGender.setText("Unspecified");
						dialogGender.dismiss();
					}
				});
				btnFemale.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						etGender.setText("Female");
						dialogGender.dismiss();
					}
				});

				dialogGender.show();
			}
		});

		eteditUserDob.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {

				dialogDOB = new Dialog(getActivity());
				dialogDOB.setContentView(R.layout.dialog_dob);
				dialogDOB.setTitle("Select Date of birth");

				Button btnDobSelect = (Button) dialogDOB.findViewById(R.id.btnokDob);
				Button btnDobCancel = (Button) dialogDOB.findViewById(R.id.btnCancelDob);
				dpEditDob = (DatePicker) dialogDOB.findViewById(R.id.dpEditDob);

				if(!eteditUserDob.getText().toString().isEmpty()&&eteditUserDob.getText().toString()!=null){
					String[] birthSplit = eteditUserDob.getText().toString().split("-");
					dpEditDob.updateDate(Integer.valueOf(birthSplit[0]), Integer.valueOf(birthSplit[1])-1, Integer.valueOf(birthSplit[2]));
				}

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
						eteditUserDob.setText(String.valueOf(year)+"-"+String.valueOf(month)+"-"+String.valueOf(day));
						dialogDOB.dismiss();
					}
				});
				dialogDOB.show();

			}
		});
	}

	protected void editNow() {
		cancel = false;

		if(!etGender.getText().toString().isEmpty()&&etGender.getText().toString()!=null){
			userGender = etGender.getText().toString();
		}
		if(!etUserName.getText().toString().isEmpty()&&etUserName.getText().toString()!=null){
			userName = etUserName.getText().toString();
		}
		if(!eteditUserDob.getText().toString().isEmpty()&&eteditUserDob.getText().toString()!=null){
			userdoB = eteditUserDob.getText().toString();
		}

		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("full_name", userName));
		params.add(new BasicNameValuePair("gender", userGender));
		params.add(new BasicNameValuePair("dob", userdoB));

		dtouser = new UserModel();
		dtouser.setUserName(userName);
		dtouser.setUserGender(userGender);
		dtouser.setUserDob(userdoB);

		if(!cancel){
			if(isImage&&userImage!=null&&!userImage.isEmpty()){
				compressTask = new CompressImageTask(params,userImage,dtouser);
				compressTask.execute();
			}
			else{
				editTask = new EditProfileTask(params, dtouser);
				editTask.execute();
			}
		}
	}




	private class EditProfileTask extends AsyncTask<Void, Void, Boolean> {
		String ok;
		String errorMsg;
		boolean error;
		private List<NameValuePair> params;
		@SuppressWarnings("unused")
		private UserModel userDto;
		private String jsonStr=null;

		public EditProfileTask(List<NameValuePair> params, UserModel userDto) {
			this.params= params;
			this.userDto = userDto;
			svEditProfile.setVisibility(View.GONE);
			pbEditProfile.setVisibility(View.VISIBLE);
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Boolean doInBackground(Void... arg0) {

			UserTableDAO usertabledao = new UserTableDAO(getActivity());
			DAOSessionTable daosessiontable = new DAOSessionTable(getActivity());
			long id = daosessiontable.getSessionUserId();
			dtoUser = usertabledao.getDataProfile(id);

			HTTPRequestHandler requestHandler = new HTTPRequestHandler();
			System.out.println("URL::" + APIUrls.URL_POST_PROFILE+dtoUser.getUserId()+"/"+dtoUser.getSecretKey());
			if(params.size()==4){
				jsonStr = requestHandler.uploadUserImage(APIUrls.URL_POST_PROFILE+dtoUser.getUserId()+"/"+dtoUser.getSecretKey(), params);
			}
			else{
				jsonStr = requestHandler.makeServiceCall(APIUrls.URL_POST_PROFILE+dtoUser.getUserId()+"/"+dtoUser.getSecretKey(),HTTPRequestHandler.POST, params);
			}
			
			if (jsonStr != null) {
				System.out.println("EDIT REPOSNDLL"+ jsonStr);
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					error = jsonObj.getBoolean("error");
					if (!error) {
						System.out.println("NO ERROR");
						ok = jsonObj.getString("message");
						getUserData(ok);
						return true;
					} else if (error) {
						errorMsg = jsonObj.getString("message");
						return false;
					}
				}catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(!result){
				Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
				svEditProfile.setVisibility(View.VISIBLE);
				pbEditProfile.setVisibility(View.GONE);
			}
			editTask = null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			editTask = null;
			cancel = true;
		}
	}


	private class CompressImageTask extends AsyncTask<Void, Void, String> {

		private String pathImage;
		private List<NameValuePair> params;
		private UserModel userDto;

		public CompressImageTask(List<NameValuePair> params,String pathImage,UserModel userDto) {
			this.pathImage = pathImage; 
			this.params = params;
			this.userDto = userDto;
			System.out.println("ASYNC TASK IMAGE Compression");
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			svEditProfile.setVisibility(View.GONE);
			pbEditProfile.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Void... arg0) {
			String filePath = compressImage(pathImage);
			return filePath;
		}
		
		public String compressImage(String imageUri) {

			String filePath = getRealPathFromURI(imageUri);
			Bitmap scaledBitmap = null;

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;						
			Bitmap bmp = BitmapFactory.decodeFile(filePath,options);

			int actualHeight = options.outHeight;
			int actualWidth = options.outWidth;
			float maxHeight = 816.0f;
			float maxWidth = 612.0f;
			float imgRatio = actualWidth / actualHeight;
			float maxRatio = maxWidth / maxHeight;

			if (actualHeight > maxHeight || actualWidth > maxWidth) {
				if (imgRatio < maxRatio) {
					imgRatio = maxHeight / actualHeight;
					actualWidth = (int) (imgRatio * actualWidth);
					actualHeight = (int) maxHeight;
				} else if (imgRatio > maxRatio) {
					imgRatio = maxWidth / actualWidth;
					actualHeight = (int) (imgRatio * actualHeight);
					actualWidth = (int) maxWidth;
				} else {
					actualHeight = (int) maxHeight;
					actualWidth = (int) maxWidth;     
				}
			}

			options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
			options.inJustDecodeBounds = false;
			options.inDither = false;
			/*
			 * Deprecated
			 */
			//			options.inPurgeable = true;
			//			options.inInputShareable = true;
			options.inTempStorage = new byte[16*1024];

			try{	
				bmp = BitmapFactory.decodeFile(filePath,options);
			}
			catch(OutOfMemoryError exception){
				exception.printStackTrace();

			}
			try{
				scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
			}
			catch(OutOfMemoryError exception){
				exception.printStackTrace();
			}

			float ratioX = actualWidth / (float) options.outWidth;
			float ratioY = actualHeight / (float)options.outHeight;
			float middleX = actualWidth / 2.0f;
			float middleY = actualHeight / 2.0f;

			Matrix scaleMatrix = new Matrix();
			scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

			Canvas canvas = new Canvas(scaledBitmap);
			canvas.setMatrix(scaleMatrix);
			canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));


			ExifInterface exif;
			try {
				exif = new ExifInterface(filePath);

				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
				Log.d("EXIF", "Exif: " + orientation);
				Matrix matrix = new Matrix();
				if (orientation == 6) {
					matrix.postRotate(90);
					Log.d("EXIF", "Exif: " + orientation);
				} else if (orientation == 3) {
					matrix.postRotate(180);
					Log.d("EXIF", "Exif: " + orientation);
				} else if (orientation == 8) {
					matrix.postRotate(270);
					Log.d("EXIF", "Exif: " + orientation);
				}
				scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileOutputStream out = null;
			String filename = getFilename();
			try {
				out = new FileOutputStream(filename);
				scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			return filename;
		}

		private String getRealPathFromURI(String contentURI) {
			Uri contentUri = Uri.parse(contentURI);
			Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
			if (cursor == null) {
				return contentUri.getPath();
			} else {
				cursor.moveToFirst();
				int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				return cursor.getString(idx);
			}
		}

		public String getFilename() {
			File file = new File(Environment.getExternalStorageDirectory().getPath(), "JokersPortal/Images");
			if (!file.exists()) {
				file.mkdirs();
			}
			String uriSting = (file.getAbsolutePath() + "/"+ System.currentTimeMillis() + ".jpg");
			return uriSting;
		}

		@Override
		protected void onPostExecute(String result) {			 
			super.onPostExecute(result);
			params.add(new BasicNameValuePair("profile_image", result));
			editTask = new EditProfileTask(params, userDto);
			editTask.execute();
			compressTask= null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			compressTask = null;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ActionBarActivity.RESULT_OK) {
			if (requestCode == CameraResult) {
				if (isDeviceSupportCamera() == true) {
					userImage = fileUri.getPath();
					Bitmap bm;
					BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
					btmapOptions.inSampleSize = 2;
					bm = BitmapFactory.decodeFile(userImage, btmapOptions);
					ivProfilePicturtureEdit.setImageBitmap(bm);
					System.out.println("Image path::Camera" + userImage);
					isImage = true;
					dialogChangeProfilePicture.dismiss();
				}

				else {
					Toast.makeText(getActivity(),
							"No camera was found", Toast.LENGTH_SHORT).show();
				}

			} else if (requestCode == GalleryResult) {
				Uri selectedImageUri = data.getData();
				String tempPath = getPath(selectedImageUri);
				Bitmap bm;
				BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
				btmapOptions.inSampleSize = 2;
				bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
				ivProfilePicturtureEdit.setImageBitmap(bm);
				userImage = tempPath;
				isImage = true;
				System.out.println("Image path::Gallery" + userImage);
				dialogChangeProfilePicture.dismiss();

			}
		}

	}
	private boolean isDeviceSupportCamera() {
		if (getActivity().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}
	private String getPath(Uri uri) {
		String[] projection = { MediaColumns.DATA };
		Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor.getString(column_index);
	}

	private void getUserData(final String ok) {
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,APIUrls.URL_GET_PROFILE+dtoUser.getUserId()+"/"+dtoUser.getSecretKey(), null,
				new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					System.out.println("RESPNSE OF PROFILE:::" + response);
					if(response.length()!=0&&response!=null){
						JSONArray jsonArray = response.getJSONArray("userData");

						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject c = jsonArray.getJSONObject(i);
							String socialMediaId = c.getString("socialMediaId");
							profileImage = c.getString("profileImage");
							String socialMediaType = c.getString("socialMediaType");
							String secretKey = c.getString("secretKey");
							String email = c.getString("email");
							long userId = c.getInt("userId");
							String gender = c.getString("gender");
							String fullName = c.getString("fullName");
							String dob = c.getString("dateOfBirth");

							if(c.getString("socialMediaType").equals("Facebook")){
								profileImage  = "https://graph.facebook.com/"+ c.getString("socialMediaId")+ "/picture?type=large";
							}
							else{
								profileImage = APIUrls.URL_IMAGE+c.getString("profileImage")+"&w=200&h=200";
							}

							UserModel userDTO = new UserModel(userId, fullName, profileImage , email, gender, secretKey, socialMediaId, socialMediaType,dob);

							if(userDTO!=null){
								UserTableDAO usertabledao = new UserTableDAO(getActivity());
								try{
									usertabledao.updateUser(userDTO);
								}
								catch(Exception e){
									e.printStackTrace();
								}
							}

							if (userDTO != null) {
								if(userDTO.getUserImage()!=null&& !userDTO.getUserImage().isEmpty()){
									Picasso.with(getActivity()).load(userDTO.getUserImage()).placeholder(R.drawable.user_load)
									.into(ivProfilePicturtureEdit);
								}

								if(userDTO.getSocialMediaType()!=null&&userDTO.getSocialMediaType().equals("facebook")){
									System.out.println();
									Picasso.with(getActivity()).load("https://graph.facebook.com/" + userDTO.getSocialID() + "/picture?type=large").placeholder(R.drawable.user_load)
									.into(ivProfilePicturtureEdit);
								}	
								if(userDTO.getUserName()!=null&& !userDTO.getUserName().isEmpty()){
									etUserName.setText(userDTO.getUserName());
								}
								if(userDTO.getUserGender()!=null&& !userDTO.getUserGender().isEmpty()){
									etGender.setText(userDTO.getUserGender());
								}
							}

							if(ok!=null&&!ok.isEmpty()){
								Toast.makeText(getActivity(), ok, Toast.LENGTH_SHORT).show();
								pbEditProfile.setVisibility(View.GONE);
								getActivity().onBackPressed();
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}) {
		};
		rq.add(jsonObjReq);


	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(editTask!=null){
			editTask.cancel(true);
			editTask = null;
		}
		rq.cancelAll(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();

	}

}
