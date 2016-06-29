package com.bidhee.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.accounts.Account;
import android.accounts.AccountManager;

import com.bidhee.chameleon.R;

public class HelpFragment extends Fragment{

	private View rootView;
	private EditText etAddQuery,etAddEmail;
	private Button btnAddQuery;
	private Toolbar toolbarHelp;

	private String email, query;
	private String to = "help@chameleonverifyapp.com";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_help, container,false);
		etAddQuery = (EditText) rootView.findViewById(R.id.etAddQuery);
		etAddEmail = (EditText) rootView.findViewById(R.id.etAddEmail);
		btnAddQuery = (Button) rootView.findViewById(R.id.btnAddQuery);

		toolbarHelp = (Toolbar) rootView.findViewById(R.id.toolbarHelp);
		toolbarHelp.setTitleTextColor(getResources().getColor(R.color.white));
		toolbarHelp.setTitle("Help");

		etAddQuery.setHintTextColor(getActivity().getResources().getColor(R.color.hint_color));
		etAddEmail.setHintTextColor(getActivity().getResources().getColor(R.color.hint_color));

		String defaultEmail = getEmail(getActivity());

		if(defaultEmail!=null&&!defaultEmail.isEmpty()){
			etAddEmail.setText(defaultEmail);
		}

		btnAddQuery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				email = etAddEmail.getText().toString();
				query = etAddQuery.getText().toString();

				if(email!=null&&!email.isEmpty()){
					if(query!=null&&!query.isEmpty()){
						emailNow();
					}else{
						Toast.makeText(getActivity(), "Add your concern to continue", Toast.LENGTH_SHORT).show();
					}
				}
				else{
					Toast.makeText(getActivity(), "Enter email to continue", Toast.LENGTH_SHORT).show();
				}
			}
		});

		return rootView;
	}

	protected void emailNow() {
		Intent emailActivity = new Intent(Intent.ACTION_SEND);
		emailActivity.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
		emailActivity.putExtra(Intent.EXTRA_SUBJECT, "Chamaleon Help");
		emailActivity.putExtra(Intent.EXTRA_TEXT, query);
		emailActivity.setType("message/rfc822");
		startActivity(Intent.createChooser(emailActivity, "Select your Email Provider :"));
	}

	static String getEmail(Context context) {
		AccountManager accountManager = AccountManager.get(context); 
		Account account = getAccount(accountManager);

		if (account == null) {
			return null;
		} else {
			return account.name;
		}
	}
	private static Account getAccount(AccountManager accountManager) {
		Account[] accounts = accountManager.getAccountsByType("com.google");
		Account account;
		if (accounts.length > 0) {
			account = accounts[0];      
		} else {
			account = null;
		}
		return account;
	}
}