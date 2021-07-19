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
import javax.ws.rs.BadRequestException;

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
import com.fieldwire.models.floorplans.Floorplan;
import com.fieldwire.models.floorplans.FloorplanRequest;
import com.fieldwire.lambda.projects.GetProjectFunctionHandler;
import com.fieldwire.lambda.projects.UpdateProjectFunctionHandler;
import com.fieldwire.models.projects.Project;

/**
 * Add Floorplan AWS Handler.
 * @author andyhunkele
 *
 */
public class AddFloorplanFunctionHandler implements RequestHandler<FloorplanRequest, Floorplan> {

	private DynamoDB dynamoDb;
	private AmazonS3 amazonS3;
	private enum ImageSizes { ORIGINAL, THUMB, LARGE }
	private static final String S3_URL_PREFIX = "s3://";
	private static final String DYNAMODB_TABLE_NAME = "Floorplan";
	private static final String BUCKET_NAME = "uploaded-images-s3";
	private static final Regions REGION = Regions.US_EAST_2;

	@Override
	public Floorplan handleRequest(FloorplanRequest request, Context context) {
		validateRequest(request);	
		this.initDynamoDbAndS3Client();

		InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(request.getOriginalImage()));
		ObjectMetadata isMeta = new ObjectMetadata();
	    String mimeType = null;
	    String fileExtension = null;
	    try {
			// Determine the image extension type
	    	isMeta.setContentLength(is.available());
	        mimeType = URLConnection.guessContentTypeFromStream(is);
	        String delimiter="[/]";
	        String[] tokens = mimeType.split(delimiter);
	        fileExtension = tokens[1];
	    } catch (IOException ioException){
			context.getLogger().log("Input stream failed" + ioException);
			throw new BadRequestException("The provided image type is not supported");
		}
		
		PutObjectRequest putRequest =
			new PutObjectRequest(BUCKET_NAME, getS3Path(request.getId(), ImageSizes.ORIGINAL, fileExtension), is, isMeta);
		amazonS3.putObject(putRequest);
		
		persistData(request, request.getId(), fileExtension);
		Floorplan floorplanResonse = new Floorplan();
		floorplanResonse.setId(request.getId());
		floorplanResonse.setName(request.getName());
		floorplanResonse.setProject_id(request.getProject_id());
		floorplanResonse.setOriginal_url(getS3Path(request.getId(), ImageSizes.ORIGINAL, fileExtension));
		floorplanResonse.setThumb_url(getS3Path(request.getId(), ImageSizes.THUMBNAIL, fileExtension));
		floorplanResonse.setLarge_url(getS3Path(request.getId(), ImageSizes.LARGE, fileExtension));
		
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
		              .withString("floorplanImage", getFileName(floorplanUID, ImageSizes.ORIGINAL, fileType))
		              .withString("floorplanThumbnail", getFileName(floorplanUID, ImageSizes.THUMBNAIL, fileType))
		              .withString("floorplanLarge", getFileName(floorplanUID, ImageSizes.LARGE, fileType)));

		  return outcome;
	}

	/**
	 * returns the s3 path s3://bucket/id/type.ext
	 */
	private void getS3Path(String id, String fileExtension, ImageSizes imageSize) {
		return String.format("%s%s/%s/%s.%s", S3_URL_PREFIX, BUCKET_NAME, id, imageSize.name().toLowerCase(), fileExtension);
	}

	/**
	 * returns the path/name of the image file
	 */
	private void getFileName(String floorplanUID, String fileExtension, ImageSizes imageSize) {
		return String.format("%s/%s.%s", floorplanUID, imageSize.name().toLowerCase(), fileExtension);
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

	private void validateRequest(FloorplanRequest request) {
		if (request.getId() == null || String.isEmpty(request.getId())
			|| request.getName() == null || String.isEmpty(reuqest.getName())
			|| request.getProject_id() == null || String.isEmpty(request.getProject_id())) {
				throw new BadRequestException("Request parameters must be present to add a floorplan");
			}
	}
}
