package com.fieldwire.lambda.floorplans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amazonaws.regions.Regions;
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
import com.fieldwire.floorplans.models.Floorplan;
import com.fieldwire.floorplans.models.FloorplanRequest;
import com.fieldwire.lambda.projects.GetProjectFunctionHandler;
import com.fieldwire.lambda.projects.UpdateProjectFunctionHandler;
import com.fieldwire.projects.models.Project;

/**
 * Add Floorplan AWS Handler.
 * @author andyhunkele
 *
 */
public class AddFloorplanFunctionHandler implements RequestHandler<FloorplanRequest, Floorplan> {

	private DynamoDB dynamoDb;
	private AmazonS3 amazonS3;
	private static final String S3_URL = "s3://";
	private static final String DYNAMODB_TABLE_NAME = "Floorplan";
	private static final String BUCKET_NAME = "original-images-s3";
	private static final Regions REGION = Regions.US_EAST_2;
	private String objKey = "";

	@Override
	public Floorplan handleRequest(FloorplanRequest request, Context context) {
		this.initDynamoDbAndS3Client();
		context.getLogger().log("ID: " + request.getId());
		context.getLogger().log("PROJECT_ID: " + request.getProject_id());
		context.getLogger().log("NAME: " + request.getName());
		context.getLogger().log("PROHO " + request.getOriginalImage());

		objKey = request.getId();
		InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(request.getOriginalImage()));
		ObjectMetadata isMeta = new ObjectMetadata();
		
	    //Find out image type
	    String mimeType = null;
	    String fileExtension = null;
	    try {
	    	isMeta.setContentLength(is.available());
	        mimeType = URLConnection.guessContentTypeFromStream(is); //mimeType is something like "image/jpeg"
	        String delimiter="[/]";
	        String[] tokens = mimeType.split(delimiter);
	        fileExtension = tokens[1];
	    } catch (IOException ioException){}
		
	    objKey += "/original." + fileExtension;
	    
		PutObjectRequest putRequest = new PutObjectRequest(BUCKET_NAME, objKey, is, isMeta);
		amazonS3.putObject(putRequest);
		
		persistData(request, request.getId(), fileExtension);
		Floorplan floorplanResonse = new Floorplan();
		floorplanResonse.setId(request.getId());
		floorplanResonse.setName(request.getName());
		floorplanResonse.setProject_id(request.getProject_id());
		floorplanResonse.setOriginal_url(S3_URL + BUCKET_NAME + "/" + request.getId() + "/original." + fileExtension);
		floorplanResonse.setThumb_url(S3_URL + BUCKET_NAME + "/" +  request.getId() + "/thumb." + fileExtension);
		floorplanResonse.setLarge_url(S3_URL + BUCKET_NAME + "/" + request.getId() + "/large." + fileExtension);
		
		Project getProjectRequest = new Project();
		getProjectRequest.setId(request.getProject_id());
		GetProjectFunctionHandler getProject = new GetProjectFunctionHandler();
		Project getProjectResponse = getProject.handleRequest(getProjectRequest, context);
				
		Project updateProjectRequest = new Project();
		updateProjectRequest.setId(request.getProject_id());
		Set<String> updateFloorplansInProjects = getProjectResponse.getFloorplans();
		
		if(updateFloorplansInProjects == null)
			updateFloorplansInProjects = new HashSet<String>();
		
		updateFloorplansInProjects.add(request.getId());
		updateProjectRequest.setFloorplans(updateFloorplansInProjects);
		
		UpdateProjectFunctionHandler updateProject = new UpdateProjectFunctionHandler();
		updateProject.handleRequest(updateProjectRequest, context);
		
		return floorplanResonse;
	}
	
	/**
	 * 
	 * @param request
	 * @param floorplanUID
	 * @param fileType
	 * @return
	 */
	private PutItemOutcome persistData(FloorplanRequest request, String floorplanUID, String fileType) {
		  Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME);
		  PutItemOutcome outcome = table.putItem(new PutItemSpec().withItem(
		    new Item().withString("floorplan_id", request.getId())
		    		  .withString("project_id", request.getProject_id())	
		              .withString("name", request.getName())
		              .withString("floorplanImage", floorplanUID + "/original." + fileType)
		              .withString("floorplanThumbnail", floorplanUID + "/thumbnail." + fileType)
		              .withString("floorplanLarge", floorplanUID + "/large." + fileType)));
		
		  return outcome;
	}
	
	/**
	 * Initiate The {@link DynamoDB} and {@link AmazonS3} clients.
	 */
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
