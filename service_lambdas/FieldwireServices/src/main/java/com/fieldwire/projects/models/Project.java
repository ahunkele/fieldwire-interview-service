package com.fieldwire.projects.models;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="Project")
public final class Project implements Serializable {
	
	private static final long serialVersionUID = 7971694693136431968L;
	private String id;
	private String name;
	private Set<String> floorplans;
	
	public Project() {
		this.id = UUID.randomUUID().toString();
	}

	@DynamoDBHashKey(attributeName="project_id")
	@DynamoDBAutoGeneratedKey
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@DynamoDBAttribute(attributeName = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@DynamoDBAttribute(attributeName = "floorplans")
	public Set<String> getFloorplans() {
		return floorplans;
	}

	public void setFloorplans(Set<String> floorplans) {
		this.floorplans = floorplans;
	}
}
