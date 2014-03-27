package com.appfibre.lifebeam.utils;

import android.content.Context;

/**
 * This class maintains the session of the App.
 * 
 * @author Angel Abellanosa
 * 
 */
public class Session {
	private final String USERNAME_KEY = "username";
	private final String USERPASSWORD_KEY = "password";
	private final String USER_FAMILY_ACCOUNT_KEY = "user_family_account";
	private final String USER_PASSCODE_KEY = "user_passcode";
	private final String SESSION_ID_KEY = "session_id";
	private final String SESSION_FAMILY_KEY = "session_family";
	private final String SESSION_FAMILY_OBJ_ID_KEY = "session_family_obj_id";
	private static final Session INSTANCE = new Session();

	private Session() {
	}

	public static Session getInstance() {
		return INSTANCE;
	}

	/**
	 * @return the userFamilyAccount
	 */
	public String getUserFamilyAccount(Context context) {
		return SharedPrefMgr.getString(context, USER_FAMILY_ACCOUNT_KEY);
	}

	/**
	 * @param userFamilyAccount
	 *            the userFamilyAccount to set
	 */
	public void setUserFamilyAccount(Context context, String userFamilyAccount) {
		SharedPrefMgr.setString(context, USER_FAMILY_ACCOUNT_KEY, userFamilyAccount);
	}

	/**
	 * @return the userPasscode
	 */
	public String getUserPasscode(Context context) {
		return SharedPrefMgr.getString(context, USER_PASSCODE_KEY);
	}

	/**
	 * @param userPasscode
	 *            the userPasscode to set
	 */
	public void setUserPasscode(Context context, String userPasscode) {
		SharedPrefMgr.setString(context, USER_PASSCODE_KEY, userPasscode);
	}

	/**
	 * @return the sessionFamily
	 */
	public String getSessionFamily(Context context) {
		return SharedPrefMgr.getString(context, SESSION_FAMILY_KEY);
	}

	/**
	 * @param sessionFamily
	 *            the sessionFamily to set
	 */
	public void setSessionFamily(Context context, String sessionFamily) {
		SharedPrefMgr.setString(context, SESSION_FAMILY_KEY, sessionFamily);
	}

	/**
	 * @return the userName
	 */
	public String getUserName(Context context) {
		return SharedPrefMgr.getString(context, USERNAME_KEY);
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(Context context, String userName) {
		SharedPrefMgr.setString(context, USERNAME_KEY, userName);
	}

	/**
	 * @return the userPassword
	 */
	public String getUserPassword(Context context) {
		return SharedPrefMgr.getString(context, USERPASSWORD_KEY);
	}

	/**
	 * @param userPassword
	 *            the userPassword to set
	 */
	public void setUserPassword(Context context, String userPassword) {
		SharedPrefMgr.setString(context, USERPASSWORD_KEY, userPassword);
	}

	/**
	 * @return the sessionId
	 */
	public String getSessionId(Context context) {
		return SharedPrefMgr.getString(context, SESSION_ID_KEY);
	}

	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionId(Context context, String sessionId) {
		SharedPrefMgr.setString(context, SESSION_ID_KEY, sessionId);
	}
	
	public void setFamilyObjectId(Context context, String objectId){
		SharedPrefMgr.setString(context, SESSION_FAMILY_OBJ_ID_KEY, objectId);
	}
	
	public String getFamilyObjectId(Context context){
		return SharedPrefMgr.getString(context, SESSION_FAMILY_OBJ_ID_KEY);
	}

	/**
	 * Clear all session data
	 * @param context
	 */
	public void reset(Context context) {
		SharedPrefMgr.removeData(context, USERNAME_KEY);
		SharedPrefMgr.removeData(context, USERPASSWORD_KEY);
		SharedPrefMgr.removeData(context, USER_FAMILY_ACCOUNT_KEY);
		SharedPrefMgr.removeData(context, USER_PASSCODE_KEY);
		SharedPrefMgr.removeData(context, SESSION_ID_KEY);
		SharedPrefMgr.removeData(context, SESSION_FAMILY_KEY);
		SharedPrefMgr.removeData(context, SESSION_FAMILY_OBJ_ID_KEY);	
	}
}