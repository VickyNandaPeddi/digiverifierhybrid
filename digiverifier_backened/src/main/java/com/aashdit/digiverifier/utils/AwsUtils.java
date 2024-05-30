package com.aashdit.digiverifier.utils;

import com.aashdit.digiverifier.common.enums.ContentViewType;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.itextpdf.io.codec.Base64.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
	
	public String uploadFileAndGetPresignedUrl_bytes(String bucketName, String path, byte[] file,ObjectMetadata metadata) {
        byte[] bytes = file;

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, path, byteArrayInputStream, metadata);
        s3Client.putObject(putObjectRequest);
        Date expiration = getExpirationDate();
        // Generate the presigned URL.
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, path).withMethod(HttpMethod.GET).withExpiration(expiration);
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
	
	public File getFileFromS3(String bucketName, String path) throws IOException {
	    GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, path);
	    S3Object s3Object = s3Client.getObject(getObjectRequest);
	    
	    // Get the content type of the object
	    ObjectMetadata objectMetadata = s3Object.getObjectMetadata();
	    String contentType = objectMetadata.getContentType();

	    String fileExtension = getFileExtension(contentType);
	    // Create a temporary file to save the content
//	    File tempFile = File.createTempFile("temp", ".pdf");
	    File tempFile = File.createTempFile("temp", fileExtension);

	    try (InputStream inputStream = s3Object.getObjectContent();
	         FileOutputStream outputStream = new FileOutputStream(tempFile)) {

	        // Copy content from input stream to the temporary file
	        byte[] buffer = new byte[1024];
	        int bytesRead;
	        while ((bytesRead = inputStream.read(buffer)) != -1) {
	            outputStream.write(buffer, 0, bytesRead);
	        }
	    }

	    return tempFile;
	}
	
	private String getFileExtension(String contentType) {
	    switch (contentType) {
	        case "application/pdf":
	            return ".pdf";
	        case "image/jpeg":
	            return ".jpg";
	        case "image/png":
	            return ".png";
	        // Add more cases for other content types as needed
	        default:
	            return ".dat"; // Default to .dat if content type not recognized
	    }
	}
	
	   public byte[] getbyteArrayFromS3(String bucketName, String path) throws IOException {

	        S3Object s3Object = s3Client.getObject(bucketName, path);
	        InputStream inputStream = s3Object.getObjectContent();
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        IOUtils.copy(s3Object.getObjectContent(), outputStream);

	        byte[] pdfBytes = outputStream.toByteArray();
	        s3Object.close();
	        outputStream.close();
	        return pdfBytes;
//	        byte[] buffer = new byte[4096];
//	        int bytesRead;
//	        while ((bytesRead = inputStream.read(buffer)) != -1) {
//	            outputStream.write(buffer, 0, bytesRead);
//	        }
	//
//	        byte[] data = outputStream.toByteArray();
//	        outputStream.close();
//	        inputStream.close();

	    }
	
}
