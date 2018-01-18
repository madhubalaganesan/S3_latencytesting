package com.aws.jcloud.s3;

import java.io.FileInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.domain.StorageType;

public class ListBucketsJ {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//credentials.properties file has the keys to authenticate to aws
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("credentials.properties");
		// load a properties file
		prop.load(input);
		
		//fetching the access and secret access keys from the properties file
		String identity = prop.getProperty("accessKey");
		String credential = prop.getProperty("secretAccessKey");
		
		//Instantiating BlobStoreContext class to authenticate		
		BlobStoreContext context = ContextBuilder.newBuilder("aws-s3")
				.credentials(identity, credential)
				.buildView(BlobStoreContext.class);

		//Instantiating Blobstore class as a Service Client to S3 services through jClouds
		BlobStore blobStore = context.getBlobStore();

		//list.size gives the number of buckets
		System.out.println("Total number of buckets : "+blobStore.list().size());
		String bucRegion = "ap-south-1";
		
		//resourceMd has the name of the buckets
		for (StorageMetadata resourceMd : blobStore.list()) {
			if (resourceMd.getType() == StorageType.CONTAINER || resourceMd.getType() == StorageType.FOLDER) {
				if(bucRegion.equals(resourceMd.getLocation().getId())){
					//to list buckets in a specific region
					System.out.println(" - " + resourceMd.getName());
				}
			}
		}		
		context.close();

	}

}
