package com.appfibre.lifebeam.utils;

/**
 * This class maintains the session of the App.
 * @author Angel Abellanosa
 *
 */
public class Session {

	private static String userName;
	private static String userPassword;
	private static String sessionId;
	/**
	 * @return the userName
	 */
	public static String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public static void setUserName(String userName) {
		Session.userName = userName;
	}
	/**
	 * @return the userPassword
	 */
	public static String getUserPassword() {
		return userPassword;
	}
	/**
	 * @param userPassword the userPassword to set
	 */
	public static void setUserPassword(String userPassword) {
		Session.userPassword = userPassword;
	}
	/**
	 * @return the sessionId
	 */
	public static String getSessionId() {
		return sessionId;
	}
	/**
	 * @param sessionId the sessionId to set
	 */
	public static void setSessionId(String sessionId) {
		Session.sessionId = sessionId;
	}
	
	
}