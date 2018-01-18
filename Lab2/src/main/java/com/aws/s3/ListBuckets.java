package com.aws.s3;
import java.time.Duration;
import java.time.Instant;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

public class ListBuckets {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Instantiating AWSCredentials class to authenticate
		AWSCredentials credentials = null;
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
				.withRegion("us-west-2")
				.build();

		try{
			//setting a region name to filter the buckets present in a specified region
			String region = "ap-south-1";
			//getting the number of buckets under the specified account
			System.out.println("Total Number of Buckets : "+s3.listBuckets().size());
			System.out.println("Listing buckets");
			//listBuckets method lists all the buckets under the specified account
			for (Bucket bucket : s3.listBuckets()) {
				new Thread()
				{
					public void run() {
						//getBucketLocation method get the region ot the bucket
						String bucRegion = s3.getBucketLocation(bucket.getName().toString());
						if(bucRegion.equals(region)){
							//to list buckets in a specific region
							System.out.println(" - " + bucket.getName().toString());
						}
						//To print the names of the buckets
						//System.out.println(" - " + bucket.getName());
						//To print the names and the region of the buckets
						//System.out.println(" - " + bucket.getName()+" : "+s3.getBucketLocation(bucket.getName().toString()));
					}
				}.start();
			}
			System.out.println();
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
