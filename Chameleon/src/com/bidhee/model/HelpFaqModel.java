package com.bidhee.model;

import org.json.JSONObject;

import com.bidhee.chameleon.global.Define;

public class HelpFaqModel {
	
	public int				faq_help_id;
	public String 			faq;
	public String 			help;
	public String			created_at;
	
	public HelpFaqModel() {
		faq_help_id = -1;
		faq = "";
		help = "";
		created_at = "";
		
	}
	
	public HelpFaqModel(String faq, String help) {
		this();
		this.faq = faq;
		this.help = help;		
	}
	
	public HelpFaqModel(JSONObject obj){
		this();
		
		if (obj == null) return;
		
		faq_help_id = obj.optInt(Define.TAG_FH_FAQ_HELP_ID, -1);
		faq = obj.optString(Define.TAG_FH_FAQ, "");
		help = obj.optString(Define.TAG_FH_HELP, "");
		created_at = obj.optString(Define.TAG_FH_CREATED_AT, "");
	}

	public String getFaq() {
		return faq;
	}

	public void setFaq(String faq) {
		this.faq = faq;
	}

	public String getHelp() {
		return help;
	}

	public void setHelp(String help) {
		this.help = help;
	}

}
