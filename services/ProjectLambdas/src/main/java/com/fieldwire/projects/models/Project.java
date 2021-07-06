package com.fieldwire.projects.models;

import java.util.List;
import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "projects")
public class Project implements Serializable {

	private static final long serialVersionUID = -4351248338380341916L;
	private String id;
	private String name;
	private List<Floorplan> floorplans;
	
	public Project() {}
	
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

}
