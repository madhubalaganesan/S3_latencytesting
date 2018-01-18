package com.aws.jcloud.s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

public class UploadObjectsJ {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//credentials.properties file has the keys to authenticate to aws, region and buckets names 
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("credentials.properties");
		// load a properties file
		prop.load(input);
		//fetching the bucketname, access and secret access keys from the properties file
		String identity = prop.getProperty("accessKey");
		String credential = prop.getProperty("secretAccessKey");
		String bucketName = prop.getProperty("bucket3");

		//Instantiating BlobStoreContext class to authenticate
		BlobStoreContext context = ContextBuilder.newBuilder("aws-s3")
				.credentials(identity, credential)
				.buildView(BlobStoreContext.class);

		//Instantiating Blobstore class as a Service Client to S3 services through jClouds
		BlobStore blobStore = context.getBlobStore();

		//defining the file to upload and the name of the file
		File file = new File(args[0]);
		String key = file.getName();
		
		try{
		//ByteSource converts the file to ByteSource to add it as payload to the blob
		ByteSource payload = Files.asByteSource(file);
		Blob blob = blobStore.blobBuilder(key)
				.payload(payload)
				.contentLength(payload.size())
				.build();
		
		//putBlob method uploads the file to the bucket
		blobStore.putBlob(bucketName, blob);
		System.out.println(key+" uploaded to "+bucketName);
		}
		catch (Exception e) {
			// TODO: handle exception
			//catches exceptions
			System.err.println("Upload failed!");
		}

		context.close();

	}

}
