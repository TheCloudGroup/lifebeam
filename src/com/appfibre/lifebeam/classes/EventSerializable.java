package com.appfibre.lifebeam.classes;

import java.io.Serializable;

/**
 * @author kimlambiguit
 *
 */
public class EventSerializable implements Serializable {
    private byte [] imageData;
    private String eventContent;
    private String family;
    
	public EventSerializable(){}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public String getEventContent() {
		return eventContent;
	}

	public void setEventContent(String eventContent) {
		this.eventContent = eventContent;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}
	
	
}
