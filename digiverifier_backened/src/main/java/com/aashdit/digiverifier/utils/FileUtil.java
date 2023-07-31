package com.aashdit.digiverifier.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Random;

@Slf4j
@Component
public class FileUtil {
	
	private static Random random = new Random();
	public static File createUniqueTempFile(String prefix,String suffix) {
		try{
			prefix.concat("_"+ random.nextInt());
			return File.createTempFile(prefix,suffix);
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
