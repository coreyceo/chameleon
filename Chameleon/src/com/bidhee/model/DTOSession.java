package com.bidhee.model;


public class DTOSession {
	
	/*
	 * Session Details
	 */
	private volatile static DTOSession uniqueInstance;
	private long sessionId;
	private long sessionUserId;
	private boolean isSessionActive;
	private String sessionCreatedAt;

	/*
	 * Singleton
	 */
	private DTOSession() {}
 
	/*
	 * Singleton getInstance()
	 */
	public static DTOSession getInstance() {
		if (uniqueInstance == null) {
			synchronized (DTOSession.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new DTOSession();
				}
			}
		}
		return uniqueInstance;
	}

	/*
	 * Getters
	 */
	public long getSessionId() {
		return sessionId;
	}
	public long getSessionUserId() {
		return sessionUserId;
	}
	public boolean isSessionActive() {
		return isSessionActive;
	}
	public String getSessionCreatedAt() {
		return sessionCreatedAt;
	}
	
	
	/*
	 * Setters
	 */
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	public void setSessionUserId(long sessionUserId) {
		this.sessionUserId = sessionUserId;
	}
	public void setSessionActive(boolean isSessionActive) {
		this.isSessionActive = isSessionActive;
	}
	public void setSessionCreatedAt(String sessionCreatedAt) {
		this.sessionCreatedAt = sessionCreatedAt;
	}
}
