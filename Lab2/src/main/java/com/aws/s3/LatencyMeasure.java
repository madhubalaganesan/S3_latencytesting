package com.aws.s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class LatencyMeasure {
	static List<String> uploadList;
	static List<String> dwList;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//Instantiating AWSCredentials class to authenticate
		AWSCredentials credentials = null;
		//config.properties file has the region names, bucket names and download location
		Properties prop = new Properties();
		InputStream input = null;
		input = new FileInputStream("config.properties");
		// load a properties file
		prop.load(input);
		final String NEW_LINE_SEPARATOR = "\n";
		//creating 2 csv files to automatically store the latency (elapsed time) of upload and download for different test cases
		FileWriter uploadOut = new FileWriter("upload.csv", true);
		CSVPrinter up_printer = CSVFormat.DEFAULT.withHeader("REGION", "BUCKETNAME", "FILENAME", "UPLOADTIME")
								.withRecordSeparator(NEW_LINE_SEPARATOR).print(uploadOut);

		FileWriter dwout = new FileWriter("download.csv", true);
		CSVPrinter dw_printer = CSVFormat.DEFAULT.withHeader("REGION", "BUCKETNAME",  "FILENAME", "DOWNLOADTIME")
								.withRecordSeparator(NEW_LINE_SEPARATOR).print(dwout);


		try {
			//'default' profile has the access key & secret access key of AWS
			credentials = new ProfileCredentialsProvider("default").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. ",e);
		}

		//creating an instance (Service Client) of amazon S3 - type : standard - 3 clients to handle 3 requests
		AmazonS3 s3_region1 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(prop.getProperty("region1"))
				.build();

		AmazonS3 s3_region2 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(prop.getProperty("region2"))
				.build();

		AmazonS3 s3_region3 = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(prop.getProperty("region3"))
				.build();

		//fetching bucketname and download location from the properties file
		String bucket1 = prop.getProperty("bucket1");
		String bucket2 = prop.getProperty("bucket2");
		String bucket3 = prop.getProperty("bucket3");
		String dwloadLoc = prop.getProperty("downloadLocation");
		//defining the file to upload (dynamic via command line args) and key name
		File file = new File(args[0]);
		String key = file.getName();
		
		//calling upload method with s3object, bucket, file to upload, key name and region - for 3 regions and the method returns a list of value
		uploadList = upload(s3_region1, bucket1, file, key, prop.getProperty("region1"));
		//the list contains values such as region, filename, bucketname and latency (time taken for upload) which is written to the csv file
		up_printer.printRecord(uploadList);
		uploadList = upload(s3_region2, bucket2, file, key, prop.getProperty("region2"));
		up_printer.printRecord(uploadList);
		uploadList = upload(s3_region3, bucket3, file, key, prop.getProperty("region3"));
		up_printer.printRecord(uploadList);
		//calling download method with s3object, bucket, filename to download, region, download location as parameters
		//and the method returns a list of values
		dwList = download(s3_region1, bucket1, key, prop.getProperty("region1"), dwloadLoc);
		//the list contains values such as region, filename, bucketname and latency (time taken to download) which is written to the csv file
		dw_printer.printRecord(dwList);
		dwList = download(s3_region2, bucket2, key, prop.getProperty("region2"), dwloadLoc);
		dw_printer.printRecord(dwList);
		dwList = download(s3_region3, bucket3, key, prop.getProperty("region3"), dwloadLoc);
		dw_printer.printRecord(dwList);
		
		uploadOut.flush();
		uploadOut.close();
		dwout.flush();
		dwout.close();
	}



	static List<String> upload(AmazonS3 s3Obj, String bucket, File file, String key, String region){
		List<String> uploadListCsv = new ArrayList<String>();
		try{
			//adding region, bucket and filename to the uploadListCsv
			uploadListCsv.add(region);
			uploadListCsv.add(bucket);
			uploadListCsv.add(key);
			System.out.println("Uploading File to bucket::: "+bucket);
			Instant start = Instant.now();
			//putObject method uploads the file to the specified bucket in a region with the key name
			s3Obj.putObject(new PutObjectRequest(bucket, key, file));
			Instant end = Instant.now();
			System.out.println("Done uploading!");
			//calculating elapsed time
			long d = Duration.between(start, end).toMillis();
			String timeElapsed = Long.toString(d);
			System.out.println("timeLapsed_upload - "+bucket+" - "+timeElapsed+"\n");
			//adding the elapsed time to the list
			uploadListCsv.add(timeElapsed);
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
		//returning the list with values
		return uploadListCsv;

	}

	static List<String> download(AmazonS3 s3Obj, String bucket, String key, String region, String dwLoc){
		List<String> dwListCsv = new ArrayList<String>();
		try{
			//adding region, bucket and key name to the dwListCsv
			dwListCsv.add(region);
			dwListCsv.add(bucket);
			dwListCsv.add(key);
			System.out.println("Downloading File from bucket - "+bucket);
			//defining the name in which the file has to be downloaded
			File localFile = new File(dwLoc+bucket+"_"+key);
			Instant s = Instant.now();
			//getObject method download the requested file (key) from the specified bucket to a local location
			s3Obj.getObject(new GetObjectRequest(bucket, key),localFile);
			Instant e = Instant.now();
			//calculating the elapsed time
			long d = Duration.between(s, e).toMillis();
			String timeElapsed = Long.toString(d);
			System.out.println("timeLapsed_download - "+bucket+" - "+timeElapsed+"\n");
			//adding the elapsed time to the list
			dwListCsv.add(timeElapsed);
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
		//returning the list with values
		return dwListCsv;
	}

}

























