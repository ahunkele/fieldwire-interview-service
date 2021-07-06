package com.fieldwire.lambda.floorplans;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class FloorplanImageModificationFuntionHandler implements RequestHandler<S3Event, String> {

	private static final float THUMB_WIDTH = 200;
	private static final float THUMB_HEIGHT = 200;
	private static final float LARGE_WIDTH = 2000;
	private static final float LARGE_HEIGHT = 2000;
	private final String JPG_TYPE = (String) "jpg";
	private final String JPG_MIME = (String) "image/jpeg";
	private final String PNG_TYPE = (String) "png";
	
    @Override
    public String handleRequest(S3Event s3event, Context context) {
        try {
            S3EventNotificationRecord record = s3event.getRecords().get(0);
            String srcBucket = record.getS3().getBucket().getName();
            
            
        } finally { }
        // TODO: implement your handler
        return "Hello from Lambda!";
    }
}
