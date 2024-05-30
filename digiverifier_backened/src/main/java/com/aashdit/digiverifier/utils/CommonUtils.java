package com.aashdit.digiverifier.utils;

import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aashdit.digiverifier.config.candidate.service.CandidateServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CommonUtils {

	private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

	public static String trimDNHEmployerName(String name) {
		name = name.trim();
		return String.join(" ", Arrays.stream(name.split(" ")).limit(2).collect(Collectors.toList()));
	}

	public static double checkStringSimilarity(String string1, String string2) {
		string1 = string1.toLowerCase().trim();
		string2 = string2.toLowerCase().trim();

		// Check if the strings contain "m/s" (case-insensitive)
        if (string1.toLowerCase().contains("m/s")) {
        	string1 = string1.replaceFirst("(?i)m/s ", "");
        }
        
        if (string2.toLowerCase().contains("m/s")) {
        	string2 = string2.replaceFirst("(?i)m/s ", "");
        }
		
		// start comparing only two initial words
		string1 = CommonUtils.retrieveFirstTwoWords(string1);
		string2 = CommonUtils.retrieveFirstTwoWords(string2);
		// Now compare these strings similarity

		// Check if the strings contain "technologies" (case-insensitive)
        if (string1.toLowerCase().contains("technologies")) {
        	string1 = string1.replaceFirst("(?i)technologies", "");
        }
        
        if (string2.toLowerCase().contains("technologies")) {
        	string2 = string2.replaceFirst("(?i)technologies", "");
        }

		if (string1.indexOf("wipro") > -1 && string2.indexOf("wipro") > -1) {
			return 1.0;
		} else {
			JaroWinklerDistance jaroWinkler = new JaroWinklerDistance();
//			String regex = "^/(?!private|limited|pvt|ltd)([a-zA-Z0-9 ]+)/$";
			String regex = "/[^a-zA-Z0-9 ]/g";
			return jaroWinkler.apply(string1.replace(regex, ""), string2.replace(regex, ""));
		}

	}

//	public static String trimEmployerName(String name) {
//		let employer = employerName.toLowerCase().replace("technologies", "").replace("innovation", "").replace("innovations", "").replace("management", "").replace("consultant", "").replace("consultants", "").replace("informatics", "").replace("private", "").replace("global", "").replace("consultancy", "").replace("services", "").replace("pvt", "").replace("ltd", "").replace("solutions", "").replace("limited", "").replace(",", "").replace(".", "").replace(/ /gi, "").trim();
//		// return employer.split(' ').slice(0,2).join(' ');
//		let stringTrimPercentage = 100; // This will trim based on the minimum given length for the string
////		if(length > 0) {
////			return employer.substring(0,length);
////		} else {
////			return employer.substring(0, Math.floor((parseInt(employer.length) * stringTrimPercentage) / 100)).toLowerCase();
////		}
//	}

	public static String retrieveFirstTwoWords(String input) {

		// Define a pattern to match words containing only letters and digits

		Pattern pattern = Pattern.compile("\\b[a-zA-Z0-9]+\\b");

		// Create a matcher for the input string

		Matcher matcher = pattern.matcher(input);

		int wordCount = 0;

		StringBuilder result = new StringBuilder();

		while (matcher.find()) {

			// Check if the word contains only letters and digits

			String word = matcher.group();

			result.append(word);

			result.append(" ");

			wordCount++;

			// Stop after retrieving two valid words

			if (wordCount >= 2) {

				break;

			}

		}

		// Trim any trailing space

		return result.toString().trim();

	}
	
	public static List<String> getYearsBetweenDates(Date startDate, Date endDate) {
        List<String> yearsBetween = new ArrayList<>();
        LocalDate localStartDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localEndDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Year startYear = Year.of(localStartDate.getYear());
        Year endYear = Year.of(localEndDate.getYear());

        while (!startYear.isAfter(endYear)) {
            yearsBetween.add(startYear.toString());
            startYear = startYear.plusYears(1);
        }
        
        if(startYear.equals(endYear)) {
        	yearsBetween.add(startYear.toString());
        }

        log.info("Years between the employer ::{}", yearsBetween);
        return yearsBetween;
    }
	
	String key="12345678901234567890123456789012";
	public String encryptXOR(String data) {
		byte[] dataBytes = data.getBytes();
		byte[] keyBytes = key.getBytes();
		byte[] encryptedBytes = new byte[dataBytes.length];
		for (int i = 0; i < dataBytes.length; i++) {
			encryptedBytes[i] = (byte) (dataBytes[i] ^ keyBytes[i % keyBytes.length]);
		}
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}
	public String decryptXOR(String encryptedData) {
		byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
		byte[] keyBytes = key.getBytes();
		byte[] decryptedBytes = new byte[decodedBytes.length];
		for (int i = 0; i < decodedBytes.length; i++) {
			decryptedBytes[i] = (byte) (decodedBytes[i] ^ keyBytes[i % keyBytes.length]);
		}
		return new String(decryptedBytes);
	}

}
