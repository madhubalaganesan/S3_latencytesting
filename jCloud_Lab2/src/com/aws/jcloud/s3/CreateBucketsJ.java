package com.aws.jcloud.s3;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;

public class CreateBucketsJ {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//credentials.properties file has the keys to authenticate to aws, region and buckets names which is accessed to create buckets
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("credentials.properties");
		// load a properties file
		prop.load(input);
		//fetching the access and secret access keys from the properties file
		String identity = prop.getProperty("accessKey");
		String credential = prop.getProperty("secretAccessKey");
		String bucketName, region;

		//Instantiating BlobStoreContext class to authenticate
		BlobStoreContext context = ContextBuilder.newBuilder("aws-s3")
				.credentials(identity, credential)
				.buildView(BlobStoreContext.class);
		
		//Instantiating Blobstore class as a Service Client to S3 services through jClouds
		BlobStore blobStore = context.getBlobStore();

		for(int i = 1; i <= 3; i++){
			//fetching bucket and region names from properties file
			bucketName = prop.getProperty("bucket"+i);
			region = prop.getProperty("region"+i);

			//setting the region in which the bucket has to be created
			LocationBuilder lb = new LocationBuilder();
			lb.id(region)
			.scope(LocationScope.REGION)
			.description("bucket in"+region);
			
			//createContainerInLocation takes location & bucketname as parameters and returns a boolean
			boolean created = blobStore.createContainerInLocation(lb.build(), bucketName);
			if (!created) {
				//in case the bucket is not created
				if (blobStore.containerExists(bucketName)) {
					throw new Exception("Container already exists: " + bucketName);
				}
				throw new Exception("Could not create container: " + bucketName);
			}		
			else{
				System.out.println("Bucket created : "+bucketName);
			}

		}
		context.close();
	}

}
