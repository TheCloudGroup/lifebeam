package com.appfibre.lifebeam.utils;

import java.util.Date;

public class MyImageItem {
	
	private String Id;
	private String URL;
	private String message;
	private String owner;
	private String family;
	private String date;
	private String time;
	private int messagecount;
	private int liked;
	
	/**
	 * 
	 * @param Id
	 * @param URL
	 * @param message
	 * @param family
	 * @param date
	 * @param liked
	 * 
	 * @author Angel Abellanosa Jr
	 */
	public MyImageItem(String Id, String URL, String message, String owner, String family, 
			String date, String time, int messagecount, int liked) {
		this.Id = Id;
		this.URL = URL;
		this.message = message;
		this.owner = owner;
		this.family = family;
		this.date = date;
		this.time = time;
		this.messagecount = messagecount;
		this.liked = liked;
		
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return Id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		Id = id;
	}

	/**
	 * @return the uRL
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * @param uRL the uRL to set
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the family
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * @param family the family to set
	 */
	public void setFamily(String family) {
		this.family = family;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the liked
	 */
	public int getLiked() {
		return liked;
	}

	/**
	 * @param liked the liked to set
	 */
	public void setLiked(int liked) {
		this.liked = liked;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * @return the messagecount
	 */
	public int getMessagecount() {
		return messagecount;
	}

	/**
	 * @param messagecount the messagecount to set
	 */
	public void setMessagecount(int messagecount) {
		this.messagecount = messagecount;
	}

	
}
