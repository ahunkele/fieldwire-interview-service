package com.fieldwire.floorplans.models;

import java.io.Serializable;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

public class Floorplan implements Serializable {

	private String id;
	private String project_id;
	private String name;
	private byte[] original;
	private byte[] thumb;
	private byte[] large;
	
	public Floorplan() {
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

	public byte[] getOriginal() {
		return original;
	}

	public void setOriginal(byte[] original) {
		this.original = original;
	}

	public byte[] getThumb() {
		return thumb;
	}

	public void setThumb(byte[] thumb) {
		this.thumb = thumb;
	}

	public byte[] getLarge() {
		return large;
	}

	public void setLarge(byte[] large) {
		this.large = large;
	}

}
