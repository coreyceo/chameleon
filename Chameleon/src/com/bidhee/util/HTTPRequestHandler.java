package com.bidhee.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;

public class HTTPRequestHandler {

	static String response = null;
	public final static int GET = 1;
	public final static int POST = 2;
	@SuppressWarnings("unused")
	private Context context;
	public HTTPRequestHandler() {

	}

	public String makeServiceCall(String url, int method) {
		return this.makeServiceCall(url, method, null);
	}

	public String makeServiceCall(String url, int method,
			List<NameValuePair> params) {

		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;

			if (method == POST) {
				HttpPost httpPost = new HttpPost(url);
				if (params != null) {
					httpPost.setEntity(new UrlEncodedFormEntity(params));
				}
				httpResponse = httpClient.execute(httpPost);

			} else if (method == GET) {
				if (params != null) {
					String paramString = URLEncodedUtils
							.format(params, "utf-8");
					url += "?" + paramString;
				}
				System.out.println("URl ::" + url);
				HttpGet httpGet = new HttpGet(url);
				httpResponse = httpClient.execute(httpGet);
			}

			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}


	
	
	

	
	
	public String uploadUserImage(String url, List<NameValuePair> params) {
		String result ="";
		try {
			HttpClient client = new DefaultHttpClient();

			HttpPost post = new HttpPost(url);

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				
			
			
			for (int index = 0; index < params.size(); index++) {
				if (params.get(index).getName().equalsIgnoreCase("profile_image")) {
					File file =new File(params.get(index).getValue());
					ContentBody foto = new FileBody(new File(params.get(index).getValue()));
					entityBuilder.addPart("profile_image",foto);
					System.out.println("IMAGE SECTION"+file.toString());
				}
				else{
					System.out.println("TEXT SECTION");
					entityBuilder.addTextBody(params.get(index).getName(), params.get(index).getValue());
				}
				
								
			}
			HttpEntity entity = entityBuilder.build();
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			HttpEntity httpEntity = response.getEntity();
			result = EntityUtils.toString(httpEntity);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
