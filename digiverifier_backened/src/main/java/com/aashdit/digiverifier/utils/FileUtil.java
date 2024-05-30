package com.aashdit.digiverifier.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.SecureRandom;

@Slf4j
@Component
public class FileUtil {
	
	private static SecureRandom secureRnd = new SecureRandom();
	public static File createUniqueTempFile(String prefix,String suffix) {
		try{
			String uniqueName =prefix.concat("_"+ secureRnd.nextInt());
			File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File tempFile = File.createTempFile(uniqueName, suffix, tempDir);

            // Set appropriate permissions for the file
            boolean readableSet = tempFile.setReadable(true, false);
            boolean writableSet = tempFile.setWritable(true, false);
            if (!readableSet) {
            	log.info("Failed to set readable permission for the temporary file.");
            }
            if (!writableSet) {
                log.info("Failed to set writable permission for the temporary file.");
            }

            // Make sure to delete the file on JVM exit
            tempFile.deleteOnExit();

            return tempFile;
		}catch(IOException e){
			log.error("unable to create file",e);
			throw new RuntimeException("unable to create file");
		}
	}
	
	public static InputStream convertToInputStream(File file) {
		try {
			return new DataInputStream(new FileInputStream(file));
		} catch(FileNotFoundException e) {
			log.error("unable to convert file to input stream",e);
			throw new RuntimeException("unable to convert file to input stream");
		}
	}
	
}
