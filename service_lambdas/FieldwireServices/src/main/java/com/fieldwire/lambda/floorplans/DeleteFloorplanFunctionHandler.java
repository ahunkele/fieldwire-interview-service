package com.fieldwire.lambda.floorplans;

import java.util.Set;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fieldwire.floorplans.models.Floorplan;
import com.fieldwire.lambda.projects.DeleteProjectFunctionHandler;
import com.fieldwire.lambda.projects.GetProjectFunctionHandler;
import com.fieldwire.lambda.projects.UpdateProjectFunctionHandler;
import com.fieldwire.projects.models.Project;

public class DeleteFloorplanFunctionHandler implements RequestHandler<Floorplan, DeleteItemResult> {
	
	private DynamoDB dynamoDb;
	private AmazonS3 amazonS3;
	private static final String DYNAMODB_TABLE_NAME = "Floorplan";
	private static final String BUCKET_NAME = "original-images-s3";
	private static final Regions REGION = Regions.US_EAST_2;
	
    @Override
    public DeleteItemResult handleRequest(Floorplan request, Context context) {
        this.initDynamoDbAndS3Client();
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME);
        
        DeleteItemOutcome outcome = table.deleteItem(new DeleteItemSpec().withPrimaryKey("project_id", request.getProject_id(), "floorplan_id", request.getId()));
        
        Project getProjectRequest = new Project();
		getProjectRequest.setId(request.getProject_id());
		GetProjectFunctionHandler getProject = new GetProjectFunctionHandler();
		Project getProjectResponse = getProject.handleRequest(getProjectRequest, context);
				

		Set<String> updateFloorplansInProjects = getProjectResponse.getFloorplans();
		updateFloorplansInProjects.remove(request.getId());
		
		if(updateFloorplansInProjects.size() > 0)
		{
			Project updateProjectRequest = new Project();
			updateProjectRequest.setId(request.getProject_id());
			UpdateProjectFunctionHandler updateProject = new UpdateProjectFunctionHandler();
			updateProjectRequest.setFloorplans(updateFloorplansInProjects);
			
			updateProject.handleRequest(updateProjectRequest, context);
		} 
		else
		{
			Project deleteProjectRequest = new Project();
			deleteProjectRequest.setId(request.getProject_id());
			DeleteProjectFunctionHandler deleteProject = new DeleteProjectFunctionHandler();
			
			deleteProject.handleRequest(deleteProjectRequest, context);
		}

		ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(BUCKET_NAME).withPrefix(request.getId());
		ListObjectsV2Result res = amazonS3.listObjectsV2(req);
		
		for(S3ObjectSummary s : res.getObjectSummaries())
		{
			context.getLogger().log("Attempthing to delete: " + s.getKey() + " from s3 bucket " + BUCKET_NAME);
			amazonS3.deleteObject(new DeleteObjectRequest(BUCKET_NAME, s.getKey()));
		}
		
        return outcome.getDeleteItemResult();
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
