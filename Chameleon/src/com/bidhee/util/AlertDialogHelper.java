package com.bidhee.util;

import com.bidhee.chameleon.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;
import android.widget.EditText;

@SuppressWarnings("deprecation")
public class AlertDialogHelper {

	
	public static ProgressDialog loadingProgressDialog(Context context) {

		ProgressDialog progressDialog = ProgressDialog.show(context, null, null);
		progressDialog.setContentView(R.layout.loader);
		return progressDialog;
	} 

	public static AlertDialog alertDialog(Context context, String title, String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		if(null == title)
			alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		else
			alertDialog.setTitle(title);
		alertDialog.setMessage(message);

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		return alertDialog;
	}

	public static void showActionConfirmationAlertDialog(Context context, String message,Boolean showCancelButton, DialogInterface.OnClickListener positiveButtonClickListener){

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);

		builder.setPositiveButton("OK", positiveButtonClickListener); 
		if (showCancelButton) {
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});	
		}

		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	public static void showInputFieldDialog(Context context, String title, String message, String editText, final InputAlertDialogInterface positiveButtonClickListener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		if(null != message) builder.setMessage(message);
		if(null != title) builder.setTitle(title);

		// Use an EditText view to get user input.
		final EditText input = new EditText(context);
		input.setId(1);
		input.setText(editText);
		builder.setView(input);

		builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {            	
				if (null != positiveButtonClickListener) positiveButtonClickListener.onPositiveButtonClicked(dialog, whichButton, input.getText().toString());
				return;
			}
		}); 
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public interface DateSelector{
		void onDateChanged(String date);
		void onDateSelected();
	}
}
