package com.example.projectmodule.data.model;

public class Exercise {

	private final String id;
	private final String categoryId;
	private final String name;
	private final String shortDescription;
	private final String detailDescription;
	private final int imageResId;

	public Exercise(String id, String categoryId, String name, String shortDescription, String detailDescription, int imageResId) {
		this.id = id;
		this.categoryId = categoryId;
		this.name = name;
		this.shortDescription = shortDescription;
		this.detailDescription = detailDescription;
		this.imageResId = imageResId;
	}

	public String getId() {
		return id;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public String getName() {
		return name;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public String getDetailDescription() {
		return detailDescription;
	}

	public int getImageResId() {
		return imageResId;
	}
}
