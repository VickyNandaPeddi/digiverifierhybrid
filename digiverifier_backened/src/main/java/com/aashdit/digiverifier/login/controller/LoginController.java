package com.aashdit.digiverifier.login.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.admin.model.Token;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.repository.TokenRepository;
import com.aashdit.digiverifier.config.admin.service.UserService;
import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.aashdit.digiverifier.login.dto.AuthenticationRequest;
import com.aashdit.digiverifier.login.dto.UserLoginDto;
import com.aashdit.digiverifier.login.service.OTPService;
import com.aashdit.digiverifier.security.JwtUtil;
import com.aashdit.digiverifier.utils.CommonUtils;
import com.aashdit.digiverifier.utils.EmailSentTask;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(path = "/api/login")
public class LoginController {

	
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
	private CommonUtils commonUtils;
	
	@Autowired
	private EnvironmentVal envirnmentVal;
	
	@Autowired
	private EmailSentTask emailSentTask;
	
	@Autowired
	private OTPService otpService;

	@Operation(summary ="To authenticate user and generate and return JWT")
	@PostMapping(path="/authenticate", produces=MediaType.APPLICATION_JSON_VALUE)
	public  ResponseEntity<ServiceOutcome<UserLoginDto>> userLogin(@RequestBody AuthenticationRequest authRequest) throws Exception {

		ServiceOutcome<UserLoginDto> response = new ServiceOutcome<>();
		try {
			
			// Decrypt username and password
            String decryptedUsername = commonUtils.decryptXOR(authRequest.getUserName());
            String decryptedPassword = commonUtils.decryptXOR(authRequest.getPassword());
            authRequest.setUserName(decryptedUsername);
            authRequest.setPassword(decryptedPassword);
			
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
						//checking both condition user.getIsLoggedIn() and db token is valid or not
						// List<Token> validtokens= tokenRepository.findAllValidTokensByUser(user.getUserId());
						// if (Boolean.TRUE.equals(user.getIsLoggedIn()) || (validtokens!=null && !validtokens.isEmpty() && !validtokens.get(validtokens.size()-1).getExpired()))
						// {
						// 	UserLoginDto userLoginDto = new UserLoginDto();
						// 	userLoginDto.setJwtToken(commonUtils.encryptXOR(jwtUtil.generateToken(user.getUserName())));
						// 	response.setData(userLoginDto);
						// 	response.setOutcome(false);
						// 	response.setMessage("Sorry. You are already logged in.");
						// 	log.error(response.getMessage());
						// 	isOK = false;
						// }
						
						if (isOK)
						{
							Long passwordPolicyDay = envirnmentVal.getPasswordPolicyDay();
							Boolean validateOtp = false;
							UserLoginDto userLoginDto = new UserLoginDto();
							String jwtToken= jwtUtil.generateToken(user.getUserName());
							if(user.getOrganization().getPasswordPolicy() != null && user.getOrganization().getPasswordPolicy() && authRequest.getOtp() == null) {
								if(user.getOrganization().getIsMFA() != null && user.getOrganization().getIsMFA() && authRequest.getOtp() == null) {
		                            userLoginDto.setRoleName(commonUtils.encryptXOR(user.getRole().getRoleName()));
		                            userLoginDto.setMFA(user.getOrganization().getIsMFA());
		                            if(user.getUserEmailId() != null) {
		                            	emailSentTask.mfaOTP(user.getUserEmailId());
		                            }
								}
								else {
									if(user.getLastUpdatedPassword() != null) {
										Date lastPasswordUpdated = user.getLastUpdatedPassword();
										Date currentDate = new Date();
										
										long timeDifference = currentDate.getTime() - lastPasswordUpdated.getTime();
										long daysDifference = TimeUnit.MILLISECONDS.toDays(timeDifference);
										log.info("daysDifference : "+daysDifference);
//									System.out.println("passwordPolicyDay : "+passwordPolicyDay);
										if (daysDifference >= passwordPolicyDay) {
											log.info("Password was updated more than "+passwordPolicyDay+" days ago.");
										} else {
											userLoginDto.setJwtToken(commonUtils.encryptXOR(jwtToken));
											userLoginDto.setRoleName(commonUtils.encryptXOR(user.getRole().getRoleName()));
											log.info("Password was updated within the last "+passwordPolicyDay+" days.");
										}
										
									}else {
										userLoginDto.setLastPasswordUpdated(user.getCreatedOn());
										userLoginDto.setJwtToken(commonUtils.encryptXOR(jwtToken));
										userLoginDto.setRoleName(commonUtils.encryptXOR(user.getRole().getRoleName()));
									}
								}
							}else {
								if(authRequest.getOtp() != null) {
//									System.out.println("OTP IS NOT NULL !"+authRequest.getOtp());
									 validateOtp = otpService.validateOtp(user.getUserName(), authRequest.getOtp());
//									System.out.println("Validate the OTP : "+validateOtp);
									if(validateOtp) {
										log.info(" OTP is VAlid!! {}");	
										if(user.getOrganization().getPasswordPolicy() != null && user.getOrganization().getPasswordPolicy() && user.getLastUpdatedPassword() != null) {
											Date lastPasswordUpdated = user.getLastUpdatedPassword();
											Date currentDate = new Date();

											long timeDifference = currentDate.getTime() - lastPasswordUpdated.getTime();
											long daysDifference = TimeUnit.MILLISECONDS.toDays(timeDifference);
											log.info("daysDifference : "+daysDifference);
											//										System.out.println("passwordPolicyDay : "+passwordPolicyDay);
											if (daysDifference >= passwordPolicyDay) {
												log.info("Password was updated more than "+passwordPolicyDay+" days ago.");
											}else {
												userLoginDto.setJwtToken(commonUtils.encryptXOR(jwtToken));
												userLoginDto.setRoleName(commonUtils.encryptXOR(user.getRole().getRoleName()));
											}
										}else {
											userLoginDto.setJwtToken(commonUtils.encryptXOR(jwtToken));
											userLoginDto.setRoleName(commonUtils.encryptXOR(user.getRole().getRoleName()));
										}
									}
//									else {
//										System.out.println("INVALID OTP!");
//										response.setData(null);
//										response.setOutcome(false);
//										response.setMessage("Invalid OTP!.");
//									}	
								}
								else if(user.getOrganization().getIsMFA() != null && user.getOrganization().getIsMFA() && authRequest.getOtp() == null) {
									log.info("MFA Block {}");
		                            userLoginDto.setRoleName(commonUtils.encryptXOR(user.getRole().getRoleName()));
		                            userLoginDto.setMFA(user.getOrganization().getIsMFA());
		                            if(user.getUserEmailId() != null) {
		                            	emailSentTask.mfaOTP(user.getUserEmailId());
		                            }
								}else {
									log.info("ELSE Block {}");
									userLoginDto.setJwtToken(commonUtils.encryptXOR(jwtToken));
									userLoginDto.setRoleName(commonUtils.encryptXOR(user.getRole().getRoleName()));
								}
							}
                            userLoginDto.setUserFirstName(commonUtils.encryptXOR(user.getUserFirstName()));
                            userLoginDto.setOrganizationId(String.valueOf(user.getOrganization() != null ? user.getOrganization().getOrganizationId() : null));
                            userLoginDto.setOrganizationId(commonUtils.encryptXOR(userLoginDto.getOrganizationId()));
                            userLoginDto.setRoleCode(commonUtils.encryptXOR(user.getRole().getRoleCode()));
//                            userLoginDto.setRoleName(commonUtils.encryptXOR(user.getRole().getRoleName()));
                            userLoginDto.setUserId(String.valueOf(user.getUserId()));
                            userLoginDto.setUserId(commonUtils.encryptXOR(userLoginDto.getUserId()));
                            userLoginDto.setPasswordPolicyDay(passwordPolicyDay);
                            if(user.getOrganization().getPasswordPolicy() != null && user.getOrganization().getPasswordPolicy()) {
                            	userLoginDto.setOrgPassPolicy(user.getOrganization().getPasswordPolicy());
                            	System.out.println("LastUpdatedPassword : "+user.getLastUpdatedPassword());	
                            	if(user.getLastUpdatedPassword() != null) {	
                            		userLoginDto.setLastPasswordUpdated(user.getLastUpdatedPassword());
                            	}else {
                            		userLoginDto.setLastPasswordUpdated(user.getCreatedOn());
                            	}
                            }
//                            if(user.getLastUpdatedPassword() != null) {      	
//                            }
							response.setData(userLoginDto);
							user.setIsLoggedIn(true);
							user.setWrongLoginCount(0);
							userService.saveUserLoginData(user);

							try {
								List<Token> existingTokens= tokenRepository.findByUserId(user.getUserId());
								if(existingTokens.isEmpty())
									response.setMessage("Change your password.");
								else if(authRequest.getOtp() != null && !validateOtp) {
									log.info("INvalid OTP {}");
									response.setData(null);
									response.setOutcome(false);
									response.setMessage("Invalid OTP!.");
								}
								else
									log.info("Logged In User");
									response.setMessage("User authenticated successfully.");
							} catch (Exception e) {
								log.info("Exception occured in new agent login check method {}", e.getMessage());
								response.setMessage("User authenticated successfully.");
							}
							
							//revoking and expiring old tokens
							revokeUserTokens(user);
							//saving user token
							saveUserToken(user,jwtToken);
							
							response.setOutcome(true);
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
	
	@Operation(summary ="To sign off a particular user using token from header")
	@PostMapping(path="/sign-off", produces=MediaType.APPLICATION_JSON_VALUE)
	public  ResponseEntity<ServiceOutcome<String>> userSignOff(@RequestHeader("Authorization") String authorization) throws Exception {

		ServiceOutcome<String> response = new ServiceOutcome<>();
		try {
			String token = authorization.substring(7);
			String username=jwtUtil.extractUsername(token);
			User user = userService.findByUsername(username).getData();
			user.setIsLoggedIn(false);
			userService.saveUserLoginData(user);
			
			//invalidating the token on logged out
			Optional<Token> storedToken = tokenRepository.findByUserToken(token);
			Token t = storedToken.get();
			t.setExpired(true);
			t.setRevoked(true);
			tokenRepository.save(t);
			
			response.setOutcome(true);
			response.setMessage("Signed off successfuly.");
			
		} catch (Exception ex) {
			response.setData(null);
			response.setOutcome(false);
			response.setMessage("Unable to sign off.");
			log.error("Exception occured in userSignOff method in LoginController-->"+ex);
		}

		return new ResponseEntity<ServiceOutcome<String>>(response, HttpStatus.OK);
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
}

