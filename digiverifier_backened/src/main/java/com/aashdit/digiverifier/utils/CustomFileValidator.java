package com.aashdit.digiverifier.utils;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class CustomFileValidator {
	
	private MultipartFile file;
    private List<String> allowedFileTypes;

    public CustomFileValidator(MultipartFile file, List<String> allowedFileTypes) {
        this.file = file;
        this.allowedFileTypes = allowedFileTypes;
    }

    public void validate() {
    	
    	boolean contains = false;
    	for(String types: allowedFileTypes) {
    		if(file.getOriginalFilename().toLowerCase().endsWith(types))
    			contains = true;
    	}
    	
//      if (!file.getOriginalFilename().endsWith(allowedFileTypes)) {
        if (!contains) {
            throw new IllegalArgumentException("Invalid file type. Only " + allowedFileTypes + " files are allowed.");
        }
    }

}
