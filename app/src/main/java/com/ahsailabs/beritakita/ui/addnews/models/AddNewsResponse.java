package com.ahsailabs.beritakita.ui.addnews.models;

import com.google.gson.annotations.SerializedName;

public class AddNewsResponse{

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private int status;

	public String getMessage(){
		return message;
	}

	public int getStatus(){
		return status;
	}
}