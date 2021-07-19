package com.fieldwire.lambda.projects;

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
import com.fieldwire.models.projects.Project;

/**
 * Add Project AWS Lambda.
 * @author andyhunkele
 *
 */
public class AddProjectFunctionHandler implements RequestHandler<Project, GatewayResponse> {

	private DynamoDB dynamoDb;
	private static final String DYNAMODB_TABLE_NAME = "Project"; 
	private static final Regions REGION = Regions.US_EAST_2;
	
	@Override
	public GatewayResponse handleRequest(Project request, Context context) {
		this.initDynamoDbClient();
		
		persistData(request);
		
		GatewayResponse response = new GatewayResponse();
		response.setStatusCode("200 ok");
		response.addResponseParametersEntry("Project_id", request.getId());
		
		return response;
	}
	
	private PutItemOutcome persistData(Project request) {
		  Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME);
		  Item item = new Item().withString("project_id", request.getId())
	              .withString("name", request.getName());
	              
		  if(request.getFloorplans() != null)
		  {
			  item.withStringSet("floorplans", request.getFloorplans());
		  }
			 
		  PutItemOutcome outcome = table.putItem(new PutItemSpec().withItem(item));
		
		  return outcome;
	}
	
	private void initDynamoDbClient() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				   .withRegion(REGION)
				   .build();
		this.dynamoDb = new DynamoDB(client);
	}
}
