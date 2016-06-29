package com.bidhee.model.comm;

import org.json.JSONObject;

import com.bidhee.util.AlertDialogHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.ProgressDialog;
import android.content.Context;
import cz.msebera.android.httpclient.Header;

public class ServiceRequest {

	private Context context;
	private RequestData requestData;
	@SuppressWarnings("unused")
	private Class<?> responseClass = null;
	private ProgressDialog progressDialog;

	public ServiceRequest(Context context, RequestData serviceRequests){

		this.context = context;
		this.requestData = serviceRequests;
	}

	public void executeRequest() {

		AsyncHttpClient client = getHttpInstance();
		client.setTimeout(CommConfig.CONNECTION_TIME);
		if (requestData.getRequestParams() != null)
			client.post(requestData.getUrl(), requestData.getRequestParams(), asyHttpResponseHandler);
		else
			client.post(requestData.getUrl(), asyHttpResponseHandler);
	}

	public void executeRequestWithoutProgressbar() {
		AsyncHttpClient client = getHttpInstance();
		client.setTimeout(CommConfig.CONNECTION_TIME);
		if (requestData.getRequestParams() != null)
			client.post(requestData.getUrl(), requestData.getRequestParams(), asyHttpResponseHandlerWithoutProgressbar);
		else
			client.post(requestData.getUrl(), asyHttpResponseHandlerWithoutProgressbar);
	}

	public void executeGetRequest() {

		AsyncHttpClient client = getHttpInstance();
		client.setTimeout(CommConfig.CONNECTION_TIME);
		if (requestData.getRequestParams() != null)
			client.get(requestData.getUrl(), requestData.getRequestParams(), asyHttpResponseHandler);
		else
			client.get(requestData.getUrl(), asyHttpResponseHandler);
	}

	public void executeGetRequestWithoutProgressBar() {

		AsyncHttpClient client = getHttpInstance();
		client.setTimeout(CommConfig.CONNECTION_TIME);
		if (requestData.getRequestParams() != null)
			client.get(requestData.getUrl(), requestData.getRequestParams(), asyHttpResponseHandlerWithoutProgressbar);
		else
			client.get(requestData.getUrl(), asyHttpResponseHandlerWithoutProgressbar);
	}

	public void executePutRequest() {

		AsyncHttpClient client = getHttpInstance();
		client.setTimeout(CommConfig.CONNECTION_TIME);
		if (requestData.getRequestParams() != null)
			client.put(requestData.getUrl(), requestData.getRequestParams(), asyHttpResponseHandler);
		else
			client.put(requestData.getUrl(), asyHttpResponseHandler);
	}

	public void executePutRequestWithoutProgressBar() {

		AsyncHttpClient client = getHttpInstance();
		client.setTimeout(CommConfig.CONNECTION_TIME);
		if (requestData.getRequestParams() != null)
			client.put(requestData.getUrl(), requestData.getRequestParams(), asyHttpResponseHandlerWithoutProgressbar);
		else
			client.put(requestData.getUrl(), asyHttpResponseHandlerWithoutProgressbar);
	}

	public void executePatchRequest() {

		AsyncHttpClient client = getHttpInstance();
		client.setTimeout(CommConfig.CONNECTION_TIME);
		if (requestData.getRequestParams() != null)
			client.patch(requestData.getUrl(), requestData.getRequestParams(), asyHttpResponseHandler);
		else
			client.patch(requestData.getUrl(), asyHttpResponseHandler);
	}

	public void executePatchRequestWithoutProgressBar() {

		AsyncHttpClient client = getHttpInstance();
		client.setTimeout(CommConfig.CONNECTION_TIME);
		if (requestData.getRequestParams() != null)
			client.patch(requestData.getUrl(), requestData.getRequestParams(), asyHttpResponseHandlerWithoutProgressbar);
		else
			client.patch(requestData.getUrl(), asyHttpResponseHandlerWithoutProgressbar);
	}

	public void executeDeleteRequest() {

		AsyncHttpClient client = getHttpInstance();
		client.setTimeout(CommConfig.CONNECTION_TIME);
		if (requestData.getRequestParams() != null)
			client.delete(requestData.getUrl(), requestData.getRequestParams(), asyHttpResponseHandler);
		else
			client.delete(requestData.getUrl(), asyHttpResponseHandler);
	}

	public void executeDeleteRequestWithoutProgressBar() {

		AsyncHttpClient client = getHttpInstance();
		client.setTimeout(CommConfig.CONNECTION_TIME);
		if (requestData.getRequestParams() != null)
			client.delete(requestData.getUrl(), requestData.getRequestParams(), asyHttpResponseHandlerWithoutProgressbar);
		else
			client.delete(requestData.getUrl(), asyHttpResponseHandlerWithoutProgressbar);
	}

	private AsyncHttpResponseHandler asyHttpResponseHandler = new AsyncHttpResponseHandler() {

		public void onStart() {

			showProgressBar();
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

			try {
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}

				String data = new String(responseBody);
				System.out.println("Print the Response :" + data);

				JSONObject retVal = new JSONObject(data);
				requestData.getResponseDelegate().succeed(retVal);

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Print the Message :" + ex.getLocalizedMessage());
				//                requestData.getResponseDelegate().onFailure(ex.getLocalizedMessage(), requestData.getAction());
				requestData.getResponseDelegate().failed(null);
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

			System.out.println("Print the Error :" + error);
			System.out.println("Print the Error Context :" + statusCode);
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			//            requestData.getResponseDelegate().onFailure(CommConfig.NETWORK_CONNECTION_ERROR, requestData.getAction());
			requestData.getResponseDelegate().failed(null);
		}
	};

	private AsyncHttpResponseHandler asyHttpResponseHandlerWithoutProgressbar = new AsyncHttpResponseHandler() {

		public void onStart() {			
		}

		@Override
		public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

			try {

				String data = new String(responseBody);
				System.out.println("Print the Response :" + data);

				JSONObject retVal = new JSONObject(data);
				requestData.getResponseDelegate().succeed(retVal);

			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Print the Message :" + ex.getLocalizedMessage());
				requestData.getResponseDelegate().failed(null);
			}
		}

		@Override
		public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

			System.out.println("Print the Error :" + error);
			System.out.println("Print the Error Context :" + statusCode);
			if ( requestData.getResponseDelegate() != null) requestData.getResponseDelegate().failed(null);
		}
	};

	private void showProgressBar(){
		progressDialog = AlertDialogHelper.loadingProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setMessage(CommConfig.NETWORK_PLEASE_WAIT);
		progressDialog.show();

	}

	private AsyncHttpClient getHttpInstance() {

		AsyncHttpClient asyClient = new AsyncHttpClient();
		asyClient.setTimeout(CommConfig.CONNECTION_TIME);
		return asyClient;
	}
}
