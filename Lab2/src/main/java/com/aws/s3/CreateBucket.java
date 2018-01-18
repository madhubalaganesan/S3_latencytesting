package com.aws.s3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class CreateBucket {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//Instantiating AWSCredentials class to authenticate
		AWSCredentials credentials = null;
		//config.properties file has the region and buckets names which is accessed to create buckets
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
		//fetching bucketnames and region names from the properties file
		String bucketName1 = prop.getProperty("bucket1");
		String bucketName2 = prop.getProperty("bucket2");
		String bucketName3 = prop.getProperty("bucket3");
		String region1 = prop.getProperty("region1");
		String region2 = prop.getProperty("region2");
		String region3 = prop.getProperty("region3");

		//creating an instance (Service Client) of amazon S3 - type : standard - 3 clients to handle 3 create requests
		AmazonS3 s3_uswest2 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(region1)
				.build();

		AmazonS3 s3_cacentral1 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(region2)
				.build();

		AmazonS3 s3_euwest2 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(region3)
				.build();

		try {
			System.out.println("Creating bucket :" + bucketName1 + "\n");
			//createBucket method create a bucket in the defined region with the bucketname specified
			s3_uswest2.createBucket(bucketName1);
			System.out.println("Creating bucket :" + bucketName2 + "\n");
			s3_cacentral1.createBucket(bucketName2);
			System.out.println("Creating bucket :" + bucketName3 + "\n");
			s3_euwest2.createBucket(bucketName3);
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
