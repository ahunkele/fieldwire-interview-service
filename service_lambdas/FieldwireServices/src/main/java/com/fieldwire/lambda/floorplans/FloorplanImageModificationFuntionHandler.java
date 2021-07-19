package com.fieldwire.lambda.floorplans;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import javax.imageio.ImageIO;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fieldwire.models.floorplans.Floorplan;
import com.fieldwire.models.floorplans.FloorplanRequest;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class FloorplanImageModificationFuntionHandler implements RequestHandler<DynamodbEvent, String> {

	private AmazonS3 amazonS3;
	private static final String BUCKET_NAME = "original-images-s3";
	private static final Regions REGION = Regions.US_EAST_2;
	private static final String EVENT_NAME = "INSERT";
	
	private static final int THUMB_WIDTH = 200;
	private static final int THUMB_HEIGHT = 200;
	private static final int LARGE_WIDTH = 2000;
	private static final int LARGE_HEIGHT = 2000;
	
    @Override
    public String handleRequest(DynamodbEvent dynamoEvent, Context context) {
    	this.initS3Client();
    	if(!dynamoEvent.getRecords().get(0).getEventName().toUpperCase().equals(EVENT_NAME))
    	{
    		return "";
    	}
    	Map<String, AttributeValue> keys = dynamoEvent.getRecords().get(0).getDynamodb().getKeys();

    	String projectId = keys.get("project_id").getS();
    	String floorplanId = keys.get("floorplan_id").getS();
    	
    	FloorplanRequest floorplan = new FloorplanRequest();
    	floorplan.setId(floorplanId);
    	floorplan.setProject_id(projectId);
    	GetFloorplanFunctionHandler floorplanHandler = new GetFloorplanFunctionHandler();
    	Floorplan actualFloorplan = floorplanHandler.handleRequest(floorplan, context);
    	
    	context.getLogger().log("ACTUAL URL = " + actualFloorplan.getOriginal_url()); 
    	
    	String key = actualFloorplan.getOriginal_url().substring(0, actualFloorplan.getOriginal_url().indexOf("/") + 1);
    	String extension = actualFloorplan.getOriginal_url().substring(actualFloorplan.getOriginal_url().indexOf(".") + 1);
    	
    	
    	S3Object image = amazonS3.getObject(new GetObjectRequest(BUCKET_NAME, actualFloorplan.getOriginal_url()));
    	
    	try {
			BufferedImage origImage = ImageIO.read(image.getObjectContent());
			context.getLogger().log("IMAGE " + image.getKey()   + " ORIG BI = " + origImage.getHeight());
			int type = origImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : origImage.getType();
			
			BufferedImage thumb = resizeImage(origImage, type, THUMB_WIDTH, THUMB_HEIGHT);
			BufferedImage large = resizeImage(origImage, type, LARGE_WIDTH, LARGE_HEIGHT);
			ByteArrayOutputStream thumb_os = new ByteArrayOutputStream();
			ByteArrayOutputStream large_os = new ByteArrayOutputStream();
			ImageIO.write(thumb, extension, thumb_os); 
			ImageIO.write(large, extension, large_os); 
			
			InputStream thumb_is = new ByteArrayInputStream(thumb_os.toByteArray());
			InputStream large_is = new ByteArrayInputStream(large_os.toByteArray());
			
			ObjectMetadata thumbMeta = new ObjectMetadata();
			ObjectMetadata largeMeta = new ObjectMetadata();
			
			thumbMeta.setContentLength(thumb_is.available());
			largeMeta.setContentLength(large_is.available());
			
			
			amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, key + "thumb." + extension, thumb_is, thumbMeta));
			amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, key + "larg."+ extension, large_is, largeMeta));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return "recieved record at " + key;
    }
    
    /**
	 * Initiate The {@link AmazonS3} client.
	 */
	private void initS3Client() {
		this.amazonS3 = AmazonS3ClientBuilder.standard()
				.withRegion(REGION)
				.build();
	}
	
	private BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
	    BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
	    Graphics2D g = resizedImage.createGraphics();
	    g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
	    g.dispose();

	    return resizedImage;
	}
}
