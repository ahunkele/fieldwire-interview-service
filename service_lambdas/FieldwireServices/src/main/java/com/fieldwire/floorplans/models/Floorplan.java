package com.fieldwire.floorplans.models;

import java.io.Serializable;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Floorplan")
public class Floorplan implements Serializable {

	private static final long serialVersionUID = 1335439899398315827L;
	private String id;
	private String project_id;
	private String name;
	private String original_url;
	private String thumb_url;
	private String large_url;
	
	public Floorplan() {
	}

	@DynamoDBRangeKey(attributeName="floorplan_id")
	@DynamoDBAttribute(attributeName="floorplan_id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@DynamoDBHashKey(attributeName="project_id")
	@DynamoDBAttribute(attributeName="project_id")
	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	@DynamoDBAttribute(attributeName="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@DynamoDBAttribute(attributeName="floorplanImage")
	public String getOriginal_url() {
		return original_url;
	}

	public void setOriginal_url(String original_url) {
		this.original_url = original_url;
	}

	@DynamoDBAttribute(attributeName="floorplanThumbnail")
	public String getThumb_url() {
		return thumb_url;
	}

	public void setThumb_url(String thumb_url) {
		this.thumb_url = thumb_url;
	}

	@DynamoDBAttribute(attributeName="floorplanLarge")
	public String getLarge_url() {
		return large_url;
	}

	public void setLarge_url(String large_url) {
		this.large_url = large_url;
	}

}
