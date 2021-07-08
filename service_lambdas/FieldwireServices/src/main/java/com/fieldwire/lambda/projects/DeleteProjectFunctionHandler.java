package com.fieldwire.lambda.projects;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.model.GatewayResponse;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fieldwire.projects.models.Project;

public class DeleteProjectFunctionHandler implements RequestHandler<Project, DeleteItemResult> {

	private DynamoDB dynamoDb;
	private static final String DYNAMODB_TABLE_NAME = "Project"; 
	private static final Regions REGION = Regions.US_EAST_2;
	
    @Override
    public DeleteItemResult handleRequest(Project request, Context context) {
        context.getLogger().log("Input: " + request);
        this.initDynamoDbClient();
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME);
        
        DeleteItemOutcome outcome = table.deleteItem(new DeleteItemSpec().withPrimaryKey("project_id", request.getId()));
        
        return outcome.getDeleteItemResult();
    }
    
	private void initDynamoDbClient() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				   .withRegion(REGION)
				   .build();
		this.dynamoDb = new DynamoDB(client);
	}
}
