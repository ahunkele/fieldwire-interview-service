package com.fieldwire.floorplans.models;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "floorplans")
public class Floorplan implements Serializable {

	private static final long serialVersionUID = -8755556817933177628L;
	private String id;
	private String projectId;
	private String name;
	
	private Object original;
	private Object thumb;
	private Object large;
	
	public Floorplan() {}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getOriginal() {
		return original;
	}
	public void setOriginal(Object original) {
		this.original = original;
	}
	public Object getThumb() {
		return thumb;
	}
	public void setThumb(Object thumb) {
		this.thumb = thumb;
	}
	public Object getLarge() {
		return large;
	}
	public void setLarge(Object large) {
		this.large = large;
	}
}
