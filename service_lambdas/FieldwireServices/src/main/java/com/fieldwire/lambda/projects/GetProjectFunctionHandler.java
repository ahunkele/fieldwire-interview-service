package com.fieldwire.lambda.projects;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fieldwire.projects.models.Project;

/**
 * Get Project AWS Lambda.
 * @author andyhunkele
 *
 */
public class GetProjectFunctionHandler implements RequestHandler<Project, Project> {

	private DynamoDBMapper mapper;
	private static final Regions REGION = Regions.US_EAST_2;
	
    @Override
    public Project handleRequest(Project request, Context context) {
        this.initDynamoDbMapper();
        Project project = mapper.load(Project.class, request.getId());

        return project;
    }
    
	private void initDynamoDbMapper() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
				   .withRegion(REGION)
				   .build();
		this.mapper = new DynamoDBMapper(client);
	}
}
