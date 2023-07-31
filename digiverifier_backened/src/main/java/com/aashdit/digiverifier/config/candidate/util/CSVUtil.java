package com.aashdit.digiverifier.config.candidate.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.common.util.RandomString;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.model.SuspectEmpMaster;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationRepository;

@Configuration
public class CSVUtil {
	
	
	@Autowired
	private OrganizationRepository organizationRepository;


	
	ResourceBundle rb = ResourceBundle.getBundle("application");
	
	// public static String TYPE = "application/vnd.ms-excel";
	public static String TYPE = "text/csv";
	  public static boolean hasCSVFormat(MultipartFile file) {
		System.out.println(file+"------inside util");
		System.out.println(file.getContentType()+"------type");
	    if (!TYPE.equals(file.getContentType())) {
	      return false;
	    }
	    return true;
	  }

	  @SuppressWarnings("unused")
	public List<Candidate> csvToCandidateList(InputStream is) {
	    try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        CSVParser csvParser = new CSVParser(fileReader,
	            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

	      List<Candidate> candidateList = new ArrayList<Candidate>();
	      Iterable<CSVRecord> csvRecords = csvParser.getRecords();
	      for (CSVRecord csvRecord : csvRecords) {
	    	  if(!rb.getString("com.dgv.candidate.candidateName").equals("")) {
	    		  Candidate candidate = new Candidate(
		    			  csvRecord.get(rb.getString("com.dgv.candidate.candidateName")),
		    			  csvRecord.get(rb.getString("com.dgv.candidate.candidateEmailId")),
		    			  csvRecord.get(rb.getString("com.dgv.candidate.candidateContactNo")),
		    			  csvRecord.get(rb.getString("com.dgv.candidate.candidateNoYExp")).equals("")?null:Integer.parseInt(csvRecord.get(rb.getString("com.dgv.candidate.candidateNoYExp"))),
		    			  csvRecord.get(rb.getString("com.dgv.candidate.candidateCcEmail")),
		    			  csvRecord.get(rb.getString("com.dgv.candidate.candidateApplicantId"))
		    	  );
	    		  String applicant = csvRecord.get(rb.getString("com.dgv.candidate.candidateApplicantId"));
	    		  if(!applicant.equals("")) {
	    			  candidate.setApplicantId(String.valueOf(applicant));
	    		  }

	    		  if(applicant.equals("")) {	    			  
	    			  Random rnd = new Random();
		    		  int n = 100000 + rnd.nextInt(900000);
		    		  candidate.setApplicantId(String.valueOf(n));
	    		  }
	    		  candidateList.add(candidate); 
	    	  }
	      }
	      return candidateList;
	    } catch (IOException e) {
	      throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
	    }
	  }

	public List<User> csvToUserList(InputStream inputStream) {
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		        CSVParser csvParser = new CSVParser(fileReader,
		            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

		      List<User> userList = new ArrayList<User>();
		      Iterable<CSVRecord> csvRecords = csvParser.getRecords();
		      for (CSVRecord csvRecord : csvRecords) {
		    	  if(!rb.getString("com.dgv.agent.agentId").equals("")) {
		    		  User user = new User(
			    			  csvRecord.get(rb.getString("com.dgv.agent.agentId")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.FirstName")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.LastName")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.EmailId")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.location")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.workNumber")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.phoneNumber")),
			    			  csvRecord.get(rb.getString("com.dgv.agent.reportingEmailId"))
			    	  );
			    	  userList.add(user);
		    	  }
		    	 
		      }
		      return userList;
		    } catch (IOException e) {
		      throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
		    }
	}


	
//updated this all are commented
	 	public List<SuspectEmpMaster> csvToSuspectEmpMaster(InputStream is,Long organizationId) {
	     try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	         CSVParser csvParser = new CSVParser(fileReader,
	            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

	       List<SuspectEmpMaster> suspectEmpMasterList = new ArrayList<SuspectEmpMaster>();
	       Iterable<CSVRecord> csvRecords = csvParser.getRecords();
	       for (CSVRecord csvRecord : csvRecords) {
	     	  if(!rb.getString("com.dgv.suspect.suspectCompanyName").equals("")) {
	     		  SuspectEmpMaster suspectEmpMaster = new SuspectEmpMaster(
	 	    			  csvRecord.get(rb.getString("com.dgv.suspect.suspectCompanyName")),
	 	    			  csvRecord.get(rb.getString("com.dgv.suspect.address"))
	 	    	);
	     		  suspectEmpMaster.setIsActive(true);
					suspectEmpMaster.setOrganization(organizationRepository.findById(organizationId).get());
					suspectEmpMaster.setCreatedOn(new Date());

	     		suspectEmpMasterList.add(suspectEmpMaster);
	     	  }
	       }
	       return suspectEmpMasterList;
	     } catch (IOException e) {
	       throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
	     }
	   }

	}
