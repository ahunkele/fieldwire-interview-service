package com.fieldwire.lambda.floorplans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.model.GatewayResponse;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fieldwire.floorplans.models.FloorplanRequest;

public class AddFloorplanFunctionHandler implements RequestHandler<FloorplanRequest, GatewayResponse> {

	private DynamoDB dynamoDb;
	private AmazonS3 amazonS3;
	private static final String DYNAMODB_TABLE_NAME = "Floorplan";
	private static final String BUCKET_NAME = "images_fieldwire";
	private static final Regions REGION = Regions.US_EAST_2;
	private String objKey = "";
	PutObjectResult resultObject;
	
    @Override
    public GatewayResponse handleRequest(FloorplanRequest request, Context context) {
        this.initDynamoDbAndS3Client();
        File file = new File(request.getOriginalImageURI());
        objKey += request.getId() + "/ORIGINAL/" + file.getName();
        
        System.out.println("THIS IS THE KEY " + objKey);
		resultObject =  amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, objKey, file));
	
        URL resourceURL = amazonS3.getUrl(BUCKET_NAME, objKey);
        persistData(request, resourceURL.toString());
       
    	GatewayResponse response = new GatewayResponse();
    	response.setStatusCode("200 ok");
   		response.addResponseParametersEntry("Project_id", request.getId());

        return response;
    }
    
	private PutItemOutcome persistData(FloorplanRequest request, String originalUrl) {
		  Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME);
		  PutItemOutcome outcome = table.putItem(new PutItemSpec().withItem(
		    new Item().withString("floorplan_id", request.getId())
		              .withString("project_id", request.getProject_id())
		              .withString("name", request.getName())
		              .withString("original_url", originalUrl)));
		
		  return outcome;
	}
	
	private void initDynamoDbAndS3Client() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				   .withRegion(REGION)
				   .build();
		
		this.amazonS3 = AmazonS3ClientBuilder.standard()
				.withRegion(REGION)
				.build();
		this.dynamoDb = new DynamoDB(client);
	}

}
