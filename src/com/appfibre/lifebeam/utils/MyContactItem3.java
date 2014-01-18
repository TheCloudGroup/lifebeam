package com.appfibre.lifebeam.utils;

public class MyContactItem3 {
	private String contactName;
	private String contactNumber;
	private String contactId;
	private boolean selected;

	/**
	 * Holder for contact items
	 * 
	 * @param contactName
	 * @param contactNumber
	 * @param contactId
	 * 
	 * 
	 * @author Angel Abellanosa Jr
	 */
	public MyContactItem3(String contactName, String contactNumber, String contactId, boolean selected) {
		this.contactName = contactName;
		this.contactNumber = contactNumber;
		this.contactId = contactId;
		this.selected = selected;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * @return the contactId
	 */
	public String getContactId() {
		return contactId;
	}

	/**
	 * @param contactId the contactId to set
	 */
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	
	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * @return the contactNumber
	 */
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * @param contactNumber the contactNumber to set
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
}