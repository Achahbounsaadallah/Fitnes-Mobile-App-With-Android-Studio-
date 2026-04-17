package com.example.projectmodule.data.model;

public class Category {

	private final String id;
	private final String title;
	private final int imageResId;

	public Category(String id, String title, int imageResId) {
		this.id = id;
		this.title = title;
		this.imageResId = imageResId;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getImageResId() {
		return imageResId;
	}
}
