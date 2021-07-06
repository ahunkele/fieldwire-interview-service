package com.fieldwire.projects.lambdas;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Client;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fieldwire.projects.models.Project;

public class GetProjectFunctionHandler implements RequestHandler<Project, String> {

	private DynamoDBMapper mapper;
	
	public GetProjectFunctionHandler() {
		AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
		mapper = new DynamoDBMapper(ddb);
	}
		
	@Override
	public String handleRequest(Project project, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("log something here");
		project = mapper.load(Project.class, project.getId());
		
		return null;
	}

}
