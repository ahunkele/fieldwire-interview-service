package com.fieldwire.lambda.floorplans;

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
import com.fieldwire.floorplans.models.Floorplan;
import com.fieldwire.floorplans.models.FloorplanRequest;
import com.fieldwire.projects.models.Project;

/**
 * Update Floorplan AWS Lambda.
 * @author andyhunkele
 */
public class UpdateFloorplanFunctionHandler implements RequestHandler<FloorplanRequest, UpdateItemResult> {

	private DynamoDB dynamoDb;
	private static final String DYNAMODB_TABLE_NAME = "Floorplan"; 
	private static final Regions REGION = Regions.US_EAST_2;
	
    @Override
    public UpdateItemResult handleRequest(FloorplanRequest request, Context context) {
        this.initDynamoDbClient();
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME);
        UpdateItemOutcome outcome = null;

        if(request.getOriginalImage() != null) {
        	Floorplan fp = new Floorplan();
        	fp.setId(request.getId());
        	fp.setProject_id(request.getProject_id());
        	
        	DeleteFloorplanFunctionHandler deleteFloorplan = new DeleteFloorplanFunctionHandler();
        	deleteFloorplan.handleRequest(fp, context);
        	
        	AddFloorplanFunctionHandler newFloorplan = new AddFloorplanFunctionHandler();
        	newFloorplan.handleRequest(request, context);
        	
        	outcome = new UpdateItemOutcome(new UpdateItemResult());
        }
        else {
         outcome = table.updateItem(new UpdateItemSpec().withPrimaryKey("project_id", request.getProject_id())
        						.withAttributeUpdate(new AttributeUpdate("name").put(request.getName())));
        }
        
        return outcome.getUpdateItemResult();
    }
    
    /**
     * Init the dynamo db client.
     */
	private void initDynamoDbClient() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				   .withRegion(REGION)
				   .build();
		this.dynamoDb = new DynamoDB(client);
	}
}
