package com.appfibre.lifebeam.utils;

/**
 * This class maintains the session of the App.
 * @author Angel Abellanosa
 *
 */
public class Session {

	private static String userName;
	private static String userPassword;
	private static String userFamilyAccount;
	private static String userPasscode;
	private static String sessionId;
	private static String sessionFamily;
	
	
	/**
	 * @return the userFamilyAccount
	 */
	public static String getUserFamilyAccount() {
		return userFamilyAccount;
	}
	/**
	 * @param userFamilyAccount the userFamilyAccount to set
	 */
	public static void setUserFamilyAccount(String userFamilyAccount) {
		Session.userFamilyAccount = userFamilyAccount;
	}
	/**
	 * @return the userPasscode
	 */
	public static String getUserPasscode() {
		return userPasscode;
	}
	/**
	 * @param userPasscode the userPasscode to set
	 */
	public static void setUserPasscode(String userPasscode) {
		Session.userPasscode = userPasscode;
	}

	/**
	 * @return the sessionFamily
	 */
	public static String getSessionFamily() {
		return sessionFamily;
	}
	/**
	 * @param sessionFamily the sessionFamily to set
	 */
	public static void setSessionFamily(String sessionFamily) {
		Session.sessionFamily = sessionFamily;
	}
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
	
	/**
	 * @param sessionId the sessionId to set
	 */
	
	public static void reset() {
		Session.setSessionId("");
		Session.setUserName("");
		Session.setUserPassword("");
		Session.setSessionFamily("");
	}
	
}