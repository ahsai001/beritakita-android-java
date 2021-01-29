package com.ahsailabs.beritakita.ui.home.models;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class NewsListResponse {

	@SerializedName("data")
	private List<News> data;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private int status;

	public List<News> getData(){
		return data;
	}

	public String getMessage(){
		return message;
	}

	public int getStatus(){
		return status;
	}
}