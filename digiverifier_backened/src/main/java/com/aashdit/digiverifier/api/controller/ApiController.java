package com.aashdit.digiverifier.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.aashdit.digiverifier.common.dto.EPFOResponseDto;


import com.aashdit.digiverifier.api.model.ApiCandidate;
import com.aashdit.digiverifier.api.service.ApiService;
import com.aashdit.digiverifier.common.dto.EPFOResponseDto;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.admin.model.Token;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.repository.TokenRepository;
import com.aashdit.digiverifier.config.admin.service.UserService;
import com.aashdit.digiverifier.config.candidate.dto.CandidateReportDTO;
import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.dto.ReportSearchDto;
import com.aashdit.digiverifier.config.superadmin.service.ReportService;
import com.aashdit.digiverifier.login.dto.AuthenticationRequest;
import com.aashdit.digiverifier.login.dto.UserLoginDto;
import com.aashdit.digiverifier.security.JwtUtil;
import com.aashdit.digiverifier.utils.CommonUtils;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(path = "/api")
public class ApiController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserService userService;
	
	@Autowired
	private ApiService apiService;
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private CommonUtils commonUtils;

	@Operation(summary ="To authenticate user and generate and return JWT")
	@PostMapping(path="/login/authenticateAPI", produces=MediaType.APPLICATION_JSON_VALUE)
	public  ResponseEntity<ServiceOutcome<UserLoginDto>> userLogin(@RequestBody AuthenticationRequest authRequest) throws Exception {

		ServiceOutcome<UserLoginDto> response = new ServiceOutcome<>();
		try {
			
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
			
			final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUserName());
			if (userDetails != null) {
				User user = userService.findByUsername(userDetails.getUsername()).getData();
				if (user != null) {
					if (!user.getIsUserBlocked()) {
						Boolean isOK = true;
						if (user.getIsLocked())
						{
							response.setData(null);
							response.setOutcome(false);
							response.setMessage("Sorry. Your account has been locked. Please contact the System Administrator");
							log.error(response.getMessage());
							isOK = false;
						}
						
						if (user.getIsLoggedIn())
						{
							UserLoginDto userLoginDto = new UserLoginDto();
							userLoginDto.setJwtToken(jwtUtil.generateToken(user.getUserName()));
							response.setData(userLoginDto);
							response.setOutcome(false);
							response.setMessage("Sorry. You are already logged in.");
							log.error(response.getMessage());
							isOK = false;
						}
						
						if (isOK)
						{
							UserLoginDto userLoginDto = new UserLoginDto();
							String jwtToken= jwtUtil.generateToken(user.getUserName());
							userLoginDto.setJwtToken(jwtToken);
							userLoginDto.setUserFirstName(user.getUserFirstName());
                            userLoginDto.setOrganizationId(String.valueOf(user.getOrganization() != null ? user.getOrganization().getOrganizationId() : null));
							userLoginDto.setRoleCode(user.getRole().getRoleCode());
							userLoginDto.setRoleName(user.getRole().getRoleName());
                            userLoginDto.setUserId(String.valueOf(user.getUserId()));
                            userLoginDto.setUserId(commonUtils.encryptXOR(userLoginDto.getUserId()));
							response.setData(userLoginDto);
							user.setIsLoggedIn(true);
							user.setWrongLoginCount(0);
							userService.saveUserLoginData(user);
							//revoking and expiring old tokens
							revokeUserTokens(user);
							//saving user token
							saveUserToken(user,jwtToken);
							
							response.setOutcome(true);
							response.setMessage("User authenticated successfully.");
						}

					} else {
						response.setData(null);
						response.setOutcome(false);
						response.setMessage("Sorry, this account has been deactivated.");
						log.error(response.getMessage());
					}
				} else {
					response.setData(null);
					response.setOutcome(false);
					response.setMessage("Invalid username or password.");
					log.error(response.getMessage());
				}
			}
			else
			{
				response.setData(null);
				response.setOutcome(false);
				response.setMessage("Request Failed Due to System Issue.");
				log.error(response.getMessage());
			}
		} catch (Exception ex) {
			//updating wrong login count
			User user = userService.findByUsername(authRequest.getUserName()).getData();
			if(user!=null) {
				int wrongCount = user.getWrongLoginCount();
				wrongCount++;
				user.setWrongLoginCount(wrongCount);
				if(wrongCount < 3) {	
					response.setData(null);
					response.setOutcome(false);
					response.setMessage("Invalid username or password. Attempt "+wrongCount+" out of 3");
				}
				else {
					user.setIsLocked(true);
					response.setData(null);
					response.setOutcome(false);
					response.setMessage("Sorry. Your account has been locked. Please contact the System Administrator");
				}
				userService.saveUserLoginData(user);
			}
//			response.setData(null);
//			response.setOutcome(false);
//			response.setMessage("Invalid username or password.");
			log.error("Exception occured in userLogin method in LoginController-->"+ex);
		}

		return new ResponseEntity<ServiceOutcome<UserLoginDto>>(response, HttpStatus.OK);
	}
	
	private void saveUserToken(User user, String userToken) {
		Token t= new Token();
		t.setUserToken(userToken);
		t.setUser(user);
		t.setExpired(false);
		t.setRevoked(false);
		t.setTokenType("Bearer");
		
		tokenRepository.save(t);
	}
	
	private void revokeUserTokens(User user) {
		List<Token> validtokens= tokenRepository.findAllValidTokensByUser(user.getUserId());
		if(validtokens!=null && !validtokens.isEmpty()) {
			validtokens.forEach(t->{
				t.setExpired(true);
				t.setRevoked(true);
			});
			tokenRepository.saveAll(validtokens);
		}
		
	}
	
	@Operation(summary ="Upload Candidate Information")
	@PostMapping("/candidate/uploadCandidateAPI")
	public ResponseEntity<ServiceOutcome<List>> uploadCandidateFile(@RequestBody List<ApiCandidate> candidateList,@RequestHeader("Authorization") String authorization){
		ServiceOutcome<List> svcSearchResult = apiService.saveCandidateInformation(candidateList);
		return new ResponseEntity<ServiceOutcome<List>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary =" get Candidate report content url ")
	@GetMapping(value = "candidate/getDigitalReportAPI/{candidateCode}")
	public ResponseEntity<ServiceOutcome<String>> getContentById(@PathVariable("candidateCode") String CandidateCode, @RequestHeader("Authorization") String authorization) {
		ServiceOutcome<String> svcSearchResult = apiService.getContentByCandidateCode(CandidateCode);
		return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}
	
