package com.aws.s3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class DeleteObjects {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//Instantiating AWSCredentials class to authenticate
		AWSCredentials credentials = null;
		//config.properties file has the region and buckets names which is accessed to delete object in a bucket
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("config.properties");
		// load the properties file
		prop.load(input);

		try {
			//'default' profile has the access key & secret access key of AWS
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. ",e);
		}
		
		//creating an instance (Service Client) of amazon S3 - type : standard 
		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(prop.getProperty("region1"))
				.build();

		//fetching bucketname from the properties file
		String bucketName = prop.getProperty("bucket1");
		//key is the name of the file that is present in S3
		String key = args[0];

		try{
			System.out.println("Deleting an object:::"+args[0]+"\n");
			//deleteObject method deletes the object with the name mentioned as key in the specified bucket
	        s3.deleteObject(bucketName, key);
			input.close();
			System.out.println("Object Deleted!");
		}

		catch (AmazonServiceException ase) {
			//catches all exceptions and gives details error messages
			System.out.println("Caught an AmazonServiceException, the request was made "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}

	}

}
