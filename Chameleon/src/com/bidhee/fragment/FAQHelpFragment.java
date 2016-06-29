package com.bidhee.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bidhee.chameleon.R;
import com.bidhee.dao.FAQHelpDao;
import com.bidhee.model.HelpFaqModel;

public class FAQHelpFragment extends Fragment {

	private View rootView;
	private WebView faqHelpWebView;
	private String type;
	private RequestQueue rq;
	private HelpFaqModel helpFaqmodel;
	private Toolbar toolbarFaqHelp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rq = Volley.newRequestQueue(getActivity());
		type = getArguments().getString("type");
		FAQHelpDao faqDao = new FAQHelpDao(getActivity());
		helpFaqmodel = faqDao.getFaqHelp();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_help_faq, container,false);
		faqHelpWebView = (WebView) rootView.findViewById(R.id.faqHelpWebView);
		toolbarFaqHelp = (Toolbar) rootView.findViewById(R.id.toolbarFaqHelp);

		toolbarFaqHelp.setTitleTextColor(getResources().getColor(R.color.white));
		if (helpFaqmodel != null) {
			if (type.equals("faq")) {
				faqHelpWebView.loadData(helpFaqmodel.getFaq(),"text/html; charset=utf8mb4", null);
				toolbarFaqHelp.setTitle("Faq");
			} else if (type.equals("help")) {
				faqHelpWebView.loadData(helpFaqmodel.getHelp(), "text/html; charset=utf8mb4", null);
				toolbarFaqHelp.setTitle("Help");
			}
		}
		return rootView;
	}



	@Override
	public void onDestroyView() {
		super.onDestroyView();
		rq.stop();
	}

}
