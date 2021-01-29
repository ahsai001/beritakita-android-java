package com.ahsailabs.beritakita.ui.home.models;

import com.google.gson.annotations.SerializedName;

public class News {
	@SerializedName("summary")
	private String summary;

	@SerializedName("photo")
	private String photo;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private String id;

	@SerializedName("title")
	private String title;

	@SerializedName("created_by")
	private String createdBy;

	public String getSummary(){
		return summary;
	}

	public String getPhoto(){
		return photo;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public String getCreatedBy(){
		return createdBy;
	}
}