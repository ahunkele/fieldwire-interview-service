package com.fieldwire.lambda.projects;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fieldwire.projects.models.Project;

/**
 * Update Project AWS lambda.
 * @author andyhunkele
 *
 */
public class UpdateProjectFunctionHandler implements RequestHandler<Project, UpdateItemResult> {

	private DynamoDB dynamoDb;
	private static final String DYNAMODB_TABLE_NAME = "Project"; 
	private static final Regions REGION = Regions.US_EAST_2;
	
    @Override
    public UpdateItemResult handleRequest(Project request, Context context) {
        this.initDynamoDbClient();
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME);
        
        UpdateItemOutcome outcome = table.updateItem(new UpdateItemSpec().withPrimaryKey("project_id", request.getId())
        						.withAttributeUpdate(new AttributeUpdate("name").put(request.getName()))
        						.withAttributeUpdate(new AttributeUpdate("floorplans").put(request.getFloorplans())));

        return outcome.getUpdateItemResult();
    }
    
    /**
     * init the dynamodb client.
     */
	private void initDynamoDbClient() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				   .withRegion(REGION)
				   .build();
		this.dynamoDb = new DynamoDB(client);
	}

}
