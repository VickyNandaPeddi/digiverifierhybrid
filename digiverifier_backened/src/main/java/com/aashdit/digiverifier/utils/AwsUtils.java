package com.aashdit.digiverifier.utils;

import com.aashdit.digiverifier.common.enums.ContentViewType;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

@Component
public class AwsUtils {
	
	@Autowired
	AmazonS3 s3Client;
	
	public String uploadFile(String bucketName,String path, File file){
		PutObjectResult putObjectResult = s3Client.putObject(bucketName, path, file);
		URL url = s3Client.getUrl(bucketName, path);
		return Objects.nonNull(url) ? String.valueOf(url) : "";
	}
	
	public String uploadFileAndGetPresignedUrl(String bucketName,String path, File file){
		s3Client.putObject(bucketName, path, file);
		Date expiration = getExpirationDate();
		
		
		// Generate the presigned URL.
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
			new GeneratePresignedUrlRequest(bucketName, path)
				.withMethod(HttpMethod.GET)
				.withExpiration(expiration);
		URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
		return Objects.nonNull(url) ? String.valueOf(url) : "";
	}
	
	public String uploadFileAndGetPresignedUrl_bytes(String bucketName,String path, byte[] file){
		byte[] bytes = file;
		ObjectMetadata metaData = new ObjectMetadata();
		metaData.setContentLength(bytes.length);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, path, byteArrayInputStream, metaData);
		s3Client.putObject(putObjectRequest);
		Date expiration = getExpirationDate();
		

		// Generate the presigned URL.
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
			new GeneratePresignedUrlRequest(bucketName, path)
				.withMethod(HttpMethod.GET)
				.withExpiration(expiration);
		URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
		return Objects.nonNull(url) ? String.valueOf(url) : "";
	}
	
	private Date getExpirationDate() {
		Date expiration = new Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * 10;
		expiration.setTime(expTimeMillis);
		return expiration;
	}
	
	public String getPresignedUrl(String bucketName, String path){
		return getPresignedUrl(bucketName,path,ContentViewType.VIEW);
	}
	

	public static void toFile(byte[] data, File destination) {
		try(FileOutputStream fos = new FileOutputStream(destination)){
		    fos.write(data);
			fos.close();
		}
		catch(Exception e) {
			
		}
	}
	
	public String getPresignedUrl(String bucketName, String path, ContentViewType type) {
		GeneratePresignedUrlRequest generatePresignedUrlRequest =
			new GeneratePresignedUrlRequest(bucketName, path)
				.withMethod(HttpMethod.GET)
				.withExpiration(getExpirationDate());
		URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
		String dispositionValue = (type.equals(ContentViewType.VIEW)) ? "inline" : "attachment";
		
		return Objects.nonNull(url) ? String.valueOf(url) : "";
	}
	
	public void getFileFromS3(String bucketName,String path,File file){
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, path);
		S3Object s3Object = s3Client.getObject(getObjectRequest);
		
		S3ObjectInputStream stream = s3Object.getObjectContent();
		try{
			FileUtils.copyInputStreamToFile(stream, file);
		}catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
	
}
