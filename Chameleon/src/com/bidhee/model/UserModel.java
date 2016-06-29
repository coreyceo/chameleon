package com.bidhee.model;

public class UserModel {
	
	private long userId;
	private String userName;
	private String userImage;
	private String userEmail;
	private String userGender;
	private String userDob;
	
	private String secretKey;
	private String socialID;
	private String socialMediaType;
		
	public UserModel() {
	
	}
	
	public UserModel(long userId, String userName, String userImage, String userEmail, String userGender, String secretKey, String socialId, String socialMediaType, String userDob) {
		this.userId = userId;
		this.userEmail = userEmail;
		this.secretKey = secretKey;
		this.userImage = userImage;
		this.userName = userName;
		this.userGender = userGender;
		this.socialID = socialId;
		this.socialMediaType = socialMediaType;
		this.userDob = userDob;
	}		

	public String getUserDob() {
		return userDob;
	}


	public void setUserDob(String userDob) {
		this.userDob = userDob;
	}


	public String getSocialID() {
		return socialID;
	}


	public void setSocialID(String socialID) {
		this.socialID = socialID;
	}


	public String getSocialMediaType() {
		return socialMediaType;
	}


	public void setSocialMediaType(String socialMediaType) {
		this.socialMediaType = socialMediaType;
	}


	public long getUserId() {
		return userId;
	}


	public void setUserId(long userId) {
		this.userId = userId;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getUserImage() {
		return userImage;
	}


	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}


	public String getUserEmail() {
		return userEmail;
	}


	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}


	public String getUserGender() {
		return userGender;
	}


	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}


	public String getSecretKey() {
		return secretKey;
	}


	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	

}
