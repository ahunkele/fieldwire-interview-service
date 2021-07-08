package com.fieldwire.lambda.floorplans;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fieldwire.floorplans.models.Floorplan;
import com.fieldwire.floorplans.models.FloorplanRequest;

/**
 * Get Floorplan AWS Handler.
 * @author andyhunkele
 *
 */
public class GetFloorplanFunctionHandler implements RequestHandler<FloorplanRequest, Floorplan> {

	private DynamoDBMapper mapper;
	private static final Regions REGION = Regions.US_EAST_2;
	
    @Override
    public Floorplan handleRequest(FloorplanRequest request, Context context) {
        this.initDynamoDbMapper();
        Floorplan floorplanResponse = mapper.load(Floorplan.class, request.getProject_id(), request.getId());

        return floorplanResponse;
    }
    
	private void initDynamoDbMapper() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				   .withRegion(REGION)
				   .build();
		this.mapper = new DynamoDBMapper(client);
	}
}
