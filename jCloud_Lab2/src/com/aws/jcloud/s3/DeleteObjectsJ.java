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
import org.jclouds.s3.domain.DeleteResult;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

public class DeleteObjectsJ {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//credentials.properties file has the keys to authenticate to aws, region and buckets names which is accessed to delete objects
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("credentials.properties");
		// load a properties file
		prop.load(input);

		//fetching the access and secret access keys & bucketname from the properties file
		String identity = prop.getProperty("accessKey");
		String credential = prop.getProperty("secretAccessKey");
		String bucketName = prop.getProperty("bucket3");

		//Instantiating BlobStoreContext class to authenticate
		BlobStoreContext context = ContextBuilder.newBuilder("aws-s3")
				.credentials(identity, credential)
				.buildView(BlobStoreContext.class);
		
		//Instantiating Blobstore class as a Service Client to S3 services through jClouds
		BlobStore blobStore = context.getBlobStore();
		//defining the name of the object to be deleted (command line arguments)
		String key = args[0];

		try{
			//removeBlob function deletes the specified file from the bucket
			blobStore.removeBlob(bucketName, key);
			System.out.println(key+" deleted from "+bucketName);
		}
		catch (Exception e) {
			// TODO: handle exception
			////catches exception
			System.err.println("Deletion failed!");
		}

		context.close();
	}
}