//	@Operation(summary ="Download The Candidate Status Tracker Report")
//	@GetMapping("candidate/getTrackerSheetDetailsAPI/{candidateCode}")
//    public ResponseEntity<byte[]> downloadCandidateStatusTrackerReport(@PathVariable("candidateCode") String CandidateCode, @RequestHeader("Authorization") String authorization) {
//    	return apiService.downloadCandidateStatusTrackerReport(CandidateCode);
//    }
    
	@Operation(summary ="Download The Candidate Status Tracker Report")
	@PostMapping("candidate/getTrackerSheetDetailsAPI")
    public ResponseEntity<byte[]> downloadCandidateStatusTrackerReport(@RequestHeader("Authorization") String authorization,@RequestBody ReportSearchDto reportSearchDto) {
    	return reportService.downloadCandidateStatusTrackerReport(reportSearchDto);
    }
    
	@Operation(summary ="get Candidate EPFO data for candidateCode")
	@GetMapping(value = "candidate/getEPFODataAPI/{candidateCode}")
	public ResponseEntity<ServiceOutcome<EPFOResponseDto>> getEPFODataAPI(@PathVariable("candidateCode") String CandidateCode, @RequestHeader("Authorization") String authorization) {
		ServiceOutcome<EPFOResponseDto> svcSearchResult = apiService.getEPFODataAPI(CandidateCode);
		return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Upload Candidate Information file CSV Or XLS and get invitation link")
	@PostMapping("/candidate/getInvitaionLink")
	public ResponseEntity<ServiceOutcome<List>> getInvitaionLink(@RequestBody List<ApiCandidate> candidateList,@RequestHeader("Authorization") String authorization){
		ServiceOutcome<List> svcSearchResult = apiService.getInvitaionLink(candidateList);
		return new ResponseEntity<ServiceOutcome<List>>(svcSearchResult, HttpStatus.OK);
	}
	
}
