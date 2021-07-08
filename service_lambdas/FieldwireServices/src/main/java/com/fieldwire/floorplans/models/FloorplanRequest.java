package com.fieldwire.floorplans.models;

import java.io.Serializable;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

public class FloorplanRequest implements Serializable {

	private static final long serialVersionUID = 8755556817933177628L;
	private String id;
	private String project_id;
	private String name;
	private String originalImage;
	
	public FloorplanRequest() {
		this.id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriginalImage() {
		return originalImage;
	}

	public void setOriginalImage(String originalImage) {
		this.originalImage = originalImage;
	}



}
