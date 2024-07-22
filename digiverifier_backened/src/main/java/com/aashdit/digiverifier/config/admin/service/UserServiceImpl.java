package com.aashdit.digiverifier.config.admin.service;

import static com.aashdit.digiverifier.digilocker.service.DigilockerServiceImpl.DIGIVERIFIER_DOC_BUCKET_NAME;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.aashdit.digiverifier.config.admin.dto.VendorInitiatDto;
import com.aashdit.digiverifier.config.admin.dto.VendorcheckdashbordtDto;
import com.aashdit.digiverifier.config.admin.dto.vendorChecksDto;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.CandidateVerificationStateRepository;
import com.aashdit.digiverifier.config.candidate.repository.ConventionalCandidateStatusRepository;
import com.aashdit.digiverifier.config.candidate.repository.ConventionalCandidateVerificationStateRepository;
import com.aashdit.digiverifier.config.candidate.repository.StatusMasterRepository;
import com.aashdit.digiverifier.config.candidate.service.ConventionalCandidateService;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.admin.repository.VendorChecksRepository;
import com.aashdit.digiverifier.config.admin.repository.VendorUploadChecksRepository;
import com.aashdit.digiverifier.config.candidate.dto.CandidateStatusCountDto;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.superadmin.repository.SourceRepository;
import com.aashdit.digiverifier.config.superadmin.model.Source;
import com.aashdit.digiverifier.config.superadmin.repository.VendorMasterNewRepository;
import com.aashdit.digiverifier.config.superadmin.model.VendorMasterNew;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.aashdit.digiverifier.config.superadmin.dto.DashboardDto;
import com.aashdit.digiverifier.config.superadmin.model.Color;
import com.aashdit.digiverifier.config.superadmin.model.Orgclientscope;
import com.aashdit.digiverifier.config.superadmin.model.ServiceSourceMaster;
import com.aashdit.digiverifier.config.superadmin.model.ServiceTypeConfig;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.aashdit.digiverifier.config.admin.service.UserService;
import com.aashdit.digiverifier.common.ContentRepository;
import com.aashdit.digiverifier.common.enums.ContentCategory;
import com.aashdit.digiverifier.common.enums.ContentSubCategory;
import com.aashdit.digiverifier.common.enums.ContentType;
import com.aashdit.digiverifier.common.enums.FileType;
import com.aashdit.digiverifier.common.model.Content;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.admin.dto.UserDto;
import com.aashdit.digiverifier.config.admin.dto.VendorCheckStatusAndCountDTO;
import com.aashdit.digiverifier.config.admin.dto.AgentInvitationSentDto;
import com.aashdit.digiverifier.config.admin.dto.CivilProceedingsDTO;
import com.aashdit.digiverifier.config.admin.dto.CriminalProceedingsDTO;
import com.aashdit.digiverifier.config.admin.dto.ECourtProofResponseDto;
import com.aashdit.digiverifier.config.admin.dto.ECourtRequestDto;
import com.aashdit.digiverifier.config.admin.dto.SearchAllVendorCheckDTO;
import com.aashdit.digiverifier.config.admin.model.AgentSampleCsvXlsMaster;
import com.aashdit.digiverifier.config.admin.model.ConventionalAttributesMaster;
import com.aashdit.digiverifier.config.admin.model.CriminalCheck;
import com.aashdit.digiverifier.config.admin.model.Role;
import com.aashdit.digiverifier.config.admin.model.VendorUploadChecks;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.model.VendorCheckStatusHistory;
import com.aashdit.digiverifier.config.admin.repository.AgentSampleCsvXlsMasterRepository;
import com.aashdit.digiverifier.config.admin.repository.ConventionalAttributesMasterRepository;
import com.aashdit.digiverifier.config.admin.repository.CriminalCheckRepository;
import com.aashdit.digiverifier.config.admin.repository.RoleRepository;
import com.aashdit.digiverifier.config.admin.repository.UserRepository;
import com.aashdit.digiverifier.config.admin.repository.VendorCheckStatusHistoryRepository;
import com.aashdit.digiverifier.config.candidate.util.CSVUtil;
import com.aashdit.digiverifier.config.candidate.repository.CandidateCaseDetailsRepository;
import com.aashdit.digiverifier.config.candidate.model.CandidateCaseDetails;
import com.aashdit.digiverifier.config.candidate.model.CandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateStatus;
import com.aashdit.digiverifier.config.candidate.model.ConventionalCandidateVerificationState;
import com.aashdit.digiverifier.config.candidate.model.SuspectEmpMaster;
import com.aashdit.digiverifier.config.candidate.model.UanSearchData;
import com.aashdit.digiverifier.config.candidate.util.ExcelUtil;
import com.aashdit.digiverifier.config.superadmin.repository.OrganizationRepository;
import com.aashdit.digiverifier.config.superadmin.repository.OrgclientscopeRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceSourceMasterRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ServiceTypeConfigRepository;
import com.aashdit.digiverifier.config.superadmin.repository.ColorRepository;
import com.aashdit.digiverifier.utils.SecurityHelper;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.aashdit.digiverifier.utils.ApplicationDateUtils;
import com.aashdit.digiverifier.utils.AwsUtils;
import com.aashdit.digiverifier.utils.EmailSentTask;
import com.aashdit.digiverifier.config.superadmin.repository.VendorCheckStatusMasterRepository;
import com.aashdit.digiverifier.config.admin.model.VendorUploadChecks;
import com.aashdit.digiverifier.config.superadmin.model.VendorCheckStatusMaster;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private ColorRepository colorRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private CSVUtil cSVUtil;

	@Autowired
	private ExcelUtil excelUtil;

	@Autowired
	private AgentSampleCsvXlsMasterRepository agentSampleCsvXlsMasterRepository;

	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private VendorMasterNewRepository vendorMasterNewRepository;

	@Autowired
	private VendorChecksRepository vendorChecksRepository;

	@Autowired
	private VendorUploadChecksRepository vendorUploadChecksRepository;

	@Autowired
	private SourceRepository sourceRepository;

	@Autowired
	private CandidateCaseDetailsRepository candidateCaseDetailsRepository;

	@Autowired
	private VendorCheckStatusMasterRepository vendorCheckStatusMasterRepository;

	@Autowired
	private EmailSentTask emailSentTask;

	@Autowired
	private ConventionalAttributesMasterRepository conventionalAttributesMasterRepository;
	
	 @Autowired
	 private CriminalCheckRepository criminalCheckRepository;
	 
	 @Autowired
	 private VendorCheckStatusHistoryRepository vendorCheckStatusHistoryRepository;
	 
	 @Autowired
	 private AwsUtils awsUtils;
	 
	 @Autowired
	 private CandidateStatusRepository candidateStatusRepository;
	 
	 @Autowired
	 private StatusMasterRepository statusMasterRepository;
	 
	 @Autowired
	 private ConventionalCandidateStatusRepository conventionalCandidateStatusRepository;
	 
	 @Autowired
	 private ConventionalCandidateService conventionalCandidateService;

	 @Autowired
	 private CandidateVerificationStateRepository candidateVerificationStateRepository;
	 
	 @Autowired
	private ConventionalCandidateVerificationStateRepository conventionalCandidateVerificationStateRepository;
	 
	@Autowired
	private OrgclientscopeRepository orgClientScopeRepository;
		
	@Autowired	
	private ServiceTypeConfigRepository serviceTypeConfigRepository;
	
	@Autowired
	private ServiceSourceMasterRepository serviceSourceMasterRepository;
		
	@Autowired
	private ContentRepository contentRepository;
	 
	 public static final String DIGIVERIFIER_DOC_BUCKET_NAME = "digiverifier-new";
	 
	@Value("${E_COURT_PROOF_URL}")
	private String eCourtURL;

	
	@Override
    public ServiceOutcome<ConventionalAttributesMaster> saveConventionalAttributesMaster(
            ConventionalAttributesMaster conventionalAttributesMaster) {
        ServiceOutcome<ConventionalAttributesMaster> svcSearchResult = new ServiceOutcome<ConventionalAttributesMaster>();
        try {

 

        	if(!conventionalAttributesMaster.getAgentAttributeList().isEmpty() || !conventionalAttributesMaster.getVendorAttributeList().isEmpty()){

        		System.out.println("Conventional==="+conventionalAttributesMaster);
        		ConventionalAttributesMaster attributesMaster = new ConventionalAttributesMaster();
        		attributesMaster.setCheckId(conventionalAttributesMaster.getCheckId());
        		//                attributesMaster.setCheckName(conventionalAttributesMaster.getCheckName());
        		attributesMaster.setAgentAttributeList(conventionalAttributesMaster.getAgentAttributeList());
        		attributesMaster.setVendorAttributeList(conventionalAttributesMaster.getVendorAttributeList()); 

        		attributesMaster.setSource(conventionalAttributesMaster.getSource());
        		attributesMaster.setGlobalCheckType(conventionalAttributesMaster.getGlobalCheckType());

        		ConventionalAttributesMaster save = conventionalAttributesMasterRepository.save(attributesMaster);
        		svcSearchResult.setMessage("Conventional Attribute Master successfully.");
        		svcSearchResult.setData( attributesMaster);

        	}
        }
        catch (Exception ex) {
        	log.error("Exception occured in saveConventionalAttributesMaster method in userServiceImpl-->"+ex);
        }
        return svcSearchResult;




    }


	@SuppressWarnings("unused")
	@Transactional
	@Override
	public ServiceOutcome<UserDto> saveUser(UserDto user) {
		ServiceOutcome<UserDto> svcSearchResult = new ServiceOutcome<>();
		UserDto userDto = new UserDto();
		try {
			User result = null;
			log.debug("User object is-->" + user);
			//this  IF object will update the user
			if (user.getUserId() != null && !user.getUserId().equals(0l) && user.getUserEmailId() != null) {
//					User findUserEmail = userRepository.findByUserEmailId(user.getUserEmailId());
				User findUserById = userRepository.findByUserId(user.getUserId());
				System.out.println("findUserEmail:::" + findUserById);
				
				//validating login user with the user whose details is updating here
				User loggedInUser = SecurityHelper.getCurrentUser();
				Optional<Role> updateUserRole = roleRepository.findById(user.getRoleId());
				List<User> roleWiseUsers = userRepository.findAllByRoleRoleIdAndIsActiveTrue(user.getRoleId());
				boolean roleWiseUsersHavingOrgId = roleWiseUsers.stream().anyMatch(u -> u.getOrganization()!=null &&
				u.getOrganization().getOrganizationId().equals(loggedInUser.getOrganization().getOrganizationId()));
				
				log.info("The loggedIn User Id is::{}", loggedInUser.getUserId());
				log.info("The loggedIn User Role is::{}", loggedInUser.getRole().getRoleCode());
				log.info("The loggedIn User ORG ID is::{}", loggedInUser.getOrganization().getOrganizationId());
				log.info("The User Id whose record needs to update is::{}", findUserById.getUserId());
				log.info("The User Role whose record needs to update is::{}", findUserById.getRole().getRoleCode());
				log.info("The User Role whose record needs to update  y considering role id is::{}", updateUserRole.get().getRoleCode());
				log.info("The ORG ID OF USER whose record needs to update is::{}", findUserById.getOrganization().getOrganizationId());
				
				//if logged in user is not cb-admin then check roles and permission of user update!!
				if(!loggedInUser.getRole().getRoleCode().equals("ROLE_CBADMIN")) {
					if((!loggedInUser.getRole().getRoleCode().equals(updateUserRole.get().getRoleCode())
							|| loggedInUser.getRole().getRoleCode().equals("ROLE_ADMIN"))
						&& !roleWiseUsersHavingOrgId) {
						//this condition checking the logged in user role code and its role own role code should be same
						svcSearchResult.setData(null);
						svcSearchResult.setOutcome(false);
						svcSearchResult.setMessage("Unauthorize To Update Another User Information..!");
						return svcSearchResult;
					}
					if(findUserById!=null
							&& (!loggedInUser.getUserId().equals(findUserById.getUserId()) || loggedInUser.getRole().getRoleCode().equals("ROLE_ADMIN"))
							&& !loggedInUser.getOrganization().getOrganizationId().equals(findUserById.getOrganization().getOrganizationId())
							) {
						svcSearchResult.setData(null);
						svcSearchResult.setOutcome(false);
						svcSearchResult.setMessage("Unauthorize To Update Another User Information..!");
						return svcSearchResult;
					}
				}

				if (findUserById != null) {

						Optional<User> getExistingPassByUserID = userRepository.findById(user.getUserId());					
						if (user.getPassword() != null && !user.getPassword().trim().isEmpty() &&
							    user.getOldPassword() != null && !user.getOldPassword().trim().isEmpty()) {
							String oldEncodePasswordDB = findUserById.getPassword().trim();						
							String oldPassword = user.getOldPassword().trim();						
							boolean passwordVerificationForPlainTextAndEncodedPassword = bCryptPasswordEncoder.matches(user.getOldPassword(), oldEncodePasswordDB);
							log.info("Password matches: {}" + passwordVerificationForPlainTextAndEncodedPassword);
							boolean passwordVerificationEncodedPasswordVsEncodedPassword = oldPassword.equals(oldEncodePasswordDB);						
							String[] existingPasswordCount = findUserById.getLastPasswords().split(",");
							String password = user.getPassword();
							String confirmPassword = user.getConfirmPassword();
//							System.out.println("NEW PASSWORD : "+password);
//							System.out.println("Confirm PASSWORD : "+confirmPassword);
							boolean passwordMatches = Arrays.asList(existingPasswordCount).contains(password);
							boolean newPasswordAndConfirmPassword = password.equals(confirmPassword);
							int lastPasswordCounts = existingPasswordCount.length;
							int orgPasswordLimit = Integer.parseInt(getExistingPassByUserID.get().getOrganization().getLastPasswords());
							if(passwordVerificationForPlainTextAndEncodedPassword || passwordVerificationEncodedPasswordVsEncodedPassword) {
								String passwoString = getExistingPassByUserID.get().getPassword();
								if (!passwordMatches && newPasswordAndConfirmPassword) {
//									System.out.println("Password does not match.:::");
								findUserById.setUserFirstName(user.getUserFirstName());
								findUserById.setUserMobileNum(user.getUserMobileNum());
								findUserById.setLocation(user.getLocation());
								findUserById.setRole(roleRepository.findById(user.getRoleId()).get());
//								System.out.println("Last Password in OrgScope : "+getExistingPassByUserID.get().getOrganization().getLastPasswords());
								findUserById.setLastUpdatedPassword(new Date());
//						        System.out.println("existingPasswordCount: " + Arrays.toString(existingPasswordCount));
//								System.out.println("lastPasswordCounts : "+lastPasswordCounts);
//								System.out.println("orgPasswordLimit : "+orgPasswordLimit);
								if(orgPasswordLimit >= lastPasswordCounts) {
//									System.out.println("Count Matches..");
									if(existingPasswordCount.length > 0) {
										 String[] newValues = new String[existingPasswordCount.length - 1];
							             System.arraycopy(existingPasswordCount, 0, newValues, 0, existingPasswordCount.length - 1);
							                String remainingLastPasswords = String.join(",", newValues);
//							                System.out.println("remainingLastPasswords : "+remainingLastPasswords);
										 findUserById.setLastPasswords(getExistingPassByUserID.get().getAddlPassword()+","+remainingLastPasswords);
									}
									
								}else {	
									findUserById.setLastPasswords(getExistingPassByUserID.get().getAddlPassword()+","+getExistingPassByUserID.get().getLastPasswords());
								}
								if (!password.equals("")) {
									findUserById.setPassword(bCryptPasswordEncoder.encode(password));
									findUserById.setAddlPassword(password);
								} else {
									findUserById.setPassword(passwoString);
								}
								result = userRepository.save(findUserById);							
								BeanUtils.copyProperties(result, userDto);
								
								setSomeUserDataInDTO(userDto, result);
								
								svcSearchResult.setData(userDto);
								svcSearchResult.setOutcome(true);
								svcSearchResult.setMessage("User information Updated successfully, Click OK To Relogin");
								svcSearchResult.setStatus("Password Changed");
								
								}else {
									if(passwordMatches) {
//										System.out.println("Password match.");
										svcSearchResult.setOutcome(false);
										svcSearchResult.setMessage("New password should not be the same as the last "+orgPasswordLimit+" password.");
									}
									else if(!newPasswordAndConfirmPassword) {
//										System.out.println("New Password And ConfirmPasssword NOT matches.");
										svcSearchResult.setOutcome(false);
										svcSearchResult.setMessage("New password and confirm password do not match.");
									}
								}
							}
							else {
								if(!passwordVerificationForPlainTextAndEncodedPassword) {	
									svcSearchResult.setOutcome(false);
									svcSearchResult.setMessage("Old Password Invalid. ");
								}
//								else if(passwordMatches) {
//									System.out.println("Password match.");
//									svcSearchResult.setOutcome(false);
//									svcSearchResult.setMessage("Password Not Should be Same has old Password. ");
//								}
								else {
									svcSearchResult.setOutcome(false);
									svcSearchResult.setMessage("Something went Wrong.");
								}
							}
	
						}else {
							String passwoString = getExistingPassByUserID.get().getPassword();							
							String userEmailId = getExistingPassByUserID.get().getUserEmailId();							
							findUserById.setUserFirstName(user.getUserFirstName());
		//					findUserEmail.setUserLastName(user.getUserLastName());
		//					findUserEmail.setUserEmailId(user.getUserEmailId());
							findUserById.setUserMobileNum(user.getUserMobileNum());
							findUserById.setLocation(user.getLocation());
							findUserById.setRole(roleRepository.findById(user.getRoleId()).get());
							result = userRepository.save(findUserById);							
							BeanUtils.copyProperties(result, userDto);
							
							setSomeUserDataInDTO(userDto, result);
							
							svcSearchResult.setData(userDto);
							svcSearchResult.setOutcome(true);
							svcSearchResult.setMessage("User information Updated successfully");
		
					     }
				}else if (findUserById != null && findUserById.getUserId() != user.getUserId()) {
	
						svcSearchResult.setData(null);
						svcSearchResult.setOutcome(false);
						svcSearchResult.setMessage("User Email Id already exists.Choose another Email Id");
			    }else {
					Optional<User> userObj = userRepository.findById(user.getUserId());
					if (userObj.isPresent()) {
						User userObj1 = userObj.get();
						String passwoString = userObj.get().getPassword();
						if (!user.getPassword().equals("")) {
							userObj1.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
							userObj1.setAddlPassword(user.getPassword());
						} else {
							userObj1.setPassword(passwoString);
							userObj1.setAddlPassword(userObj1.getAddlPassword());
						}
						userObj1.setRole(roleRepository.findById(user.getRoleId()).get());
						userObj1.setEmployeeId(user.getEmployeeId());
						userObj1.setUserFirstName(user.getUserFirstName());
						userObj1.setUserLastName(user.getUserLastName());
						userObj1.setUserLandlineNum(user.getUserLandlineNum());
						userObj1.setLocation(user.getLocation());
						userObj1.setUserMobileNum(user.getUserMobileNum());
						userObj1.setUserEmailId(user.getUserEmailId());
						userObj1.setLastUpdatedOn(new Date());
						userObj1.setLastUpdatedBy(SecurityHelper.getCurrentUser());
						userObj1.setIsActive(user.getIsActive() != null ? user.getIsActive() : userObj1.getIsActive());
						if (SecurityHelper.getCurrentUser().getRole().getRoleCode().equals("ROLE_AGENTSUPERVISOR")) {
							userObj1.setAgentSupervisor(SecurityHelper.getCurrentUser());
						} else {
							userObj1.setAgentSupervisor(user.getAgentSupervisorId() != null
									? userRepository.findById(user.getAgentSupervisorId()).get()
									: null);
						}
						result = userRepository.save(userObj1);

						BeanUtils.copyProperties(result, userDto);

						setSomeUserDataInDTO(userDto, result);

						svcSearchResult.setData(userDto);
						svcSearchResult.setOutcome(true);
						svcSearchResult.setMessage("User information Updated successfully");
					}
				}
				//this  IF object will save the user
			} else {
				if (user.getUserEmailId() != null && user.getEmployeeId() != null) {
					User findUserEmail = userRepository.findByUserEmailId(user.getUserEmailId());
					if (findUserEmail != null) {

						svcSearchResult.setData(null);
						svcSearchResult.setOutcome(false);
						svcSearchResult.setMessage("User Email Id exists present.Choose another Email Id");
					} else {
						User userObj = userRepository.findByEmployeeId(user.getEmployeeId());
						if (userObj != null) {
							svcSearchResult.setData(null);
							svcSearchResult.setOutcome(false);
							svcSearchResult.setMessage("EmployeeId already exists.Choose another EmployeeId");
						} else {
							User saveNewUser = new User();

							BeanUtils.copyProperties(user, saveNewUser);

							//saveNewUser.setUserName(user.getEmployeeId());
							saveNewUser.setUserName(user.getUserEmailId().toLowerCase());
							saveNewUser.setAddlPassword(user.getPassword());
							saveNewUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
							saveNewUser.setIsUserBlocked(false);
							saveNewUser
									.setOrganization(organizationRepository.findById(user.getOrganizationId()).get());
							saveNewUser.setRole(roleRepository.findById(user.getRoleId()).get());
							saveNewUser.setIsActive(true);
							saveNewUser.setIsLocked(false);
							saveNewUser.setWrongLoginCount(0);
							saveNewUser.setIsLoggedIn(false);
							saveNewUser.setCreatedOn(new Date());
							if (SecurityHelper.getCurrentUser().getRole().getRoleCode()
									.equals("ROLE_AGENTSUPERVISOR")) {
								saveNewUser.setAgentSupervisor(SecurityHelper.getCurrentUser());
							} else {
								saveNewUser.setAgentSupervisor(user.getAgentSupervisorId() != null
										? userRepository.findById(user.getAgentSupervisorId()).get()
										: null);
							}
							saveNewUser.setCreatedBy(SecurityHelper.getCurrentUser());
							log.debug("User username is-->" + saveNewUser.getUserName());
							result = userRepository.save(saveNewUser);

							BeanUtils.copyProperties(result, userDto);

							setSomeUserDataInDTO(userDto, result);
							svcSearchResult.setData(userDto);
							svcSearchResult.setOutcome(true);
							svcSearchResult.setMessage("User information saved successfully");
						}
					}
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in saveUser method in UserServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes");
		}
		return svcSearchResult;
	}

	private UserDto setSomeUserDataInDTO(UserDto userDto, User result) {
		userDto.setOrganizationId(
				result.getOrganization() != null ? result.getOrganization().getOrganizationId() : null);
		userDto.setRoleId(result.getRole().getRoleId());
		userDto.setRoleName(result.getRole().getRoleName());
		userDto.setCreatedBy(result.getCreatedBy() != null ? result.getCreatedBy().getUserFirstName() : null);
		userDto.setCreatedOn(result.getCreatedOn());
		userDto.setLastUpdatedBy(result.getLastUpdatedBy() != null ? result.getLastUpdatedBy().getUserFirstName() : "");
		userDto.setLastUpdatedOn(result.getLastUpdatedOn() != null ? result.getLastUpdatedOn() : null);
		userDto.setAgentSupervisorId(
				result.getAgentSupervisor() != null ? result.getAgentSupervisor().getUserId() : null);
		userDto.setPassword(null);
		userDto.setOldPassword(null);
		return userDto;
	}

	@Override
	public ServiceOutcome<List<UserDto>> getUserByOrganizationIdAndUser(Long organizationId, User user) {
		ServiceOutcome<List<UserDto>> svcSearchResult = new ServiceOutcome<List<UserDto>>();
		List<UserDto> userDtoList = new ArrayList<UserDto>();
		List<User> userList = new ArrayList<User>();
		try {
			if (user.getRole().getRoleCode().equals("ROLE_ADMIN")) {
				userList = userRepository.findAllByOrganizationOrganizationId(organizationId);
				userList = userList.stream().filter(u -> !u.getRole().getRoleCode().equals("ROLE_ADMIN"))
						.collect(Collectors.toList());
			} else if (user.getRole().getRoleCode().equals("ROLE_PARTNERADMIN")) {
				userList = userRepository.findAllByOrganizationOrganizationId(organizationId);
				userList = userList.stream().filter(
						u -> !u.getRole().getRoleCode().equals("ROLE_ADMIN") && u.getUserId() != user.getUserId())
						.collect(Collectors.toList());
			} else {
				userList = userRepository.findAllByOrganizationOrganizationIdAndCreatedByUserId(organizationId,
						user.getUserId());
			}
			for (User userobj : userList) {
				if (userList != null) {
					UserDto userDto = new UserDto();
					BeanUtils.copyProperties(userobj, userDto);
					setSomeUserDataInDTO(userDto, userobj);
					
					userDto.setPassword(null);
					userDto.setOldPassword(null);
					
					userDtoList.add(userDto);
				}
			}
			if (!userDtoList.isEmpty()) {
				svcSearchResult.setData(userDtoList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO USER FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getUserByOrganizationId method in UserServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<UserDto> getUserById(Long userId) {
		ServiceOutcome<UserDto> svcSearchResult = new ServiceOutcome<UserDto>();
		try {
			Optional<User> user = userRepository.findById(userId);
			if (user.isPresent()) {

				UserDto userDto = new UserDto();

				BeanUtils.copyProperties(user.get(), userDto);
				setSomeUserDataInDTO(userDto, user.get());
				svcSearchResult.setData(userDto);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO USER FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getUserById method in UserServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<User> activeAndInactiveUserById(Long userId, Boolean isActive) {
		ServiceOutcome<User> svcSearchResult = new ServiceOutcome<>();
		try {
			User result = null;
			if (userId == null || userId.equals(0l)) {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("Please specify User");
			} else {
				Optional<User> userObj = userRepository.findById(userId);
				if (userObj.isPresent()) {
					User user = userObj.get();
					user.setIsActive(isActive);
					user.setIsUserBlocked(!isActive);
					result = userRepository.save(user);
					svcSearchResult.setData(result);
					svcSearchResult.setOutcome(true);
					if (isActive) {
						svcSearchResult.setMessage("User activated successfully.");
					}
					if (!isActive) {
						svcSearchResult.setMessage("User deactivated successfully.");
					}
				} else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage("No User Found");
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in activeAndInactiveUserById method in UserServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<User> findByUsername(String userName) {

		ServiceOutcome<User> svcOutcome = new ServiceOutcome<User>();
		try {
			User user = userRepository.findByUserName(userName);
			
			svcOutcome.setData(user);
		} catch (Exception ex) {
			log.error("Exception occured in findByUsername method in UserServiceImpl-->" + ex);

			svcOutcome.setData(null);
			svcOutcome.setOutcome(false);
			svcOutcome.setMessage("Error");

		}
		return svcOutcome;
	}

	@Override
	public ServiceOutcome<User> saveUserLoginData(User user) {
		ServiceOutcome<User> svcOutcome = new ServiceOutcome<User>();
		try {
			user = userRepository.saveAndFlush(user);
			svcOutcome.setData(user);
		} catch (Exception ex) {
			log.error("Exception occured in save method in UserServiceImpl-->" + ex);

			svcOutcome.setData(null);
			svcOutcome.setOutcome(false);
			svcOutcome.setMessage("Error");
		}

		return svcOutcome;
	}

	@Override
	public ServiceOutcome<User> getAdminDetailsForOrganization(Long organizationId) {
		ServiceOutcome<User> svcOutcome = new ServiceOutcome<User>();
		try {
			Role role = roleRepository.findRoleByRoleCode("ROLE_ADMIN");
			User user = userRepository.findByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue(organizationId,
					role.getRoleId());
			if (user != null) {
				
				svcOutcome.setData(user);
				svcOutcome.setOutcome(true);
				svcOutcome.setMessage("SUCCESS");
			} else {
				svcOutcome.setData(null);
				svcOutcome.setOutcome(false);
				svcOutcome.setMessage("ADMIN NOT FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getAdminDetailsForOrganization Method-->" + ex);
			svcOutcome.setData(null);
			svcOutcome.setOutcome(false);
			svcOutcome.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcOutcome;
	}

	@Override
	public ServiceOutcome<List<User>> getAgentSupervisorList(Long organizationId) {
		ServiceOutcome<List<User>> svcOutcome = new ServiceOutcome<List<User>>();
		try {
			Role role = roleRepository.findRoleByRoleCode("ROLE_AGENTSUPERVISOR");
			if (role != null) {
				List<User> userList = userRepository.findAllByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue(
						organizationId, role.getRoleId());
				if (!userList.isEmpty()) {
					userList = userList.stream().map(user -> {
				        user.setPassword(null);
				        user.setAddlPassword(null);
				        
				        if (user.getOrganization()!=null && user.getOrganization().getCreatedBy() != null) {
				        	//log.info("user.getOrganization()");
				        	user.getOrganization().getCreatedBy().setPassword(null);
				        	user.getOrganization().getCreatedBy().setAddlPassword(null);
				        	if(user.getOrganization().getCreatedBy().getOrganization()!=null &&
				        			user.getOrganization().getCreatedBy().getOrganization().getCreatedBy() != null) {
				        		//log.info("user.getOrganization().getCreatedBy().getOrganization()");
				        		user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().setPassword(null);
				        		user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().setAddlPassword(null);
				        		if(user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization()!=null
				        				&& user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy() != null) {
				        			//log.info("user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization()");
				        			user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy().setPassword(null);
				        			user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy().setAddlPassword(null);
				        		}
				        	}
				        }
				        
				        return user;
				    }).collect(Collectors.toList());
					svcOutcome.setData(userList);
					svcOutcome.setOutcome(true);
					svcOutcome.setMessage("SUCCESS");
				} else {
					svcOutcome.setData(null);
					svcOutcome.setOutcome(false);
					svcOutcome.setMessage("No Agent Supervisor found for this Organization");
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in getAgentSupervisorList Method-->" + ex);
			svcOutcome.setData(null);
			svcOutcome.setOutcome(false);
			svcOutcome.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcOutcome;
	}

	@Transactional
	@Override
	public ServiceOutcome<Boolean> saveAgentInformation(MultipartFile file) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		AgentSampleCsvXlsMaster agentSampleCsvXlsMaster = null;
		try {
			System.out.println("inside service");
			User user = SecurityHelper.getCurrentUser();
			List<User> users = null;
			if (CSVUtil.hasCSVFormat(file)) {
				users = cSVUtil.csvToUserList(file.getInputStream());
				agentSampleCsvXlsMaster = new AgentSampleCsvXlsMaster();
				agentSampleCsvXlsMaster.setAgentSampleCsv(file.getBytes());
			}
			if (ExcelUtil.hasExcelFormat(file)) {
				users = excelUtil.excelToUserList(file.getInputStream());
				agentSampleCsvXlsMaster = new AgentSampleCsvXlsMaster();
				agentSampleCsvXlsMaster.setAgentSampleXls(file.getBytes());
			}
			List<String> employeeIdList = new ArrayList<String>();

			for (User userObj : users) {
				System.out.println("inside for");

				userObj.setUserName(userObj.getUserName());
				employeeIdList.add(userObj.getEmployeeId());
				userObj.setOrganization(
						organizationRepository.findById(user.getOrganization().getOrganizationId()).get());
				userObj.setCreatedOn(new Date());
				userObj.setCreatedBy(user);
				char[] possibleCharacters = (new String("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?")).toCharArray();
				String randomStr = RandomStringUtils.random( 15, 0, possibleCharacters.length-1, false, false, possibleCharacters, new SecureRandom() );
				userObj.setAddlPassword(randomStr);
				userObj.setPassword(bCryptPasswordEncoder.encode(randomStr));
				userObj.setIsUserBlocked(false);
				userObj.setIsActive(true);
				userObj.setIsLocked(false);
				userObj.setWrongLoginCount(0);
				userObj.setIsLoggedIn(false);
				userObj.setRole(roleRepository.findRoleByRoleCode("ROLE_AGENTHR"));

			}
			List<User> userList = userRepository.saveAllAndFlush(users);

			if (!userList.isEmpty()) {
				AgentInvitationSentDto agentInvitationSentDto = new AgentInvitationSentDto();
				agentInvitationSentDto.setEmployeeId(employeeIdList);
				System.out.println(employeeIdList + "referenceList");
				ServiceOutcome<Boolean> svcOutcome = userService.invitationSent(agentInvitationSentDto);

				agentSampleCsvXlsMaster.setOrganization(
						organizationRepository.findById(user.getOrganization().getOrganizationId()).get());
				agentSampleCsvXlsMaster.setUploadedTimestamp(new Date());
				agentSampleCsvXlsMaster.setCreatedBy(user);
				agentSampleCsvXlsMaster.setCreatedOn(new Date());
				agentSampleCsvXlsMasterRepository.save(agentSampleCsvXlsMaster);

				svcSearchResult.setData(true);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Agents uploaded successfully.");
			} else {
				svcSearchResult.setData(false);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage(file.getOriginalFilename() + " could not be uploaded.");
			}
		} catch (IOException e) {
			svcSearchResult.setData(false);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Unable to upload agent details.");
			log.error("Exception occured in saveAgentInformation method in UserServiceImpl-->" + e);
			throw new RuntimeException("fail to store csv/xls data: " + e.getMessage());
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public ServiceOutcome<Boolean> invitationSent(AgentInvitationSentDto agentInvitationSentDto) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			System.out.println(agentInvitationSentDto + "agentInvitationSentDto");
			for (int i = 0; i < agentInvitationSentDto.getEmployeeId().size(); i++) {
				User users = userRepository.findByEmployeeId(agentInvitationSentDto.getEmployeeId().get(i));
				if (users != null) {
					Boolean result = emailSentTask.sendAgentEmail(users.getAddlPassword(), users.getUserFirstName(),
							users.getUserEmailId(), users.getReportingEmailId());
					System.out.println(agentInvitationSentDto + "agentInvi");
				}
			}

		} catch (Exception ex) {
			log.error("Exception occured in invitationSent method in CandidateServiceImpl-->", ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			// svcSearchResult.setMessage(messageSource.getMessage("ERROR.MESSAGE", null,
			// LocaleContextHolder.getLocale()));
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<UserDto> getUserProfile() {
		ServiceOutcome<UserDto> svcSearchResult = new ServiceOutcome<UserDto>();
		try {
			User user = SecurityHelper.getCurrentUser();
			Optional<User> userObj = userRepository.findById(user.getUserId());
			if (userObj.isPresent()) {
				UserDto userDto = new UserDto();
				BeanUtils.copyProperties(userObj.get(), userDto);
				setSomeUserDataInDTO(userDto, userObj.get());
				svcSearchResult.setData(userDto);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO USER FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getUserProfile method in UserServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<List<User>> getAdminList() {
		ServiceOutcome<List<User>> svcSearchResult = new ServiceOutcome<List<User>>();
		try {
			List<User> adminUserList = userRepository.findByRoleRoleCode("ROLE_ADMIN");
			adminUserList = adminUserList.stream().map(user -> {
		        user.setPassword(null);
		        user.setAddlPassword(null);
		        
		        if (user.getOrganization()!=null && user.getOrganization().getCreatedBy() != null) {
		        	//log.info("user.getOrganization()");
		        	user.getOrganization().getCreatedBy().setPassword(null);
		        	user.getOrganization().getCreatedBy().setAddlPassword(null);
		        	if(user.getOrganization().getCreatedBy().getOrganization()!=null &&
		        			user.getOrganization().getCreatedBy().getOrganization().getCreatedBy() != null) {
		        		//log.info("user.getOrganization().getCreatedBy().getOrganization()");
		        		user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().setPassword(null);
		        		user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().setAddlPassword(null);
		        		if(user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization()!=null
		        				&& user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy() != null) {
		        			//log.info("user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization()");
		        			user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy().setPassword(null);
		        			user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy().setAddlPassword(null);
		        		}
		        	}
		        }
		        
		        return user;
		    }).collect(Collectors.toList());
			svcSearchResult.setData(adminUserList);
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("SUCCESS");
		} catch (Exception ex) {
			log.error("Exception occured in getAdminList method in UserServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<User> activeNInAtiveAdmin(Long userId, Boolean isActive) {
		ServiceOutcome<User> svcSearchResult = new ServiceOutcome<User>();
		try {
			Optional<User> userObj = userRepository.findById(userId);
			if (userObj.isPresent()) {
				User user = userObj.get();
				Role role = roleRepository.findRoleByRoleCode("ROLE_ADMIN");
				User userActive = userRepository.findByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue(
						user.getOrganization().getOrganizationId(), role.getRoleId());
				if (userActive != null) {
					svcSearchResult.setData(user);
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage(
							"Only one admin can be active in one time. Please deactivate one before continuing.");
				} else {
					user.setIsActive(isActive);
					user.setIsUserBlocked(!isActive);
					user = userRepository.save(user);
					svcSearchResult.setData(user);
					svcSearchResult.setOutcome(true);
					if (isActive) {
						svcSearchResult.setMessage("Admin activated successfully.");
					}
					if (!isActive) {
						svcSearchResult.setMessage("Admin deactivated successfully.");
					}
				}

			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("No User Found");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getUserProfile method in UserServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<List<User>> getAgentList(Long organizationId) {
		ServiceOutcome<List<User>> svcOutcome = new ServiceOutcome<List<User>>();
		try {
			User user = SecurityHelper.getCurrentUser();
			List<User> userList = new ArrayList<User>();
			Role role = roleRepository.findRoleByRoleCode("ROLE_AGENTHR");
			if (role != null) {
				if (organizationId != 0) {
					if (user.getRole().getRoleCode().equals("ROLE_AGENTSUPERVISOR")) {
						userList = userRepository.findAllByAgentSupervisorUserIdAndRoleRoleIdAndIsActiveTrue(
								user.getUserId(), role.getRoleId());
					} else {
						userList = userRepository.findAllByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue(
								organizationId, role.getRoleId());
					}
				} else {
					userList = userRepository.findAllByRoleRoleIdAndIsActiveTrue(role.getRoleId());
				}
				if (!userList.isEmpty()) {
					userList = userList.stream().map(u -> {
				        u.setPassword(null);
				        u.setAddlPassword(null);
				        
				        if (u.getOrganization()!=null && u.getOrganization().getCreatedBy() != null) {
				        	//log.info("user.getOrganization()");
				        	u.getOrganization().getCreatedBy().setPassword(null);
				        	u.getOrganization().getCreatedBy().setAddlPassword(null);
				        	if(u.getOrganization().getCreatedBy().getOrganization()!=null &&
				        			u.getOrganization().getCreatedBy().getOrganization().getCreatedBy() != null) {
				        		//log.info("user.getOrganization().getCreatedBy().getOrganization()");
				        		u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().setPassword(null);
				        		u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().setAddlPassword(null);
				        		if(u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization()!=null
				        				&& u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy() != null) {
				        			//log.info("user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization()");
				        			u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy().setPassword(null);
				        			u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy().setAddlPassword(null);
				        		}
				        	}
				        }
				        
				        return u;
				    }).collect(Collectors.toList());
					svcOutcome.setData(userList);
					svcOutcome.setOutcome(true);
					svcOutcome.setMessage("SUCCESS");
				} else {
					svcOutcome.setData(null);
					svcOutcome.setOutcome(false);
					svcOutcome.setMessage("No Agent found for this Organization");
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in getAgentList Method-->", ex);
			svcOutcome.setData(null);
			svcOutcome.setOutcome(false);
			svcOutcome.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcOutcome;
	}

	@Override
	public ServiceOutcome<User> getUserByUserId(Long userId) {
		ServiceOutcome<User> svcSearchResult = new ServiceOutcome<User>();
		try {
			Optional<User> user = userRepository.findById(userId);
			if (user.isPresent()) {
				
				svcSearchResult.setData(user.get());
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO USER FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getUserById method in UserServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<List<User>> getUsersByRoleCode(String roleCode) {
		roleCode = roleCode.replaceAll("\"", "");
		ServiceOutcome<List<User>> svcSearchResult = new ServiceOutcome<List<User>>();
		List<User> userList = new ArrayList<User>();
		try {
			if (roleCode.equals("ROLE_ADMIN")) {
				User user = SecurityHelper.getCurrentUser();
				userList = userRepository
						.findAllByOrganizationOrganizationId(user.getOrganization().getOrganizationId());
				userList = userList.stream().filter(u -> !u.getRole().getRoleCode().equals("ROLE_ADMIN"))
						.collect(Collectors.toList());
			} else if (roleCode.equals("ROLE_PARTNERADMIN")) {
				User user = SecurityHelper.getCurrentUser();
				userList = userRepository
						.findAllByOrganizationOrganizationId(user.getOrganization().getOrganizationId());
				userList = userList.stream().filter(
						u -> !u.getRole().getRoleCode().equals("ROLE_ADMIN") && u.getUserId() != user.getUserId())
						.collect(Collectors.toList());
			} else if (roleCode.equals("ROLE_AGENTSUPERVISOR")) {
				User user = SecurityHelper.getCurrentUser();
				userList = userRepository.findAllByOrganizationOrganizationIdAndCreatedByUserId(
						user.getOrganization().getOrganizationId(), user.getUserId());
			} else {
				userList = userRepository.findByIsActiveTrue();
			}
			if (!userList.isEmpty()) {
				userList = userList.stream().map(user -> {
					        user.setPassword(null);
					        user.setAddlPassword(null);
					        
					        if (user.getOrganization()!=null && user.getOrganization().getCreatedBy() != null) {
					        	//log.info("user.getOrganization()");
					        	user.getOrganization().getCreatedBy().setPassword(null);
					        	user.getOrganization().getCreatedBy().setAddlPassword(null);
					        	if(user.getOrganization().getCreatedBy().getOrganization()!=null &&
					        			user.getOrganization().getCreatedBy().getOrganization().getCreatedBy() != null) {
					        		//log.info("user.getOrganization().getCreatedBy().getOrganization()");
					        		user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().setPassword(null);
					        		user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().setAddlPassword(null);
					        		if(user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization()!=null
					        				&& user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy() != null) {
					        			//log.info("user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization()");
					        			user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy().setPassword(null);
					        			user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy().setAddlPassword(null);
					        		}
					        	}
					        }
					        
					        return user;
					    }).collect(Collectors.toList());
				svcSearchResult.setData(userList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO USERS FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getUsersByRoleCode method in UserServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public void logoutUserAfter5Mins() {
		try {
			userRepository.logoutUserAfter5Mins();
		} catch (Exception ex) {
			log.error("Exception occured in logoutUserAfter5Mins method in UserServiceImpl-->", ex);
		}

	}

	@Override
	public ServiceOutcome<List<User>> getVendorList(Long organizationId) {
		ServiceOutcome<List<User>> svcOutcome = new ServiceOutcome<List<User>>();
		try {
			User user = SecurityHelper.getCurrentUser();
			List<User> userList = new ArrayList<User>();
			Role role = roleRepository.findRoleByRoleCode("ROLE_VENDOR");
			if (role != null) {
//				if (organizationId != 0) {
//					if (user.getRole().getRoleCode().equals("ROLE_AGENTSUPERVISOR")) {
//						userList = userRepository.findAllByAgentSupervisorUserIdAndRoleRoleIdAndIsActiveTrue(
//								user.getUserId(), role.getRoleId());
//					} else {
//						userList = userRepository.findAllByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue(
//								organizationId, role.getRoleId());
//					}
//				} else {
					userList = userRepository.findAllByRoleRoleIdAndIsActiveTrue(role.getRoleId());
						
				//}
				if (!userList.isEmpty()) {
					userList = userList.stream().map(u -> {
				        u.setPassword(null);
				        u.setAddlPassword(null);
				        
				        if (u.getOrganization()!=null && u.getOrganization().getCreatedBy() != null) {
				        	//log.info("user.getOrganization()");
				        	u.getOrganization().getCreatedBy().setPassword(null);
				        	u.getOrganization().getCreatedBy().setAddlPassword(null);
				        	if(u.getOrganization().getCreatedBy().getOrganization()!=null &&
				        			u.getOrganization().getCreatedBy().getOrganization().getCreatedBy() != null) {
				        		//log.info("user.getOrganization().getCreatedBy().getOrganization()");
				        		u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().setPassword(null);
				        		u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().setAddlPassword(null);
				        		if(u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization()!=null
				        				&& u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy() != null) {
				        			//log.info("user.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization()");
				        			u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy().setPassword(null);
				        			u.getOrganization().getCreatedBy().getOrganization().getCreatedBy().getOrganization().getCreatedBy().setAddlPassword(null);
				        		}
				        	}
				        }
				        
				        return u;
				    }).collect(Collectors.toList());
					svcOutcome.setData(userList);
					svcOutcome.setOutcome(true);
					svcOutcome.setMessage("SUCCESS");
				} else {
					svcOutcome.setData(null);
					svcOutcome.setOutcome(false);
					svcOutcome.setMessage("No Vendor found for this Organization");
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in getAgentList Method-->", ex);
			svcOutcome.setData(null);
			svcOutcome.setOutcome(false);
			svcOutcome.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcOutcome;
	}

	// This is for IntiateVendor
	@Override
	public ServiceOutcome<List<VendorInitiatDto>> getVendorCheckDetails(Long candidateId) {
		ServiceOutcome<List<VendorInitiatDto>> svcSearchResult = new ServiceOutcome<List<VendorInitiatDto>>();
		try {
			System.out.println(candidateId);
			// List<VendorChecks> vendorList= vendorChecksRepository.findAll();

			List<VendorChecks> vendorList = vendorChecksRepository.findAllByCandidateCandidateId(candidateId);
			
			List<VendorInitiatDto> vendorInitiateDto = new ArrayList<>();
			String addressPresent = null;
			String addressPermanent = null;
			for(VendorChecks vendorCheck : vendorList) {
				VendorInitiatDto vendorDto = new VendorInitiatDto();
				
				ArrayList<String> attributeValue = vendorCheck.getAgentAttirbuteValue();

				for (String attribute : attributeValue) {
				    // Check if the attribute contains "checkType=Address present"
				    if (attribute.contains("checkType=Address present")) {
				        for (String attributeIN : attributeValue) {
				            // Check if the source name is "Address"
				            if (vendorCheck.getSource().getSourceName().equalsIgnoreCase("Address")) {
				                int addressIndex = attributeIN.toLowerCase().indexOf("address=");
				                if (addressIndex != -1) {
				                    int commaIndex = attributeIN.indexOf(",", addressIndex);
				                    if (commaIndex == -1) {
				                        commaIndex = attributeIN.length();
				                    }
				                    addressPresent = attributeIN.substring(addressIndex + 8).trim();
				                    break;
				                }
				            }
				        }
				    }
				    
				    else if(attribute.contains("checkType=Address permanent")) {
				    	for (String attributeIN : attributeValue) {
				            // Check if the source name is "Address"
				            if (vendorCheck.getSource().getSourceName().equalsIgnoreCase("Address")) {
				                // Extract the address value
				                int addressIndex = attributeIN.toLowerCase().indexOf("address=");
				                if (addressIndex != -1) {
				                    int commaIndex = attributeIN.indexOf(",", addressIndex);
				                    if (commaIndex == -1) {
				                        commaIndex = attributeIN.length();
				                    }
				                    // Extract the address value
				                    addressPermanent = attributeIN.substring(addressIndex + 8).trim();
				                    break;
				                }
				            }
				        }
				    }
				}
				
//				System.out.println("Address: Prsent: " + addressPresent);
//				System.out.println("Address: Permanent: " + addressPermanent);
//				System.out.println("Address: Prsent for Criminal " + criminalPresent);
//				System.out.println("Address: Permanent for Criminal " + criminalPermanent);
				
				String agentAttributeAndValue = vendorCheck.getAgentAttirbuteValue().toString();				
			


				// Extracting type value
				String type = extractValue(agentAttributeAndValue, "type");
				// Extracting Employer Name value
				String employerName = extractValue(agentAttributeAndValue, "Employers Name");
				String qualification = extractValue(agentAttributeAndValue,"Qualification attained");
				String address = extractAddress(agentAttributeAndValue);
				String idItems = null;

				if(vendorCheck.getSource().getSourceName().equalsIgnoreCase("Employment")) {
					VendorUploadChecks byVendorChecksVendorcheckId = vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorCheck.getVendorcheckId());
					if(employerName == null || employerName.isEmpty()) {
						if(byVendorChecksVendorcheckId != null) {	
							String vendorAttributeValue = byVendorChecksVendorcheckId.toString();
							String employerNameByQc = extractValue(vendorAttributeValue, "Employers Name");
							vendorDto.setDetails(employerNameByQc);
						}
					}
					else {
						vendorDto.setDetails(employerName);
					}
					

				}else if(vendorCheck.getSource().getSourceName().equalsIgnoreCase("Education")) {
					VendorUploadChecks byVendorChecksVendorcheckId = vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorCheck.getVendorcheckId());
					if(byVendorChecksVendorcheckId != null) {	
						if(qualification != null) {	
							vendorDto.setDetails(qualification);
						}else {
							String prodaptQualification = extractValue(byVendorChecksVendorcheckId.toString(),"Complete Name of Qualification/ Degree Attained");
							vendorDto.setDetails(prodaptQualification);
						}
					}
				}else if(vendorCheck.getSource().getSourceName().equalsIgnoreCase("Address") ||
						vendorCheck.getSource().getSourceName().equalsIgnoreCase("Criminal") ||
						vendorCheck.getSource().getSourceName().equalsIgnoreCase("Global Database check")) {
					vendorDto.setDetails(address);
				}
				else if(vendorCheck.getSource().getSourceName().equalsIgnoreCase("ID Items")) {
					VendorUploadChecks byVendorChecksVendorcheckId = vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorCheck.getVendorcheckId());
					if(byVendorChecksVendorcheckId != null) {
						String vendorAttributeValue = byVendorChecksVendorcheckId.toString();
						idItems = extractValue(vendorAttributeValue, "proofName");
						vendorDto.setDetails(idItems);		
					}
				}
				
				vendorDto.setCandidateName(vendorCheck.getCandidateName());
				vendorDto.setAgentAttirbuteValue(vendorCheck.getAgentAttirbuteValue());
//				vendorDto.setAgentUploadedDocument(vendorCheck.getAgentUploadedDocument());
				vendorDto.setAgentUploadDocumentPathKey(vendorCheck.getAgentUploadDocumentPathKey());
				vendorDto.setCandidate(vendorCheck.getCandidate());
				vendorDto.setContactNo(vendorCheck.getContactNo());
				vendorDto.setCreatedBy(vendorCheck.getCreatedBy());
				vendorDto.setCreatedOn(vendorCheck.getCreatedOn());
				vendorDto.setDateOfBirth(vendorCheck.getDateOfBirth());
				vendorDto.setIsproofuploaded(vendorCheck.getIsproofuploaded());
				vendorDto.setSource(vendorCheck.getSource());
				vendorDto.setStopCheck(vendorCheck.getStopCheck());
				vendorDto.setStopCheckCreatedOn(vendorCheck.getStopCheckCreatedOn());
				vendorDto.setVenderAttirbuteValue(vendorCheck.getVenderAttirbuteValue());
				vendorDto.setVendorcheckId(vendorCheck.getVendorcheckId());
				vendorDto.setVendorCheckStatusMaster(vendorCheck.getVendorCheckStatusMaster());
				vendorDto.setVendorIds(userRepository.findByUserId(vendorCheck.getVendorId()));
				vendorDto.setDocumentname(vendorCheck.getDocumentname());
				vendorDto.setCheckType(vendorCheck.getCheckType());
				vendorDto.setType(type);
//				vendorDto.setClientApproval(vendorCheck.getClientApproval());
				Long orgId = vendorCheck.getCandidate().getOrganization().getOrganizationId();
				List<ServiceTypeConfig> allByOrganizationOrganizationId = serviceTypeConfigRepository.findAllByOrganizationOrganizationId(orgId);
				List<Long> sourceServiceIds = allByOrganizationOrganizationId.stream()
					    .map(ServiceTypeConfig::getServiceSourceMaster) // returns ServiceSourceMaster
					    .map(ServiceSourceMaster::getSourceServiceId) // returns String
					    .collect(Collectors.toList());
		        
		        ServiceSourceMaster byServiceCode = serviceSourceMasterRepository.findByServiceCode("CONVENTIONALCLIENTAPPROVAL");
		        if(byServiceCode != null) {
		        	if(!sourceServiceIds.isEmpty() && sourceServiceIds != null && sourceServiceIds.contains(byServiceCode.getSourceServiceId())) {
		        		vendorDto.setClientApproval(vendorCheck.getClientApproval());
		        	}else {
		        		vendorDto.setClientApproval(true);
		        	}
		        }

				vendorInitiateDto.add(vendorDto);

				}			
			if (!vendorList.isEmpty()) {
				List<VendorInitiatDto> newList = new ArrayList<VendorInitiatDto>();
				newList.addAll(vendorInitiateDto);
				svcSearchResult.setData(newList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO VENDORCHECKS FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getVendorCheckDetails method in userServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}
	
	private static String extractValue(String response, String key) {
        String pattern = key + "=([^,\\]]+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(response);
        if (m.find()) {
            return m.group(1).trim();
        }
        return null;
    }
 
 private static String extractAddress(String response) {
        String pattern = "Address=([^=]+)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(response);
        if (m.find()) {
        	String addressWithComma = m.group(1).trim();
            int lastCommaIndex = addressWithComma.lastIndexOf(",");
            if (lastCommaIndex != -1) {
                return addressWithComma.substring(0, lastCommaIndex);
            }
            return addressWithComma;
        }
        return null;
    }
	
//		public static String extractAddress(String response) {
//	        // Match the address, which is everything after "Address=" and before ", type="
//	        String pattern = "Address=([^,]+(?:,[^,=]+)*)";
//	        Pattern p = Pattern.compile(pattern);
//	        Matcher m = p.matcher(response);
//	        if (m.find()) {
//	            String address = m.group(1).trim();
//	            // Remove trailing commas or non-address parts
//	            if (address.contains(", type")) {
//	                address = address.substring(0, address.indexOf(", type"));
//	            }
//	            else if(address.contains(", Address")) {
//	                address = address.substring(0, address.indexOf(", Address"));
//	            }
//	            // Trim any trailing square brackets or spaces
//	            address = address.replaceAll("\\]$", "").trim();
//	            return address;
//	        }
//	        return null;
//	    }
	

 // This is for Vendor Dashboard
	@Override
	public ServiceOutcome<List<VendorcheckdashbordtDto>> getallVendorCheckDetails(Long vendorId,Map<String, String> dateSearchFilter,int pageNumber, int pageSize, String vendorCheckDashboardStatusCode) {
		ServiceOutcome<List<VendorcheckdashbordtDto>> svcSearchResult = new ServiceOutcome<List<VendorcheckdashbordtDto>>();
		try {
			System.out.println(vendorId + "5666666666666666666");
			String fromDate = dateSearchFilter.get("fromDate");
			String toDate = dateSearchFilter.get("toDate");
            
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			LocalDate from = LocalDate.parse(fromDate, inputFormatter);
			LocalDate to = LocalDate.parse(toDate, inputFormatter);

			// Set the time portions to 12:00 AM and 11:59 PM, respectively
			LocalTime fromTime = LocalTime.of(0, 0);
			LocalTime toTime = LocalTime.of(23, 59, 59);

			LocalDateTime fromDateTime = from.atTime(fromTime);
			LocalDateTime toDateTime = to.atTime(toTime);

			String formattedFromDate = fromDateTime.format(outputFormatter);
			String formattedToDate = toDateTime.format(outputFormatter);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date fromDateFormat = dateFormat.parse(formattedFromDate);
			Date toDateFormat = dateFormat.parse(formattedToDate);
			
			Pageable pageable = PageRequest.of(pageNumber, pageSize);
			
			if(vendorCheckDashboardStatusCode.equalsIgnoreCase("NEW UPLOAD")) {
				vendorCheckDashboardStatusCode = "INPROGRESS";
			}
			
			VendorCheckStatusMaster findByCheckStatusCode = vendorCheckStatusMasterRepository.findByCheckStatusCode(vendorCheckDashboardStatusCode);
			Long vendorCheckStatusMasterId = findByCheckStatusCode.getVendorCheckStatusMasterId();
			
			System.out.println("findByCheckStatusCode : ");
//			System.out.println("VENDORCHECKSTATUSMASTERID =================== :::::::::"+vendorCheckStatusMasterId);
			
			Page<VendorChecks> vendorList = vendorChecksRepository.findAllByVendorId(vendorId,fromDateFormat,toDateFormat,vendorCheckStatusMasterId,pageable);
			
			// Retrieve the list of items on the current page
			List<VendorChecks> vendorCheckList = vendorList.getContent();
			
			List<VendorcheckdashbordtDto> vendorCheckList2 = vendorCheckList.stream()
			        .map(vendorChecks -> {
			            // Your conversion logic here
			        	VendorcheckdashbordtDto dto = new VendorcheckdashbordtDto();
//			            dto.setApplicantId(vendorChecks.getCandidate().getApplicantId());
//			            dto.setAgentUploadedDocument(vendorChecks.getAgentUploadedDocument());
//			            dto.setCandidate_name(vendorChecks.getCandidateName());
			        	dto.setAgentUploadDocumentPathKey(vendorChecks.getAgentUploadDocumentPathKey());
			        	dto.setCandidate(vendorChecks.getCandidate());
			            dto.setSource(vendorChecks.getSource());
			            dto.setCreatedOn(vendorChecks.getCreatedOn());
			            dto.setAgentAttirbuteValue(vendorChecks.getAgentAttirbuteValue());
			            dto.setVendorCheckStatusMaster(vendorChecks.getVendorCheckStatusMaster());
			            dto.setStopCheck(vendorChecks.getStopCheck());
			            dto.setVendorUploadCheck(vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId()));
			            dto.setDocumentname(vendorChecks.getDocumentname());
			            dto.setVendorcheckId(vendorChecks.getVendorcheckId());
			          //  if ("Prodapt Solutions Private Limited".equalsIgnoreCase(vendorChecks.getCandidate().getOrganization().getOrganizationName())) dto.setClientApproval(vendorChecks.getClientApproval());
			            if ("Prodapt Solutions Private Limited".equalsIgnoreCase(vendorChecks.getCandidate().getOrganization().getOrganizationName())) {
			                dto.setClientApproval(vendorChecks.getClientApproval());
			            } else {
			                dto.setClientApproval(true);
			            }
			            return dto;
			        })
			        .collect(Collectors.toList());
			
			
			int currentPageNumber = vendorList.getNumber();  
			int totalPages = vendorList.getTotalPages();
			String totalPagesString = String.valueOf(totalPages);
			long totalElements = vendorList.getTotalElements();
			boolean hasNextPage = vendorList.hasNext(); 
			boolean hasPreviousPage = vendorList.hasPrevious();

			if (!vendorList.isEmpty()) {
				List<VendorcheckdashbordtDto> newList = new ArrayList<VendorcheckdashbordtDto>();
				newList.addAll(vendorCheckList2);
				svcSearchResult.setData(newList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
				svcSearchResult.setStatus(totalPagesString);
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO VENDORCHECKS FOUND");
			}
		} catch (Exception ex) {
			log.error("Exception occured in getVendorCheckDetails method in userServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

//	@Override
//	public ServiceOutcome<VendorChecks> saveproofuploadVendorChecks(String vendorChecksString,MultipartFile proofDocumentNew) {
//		System.out.println(proofDocumentNew+"==========================="+vendorChecksString);
//		ServiceOutcome<VendorChecks> svcSearchResult = new ServiceOutcome<VendorChecks>();
//		VendorUploadChecks result=null;
//		// VendorCheckStatusMaster vendorCheckStatusMaster =null;
//		
//		try {
//			
//			VendorcheckdashbordtDto vendorcheckdashbordtDto  = new ObjectMapper().readValue(vendorChecksString, VendorcheckdashbordtDto.class);
//			System.out.println(vendorcheckdashbordtDto+"------------------------");
//			VendorChecks vendorCheckss= vendorChecksRepository.findByVendorcheckId(vendorcheckdashbordtDto.getVendorcheckId());
//			System.out.println(vendorCheckss.getVendorcheckId()+"------------------ert------");
//			VendorUploadChecks vendorChecks= vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorcheckdashbordtDto.getVendorcheckId());
//			User user = SecurityHelper.getCurrentUser();
//			
//			if(vendorChecks == null ){
//				VendorUploadChecks vendorUploadChecks=new VendorUploadChecks();
//				System.out.println("-------------create------");
//				vendorUploadChecks.setVendorUploadedDocument(proofDocumentNew!=null?proofDocumentNew.getBytes():null);
//				vendorUploadChecks.setAgentColor(colorRepository.findById(vendorcheckdashbordtDto.getColorid()).get());
//				vendorUploadChecks.setCreatedOn(new Date());
//				vendorUploadChecks.setCreatedBy(user);
//				vendorUploadChecks.setVendorChecks(vendorCheckss);
//				vendorUploadChecks.setDocumentname(vendorcheckdashbordtDto.getDocumentname());
//				
//				
//				
//				// vendorCheckStatusMaster=vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId());
//				// vendorUploadChecks.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId()).get());
//				// vendorUploadChecks.setVendorCheckStatusId(vendorcheckdashbordtDto.getVendorCheckStatusMasterId());
//				System.out.println("-------------------==========getVendorCheckStatusMasterId");
//				result=vendorUploadChecksRepository.save(vendorUploadChecks);
//				if(result!=null) {
//					System.out.println("-------------------==========getVendorCheckStatusMasterId");
//					System.out.println("candidate");
//					VendorChecks vendorChecksnew= vendorChecksRepository.findByVendorcheckId(vendorcheckdashbordtDto.getVendorcheckId());
//					vendorChecksnew.setIsproofuploaded(true);
//					vendorChecksnew.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId()).get());
//					vendorChecksRepository.save(vendorChecksnew);
//					svcSearchResult.setMessage("vendorchecks document saved successfully.");
//			
//				}else {
//					System.out.println("-------------candidate-----else------");
//					svcSearchResult.setData(null);
//					svcSearchResult.setOutcome(false);
//					// svcSearchResult.setMessage(messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
//				}
//				
//				}
//			else{
//				System.out.println("-------------update------");
//				vendorChecks.setVendorUploadedDocument(proofDocumentNew!=null?proofDocumentNew.getBytes():null);
//				vendorChecks.setAgentColor(colorRepository.findById(vendorcheckdashbordtDto.getColorid()).get());
//				vendorChecks.setCreatedOn(new Date());
//				vendorChecks.setCreatedBy(user);
//				vendorChecks.setDocumentname(vendorcheckdashbordtDto.getDocumentname());
//				// vendorChecks.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId()).get());
//				result=vendorUploadChecksRepository.save(vendorChecks);
//			
//				if(result!=null) {
//				
//					System.out.println("candidate");
//					VendorChecks vendorChecksnew= vendorChecksRepository.findByVendorcheckId(vendorcheckdashbordtDto.getVendorcheckId());
//					vendorChecksnew.setIsproofuploaded(true);
//					vendorChecksnew.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId()).get());
//					vendorChecksRepository.save(vendorChecksnew);
//					svcSearchResult.setMessage("vendorchecks document update successfully.");
//			
//				}else {
//					System.out.println("-------------candidate-----else------");
//					svcSearchResult.setData(null);
//					svcSearchResult.setOutcome(false);
//					// svcSearchResult.setMessage(messageSource.getMessage("msg.error", null, LocaleContextHolder.getLocale()));
//				}
//
//			}
//			
//			
//		}
//		catch(Exception ex)
//		{
//			log.error("Exception occured in saveproofuploadVendorChecks method in userServiceImpl-->"+ex);
//			
//		}
//		return svcSearchResult;
//	}

	@Override
	public ServiceOutcome<VendorChecks> saveproofuploadVendorChecks(String vendorChecksString,
			String vendorAttributesValue, MultipartFile proofDocumentNew) {
		System.out.println(proofDocumentNew + "===========================" + vendorChecksString);
		ServiceOutcome<VendorChecks> svcSearchResult = new ServiceOutcome<VendorChecks>();
		VendorUploadChecks result = null;
		// VendorCheckStatusMaster vendorCheckStatusMaster =null;
		boolean containsScriptOrHTMLTags = false;
		try {
			String content = null;
			if(proofDocumentNew != null) {
				 content = new String(proofDocumentNew.getBytes(), StandardCharsets.UTF_8);
				containsScriptOrHTMLTags = validatePDFContent(content);
//             System.out.println("containsScriptOrHTMLTags>>>>>>"+containsScriptOrHTMLTags);
				if (containsScriptOrHTMLTags) {
					log.warn("PDF content contains script or HTML tags.");
					
					// Handle validation failure
				} else {
					log.info("PDF content does not contain script or HTML tags.");
					// Continue processing PDF content
				}
			}
//			log.info("VENDOR CHECK PRROF FILE content::{}",content);
		} catch (IOException e) {
			log.error("EXCEPTION IN READING THE VENDOR CHECK PRROF FILE::{}",e);
		}
		try {
			System.out.println("VENDOR_ATTRIBUTE_VALUE++++++" + vendorAttributesValue);
			if(!containsScriptOrHTMLTags) {
				ObjectMapper objectMapper = new ObjectMapper();
			VendorcheckdashbordtDto vendorcheckdashbordtDto = new ObjectMapper().readValue(vendorChecksString,
					VendorcheckdashbordtDto.class);
//			System.out.println(vendorcheckdashbordtDto + "------------------------");
			System.out.println("--------getVendorcheckId---------------"+vendorcheckdashbordtDto.getVendorcheckId());

			if (vendorcheckdashbordtDto.getVendorcheckId() != null) {
				VendorChecks vendorCheckss = vendorChecksRepository
						.findByVendorcheckId(vendorcheckdashbordtDto.getVendorcheckId());
				System.out.println(vendorCheckss.getVendorcheckId() + "------------------ert------");
				VendorCheckStatusMaster vendorCheckStatusMaster = vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(vendorcheckdashbordtDto.getVendorCheckStatusMasterId());
				vendorCheckss.setVendorCheckStatusMaster(vendorCheckStatusMaster);
				VendorChecks saveObj = vendorChecksRepository.save(vendorCheckss);
				
				VendorUploadChecks vendorChecks = vendorUploadChecksRepository
						.findByVendorChecksVendorcheckId(vendorcheckdashbordtDto.getVendorcheckId());

				System.out.println("vendorChecks=====>" + vendorChecks);
				User user = SecurityHelper.getCurrentUser();

				if (vendorChecks == null) {
//                 
					VendorUploadChecks vendorUploadChecks = new VendorUploadChecks();
					System.out.println("-------------create------");
					if (proofDocumentNew != null) {
						byte[] vendorProof = proofDocumentNew.getBytes();
//						printSizeInMB("PDF DOC", vendorProof);

						if (vendorProof != null) {
//							vendorUploadChecks.setVendorUploadedDocument(vendorProof);
	                        String contentType = proofDocumentNew.getContentType();
	                      //unsetting the document  to  uplaod to  aws bucked
//	                       vendorUploadCheckNew.setVendorUploadedDocument(vendorProof);
	                        ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
	                        metadataDocumentContentType.setContentType(contentType);
	                        String filekey = "Candidate/HybridConventional/VendorUploadDocument/" + vendorcheckdashbordtDto.getVendorcheckId();
	                        String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, filekey,vendorProof, metadataDocumentContentType);
	                        log.info("precisedUrl for vendoruploaded document" + documentPresicedUrl);
	                        vendorUploadChecks.setVendorUploadDocumentPathKey(filekey);
	                        if (contentType.equalsIgnoreCase("application/pdf")) {

	                        	// NEW CHANGE FOR CONVERTING PDF TO IMAGE THIS IS NEW PROOF START
	                        	List<byte[]> imageBytes = convertPDFToImage(vendorProof);

	                        	List<Map<String, List<String>>> encodedImageMapsList = new ArrayList<>();
	                        	
//	                        	if (imageBytes != null && !imageBytes.isEmpty()) {
//	                        		for (int j = 0; j < imageBytes.size(); j++) {
//	                        			byte[] imageBytess = imageBytes.get(j);
////	    	    						printSizeInMB("PDF TO IMAGE", imageBytess);
//	                        			String encodedImage = Base64.getEncoder().encodeToString(imageBytess);
//	                        			String key = "image" + (j + 1);
//	                        			log.info("Encoded image {} added to list.", key);
//	                        			
//	                        			// Create a new list for each image
//	                        			List<String> encodedImagesForDocument = new ArrayList<>();
//	                        			encodedImagesForDocument.add(encodedImage);
//	                        			
//	                        			// Create a new map for each image
//	                        			Map<String, List<String>> encodedImageMap = new HashMap<>();
//	                        			encodedImageMap.put(key, encodedImagesForDocument);
//	                        			
//	                        			// Add the map to the list
//	                        			encodedImageMapsList.add(encodedImageMap);
//	                        		}
	                        		
	                        		log.info("encodedImagesForDocument size: {}", encodedImageMapsList.size());
	                        		// Convert the list to a JSON string
//	                        		try {
//	                        			ObjectMapper objectMapper1 = new ObjectMapper();
//	                        			String jsonEncodedImageMapsList = objectMapper1.writeValueAsString(encodedImageMapsList);
//	                        			// Set the JSON string to the entity field
////	                        			vendorUploadChecks.setVendorUploadedImage(jsonEncodedImageMapsList);
//	                        			 byte[] fileContent   =  java.util.Base64.getDecoder().decode(jsonEncodedImageMapsList);
//	                                     jsonEncodedImageMapsList.getBytes();
//	                                     ObjectMetadata metadataProofdocumentImage = new ObjectMetadata();
//	                                     String contentImageType = "image/png";
//	                                     metadataProofdocumentImage.setContentType(contentImageType);
//	                                     String imageProofFilekey = "Candidate/HybridConventional/VendorUploadImage/" + vendorcheckdashbordtDto.getVendorcheckId();
//	                                     String imagePrecisedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, imageProofFilekey,fileContent , metadataProofdocumentImage);
//	                                     log.info("precisedUrl url of pdf when its saved   ----" + imagePrecisedUrl);
//	                                     vendorUploadChecks.setVendorUploadImagePathKey(imageProofFilekey);
//	                        			
//	                        		} catch (JsonProcessingException e) {
//	                        			log.error("Exception occured in saveproofuploadVendorChecks method in userServiceImpl-->" + e);
//	                        		}
	                        		
	                        		
//	                        		try {
//	                        		    ObjectMapper objectMapper1 = new ObjectMapper();
//	                        		    String jsonEncodedImageMapsList = objectMapper1.writeValueAsString(encodedImageMapsList);
//
//	                        		    // Iterate over each entry in the encodedImageMapsList
//	                        		    Set<String> imagePaths = new HashSet<>(); // Use Set to ensure uniqueness
//	                        		    for (Map<String, List<String>> encodedImageMap : encodedImageMapsList) {
//	                        		        for (Map.Entry<String, List<String>> entry : encodedImageMap.entrySet()) {
//	                        		            String key = entry.getKey();
//	                        		            List<String> encodedImagesForDocument = entry.getValue();
//
//	                        		            // Iterate over each encoded image string
//	                        		            for (String encodedImage : encodedImagesForDocument) {
//	                        		                byte[] fileContent = java.util.Base64.getDecoder().decode(encodedImage);
//	                        		                // Upload the decoded binary data
//	                        		                ObjectMetadata metadataProofdocumentImage = new ObjectMetadata();
//	                        		                String contentImageType = "image/png";
//	                        		                metadataProofdocumentImage.setContentType(contentImageType);
//	                        		                String imageProofFilekey = "Candidate/HybridConventional/VendorUploadImage/" + vendorcheckdashbordtDto.getVendorcheckId()+"_"+key;
//	                        		                String imagePrecisedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, imageProofFilekey, fileContent, metadataProofdocumentImage);
//	                        		                log.info("precisedUrl url of pdf when it's saved ----" + imagePrecisedUrl);
//	                        		                // Store the image path/key
//	                        		                imagePaths.add(imageProofFilekey);
////	                        		                vendorUploadChecks.setVendorUploadImagePathKey(imageProofFilekey);
//	                        		            }
//	                        		        }
//	                        		    }
//	                        		    StringBuilder concatenatedPaths = new StringBuilder();
//	                        		    if (imagePaths.size() == 1) {
//	                        		    	log.info("Single Image..{}");
//	                        		    	vendorUploadChecks.setVendorUploadImagePathKey(imagePaths.iterator().next());
//	                        		    } else {
//	                        		    	log.info("Multi Image...{}");
//	                        		        List<String> imagePathList = new ArrayList<>(imagePaths); // Convert set to list
//	                        		        for (String imagePath : imagePaths) {
//	                        		        concatenatedPaths.append(imagePath).append(";"); // Use your preferred delimiter
//	                        		    }
//	                        		        vendorUploadChecks.setVendorUploadImagePathKey(concatenatedPaths.toString());
//	                        		    }
//	                        		} catch (JsonProcessingException e) {
//	                        		    log.error("Exception occurred in saveproofuploadVendorChecks method in userServiceImpl-->" + e);
//	                        		}
//	                        	}
	                        	
	                        	// NEW CHANGE FOR CONVERTING PDF TO IMAGE END 
	                        }
	                        else {
	                            // Directly encode the image to Base64
	                            String encodedImage = Base64.getEncoder().encodeToString(vendorProof);
	                            // Create a new map for each image
	                            Map<String, List<String>> encodedImageMap = new HashMap<>();
	                            String key = "image1"; // You can customize the key as needed
	                            List<String> encodedImagesForDocument = new ArrayList<>();
	                            encodedImagesForDocument.add(encodedImage);
	                            encodedImageMap.put(key, encodedImagesForDocument);

	                            // Add the map to the list
	                            List<Map<String, List<String>>> encodedImageMapsList = new ArrayList<>();
	                            encodedImageMapsList.add(encodedImageMap);

	                            log.info("encodedImagesForDocument size: {}", encodedImageMapsList.size());

	                            // Convert the list to a JSON string
	                            try {
	                                ObjectMapper objectMapper1 = new ObjectMapper();
	                                String jsonEncodedImageMapsList = objectMapper1.writeValueAsString(encodedImageMapsList);
	                                // Set the JSON string to the entity field
//	                                vendorUploadChecks.setVendorUploadedImage(jsonEncodedImageMapsList);
	                                byte[] byteEncodedImageMapsList = jsonEncodedImageMapsList.getBytes();
	                                System.out.println("byteEncodedImageMapsList >>>>>> 1::::"+byteEncodedImageMapsList);
	                                ObjectMetadata metadataProofdocumentImage = new ObjectMetadata();
	                                metadataProofdocumentImage.setContentType(contentType);
	                                String imageProofFilekey = "Candidate/HybridConventional/VendorUploadImage/" + vendorcheckdashbordtDto.getVendorcheckId();
	                                String imagePrecisedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, imageProofFilekey,vendorProof , metadataProofdocumentImage);
	                                log.info("precisedUrl for image when on save -----" + imagePrecisedUrl);
	                                vendorUploadChecks.setVendorUploadImagePathKey(imageProofFilekey);
	                            } catch (JsonProcessingException e) {
	                                // Handle the exception (e.g., log or throw)
	                                e.printStackTrace();
	                            }
	                        
	                        }

						}
					}
//					vendorUploadChecks
//							.setVendorUploadedDocument(proofDocumentNew != null ? proofDocumentNew.getBytes() : null);
					vendorUploadChecks
							.setAgentColor(colorRepository.findById(vendorcheckdashbordtDto.getColorid()).get());
					vendorUploadChecks.setCreatedOn(new Date());
					vendorUploadChecks.setCreatedBy(user);
					vendorUploadChecks.setVendorChecks(saveObj);
					vendorUploadChecks.setDocumentname(vendorcheckdashbordtDto.getDocumentname());
//                 
//					System.out.println("VENDORUPLOADCHECKS:::" + vendorUploadChecks);
//					ObjectMapper objectMapper = new ObjectMapper();
					Map<String, String> venderAttributeMap = objectMapper.readValue(vendorAttributesValue,
							new TypeReference<Map<String, String>>() {
							});

					// Convert the map to an ArrayList of concatenated key-value strings
					ArrayList<String> venderAttributeList = new ArrayList<>();
					for (Map.Entry<String, String> entry : venderAttributeMap.entrySet()) {
						String concatenated = entry.getKey() + "=" + entry.getValue();

						venderAttributeList.add(concatenated);
					}
					log.info("agentAttributeList@@@@@@@@@@@@@@@@@>" + venderAttributeList);
//                 Set the agentAttirbuteValue field in vendorChecks
					vendorUploadChecks.setVendorAttirbuteValue(venderAttributeList);

					svcSearchResult.setMessage("Vendor checks document updated successfully.");
					// vendorCheckStatusMaster=vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId());
					// vendorUploadChecks.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId()).get());
					// vendorUploadChecks.setVendorCheckStatusId(vendorcheckdashbordtDto.getVendorCheckStatusMasterId());
					System.out.println("-------------------==========getVendorCheckStatusMasterId");
					result = vendorUploadChecksRepository.save(vendorUploadChecks);
					if (result != null) {
						log.info("-------------------==========getVendorCheckStatusMasterId");
						VendorChecks vendorChecksnew = vendorChecksRepository
								.findByVendorcheckId(vendorcheckdashbordtDto.getVendorcheckId());
						vendorChecksnew.setIsproofuploaded(true);
//                    vendorChecksnew.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId()).orElse(null));
						VendorChecks saveVendorCheck = vendorChecksRepository.save(vendorChecksnew);
						
						if(saveVendorCheck != null) {
							VendorCheckStatusHistory vendorCheckStatusHistory = new VendorCheckStatusHistory();
							vendorCheckStatusHistory.setCandidate(candidateRepository.findByCandidateId(vendorChecksnew.getCandidate().getCandidateId()));
							vendorCheckStatusHistory.setCandidateStatus(null);				
							vendorCheckStatusHistory.setCreatedOn(new Date());
							vendorCheckStatusHistory.setCheckName(saveVendorCheck.getCheckType());	
							vendorCheckStatusHistory.setCreatedBy(vendorChecksnew.getCandidate().getCreatedBy().getUserId());
							vendorCheckStatusHistory.setCheckId(saveVendorCheck.getVendorcheckId());
							vendorCheckStatusHistory.setCheckStatus(saveVendorCheck.getVendorCheckStatusMaster().getCheckStatusCode());
							
							VendorCheckStatusHistory save = vendorCheckStatusHistoryRepository.save(vendorCheckStatusHistory);
							
							log.info(" Upload VendorProof CandidateId : "+save.getCandidate().getCandidateId()+" | Unique CheckID In upload Vendor proof : "+save.getCheckId()+" | Moving the CheckStatus  to : "+save.getCheckStatus());
							
						}
						svcSearchResult.setMessage("vendorchecks document saved successfully.");

					} else {
						log.info("-------------candidate-----else------");
						svcSearchResult.setData(null);
						svcSearchResult.setOutcome(false);
						// svcSearchResult.setMessage(messageSource.getMessage("msg.error", null,
						// LocaleContextHolder.getLocale()));
					}

				} else {
//                ObjectMapper objectMapper = new ObjectMapper();
//                JsonNode vendorChecksNode = objectMapper.readTree(vendorChecksString);

					log.info("-------------update------");
					
	                // NEW CHANGE FOR CONVERT PDF TO IMAGE THIS FOR UPDATE PROOF START
					if(vendorcheckdashbordtDto.isRoleAdmin()) {
						log.info("VEndor Role is False>>>>>");
						
//						ObjectMapper objectMapper = new ObjectMapper();
						 ArrayList<String> agentAttributeList = new ArrayList<>();
							ArrayList<String> venderAttributeList = new ArrayList<>();


							if(vendorCheckss.getSource().getSourceName().equalsIgnoreCase("GLOBAL DATABASE CHECK")) {
								 JSONObject jsonObject = new JSONObject(vendorChecksString);
				            	TypeReference<Map<String, List<Map<String, String>>>> typeReference = new TypeReference<Map<String, List<Map<String, String>>>>() {
				            	};
				            	Map<String, List<Map<String, String>>> data = objectMapper.readValue(vendorAttributesValue, typeReference);
//				            	System.out.println("DATA>>>>>>>>>"+data);
				            	// Convert the map entries to a list
				            	List<Map<String, List<Map<String, String>>>> resultList = new ArrayList<>();
				            	for (Map.Entry<String, List<Map<String, String>>> entry : data.entrySet()) {
				            		Map<String, List<Map<String, String>>> groupMap = new HashMap<>();
				            		 // Retrieve vendorCheckStatusMasterId and remarks from jsonObject
//				            	    String vendorCheckStatusMasterId = jsonObject.getString("vendorCheckStatusMasterId");
//				            	    String remarks = jsonObject.getString("remarks");
				            		
				            		String vendorCheckStatusMasterId = String.valueOf(jsonObject.opt("vendorCheckStatusMasterId"));
				            		String remarks = String.valueOf(jsonObject.opt("remarks"));

				            	    // Add additional fields to the groupMap
				            	    groupMap.put("vendorCheckStatusMasterId", Collections.singletonList(Collections.singletonMap("vendorCheckStatusMasterId", vendorCheckStatusMasterId)));
				            	    groupMap.put("remarks", Collections.singletonList(Collections.singletonMap("remarks", remarks)));
				            		groupMap.put(entry.getKey(), entry.getValue());
				            		resultList.add(groupMap);
				            		String entryJson = objectMapper.writeValueAsString(groupMap);
				            		venderAttributeList.add(entryJson);
				            	}
				            }
							
							else {
								if(!vendorCheckss.getSource().getSourceName().equalsIgnoreCase("Criminal")) {
									Map<String, String> venderAttributeMap = objectMapper.readValue(vendorAttributesValue,
											new TypeReference<Map<String, String>>() {
									});
									
									// Convert the map to an ArrayList of concatenated key-value strings
//								ArrayList<String> venderAttributeList = new ArrayList<>();
									for (Map.Entry<String, String> entry : venderAttributeMap.entrySet()) {
										String concatenated = entry.getKey() + "=" + entry.getValue();
										
										venderAttributeList.add(concatenated);
									}
								}
								if(vendorCheckss.getSource().getSourceName().equalsIgnoreCase("Criminal")) {
									JSONObject jsonObject = new JSONObject(vendorChecksString);
//									String vendorCheckStatusMasterId = "vendorCheckStatusMasterId=" + jsonObject.getString("vendorCheckStatusMasterId");
									String vendorCheckStatusMasterId = "vendorCheckStatusMasterId=" + 
										    (jsonObject.get("vendorCheckStatusMasterId") instanceof Integer 
										        ? jsonObject.getInt("vendorCheckStatusMasterId") 
										        : jsonObject.getString("vendorCheckStatusMasterId"));
									String remarks = "remarks=" + jsonObject.getString("remarks");
									venderAttributeList.add(vendorCheckStatusMasterId);	
									venderAttributeList.add(remarks);
								}
							}
						

						log.info("agentAttributeList@@@@@@@@@@@@@@@@@>" + venderAttributeList);
//	                 Set the vendorAttirbuteValue field in vendorChecks
						vendorChecks.setVendorAttirbuteValue(venderAttributeList);
						svcSearchResult.setMessage("Vendor checks document updated successfully.");
						// vendorChecks.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId()).get());
						result = vendorUploadChecksRepository.save(vendorChecks);

						if (result != null) {

							System.out.println("candidate");
							VendorChecks vendorChecksnew = vendorChecksRepository
									.findByVendorcheckId(vendorcheckdashbordtDto.getVendorcheckId());
							vendorChecksnew.setIsproofuploaded(true);
//	                    vendorChecksnew.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId()).get());
							VendorChecks saveVendorCheck = vendorChecksRepository.save(vendorChecksnew);
							
							if(saveVendorCheck != null) {
								VendorCheckStatusHistory vendorCheckStatusHistory = new VendorCheckStatusHistory();
								vendorCheckStatusHistory.setCandidate(candidateRepository.findByCandidateId(vendorChecksnew.getCandidate().getCandidateId()));
								vendorCheckStatusHistory.setCandidateStatus(null);				
								vendorCheckStatusHistory.setCreatedOn(new Date());
								vendorCheckStatusHistory.setCheckName(saveVendorCheck.getCheckType());	
								vendorCheckStatusHistory.setCreatedBy(vendorChecksnew.getCandidate().getCreatedBy().getUserId());
								vendorCheckStatusHistory.setCheckId(saveVendorCheck.getVendorcheckId());
								vendorCheckStatusHistory.setCheckStatus(saveVendorCheck.getVendorCheckStatusMaster().getCheckStatusCode());
								
								VendorCheckStatusHistory save = vendorCheckStatusHistoryRepository.save(vendorCheckStatusHistory);
								log.info(" Upload VendorProof CandidateId : "+save.getCandidate().getCandidateId()+" | Unique CheckID In upload Vendor proof : "+save.getCheckId()+" | Moving the CheckStatus  to : "+save.getCheckStatus());
								}
							
							svcSearchResult.setMessage("vendorchecks document update successfully.");

						} else {
							log.info("-------------candidate-----else------");
							svcSearchResult.setData(null);
							svcSearchResult.setOutcome(false);
							// svcSearchResult.setMessage(messageSource.getMessage("msg.error", null,
							// LocaleContextHolder.getLocale()));
						}

						
					}
					else{
	                if (proofDocumentNew != null) {
	                    String contentType = proofDocumentNew.getContentType();
                    	byte[] vendorProof = proofDocumentNew.getBytes();
//						printSizeInMB("UPDATE PDF DOC", vendorProof);
	                    ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
	                    metadataDocumentContentType.setContentType(contentType);
	                    String filekey = "Candidate/HybridConventional/VendorUploadDocument/" + vendorcheckdashbordtDto.getVendorcheckId();
	                    String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, filekey,vendorProof, metadataDocumentContentType);
	                    log.info("precisedUrl for vendoruploaded document" + documentPresicedUrl);
	                    vendorChecks.setVendorUploadDocumentPathKey(filekey);
	                    
	                    
	                    if (contentType.equalsIgnoreCase("application/pdf")) {
	                    	
//	                    	String contentType = proofDocumentNew.getContentType();
//	                    	byte[] vendorProof = proofDocumentNew.getBytes();
//							printSizeInMB("UPDATE PDF DOC", vendorProof);
//		                    ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
//		                    metadataDocumentContentType.setContentType(contentType);
//		                    String filekey = "Candidate/HybridConventional/VendorUploadDocument/" + vendorcheckdashbordtDto.getVendorcheckId();
//		                    String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, filekey,vendorProof, metadataDocumentContentType);
//		                    log.info("precisedUrl for vendoruploaded document" + documentPresicedUrl);
//		                    vendorChecks.setVendorUploadDocumentPathKey(filekey);

	                    	if (vendorProof != null) {
//	                    		List<byte[]> imageBytes = convertPDFToImage(vendorProof);
	                    		List<Map<String, List<String>>> encodedImageMapsList = new ArrayList<>();
	                    		
//	                    		if (imageBytes != null && !imageBytes.isEmpty()) {
//	                    			for (int j = 0; j < imageBytes.size(); j++) {
//	                    				byte[] imageBytess = imageBytes.get(j);
////	            						printSizeInMB("UPDATE IMAGE", imageBytess);
//	                    				String encodedImage = Base64.getEncoder().encodeToString(imageBytess);
//	                    				String key = "image" + (j + 1);
//	                    				log.info("Encoded image {} added to list.", key);
//	                    				
//	                    				// Create a new list for each image
//	                    				List<String> encodedImagesForDocument = new ArrayList<>();
//	                    				encodedImagesForDocument.add(encodedImage);
//	                    				
//	                    				// Create a new map for each image
//	                    				Map<String, List<String>> encodedImageMap = new HashMap<>();
//	                    				encodedImageMap.put(key, encodedImagesForDocument);
//	                    				
//	                    				// Add the map to the list
//	                    				encodedImageMapsList.add(encodedImageMap);
//	                    			}
	                    			
	                    			log.info("encodedImagesForDocument size: {}", encodedImageMapsList.size());
	                    			
	                    			// Convert the list to a JSON string
//	                    			try {
//	                    				ObjectMapper objectMapper1 = new ObjectMapper();
//	                    				String jsonEncodedImageMapsList = objectMapper1.writeValueAsString(encodedImageMapsList);
//	                    				 Set<String> imagePaths = new HashSet<>(); // Use Set to ensure uniqueness
//		                        		    for (Map<String, List<String>> encodedImageMap : encodedImageMapsList) {
//		                        		        for (Map.Entry<String, List<String>> entry : encodedImageMap.entrySet()) {
//		                        		            String key = entry.getKey();
//		                        		            List<String> encodedImagesForDocument = entry.getValue();
//
//		                        		            // Iterate over each encoded image string
//		                        		            for (String encodedImage : encodedImagesForDocument) {
//		                        		            	byte[] fileContent = java.util.Base64.getDecoder().decode(encodedImage);
//		                        		            	// Upload the decoded binary data
//		                        		            	ObjectMetadata metadataProofdocumentImage = new ObjectMetadata();
//		                        		            	String contentImageType = "image/png";
//		                        		            	metadataProofdocumentImage.setContentType(contentImageType);
//		                        		            	String imageProofFilekey = "Candidate/HybridConventional/VendorUploadImage/" + vendorcheckdashbordtDto.getVendorcheckId()+"_"+key;
//		                        		            	String imagePrecisedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, imageProofFilekey, fileContent, metadataProofdocumentImage);
//		                        		            	log.info("precisedUrl url of pdf when it's saved ----" + imagePrecisedUrl);
//		                        		            	// Store the image path/key
//		                        		            	imagePaths.add(imageProofFilekey);
//		                        		            	//vendorUploadChecks.setVendorUploadImagePathKey(imageProofFilekey);
//		                        		            }
//		                        		        }
//		                        		    }
//		                        		    StringBuilder concatenatedPaths = new StringBuilder();
//		                        		    if (imagePaths.size() == 1) {
//		                        		    	log.info("Single IMage...{}");
//		                        		        vendorChecks.setVendorUploadImagePathKey(imagePaths.iterator().next());
//		                        		    } else {
//		                        		    	log.info("Multi Image...{}");
//		                        		        List<String> imagePathList = new ArrayList<>(imagePaths); // Convert set to list
//		                        		        for (String imagePath : imagePaths) {
//		                        		        concatenatedPaths.append(imagePath).append(";"); // Use your preferred delimiter
//		                        		    }
//		                        		        vendorChecks.setVendorUploadImagePathKey(concatenatedPaths.toString());
//		                        		    }
//
//	                    			} catch (JsonProcessingException e) {
//	                    				// Handle the exception (e.g., log or throw)
//	                    				log.error("Exception occured in saveproofuploadVendorChecks method in userServiceImpl-->" + e);
//	                    			}
//	                    		}
	                    		
	                    	}
	                    }
	                    else {

//	                        byte[] vendorProof = proofDocumentNew.getBytes();
	                        System.out.println(contentType + "Content type");
	                        // Directly encode the image to Base64
	                        String encodedImage = Base64.getEncoder().encodeToString(vendorProof);
//	                        byte[] convertToPdf = convertToPdf(vendorProof);
//	                        String encodedImgetoPdf = Base64.getEncoder().encodeToString(convertToPdf);
//System.out.println("encodedImgetoPdf>>>"+encodedImgetoPdf);
	                        // Create a new map for each image
	                        Map<String, List<String>> encodedImageMap = new HashMap<>();
	                        String key = "image1"; // You can customize the key as needed
	                        List<String> encodedImagesForDocument = new ArrayList<>();
	                        encodedImagesForDocument.add(encodedImage);
	                        encodedImageMap.put(key, encodedImagesForDocument);

	                        // Add the map to the list
	                        List<Map<String, List<String>>> encodedImageMapsList = new ArrayList<>();
	                        encodedImageMapsList.add(encodedImageMap);

	                        log.info("encodedImagesForDocument size: {}", encodedImageMapsList.size());

	                        // Convert the list to a JSON string
	                        try {
	                        	ObjectMapper objectMapper1 = new ObjectMapper();
	                        	String jsonEncodedImageMapsList = objectMapper1.writeValueAsString(encodedImageMapsList);
	                        	// Set the JSON string to the entity field
	                        	//vendorChecks.setVendorUploadedImage(jsonEncodedImageMapsList);
	                        	byte[] byteEncodedImageMapsList = jsonEncodedImageMapsList.getBytes();
	                        	//byte[] vendorProof = proofDocumentNew.getBytes();
	                        	ObjectMetadata metadataProofdocumentImage = new ObjectMetadata();
	                        	metadataProofdocumentImage.setContentType(contentType);
	                        	String imageProofFilekey = "Candidate/HybridConventional/VendorUploadImage/" + vendorcheckdashbordtDto.getVendorcheckId();
	                        	String imagePrecisedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, imageProofFilekey,vendorProof , metadataProofdocumentImage);
	                        	log.info("precisedUrl in update when its a image  ----" + imagePrecisedUrl);
	                        	vendorChecks.setVendorUploadImagePathKey(imageProofFilekey);
	                        } catch (JsonProcessingException e) {
	                        	// Handle the exception (e.g., log or throw)
	                        	e.printStackTrace();
	                        }

	                        
	                        // NEW CHANGE FOR CONVERT PDF TO IMAGE THIS FOR UPDATE PROOF END
	                    }
	                
	                }
	              
					
//					vendorChecks
//							.setVendorUploadedDocument(proofDocumentNew != null ? proofDocumentNew.getBytes() : null);
					vendorChecks.setAgentColor(colorRepository.findById(vendorcheckdashbordtDto.getColorid()).get());
					vendorChecks.setCreatedOn(new Date());
					vendorChecks.setCreatedBy(user);
					vendorChecks.setDocumentname(vendorcheckdashbordtDto.getDocumentname());
					vendorChecks.setVendorChecks(saveObj);
//                 
//					ObjectMapper objectMapper = new ObjectMapper();
					Map<String, String> venderAttributeMap = objectMapper.readValue(vendorAttributesValue,
							new TypeReference<Map<String, String>>() {
							});

					// Convert the map to an ArrayList of concatenated key-value strings
					ArrayList<String> venderAttributeList = new ArrayList<>();
					for (Map.Entry<String, String> entry : venderAttributeMap.entrySet()) {
						String concatenated = entry.getKey() + "=" + entry.getValue();

						venderAttributeList.add(concatenated);
					}
					log.info("agentAttributeList@@@@@@@@@@@@@@@@@>" + venderAttributeList);
//                 Set the vendorAttirbuteValue field in vendorChecks
					vendorChecks.setVendorAttirbuteValue(venderAttributeList);
					svcSearchResult.setMessage("Vendor checks document updated successfully.");
					// vendorChecks.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId()).get());
					result = vendorUploadChecksRepository.save(vendorChecks);

					if (result != null) {
						VendorChecks vendorChecksnew = vendorChecksRepository
								.findByVendorcheckId(vendorcheckdashbordtDto.getVendorcheckId());
						vendorChecksnew.setIsproofuploaded(true);
//                    vendorChecksnew.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findById(vendorcheckdashbordtDto.getVendorCheckStatusMasterId()).get());
						VendorChecks saveVendorCheck = vendorChecksRepository.save(vendorChecksnew);
						
						if(saveVendorCheck != null) {
							VendorCheckStatusHistory vendorCheckStatusHistory = new VendorCheckStatusHistory();
							vendorCheckStatusHistory.setCandidate(candidateRepository.findByCandidateId(vendorChecksnew.getCandidate().getCandidateId()));
							vendorCheckStatusHistory.setCandidateStatus(null);				
							vendorCheckStatusHistory.setCreatedOn(new Date());
							vendorCheckStatusHistory.setCheckName(saveVendorCheck.getCheckType());	
							vendorCheckStatusHistory.setCreatedBy(vendorChecksnew.getCandidate().getCreatedBy().getUserId());
							vendorCheckStatusHistory.setCheckId(saveVendorCheck.getVendorcheckId());
							vendorCheckStatusHistory.setCheckStatus(saveVendorCheck.getVendorCheckStatusMaster().getCheckStatusCode());
							
							VendorCheckStatusHistory save = vendorCheckStatusHistoryRepository.save(vendorCheckStatusHistory);
							log.info(" Upload VendorProof CandidateId : "+save.getCandidate().getCandidateId()+" | Unique CheckID In upload Vendor proof : "+save.getCheckId()+" | Moving the CheckStatus  to : "+save.getCheckStatus());

							}
						svcSearchResult.setMessage("vendorchecks document update successfully.");

					} else {
						System.out.println("-------------candidate-----else------");
						svcSearchResult.setData(null);
						svcSearchResult.setOutcome(false);
						// svcSearchResult.setMessage(messageSource.getMessage("msg.error", null,
						// LocaleContextHolder.getLocale()));
					}

				}

			}
		        ArrayList<CriminalCheck> criminalChecks = new ArrayList<>();
	            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
	            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
	            if (vendorcheckdashbordtDto.getLegalProcedings() != null) {
	                log.info("vendorupload check id {}" + result.getVendoruploadcheckId());
//	                System.out.println("vendorcheckdashbordtDto.getCandidate().getCandidateId()>>>>>>>>>>>"+vendorCheckss.getCandidate().getCandidateId());
//	                Optional<ConventionalVendorliChecksToPerform> findById = liCheckToPerformRepository
//	                        .findById(vendorCheckss.getLicheckId());
	                if (vendorcheckdashbordtDto.getLegalProcedings().getCivilProceedingsList() != null) {
	                    List<CivilProceedingsDTO> civilProceedings = vendorcheckdashbordtDto.getLegalProcedings()
	                            .getCivilProceedingsList();
	                    for (CivilProceedingsDTO civilProceeding : civilProceedings) {
	                        // Find existing CriminalCheck by vendor upload check ID, proceedings type, and court
	                        CriminalCheck criminalproceeding = criminalCheckRepository.findByVendorUploadCheckIdAndProceedingsTypeAndCourt(
	                                result.getVendoruploadcheckId(), "CIVILPROCEDING", civilProceeding.getCourt());


	                        // If a matching CriminalCheck is found
	                        if (criminalproceeding != null) {
//	                            criminalproceeding.setCheckUniqueId(String.valueOf(findById.get().getCheckUniqueId()));
	                            criminalproceeding.setVendorCheckId(vendorcheckdashbordtDto.getVendorcheckId());
//	                            criminalproceeding.setRequestId(findById.get().getRequestId());
	                            criminalproceeding.setCandidateId(vendorCheckss.getCandidate().getCandidateId());
	                            criminalproceeding.setCreatedOn(new Date());
	                            criminalproceeding.setProceedingsType("CIVILPROCEDING");
	                            criminalproceeding.setJurisdiction(civilProceeding.getJurisdiction());
	                            criminalproceeding.setNameOfTheCourt(civilProceeding.getNameOfTheCourt());
	                            criminalproceeding.setResult(civilProceeding.getResult());
	                            criminalproceeding.setCourt(civilProceeding.getCourt());
	                            
	                            
	                            Date date = inputFormat.parse(civilProceeding.getDateOfSearch());
								String outputDateString = outputFormat.format(date);

	                            criminalproceeding.setDateOfSearch(outputDateString);
	                            criminalproceeding.setVendorUploadCheckId(result.getVendoruploadcheckId());

	                            
	                            criminalChecks.add(criminalproceeding);
	                        } else {
	                            CriminalCheck criminalCheckForCivilProcedings = new CriminalCheck();
//	                            criminalCheckForCivilProcedings.setCheckUniqueId(String.valueOf(findById.get().getCheckUniqueId()));
	                            criminalCheckForCivilProcedings.setVendorCheckId(vendorcheckdashbordtDto.getVendorcheckId());
//	                            criminalCheckForCivilProcedings.setRequestId(findById.get().getRequestId());
	                            criminalCheckForCivilProcedings.setCandidateId(vendorCheckss.getCandidate().getCandidateId());
	                            criminalCheckForCivilProcedings.setCreatedOn(new Date());
	                            criminalCheckForCivilProcedings.setProceedingsType("CIVILPROCEDING");
	                            criminalCheckForCivilProcedings.setJurisdiction(civilProceeding.getJurisdiction());
	                            criminalCheckForCivilProcedings.setNameOfTheCourt(civilProceeding.getNameOfTheCourt());
	                            criminalCheckForCivilProcedings.setResult(civilProceeding.getResult());
	                            criminalCheckForCivilProcedings.setCourt(civilProceeding.getCourt());
	                            
	                            Date date = inputFormat.parse(civilProceeding.getDateOfSearch());
								String outputDateString = outputFormat.format(date);

	                            criminalCheckForCivilProcedings.setDateOfSearch(outputDateString);
	                            criminalCheckForCivilProcedings.setVendorUploadCheckId(result.getVendoruploadcheckId());
	                            criminalChecks.add(criminalCheckForCivilProcedings);
	                        }
	                    }
	                }
	                if (vendorcheckdashbordtDto.getLegalProcedings().getCriminalProceedingsList() != null) {
	                    List<CriminalProceedingsDTO> criminalProceedings = vendorcheckdashbordtDto.getLegalProcedings()
	                            .getCriminalProceedingsList();
	                    
	                    for (CriminalProceedingsDTO criminalProceeding : criminalProceedings) {
	                        CriminalCheck criminalproceding = criminalCheckRepository.findByVendorUploadCheckIdAndProceedingsTypeAndCourt(
	                                result.getVendoruploadcheckId(), "CRIMINALPROCEDING", criminalProceeding.getCourt());

	                        if (criminalproceding != null) {
//	                            criminalproceding.setCheckUniqueId(String.valueOf(findById.get().getCheckUniqueId()));
	                            criminalproceding.setVendorCheckId(vendorcheckdashbordtDto.getVendorcheckId());
	                            criminalproceding.setCandidateId(vendorCheckss.getCandidate().getCandidateId());
//	                            criminalproceding.setRequestId(findById.get().getRequestId());
	                            criminalproceding.setCreatedOn(new Date());
	                            criminalproceding.setProceedingsType("CRIMINALPROCEDING");
	                            criminalproceding.setJurisdiction(criminalProceeding.getJurisdiction());
	                            criminalproceding.setNameOfTheCourt(criminalProceeding.getNameOfTheCourt());
	                            criminalproceding.setResult(criminalProceeding.getResult());
	                            criminalproceding.setCourt(criminalProceeding.getCourt());
	                            
	                            Date date = inputFormat.parse(criminalProceeding.getDateOfSearch());
								String outputDateString = outputFormat.format(date);
	                            
	                            criminalproceding.setDateOfSearch(outputDateString);
	                            criminalproceding.setVendorUploadCheckId(result.getVendoruploadcheckId());
	                            criminalChecks.add(criminalproceding);
	                        } else {
	                            CriminalCheck criminalCheckForCriminalProceding = new CriminalCheck();
//	                            criminalCheckForCriminalProceding.setCheckUniqueId(String.valueOf(findById.get().getCheckUniqueId()));
	                            criminalCheckForCriminalProceding.setVendorCheckId(vendorcheckdashbordtDto.getVendorcheckId());
	                            criminalCheckForCriminalProceding.setCandidateId(vendorCheckss.getCandidate().getCandidateId());
//	                            criminalCheckForCriminalProceding.setRequestId(findById.get().getRequestId());
	                            criminalCheckForCriminalProceding.setCreatedOn(new Date());
	                            criminalCheckForCriminalProceding.setProceedingsType("CRIMINALPROCEDING");
	                            criminalCheckForCriminalProceding.setJurisdiction(criminalProceeding.getJurisdiction());
	                            criminalCheckForCriminalProceding.setNameOfTheCourt(criminalProceeding.getNameOfTheCourt());
	                            criminalCheckForCriminalProceding.setResult(criminalProceeding.getResult());
	                            criminalCheckForCriminalProceding.setCourt(criminalProceeding.getCourt());
	                            Date date = inputFormat.parse(criminalProceeding.getDateOfSearch());
								String outputDateString = outputFormat.format(date);
	                            criminalCheckForCriminalProceding.setDateOfSearch(outputDateString);
	                            criminalCheckForCriminalProceding.setVendorUploadCheckId(result.getVendoruploadcheckId());
	                            criminalChecks.add(criminalCheckForCriminalProceding);
	                        }
	                    }
	                }
	                
	                log.info("Number of criminal checks to save: " + criminalChecks.size());
	                List<CriminalCheck> criminalChecks1 = criminalCheckRepository.saveAll(criminalChecks);
	                log.info("saved criminal checks" + criminalChecks1);
	            }
			}
		}
				else {
					svcSearchResult.setMessage("InValid PDF Content..");       
	                svcSearchResult.setOutcome(false);
	                svcSearchResult.setData(null);
	               }
		} catch (Exception ex) {
			log.error("Exception occured in saveproofuploadVendorChecks method in userServiceImpl-->" + ex);

		}
		return svcSearchResult;
	}
	
	public static boolean validatePDFContent(String pdfContent) {
	    try {
	        PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfContent.getBytes(StandardCharsets.UTF_8)));
	        boolean containsScriptOrHTMLTags = false;

	        // Check if OpenAction is a JavaScript action
	        PDDestinationOrAction openAction = document.getDocumentCatalog().getOpenAction();
	        if (openAction instanceof PDAction) {
	            PDAction action = (PDAction) openAction;
	            if (action instanceof PDActionJavaScript) {
	                containsScriptOrHTMLTags = true;
	            }
	        } else {
	            // Extract text and check for script or HTML tags
	            PDFTextStripper pdfStripper = new PDFTextStripper();
	            StringBuilder text = new StringBuilder();
	            for (int i = 0; i < document.getNumberOfPages(); i++) {
	                pdfStripper.setStartPage(i + 1);
	                pdfStripper.setEndPage(i + 1);
	                text.append(pdfStripper.getText(document));
	            }
	            containsScriptOrHTMLTags = text.toString().contains("<script>") || text.toString().contains("<html>");
	        }
	        return containsScriptOrHTMLTags;
	    } catch (IOException e) {
//	        log.error("Error validating PDF content: {}", e.getMessage());
//	        e.printStackTrace();
	        return false;
	    } 
	}
	
	 public List<byte[]> convertPDFToImage(byte[] pdfBytes) throws IOException {
		    try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes))) {
		        PDFRenderer pdfRenderer = new PDFRenderer(document);
		        int numberOfPages = document.getNumberOfPages();

		        List<byte[]> imageBytesList = new ArrayList<>();

		        for (int pageIndex = 0; pageIndex < numberOfPages; pageIndex++) {
		            BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300);
		            ByteArrayOutputStream baos = new ByteArrayOutputStream();
		            javax.imageio.ImageIO.write(image, "jpeg", baos);
		            imageBytesList.add(baos.toByteArray());
		        }

		        log.info("Number of Images: {}" + imageBytesList.size());
		        // If needed, you can return the list of image bytes
		        return imageBytesList;
		    }
		}
	
	 
	 public static void printSizeInMB(String name, byte[] bytes) {
		    double sizeInMB = bytes.length / (1024.0 * 1024.0);
		    DecimalFormat df = new DecimalFormat("#.##");
		    System.out.println(name + " size: " + df.format(sizeInMB) + " MB");
		}

	@Override
	public ServiceOutcome<VendorChecks> saveInitiateVendorChecks(String vendorChecksString,
			MultipartFile proofDocumentNew,byte[] proofConventionalCandidate) {
		System.out.println(proofDocumentNew + "===========================" + vendorChecksString);
		ServiceOutcome<VendorChecks> svcSearchResult = new ServiceOutcome<VendorChecks>();
		Candidate Candidatelist = null;
//		VendorChecks vendorcheckObj=null;
		// Long sourceid=2;
		Long vendorid = null;
		CandidateCaseDetails result = null;

		try {
			VendorInitiatDto vendorInitiatDto = new ObjectMapper().readValue(vendorChecksString,
					VendorInitiatDto.class);
			System.out.println(vendorInitiatDto + "------------------------+++++++++++++++");
			System.out.println("vendorInitiatDto.getVendorCheckId>>>>>"+vendorInitiatDto.getVendorcheckId());
			User user = SecurityHelper.getCurrentUser();
			Source source = sourceRepository.findById(vendorInitiatDto.getSourceId()).get();
			System.out.println(source + "sourceee000000" + vendorInitiatDto.getDocumentname() + "-------");
			if (vendorInitiatDto.getDocumentname() != null) {
				VendorChecks vendorChecksobj = vendorChecksRepository
						.findByCandidateCandidateIdAndVendorIdAndSourceSourceIdAndDocumentnameAndCheckType(
								vendorInitiatDto.getCandidateId(), vendorInitiatDto.getVendorId(),
								vendorInitiatDto.getSourceId(), vendorInitiatDto.getDocumentname(),vendorInitiatDto.getCheckType());
				System.out.println("--------------docis----------" + vendorInitiatDto.getVendorId() + "---"
						+ vendorInitiatDto.getSourceId());
				if (vendorChecksobj != null) {
					// vendorChecksobj.setDocumentname(vendorInitiatDto.getDocumentname());
					 //unsetting the document  to  uplaod to  aws bucked

//                  vendorUploadCheckNew.setVendorUploadedDocument(vendorProof);
					System.out.println("vendorChecksobj.getVendorCheckId>>>>>"+vendorChecksobj.getVendorcheckId());

					if(proofDocumentNew != null) {
						
						ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
						metadataDocumentContentType.setContentType(proofDocumentNew.getContentType());
						String filekey = "Candidate/HybridConvetional/VendorUploadDocument/"+vendorInitiatDto.getCheckType()+"_"+vendorInitiatDto.getCandidateId();
						String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, filekey,proofDocumentNew.getBytes(), metadataDocumentContentType);
						log.info("precisedUrl for vendoruploaded document" + documentPresicedUrl);
//					vendorChecksobj
//							.setAgentUploadedDocument(proofDocumentNew != null ? proofDocumentNew.getBytes() : null);
						vendorChecksobj.setAgentUploadDocumentPathKey(filekey);
					}
					if(proofConventionalCandidate != null) {
						ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
				        String contentType = detectContentType(proofConventionalCandidate);
						metadataDocumentContentType.setContentType(contentType);
						String filekey = "Candidate/HybridConvetional/VendorUploadDocument/"+vendorInitiatDto.getCheckType()+"_"+vendorInitiatDto.getCandidateId();
						String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, filekey,proofConventionalCandidate, metadataDocumentContentType);
						log.info("precisedUrl for vendoruploaded document" + documentPresicedUrl);
//					vendorChecks
//							.setAgentUploadedDocument(proofDocumentNew != null ? proofDocumentNew.getBytes() : null);
						vendorChecksobj.setAgentUploadDocumentPathKey(filekey);
					}
					if(user != null && user.getUserId() != null) {
						vendorChecksobj.setCreatedBy(user);						
					}
					else {
						vendorChecksobj.setCreatedBy(candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()).getCreatedBy());
					}
//					vendorChecksobj.setCreatedBy(user);
					vendorChecksobj.setCandidateName(vendorInitiatDto.getCandidateName());
					vendorChecksobj.setDateOfBirth(vendorInitiatDto.getDateOfBirth());
					vendorChecksobj.setContactNo(vendorInitiatDto.getContactNo());
					vendorChecksobj.setFatherName(vendorInitiatDto.getFatherName());
					vendorChecksobj.setAddress(vendorInitiatDto.getAddress());
					vendorChecksobj.setAlternateContactNo(vendorInitiatDto.getAlternateContactNo());
					vendorChecksobj.setTypeOfPanel(vendorInitiatDto.getTypeOfPanel());
					vendorChecksobj.setCreatedOn(new Date());
					vendorChecksobj.setStopCheck(false);
					vendorChecksobj.setCheckType(vendorInitiatDto.getCheckType());
					vendorChecksobj.setVendorCheckStatusMaster(
						    vendorInitiatDto.getVendorCheckStatusMasterId() != null ?
						    vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(vendorInitiatDto.getVendorCheckStatusMasterId()) :
						    vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(2L)
						);
					
					
					
					//vendorChecksobj.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(vendorInitiatDto.getVendorCheckStatusMasterId()));
//					vendorChecksobj.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findByCheckStatusCode("INPROGRESS"));
					VendorChecks vendorIntialCheck = vendorChecksRepository.save(vendorChecksobj);
					
					if(vendorIntialCheck != null) {
						VendorCheckStatusHistory vendorCheckStatusHistory = new VendorCheckStatusHistory();
						vendorCheckStatusHistory.setCandidate(candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()));
						vendorCheckStatusHistory.setCandidateStatus(null);				
						vendorCheckStatusHistory.setCreatedOn(new Date());
						vendorCheckStatusHistory.setCheckName(vendorInitiatDto.getCheckType());	
						if(user != null && user.getUserId() != null) {
							vendorCheckStatusHistory.setCreatedBy(user.getUserId());
						}
						else {
							User createdByUser = candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()).getCreatedBy();
							Long createdByUserId = createdByUser.getUserId();
							vendorCheckStatusHistory.setCreatedBy(createdByUserId);
						}
//						vendorCheckStatusHistory.setCreatedBy(user.getUserId());
						vendorCheckStatusHistory.setCheckId(vendorIntialCheck.getVendorcheckId());
						vendorCheckStatusHistory.setCheckStatus(vendorIntialCheck.getVendorCheckStatusMaster().getCheckStatusCode());
						
						VendorCheckStatusHistory save = vendorCheckStatusHistoryRepository.save(vendorCheckStatusHistory);
						
						User byUserId = userRepository.findByUserId(vendorIntialCheck.getVendorId());						
						log.info("CandidateId In intiate Vendor Check : "+save.getCandidate().getCandidateId()+" | Unique CheckID In Intiate Vendor Check : "+save.getCheckId()+" | Moving the CheckStatus (NEW UPLOAD) to : "+save.getCheckStatus());
						log.info("CheckStatus : "+save.getCheckStatus()+" | Assigned to "+byUserId.getUserFirstName()+" "+byUserId.getUserLastName());

						}
					
					svcSearchResult.setMessage("vendor Checks document update successfully.");

				} else {
					VendorChecks vendorChecks = new VendorChecks();
					vendorChecks.setCandidate(candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()));
					vendorChecks.setVendorId(vendorInitiatDto.getVendorId());
					vendorChecks.setSource(sourceRepository.findById(vendorInitiatDto.getSourceId()).get());
					vendorChecks.setDocumentname(vendorInitiatDto.getDocumentname());
					if(proofDocumentNew != null) {
						
						ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
						metadataDocumentContentType.setContentType(proofDocumentNew.getContentType());
						String filekey = "Candidate/HybridConvetional/VendorUploadDocument/"+vendorInitiatDto.getCheckType()+"_"+vendorInitiatDto.getCandidateId();
						String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, filekey,proofDocumentNew.getBytes(), metadataDocumentContentType);
						log.info("precisedUrl for vendoruploaded document" + documentPresicedUrl);
//					vendorChecks
//							.setAgentUploadedDocument(proofDocumentNew != null ? proofDocumentNew.getBytes() : null);
						vendorChecks.setAgentUploadDocumentPathKey(filekey);
					}
					if(proofConventionalCandidate != null) {
						ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
				        String contentType = detectContentType(proofConventionalCandidate);
						metadataDocumentContentType.setContentType(contentType);
						String filekey = "Candidate/HybridConvetional/VendorUploadDocument/"+vendorInitiatDto.getCheckType()+"_"+vendorInitiatDto.getCandidateId();
						String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, filekey,proofConventionalCandidate, metadataDocumentContentType);
						log.info("precisedUrl for vendoruploaded document" + documentPresicedUrl);
//					vendorChecks
//							.setAgentUploadedDocument(proofDocumentNew != null ? proofDocumentNew.getBytes() : null);
						vendorChecks.setAgentUploadDocumentPathKey(filekey);
					}
					if(user != null && user.getUserId() != null) {
						System.out.println(">>>>>>>>>>>>>>>>>>>>>> 4");
						vendorChecks.setCreatedBy(user);						
					}
					else {
						System.out.println(">>>>>>>>>>>>>>>>>>>>>> 5");
						vendorChecks.setCreatedBy(candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()).getCreatedBy());
					}
//					vendorChecks.setCreatedBy(user);
					vendorChecks.setCandidateName(vendorInitiatDto.getCandidateName());
					vendorChecks.setDateOfBirth(vendorInitiatDto.getDateOfBirth());
					vendorChecks.setContactNo(vendorInitiatDto.getContactNo());
					vendorChecks.setFatherName(vendorInitiatDto.getFatherName());
					vendorChecks.setAddress(vendorInitiatDto.getAddress());
					vendorChecks.setAlternateContactNo(vendorInitiatDto.getAlternateContactNo());
					vendorChecks.setTypeOfPanel(vendorInitiatDto.getTypeOfPanel());
					vendorChecks.setCreatedOn(new Date());
					vendorChecks.setStopCheck(false);
					vendorChecks.setCheckType(vendorInitiatDto.getCheckType());
					vendorChecks.setVendorCheckStatusMaster(
						    vendorInitiatDto.getVendorCheckStatusMasterId() != null ?
						    vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(vendorInitiatDto.getVendorCheckStatusMasterId()) :
						    vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(2L)
						);

					//vendorChecks.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findByCheckStatusCode("INPROGRESS"));
					ObjectMapper objectMapper = new ObjectMapper();
					Map<String, String> agentAttributeMap = objectMapper.readValue(vendorChecksString,
							new TypeReference<Map<String, String>>() {
							});
					// Convert the map to an ArrayList of concatenated key-value strings
					ArrayList<String> agentAttributeList = new ArrayList<>();
					for (Map.Entry<String, String> entry : agentAttributeMap.entrySet()) {
//						if (entry.getKey() != "candidateName" && entry.getKey() != "dateOfBirth"
//								&& entry.getKey() != "contactNo" && entry.getKey() != "fatherName"
//								&& entry.getKey() != "address" && entry.getKey() != "vendorId"
//								&& entry.getKey() != "sourceId" && entry.getKey() != "candidateId"
//								&& entry.getKey() != "value" && entry.getKey() != "documentname"
//								&& entry.getKey() != "vendorCheckStatusMasterId") {
//							
//							String concatenated = entry.getKey() + "=" + entry.getValue();
//							agentAttributeList.add(concatenated);
//						}
						if (entry.getKey() != "candidateName"
								&& entry.getKey() != "contactNo"
								 && entry.getKey() != "vendorId"
								&& entry.getKey() != "sourceId" && entry.getKey() != "candidateId"
								&& entry.getKey() != "value" && entry.getKey() != "documentname"
								&& entry.getKey() != "vendorCheckStatusMasterId"
								&& entry.getKey() != "email") {
							
							String concatenated = entry.getKey() + "=" + entry.getValue();

							agentAttributeList.add(concatenated);
						}
					}
					System.out.println("agentAttributeList@@@@@@@@@@@@@@@@@>" + agentAttributeList);
//                     Set the agentAttirbuteValue field in vendorChecks
					vendorChecks.setAgentAttirbuteValue(agentAttributeList);

					VendorChecks vendorIntialCheck = vendorChecksRepository.save(vendorChecks);
					
					if(vendorIntialCheck != null) {
						VendorCheckStatusHistory vendorCheckStatusHistory = new VendorCheckStatusHistory();
						vendorCheckStatusHistory.setCandidate(candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()));
						vendorCheckStatusHistory.setCandidateStatus(null);				
						vendorCheckStatusHistory.setCreatedOn(new Date());
						vendorCheckStatusHistory.setCheckName(vendorInitiatDto.getCheckType());	
						if(user != null && user.getUserId() != null) {
							vendorCheckStatusHistory.setCreatedBy(user.getUserId());
						}
						else {
							User createdByUser = candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()).getCreatedBy();
							Long createdByUserId = createdByUser.getUserId();
							vendorCheckStatusHistory.setCreatedBy(createdByUserId);
						}
//						vendorCheckStatusHistory.setCreatedBy(user.getUserId());
						vendorCheckStatusHistory.setCheckId(vendorIntialCheck.getVendorcheckId());
						vendorCheckStatusHistory.setCheckStatus(vendorIntialCheck.getVendorCheckStatusMaster().getCheckStatusCode());
						
						VendorCheckStatusHistory save = vendorCheckStatusHistoryRepository.save(vendorCheckStatusHistory);
						
						User byUserId = userRepository.findByUserId(vendorIntialCheck.getVendorId());						
						log.info("CandidateId In intiate Vendor Check : "+save.getCandidate().getCandidateId()+" | Unique CheckID In Intiate Vendor Check : "+save.getCheckId()+" | Moving the CheckStatus (NEW UPLOAD) to : "+save.getCheckStatus());
						log.info("CheckStatus : "+save.getCheckStatus()+" | Assigned to "+byUserId.getUserFirstName()+" "+byUserId.getUserLastName());
						
					}
					
					svcSearchResult.setMessage("vendor Checks document saved successfully.");

				}

			} else {
				VendorChecks vendorChecksobj = vendorChecksRepository
						.findByCandidateCandidateIdAndVendorIdAndSourceSourceIdAndCheckType(vendorInitiatDto.getCandidateId(),
								vendorInitiatDto.getVendorId(), vendorInitiatDto.getSourceId(),vendorInitiatDto.getCheckType());
				log.info("--------docelse---------------- {}" + vendorInitiatDto.getVendorId() + "---"
						+ vendorInitiatDto.getSourceId());
				if (vendorChecksobj != null) {
					System.out.println("inside ifff");
					vendorChecksobj.setDocumentname(vendorInitiatDto.getDocumentname());
					if(proofDocumentNew != null) {
						ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
						metadataDocumentContentType.setContentType(proofDocumentNew.getContentType());
						String filekey = "Candidate/HybridConvetional/VendorUploadDocument/"+vendorInitiatDto.getCheckType()+"_"+vendorInitiatDto.getCandidateId();
						String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, filekey,proofDocumentNew.getBytes(), metadataDocumentContentType);
						log.info("precisedUrl for vendoruploaded document" + documentPresicedUrl);
//						vendorChecksobj
//								.setAgentUploadedDocument(proofDocumentNew != null ? proofDocumentNew.getBytes() : null);
						vendorChecksobj.setAgentUploadDocumentPathKey(filekey);
					}
					if(proofConventionalCandidate != null) {
						ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
				        String contentType = detectContentType(proofConventionalCandidate);
						metadataDocumentContentType.setContentType(contentType);
						String filekey = "Candidate/HybridConvetional/VendorUploadDocument/"+vendorInitiatDto.getCheckType()+"_"+vendorInitiatDto.getCandidateId();
						String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, filekey,proofConventionalCandidate, metadataDocumentContentType);
						log.info("precisedUrl for vendoruploaded document" + documentPresicedUrl);
//					vendorChecks
//							.setAgentUploadedDocument(proofDocumentNew != null ? proofDocumentNew.getBytes() : null);
						vendorChecksobj.setAgentUploadDocumentPathKey(filekey);
					}
					vendorChecksobj.setCreatedBy(user);
					vendorChecksobj.setCandidateName(vendorInitiatDto.getCandidateName());
					vendorChecksobj.setDateOfBirth(vendorInitiatDto.getDateOfBirth());
					vendorChecksobj.setContactNo(vendorInitiatDto.getContactNo());
					vendorChecksobj.setFatherName(vendorInitiatDto.getFatherName());
					vendorChecksobj.setAddress(vendorInitiatDto.getAddress());
					vendorChecksobj.setAlternateContactNo(vendorInitiatDto.getAlternateContactNo());
					vendorChecksobj.setTypeOfPanel(vendorInitiatDto.getTypeOfPanel());
					vendorChecksobj.setCreatedOn(new Date());
					vendorChecksobj.setDocumentname(vendorInitiatDto.getDocumentname());
					vendorChecksobj.setStopCheck(false);
					vendorChecksobj.setCheckType(vendorInitiatDto.getCheckType());
					vendorChecksobj.setVendorCheckStatusMaster(
						    vendorInitiatDto.getVendorCheckStatusMasterId() != null ?
						    vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(vendorInitiatDto.getVendorCheckStatusMasterId()) :
						    vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(2L)
						);
					//vendorChecksobj.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findByCheckStatusCode("INPROGRESS"));
					VendorChecks vendorIntialCheck = vendorChecksRepository.save(vendorChecksobj);
					
					if(vendorIntialCheck != null) {
						VendorCheckStatusHistory vendorCheckStatusHistory = new VendorCheckStatusHistory();
						vendorCheckStatusHistory.setCandidate(candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()));
						vendorCheckStatusHistory.setCandidateStatus(null);				
						vendorCheckStatusHistory.setCreatedOn(new Date());
						vendorCheckStatusHistory.setCheckName(vendorInitiatDto.getCheckType());	
						vendorCheckStatusHistory.setCreatedBy(user.getUserId());
						vendorCheckStatusHistory.setCheckId(vendorIntialCheck.getVendorcheckId());
						vendorCheckStatusHistory.setCheckStatus(vendorIntialCheck.getVendorCheckStatusMaster().getCheckStatusCode());
						
						VendorCheckStatusHistory save = vendorCheckStatusHistoryRepository.save(vendorCheckStatusHistory);
						
						User byUserId = userRepository.findByUserId(vendorIntialCheck.getVendorId());						
						log.info("CandidateId In intiate Vendor Check : "+save.getCandidate().getCandidateId()+" | Unique CheckID In Intiate Vendor Check : "+save.getCheckId()+" | Moving the CheckStatus (NEW UPLOAD) : to "+save.getCheckStatus());
						log.info("CheckStatus : "+save.getCheckStatus()+" | Assigned to "+byUserId.getUserFirstName()+" "+byUserId.getUserLastName());

						}
					
					svcSearchResult.setMessage("vendor Checks  update successfully.");

				} else {

					VendorChecks vendorChecks = new VendorChecks();
					vendorChecks.setCandidate(candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()));
					vendorChecks.setVendorId(vendorInitiatDto.getVendorId());
					vendorChecks.setSource(sourceRepository.findById(vendorInitiatDto.getSourceId()).get());
					vendorChecks.setDocumentname(vendorInitiatDto.getDocumentname());
					if(proofDocumentNew != null) {
						ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
						metadataDocumentContentType.setContentType(proofDocumentNew.getContentType());
						String filekey = "Candidate/HybridConvetional/VendorUploadDocument/"+vendorInitiatDto.getCheckType()+"_"+vendorInitiatDto.getCandidateId();
						String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, filekey,proofDocumentNew.getBytes(), metadataDocumentContentType);
						log.info("precisedUrl for vendoruploaded document" + documentPresicedUrl);
//					vendorChecks
//							.setAgentUploadedDocument(proofDocumentNew != null ? proofDocumentNew.getBytes() : null);
						vendorChecksobj.setAgentUploadDocumentPathKey(filekey);
					}
					if(proofConventionalCandidate != null) {
						ObjectMetadata metadataDocumentContentType = new ObjectMetadata();
				        String contentType = detectContentType(proofConventionalCandidate);
						metadataDocumentContentType.setContentType(contentType);
						String filekey = "Candidate/HybridConvetional/VendorUploadDocument/"+vendorInitiatDto.getCheckType()+"_"+vendorInitiatDto.getCandidateId();
						String documentPresicedUrl = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, filekey,proofConventionalCandidate, metadataDocumentContentType);
						log.info("precisedUrl for vendoruploaded document" + documentPresicedUrl);
//					vendorChecks
//							.setAgentUploadedDocument(proofDocumentNew != null ? proofDocumentNew.getBytes() : null);
						vendorChecks.setAgentUploadDocumentPathKey(filekey);
					}
					if(user != null && user.getUserId() != null) {
						vendorChecks.setCreatedBy(user);						
					}
					else {
						vendorChecks.setCreatedBy(candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()).getCreatedBy());
					}
					vendorChecks.setCandidateName(vendorInitiatDto.getCandidateName());
					vendorChecks.setDateOfBirth(vendorInitiatDto.getDateOfBirth());
					vendorChecks.setContactNo(vendorInitiatDto.getContactNo());
					vendorChecks.setFatherName(vendorInitiatDto.getFatherName());
					vendorChecks.setAddress(vendorInitiatDto.getAddress());
					vendorChecks.setAlternateContactNo(vendorInitiatDto.getAlternateContactNo());
					vendorChecks.setTypeOfPanel(vendorInitiatDto.getTypeOfPanel());
					vendorChecks.setCreatedOn(new Date());
					vendorChecks.setStopCheck(false);
					vendorChecks.setCheckType(vendorInitiatDto.getCheckType());
					vendorChecks.setVendorCheckStatusMaster(
						    vendorInitiatDto.getVendorCheckStatusMasterId() != null ?
						    vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(vendorInitiatDto.getVendorCheckStatusMasterId()) :
						    vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(2L)
						);//					vendorChecks.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findByCheckStatusCode("INPROGRESS"));
					ObjectMapper objectMapper = new ObjectMapper();
					Map<String, String> agentAttributeMap = objectMapper.readValue(vendorChecksString,
							new TypeReference<Map<String, String>>() {
							});
					// Convert the map to an ArrayList of concatenated key-value strings
					ArrayList<String> agentAttributeList = new ArrayList<>();
					for (Map.Entry<String, String> entry : agentAttributeMap.entrySet()) {
//						if (entry.getKey() != "candidateName" && entry.getKey() != "dateOfBirth"
//								&& entry.getKey() != "contactNo" && entry.getKey() != "fatherName"
//								&& entry.getKey() != "address" && entry.getKey() != "vendorId"
//								&& entry.getKey() != "sourceId" && entry.getKey() != "candidateId"
//								&& entry.getKey() != "value" && entry.getKey() != "documentname"
//								&& entry.getKey() != "vendorCheckStatusMasterId") {
//							
//							String concatenated = entry.getKey() + "=" + entry.getValue();
//
//							agentAttributeList.add(concatenated);
//						}
						if (entry.getKey() != "candidateName"
								&& entry.getKey() != "contactNo"
								 && entry.getKey() != "vendorId"
								&& entry.getKey() != "sourceId" && entry.getKey() != "candidateId"
								&& entry.getKey() != "value" && entry.getKey() != "documentname"
								&& entry.getKey() != "vendorCheckStatusMasterId"
								&& entry.getKey() != "email") {
							
							String concatenated = entry.getKey() + "=" + entry.getValue();

							agentAttributeList.add(concatenated);
						}
					}
					System.out.println("agentAttributeList@@@@@@@@@@@@@@@@@>" + agentAttributeList);
//                     Set the agentAttirbuteValue field in vendorChecks
					vendorChecks.setAgentAttirbuteValue(agentAttributeList);

					VendorChecks vendorIntialCheck = vendorChecksRepository.save(vendorChecks);
					
					if(vendorIntialCheck != null) {
						VendorCheckStatusHistory vendorCheckStatusHistory = new VendorCheckStatusHistory();
						vendorCheckStatusHistory.setCandidate(candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()));
						vendorCheckStatusHistory.setCandidateStatus(null);				
						vendorCheckStatusHistory.setCreatedOn(new Date());
						vendorCheckStatusHistory.setCheckName(vendorInitiatDto.getCheckType());	
						if(user != null && user.getUserId() != null) {
							vendorCheckStatusHistory.setCreatedBy(user.getUserId());
						}
						else {
							User createdByUser = candidateRepository.findByCandidateId(vendorInitiatDto.getCandidateId()).getCreatedBy();
							Long createdByUserId = createdByUser.getUserId();
							vendorCheckStatusHistory.setCreatedBy(createdByUserId);
						}
						vendorCheckStatusHistory.setCheckId(vendorIntialCheck.getVendorcheckId());
						vendorCheckStatusHistory.setCheckStatus(vendorIntialCheck.getVendorCheckStatusMaster().getCheckStatusCode());
						
						VendorCheckStatusHistory save = vendorCheckStatusHistoryRepository.save(vendorCheckStatusHistory);
						
						User byUserId = userRepository.findByUserId(vendorIntialCheck.getVendorId());						
						log.info("CandidateId In intiate Vendor Check : "+save.getCandidate().getCandidateId()+" | Unique CheckID In Intiate Vendor Check : "+save.getCheckId()+" | Moving the CheckStatus (NEW UPLOAD)  to : "+save.getCheckStatus());
						log.info("CheckStatus : "+save.getCheckStatus()+" | Assigned to "+byUserId.getUserFirstName()+" "+byUserId.getUserLastName());

						}
					svcSearchResult.setMessage("vendor Checks saved successfully.");

				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in saveInitiateVendorChecks method in userServiceImpl-->" + ex);

		}
		return svcSearchResult;
	}
	
	public String detectContentType(byte[] data) {
        if (data.length >= 5 && data[0] == (byte)0x25 && data[1] == (byte)0x50 && data[2] == (byte)0x44 && data[3] == (byte)0x46 && data[4] == (byte)0x2D) {
            return "application/pdf";
        } else if (data.length >= 4 && data[0] == (byte)0xFF && data[1] == (byte)0xD8 && data[2] == (byte)0xFF) {
            return "image/jpeg";
        } else if (data.length >= 4 && data[0] == (byte)0x89 && data[1] == (byte)0x50 && data[2] == (byte)0x4E && data[3] == (byte)0x47) {
            return "image/png";
        } else if (data.length >= 4 && data[0] == (byte)0x47 && data[1] == (byte)0x49 && data[2] == (byte)0x46 && data[3] == (byte)0x38) {
            return "image/gif";
        } else if (data.length >= 2 && data[0] == (byte)0x42 && data[1] == (byte)0x4D) {
            return "image/bmp";
        } else if (data.length >= 3 && data[0] == (byte)0x49 && data[1] == (byte)0x49 && data[2] == (byte)0x2A) {
            return "image/tiff";
        } else if (data.length >= 3 && data[0] == (byte)0x4D && data[1] == (byte)0x4D && data[2] == (byte)0x00) {
            return "image/tiff";
        } else if (data.length >= 2 && data[0] == (byte)0x1F && data[1] == (byte)0x8B) {
            return "application/gzip";
        } else if (data.length >= 4 && data[0] == (byte)0x50 && data[1] == (byte)0x4B && data[2] == (byte)0x03 && data[3] == (byte)0x04) {
            return "application/zip";
        } else {
            return "application/octet-stream"; // Default content type
        }
    }

	@Override
	public ServiceOutcome<ConventionalAttributesMaster> getConventionalAttributesMasterById(Long Id,String type) {
		ServiceOutcome<ConventionalAttributesMaster> svcSearchResult = new ServiceOutcome<ConventionalAttributesMaster>();

//		List<Long> ids = Arrays.asList(1L, 2L,3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
//
//		List<Source> findByparticularSourceIds = sourceRepository.findByParticularSourceIdsIn(ids);
//
//		List<String> sourceNames = findByparticularSourceIds.stream()
//				.map(Source::getSourceName)
//				.collect(Collectors.toList());
//
//		Optional<Source> findById2 = sourceRepository.findById(Id);
//
//		for (String sourceName : sourceNames) {
//			String findById2SourceName = findById2.get().getSourceName().toLowerCase().trim();
//			String sourceNameLower = sourceName.toLowerCase().trim();
//
//			// Splitting the source names into words
//			String[] findById2Words = findById2SourceName.split("\\s+");
//			String firstWordOfFindById2 = findById2Words.length > 0 ? findById2Words[0] : "";
//
//			String[] sourceNameWords = sourceNameLower.split("\\s+");
//			String firstWordOfSourceName = sourceNameWords.length > 0 ? sourceNameWords[0] : "";
//
//			if (firstWordOfFindById2.contains(firstWordOfSourceName)) {   
//				Source findBySourceName = sourceRepository.findBySourceName(sourceName);
//				Id = findBySourceName.getSourceId();
//				break;
//			}
//		}

		List<ConventionalAttributesMaster> findById;
		System.out.println("TYPE before condition: " + type);

		if (type == null ||  type.trim().isEmpty()) {
//		    System.out.println("Condition triggered");
		    findById = conventionalAttributesMasterRepository.findBySourceId(Id);
		} else {
		    findById = conventionalAttributesMasterRepository.findBySourceIdWithType(Id, type);
		}

		if (!findById.isEmpty()) {
		    svcSearchResult.setMessage("Fetched Data");
		    svcSearchResult.setData(findById.get(0));
		} else {
		    svcSearchResult.setMessage("No Data Found");
		}

		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<ConventionalAttributesMaster> findBySourceName(String sourceName) {
		ServiceOutcome<ConventionalAttributesMaster> svcSearchResult = new ServiceOutcome<ConventionalAttributesMaster>();

		try {
//            Source sourceList= sourceRepository.findBySourceName(sourceName);
			Source findByName = sourceRepository.findBySourceName(sourceName);
//            if(!source.isEmpty()) {
//                svcSearchResult.setData(sourceList);
//                svcSearchResult.setOutcome(true);
//                svcSearchResult.setMessage("SUCCESS");
//            }else {
//                svcSearchResult.setData(null);
//                svcSearchResult.setOutcome(false);
//                svcSearchResult.setMessage("NO SOURCE FOUND");
//            }
			Long sourceId = findByName.getSourceId();
			System.out.println("sourceId======>" + sourceId);

			List<ConventionalAttributesMaster> findById = conventionalAttributesMasterRepository
					.findBySourceId(sourceId);
			System.out.println("findById=====" + findById.toString());
			svcSearchResult.setData(findById.get(0));

			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("SUCCESS");
		} catch (Exception ex) {
			log.error("Exception occured in getSource method in OrganizationServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}


	// This is for Vendor Dashboard Search!
	@Override
	public ServiceOutcome<List<VendorcheckdashbordtDto>> getAllSearchDataForVendor(SearchAllVendorCheckDTO searchAllVendorCheck) {

		ServiceOutcome<List<VendorcheckdashbordtDto>> svcSearchResult = new ServiceOutcome<>();

		try {
			Long vendorId = searchAllVendorCheck.getVendor_Id();
			String searchInput = searchAllVendorCheck.getUserSearchInput().trim();
			List<VendorChecks> searchAllVendorChecksByVendorIdAndUserSearchInput = null;

			List<Long> byCandidateName = candidateRepository.getByApplicantIdAndCandidateName(searchInput);
			List<Long> applicantIdAndCandidateName = byCandidateName;
			searchAllVendorChecksByVendorIdAndUserSearchInput =	vendorChecksRepository.searchAllVendorChecksByVendorIdAndUserSearchInputByCandidateName(vendorId, applicantIdAndCandidateName, null,null,null);	
			if(searchAllVendorChecksByVendorIdAndUserSearchInput.isEmpty()){
			    String sourceSearchInput = "%" + searchInput + "%";
			    searchAllVendorChecksByVendorIdAndUserSearchInput = vendorChecksRepository.searchAllVendorChecksByVendorIdAndUserSearchInputBySourceName(vendorId, sourceSearchInput, null,null,null);
			}
			if(searchAllVendorChecksByVendorIdAndUserSearchInput.isEmpty()){
			    String sourceSearchInput = "%" + searchInput + "%";
			    searchAllVendorChecksByVendorIdAndUserSearchInput = vendorChecksRepository.searchAllVendorChecksByVendorIdAndUserSearchInputByCheckStatus(vendorId, sourceSearchInput, null,null,null);
			}
			List<Long> vendorCheckIds = searchAllVendorChecksByVendorIdAndUserSearchInput.stream()
			        .map(vendorChecks -> vendorChecks.getVendorcheckId()) // Assuming getVendorCheckId() returns Long
			        .collect(Collectors.toList());
//			List<Long> vendorCheckIds = searchAllVendorChecksByVendorIdAndUserSearchInput.stream()
//			        .map(vendorChecks -> vendorChecks.getVendorcheckId()) // Assuming getVendorCheckId() returns Long
//			        .collect(Collectors.toList());
						
			List<VendorUploadChecks> byVendorChecksVendorcheckIds = vendorUploadChecksRepository.findByVendorChecksVendorcheckIds(vendorCheckIds);
			
//			List<Object> mergedList = new ArrayList<>(searchAllVendorChecksByVendorIdAndUserSearchInput);
//			mergedList.addAll(byVendorChecksVendorcheckIds);
			
			List<VendorChecks> vendorCheckList = searchAllVendorChecksByVendorIdAndUserSearchInput;

			
			List<VendorcheckdashbordtDto> vendorCheckList2 = vendorCheckList.stream()
			        .map(vendorChecks -> {
			            // Your conversion logic here
			        	VendorcheckdashbordtDto dto = new VendorcheckdashbordtDto();
//			            dto.setApplicantId(vendorChecks.getCandidate().getApplicantId());
//			            dto.setAgentUploadedDocument(vendorChecks.getAgentUploadedDocument());
//			            dto.setCandidate_name(vendorChecks.getCandidateName());
			        	dto.setAgentUploadDocumentPathKey(vendorChecks.getAgentUploadDocumentPathKey());
			        	dto.setCandidate(vendorChecks.getCandidate());
			            dto.setSource(vendorChecks.getSource());
			            dto.setCreatedOn(vendorChecks.getCreatedOn());
			            dto.setAgentAttirbuteValue(vendorChecks.getAgentAttirbuteValue());
			            dto.setVendorCheckStatusMaster(vendorChecks.getVendorCheckStatusMaster());
			            dto.setStopCheck(vendorChecks.getStopCheck());
			            dto.setVendorUploadCheck(vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId()));
			            dto.setDocumentname(vendorChecks.getDocumentname());
			            dto.setVendorcheckId(vendorChecks.getVendorcheckId());
			            if ("Prodapt Solutions Private Limited".equalsIgnoreCase(vendorChecks.getCandidate().getOrganization().getOrganizationName())) {
			                dto.setClientApproval(vendorChecks.getClientApproval());
			            } else {
			                dto.setClientApproval(true);
			            }
			            return dto;
			        })
			        .collect(Collectors.toList());
			
			if(applicantIdAndCandidateName.isEmpty()) {
				User findByEmployeeId = userRepository.findByEmployeeId(searchInput);
				Source findBySourceName = sourceRepository.findBySourceName(searchInput);
				if (findByEmployeeId != null) {
					Long agentId = findByEmployeeId.getUserId();
					searchAllVendorChecksByVendorIdAndUserSearchInput =	vendorChecksRepository.searchAllVendorChecksByVendorIdAndUserSearchInputByCandidateName(vendorId, applicantIdAndCandidateName, agentId,null,null);
				}
				else if(findBySourceName != null) {
					Long sourceId = findBySourceName.getSourceId();
					searchAllVendorChecksByVendorIdAndUserSearchInput =	vendorChecksRepository.searchAllVendorChecksByVendorIdAndUserSearchInputByCandidateName(vendorId, applicantIdAndCandidateName, null,sourceId,null);				
				}
				else {
					VendorCheckStatusMaster findByCheckStatusName = vendorCheckStatusMasterRepository.findByCheckStatusName(searchInput);
					Long vendorCheckStatusId = findByCheckStatusName.getVendorCheckStatusMasterId();
					searchAllVendorChecksByVendorIdAndUserSearchInput =	vendorChecksRepository.searchAllVendorChecksByVendorIdAndUserSearchInputByCandidateName(vendorId, applicantIdAndCandidateName, null,null,vendorCheckStatusId);
				}
			}

			if (!searchAllVendorChecksByVendorIdAndUserSearchInput.isEmpty()) {
				List<VendorcheckdashbordtDto> newList = new ArrayList<VendorcheckdashbordtDto>();
				newList.addAll(vendorCheckList2);
				svcSearchResult.setData(newList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO VENDORCHECKS FOUND");
			}


		} catch (Exception e) {
			log.error("Exception occured in getVendorCheckDetails method in userServiceImpl-->" + e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}

		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<VendorcheckdashbordtDto> getVendorCheckStatusAndCount(VendorcheckdashbordtDto dashboardDto) {

		ServiceOutcome<VendorcheckdashbordtDto> svcSearchResult = new ServiceOutcome<VendorcheckdashbordtDto>();
		List<VendorCheckStatusAndCountDTO> vendorCheckStatusAndCount = new ArrayList<VendorCheckStatusAndCountDTO>();

		try {
			if (dashboardDto.getUserId() != null && dashboardDto.getUserId() != 0l) {
//				System.out.println("VendorcheckdashbordtDto SERVICE ================ "+dashboardDto.getFromDate());
//				System.out.println("VendorcheckdashbordtDto SERVICE ================ "+dashboardDto.getToDate());
//				System.out.println("VendorcheckdashbordtDto SERVICE ================ "+dashboardDto.getUserId());

				String fromDate = dashboardDto.getFromDate();
				String toDate = dashboardDto.getToDate();
				Long vendorId = dashboardDto.getUserId();

				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

				LocalDate from = LocalDate.parse(fromDate, inputFormatter);
				LocalDate to = LocalDate.parse(toDate, inputFormatter);

				// Set the time portions to 12:00 AM and 11:59 PM, respectively
				LocalTime fromTime = LocalTime.of(0, 0);
				LocalTime toTime = LocalTime.of(23, 59, 59);

				LocalDateTime fromDateTime = from.atTime(fromTime);
				LocalDateTime toDateTime = to.atTime(toTime);

				String formattedFromDate = fromDateTime.format(outputFormatter);
				String formattedToDate = toDateTime.format(outputFormatter);

				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date fromDateFormat = dateFormat.parse(formattedFromDate);
				Date toDateFormat = dateFormat.parse(formattedToDate);

				AtomicInteger clear = new AtomicInteger(0);
				AtomicInteger inProgress = new AtomicInteger(0);
				AtomicInteger inSufficiency = new AtomicInteger(0);
				AtomicInteger majorDiscrepancy = new AtomicInteger(0);
				AtomicInteger minorDiscrepancy = new AtomicInteger(0);
				AtomicInteger unableToVerify = new AtomicInteger(0);

				List<VendorChecks> vendorList = vendorChecksRepository.vendorDashboardStatusAndCount(vendorId,fromDateFormat,toDateFormat);

				vendorList.stream()
				.map(VendorChecks::getVendorCheckStatusMaster)
				.forEach(vendorCheckStatusId -> {
//					System.out.println("VendorCheckStatusId: " + vendorCheckStatusId);

					if (vendorCheckStatusId != null) {
						if (vendorCheckStatusId.getVendorCheckStatusMasterId() == 1) {
							clear.incrementAndGet();
						} else if (vendorCheckStatusId.getVendorCheckStatusMasterId() == 2) {
							inProgress.incrementAndGet();
						} else if (vendorCheckStatusId.getVendorCheckStatusMasterId() == 3) {
							inSufficiency.incrementAndGet();
						} else if (vendorCheckStatusId.getVendorCheckStatusMasterId() == 4) {
							majorDiscrepancy.incrementAndGet();
						} else if (vendorCheckStatusId.getVendorCheckStatusMasterId() == 5) {
							minorDiscrepancy.incrementAndGet();
						} else if (vendorCheckStatusId.getVendorCheckStatusMasterId() == 6) {
							unableToVerify.incrementAndGet();
						}
					}

				});

				int clearCount = clear.get();
				int inProgressCount = inProgress.get();
				int inSufficientCount = inSufficiency.get();
				int majorDiscrepancyCount = majorDiscrepancy.get();
				int minorDiscrepancyCount = minorDiscrepancy.get();
				int unableToVerifyCountCount = unableToVerify.get();

//				log.info("Clear: {}" + clearCount);
//				log.info("In Progress: {}" + inProgressCount);
//				log.info("In Sufficiency: {}" + inSufficientCount);
//				log.info("Major Discrepancy: {}" + majorDiscrepancyCount);
//				log.info("Minor Discrepancy: {}" + minorDiscrepancyCount);
//				log.info("Unable To Verify: {}" + unableToVerifyCountCount);

				vendorCheckStatusAndCount.add(0,new VendorCheckStatusAndCountDTO("Clear","CLEAR",clearCount));		
				vendorCheckStatusAndCount.add(1,new VendorCheckStatusAndCountDTO("New Upload","NEW UPLOAD",inProgressCount));		
				vendorCheckStatusAndCount.add(2,new VendorCheckStatusAndCountDTO("In Sufficiency","INSUFFICIENCY",inSufficientCount));
				vendorCheckStatusAndCount.add(3,new VendorCheckStatusAndCountDTO("Major Discrepancy","MAJORDISCREPANCY",majorDiscrepancyCount));
				vendorCheckStatusAndCount.add(4,new VendorCheckStatusAndCountDTO("Minor Discrepancy","MINORDISCREPANCY",minorDiscrepancyCount));
				vendorCheckStatusAndCount.add(5,new VendorCheckStatusAndCountDTO("Unable to Verify","UNABLETOVERIFY",unableToVerifyCountCount));

				VendorcheckdashbordtDto vendorDashboardDTO = new VendorcheckdashbordtDto(null,null,null,null,null,vendorCheckStatusAndCount,fromDate,toDate,vendorId);
				svcSearchResult.setData(vendorDashboardDTO);
				svcSearchResult.setOutcome(true);

				//				vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId()			

			}
			else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("please specify user.");
			}

		} catch (Exception e) {
			log.error("Exception occured in getVendorCheckStatusAndCount method in UserServiceIMPL-->",e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);			
		}

		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<VendorcheckdashbordtDto> updateVendor(VendorcheckdashbordtDto updateVendor) {

		ServiceOutcome<VendorcheckdashbordtDto> svcSearchResult = new ServiceOutcome<VendorcheckdashbordtDto>();

		try {

			Long vendorCheckId = updateVendor.getVendorcheckId();
			Long vendorId = updateVendor.getVendorId();

			if(vendorCheckId != null) {
				VendorChecks findByVendorcheckId = vendorChecksRepository.findByVendorcheckId(vendorCheckId);

				if(findByVendorcheckId != null) {
					findByVendorcheckId.setVendorId(vendorId);
					VendorChecks save = vendorChecksRepository.save(findByVendorcheckId);

					if(save != null) {
						svcSearchResult.setMessage("Vendor Updated Successfully");
						svcSearchResult.setOutcome(true);
					}					
				}		

			}

		} catch (Exception e) {
			log.error("Exception occured in updateVendor method in UserServiceIMPL-->",e);

		}

		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<VendorcheckdashbordtDto> stopCheck(VendorcheckdashbordtDto stopCheck) {

		ServiceOutcome<VendorcheckdashbordtDto> svcSearchResult = new ServiceOutcome<VendorcheckdashbordtDto>();

		try {
			Long vendorCheckId = stopCheck.getVendorcheckId();
			Boolean stopCheckStatus = stopCheck.getStopCheck();
			
			Date currentDate = new Date();

	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String formattedDateStr = dateFormat.format(currentDate);

            Date parsedDate = dateFormat.parse(formattedDateStr);
            System.out.println("Parsed Date: " + parsedDate);

			
			if (stopCheckStatus == null) {
				stopCheckStatus = false;
			}		
			stopCheckStatus = !stopCheckStatus;			
			if(vendorCheckId != null) {
				VendorChecks findByVendorcheckId = vendorChecksRepository.findByVendorcheckId(vendorCheckId);

				if(findByVendorcheckId != null) {
					findByVendorcheckId.setStopCheck(stopCheckStatus);
					findByVendorcheckId.setStopCheckCreatedOn(parsedDate);
					VendorChecks save = vendorChecksRepository.save(findByVendorcheckId);

					if(save != null) {
						svcSearchResult.setMessage("StopCheck Status Updated Successfully");
						svcSearchResult.setOutcome(true);
					}					
				}		

			}

		} catch (Exception e) {
			log.error("Exception occured in stopCheck method in UserServiceIMPL-->",e);
		}

		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<?> addChecks(String addCheckData) {

		ServiceOutcome<?> svcSearchResult = new ServiceOutcome<>();

		try {			
			String jsonString = addCheckData;
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(jsonString);

			String sourceIdAsString = jsonNode.get("sourceId").asText();	
			Long sourceId = null;

			if (!sourceIdAsString.isEmpty()) {
				sourceId = Long.parseLong(sourceIdAsString);
			}

			String sourceName = jsonNode.get("sourceName").asText();
			String sourceCode = jsonNode.get("sourceCode").asText();
			String Status = jsonNode.get("changeActiveStatus").asText();

			boolean changeActiveStatus = Boolean.parseBoolean(Status);

			if(sourceId != null && !changeActiveStatus) {
				Optional<Source> findById = sourceRepository.findById(sourceId);
				Source existingSource = findById.get();
				existingSource.setSourceName(sourceName);
				existingSource.setSourceCode(sourceCode.toUpperCase());

				Source updateCheck = sourceRepository.save(existingSource);

				if(updateCheck != null) {
					svcSearchResult.setMessage("Check Updated Successfully");
					svcSearchResult.setOutcome(true);
				}
			}
			else if(changeActiveStatus) {
				String isActive = jsonNode.get("isActive").asText();
				boolean parseIsActive = Boolean.parseBoolean(isActive);

				Optional<Source> findById = sourceRepository.findById(sourceId);
				Source existingSource = findById.get();
				existingSource.setIsActive(!parseIsActive);			    
				Source updateIsActiveStatus = sourceRepository.save(existingSource);

				if(updateIsActiveStatus != null) {
					svcSearchResult.setMessage("Check is Updated to " + (!parseIsActive ? "Active" : "Inactive"));
					svcSearchResult.setOutcome(true);
				}
			}
			else {
				if(sourceName != null) {
					  String sanitizedString = sourceName.replaceAll("[^a-zA-Z0-9]", "");

				        // Remove whitespace
					   sanitizedString = sanitizedString.replaceAll("\\s", "");
  
				        List<Source> findAllSource = sourceRepository.findAll();
//				        List<String> sourceNames = findAllSource.stream()
//				                .map(Source::getSourceName)
//				                .collect(Collectors.toList());
				        
				        List<String> sanitizedSourceNames = findAllSource.stream()
				                .map(Source::getSourceName)
				                .map(sourceNames -> sourceNames.replaceAll("\\s", "").replaceAll("[^a-zA-Z0-9]", ""))
				                .collect(Collectors.toList());
				        	        
				        
				        if(sanitizedSourceNames.contains(sanitizedString)){
							svcSearchResult.setMessage("Check Already Exists");
							svcSearchResult.setOutcome(false);
				        }

				        else {
							Date currentDate = new Date();
							Source source = new Source();
							source.setSourceName(sourceName);
							source.setSourceCode(sourceCode.toUpperCase());
							source.setCreatedOn(currentDate);
							source.setIsActive(true);				
							Source newCheck = sourceRepository.save(source);

							if(newCheck != null) {
								svcSearchResult.setMessage("Check Added Successfully");
								svcSearchResult.setOutcome(true);
							}

						}
				        }
				        
				}



		} catch (Exception e) {
			log.error("Exception occured in addCheck method in UserServiceIMPL-->",e);
		}

		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<List<Source>> getAllSource() {
		ServiceOutcome<List<Source>> svcSearchResult = new ServiceOutcome<List<Source>>();
		try {
			List<Source> sourceList= sourceRepository.findAll();
			if(!sourceList.isEmpty()) {
				svcSearchResult.setData(sourceList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO SOURCE FOUND");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getAllSource method in USERSERVICEIMPL-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	
	@Override
	public ServiceOutcome<Source> deleteCheckById(Long sourceId) {

		ServiceOutcome<Source> svcSearchResult = new ServiceOutcome<>();

		try {
			if(sourceId != null) {
				sourceRepository.deleteById(sourceId);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Check Deleted Successfully");				
			}
		}
		catch (Exception e) {
			log.error("Exception occured in deleteCheckById method in USERSERVICEIMPL-->"+e);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("This Check Cannot be Deleted");
		}

		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<?> inSufficiencyRemarks(String vendorCheckIdAndRemarks) {
		ServiceOutcome<?> svcSearchResult = new ServiceOutcome<>();

		try {
			
			JSONObject jsonObject = new JSONObject(vendorCheckIdAndRemarks);

	        Long vendorCheckId = jsonObject.getLong("vendorCheckId");
	        Long candidateId = jsonObject.getLong("candidateId");
	        String remarks = jsonObject.getString("remarks");
	        
	        System.out.println("vendorCheckId:::"+vendorCheckId);
	        System.out.println("candidateId:::"+candidateId);
	        System.out.println("remarks:::"+remarks);

	        if(vendorCheckId != null && candidateId != null) {
	        	
	        	VendorChecks findByCandidateIdAndVendorCheckId = vendorChecksRepository.findByCandidateCandidateIdAndvendorcheckId(candidateId, vendorCheckId);
	        	
	        	System.out.println("findByCandidateIdAndVendorCheckId>>>>>>"+findByCandidateIdAndVendorCheckId);
	        	
	        	if(findByCandidateIdAndVendorCheckId != null) {
	        		findByCandidateIdAndVendorCheckId.setVendorCheckStatusMaster(vendorCheckStatusMasterRepository.findByCheckStatusCode("INSUFFICIENCY"));
	        		findByCandidateIdAndVendorCheckId.setInSufficiencyRemarks(remarks);
	        		
	        		VendorChecks insufficienyRaised = vendorChecksRepository.save(findByCandidateIdAndVendorCheckId);        		
	        		
	        		if(insufficienyRaised != null) {
	        			emailSentTask.inSufficiencyRaiseEmail(candidateId,insufficienyRaised.getInSufficiencyRemarks(),insufficienyRaised.getSource().getSourceCode());
	        			svcSearchResult.setMessage("Insufficieny Raised");
	        			svcSearchResult.setOutcome(true);
	        			svcSearchResult.setStatus("Success");
	        			}
	        		
	        		}
	        	
	        }
	        
	        
		} catch (Exception e) {
			log.error("Exception occured in inSufficiencyRemarks method in USERSERVICEIMPL-->"+e);
		}
		
		return svcSearchResult;
	}


	@Override
	public ResponseEntity<Resource> getFilesFromResource(String uploadFor, String uploadType) {
	    Map<String, Map<String, String>> resourceMappings = new HashMap<>();

	    // Define resource mappings
	    resourceMappings.put("candidate", Map.of("csv", "candidate.csv", "xlsx", "candidate.xlsx"));
	    resourceMappings.put("agent", Map.of("csv", "Agent.csv", "xlsx", "Agent.xlsx"));
	    resourceMappings.put("client_scope", Map.of("csv", "Client_Scope.csv", "xlsx", "Client_Scope.xlsx"));
	    resourceMappings.put("bulkuansearch",Map.of("csv","BulkUanSearch.csv","xlsx", "BulkUanSearch.xlsx"));
	    resourceMappings.put("dnhdb", Map.of("csv", "DNHDB.csv", "xlsx", "DNHDB.xlsx"));
	    resourceMappings.put("bulkpantouan",Map.of("xlsx", "BulkPanToUan.xlsx"));
	    resourceMappings.put("conventional_candidate", Map.of("csv", "conventional_candidate.csv", "xlsx", "conventional_candidate.xlsx"));
	    // Fetch resource file name based on uploadFor and uploadType
	    String resourceName = resourceMappings
	            .getOrDefault(uploadFor.toLowerCase(), Collections.emptyMap())
	            .get(uploadType.toLowerCase());

	    if (resourceName != null) {
	        log.info("Downloading {} {}", uploadFor, uploadType);
	        ClassPathResource resource = new ClassPathResource(resourceName);

	        HttpHeaders headers = new HttpHeaders();
	        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
	        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(resource);
	    } else {
	        log.info("Resource Null");
	        HttpHeaders headers = new HttpHeaders();
	        return ResponseEntity.ok()
	                .headers(headers)
	                .body(null);
	    }
	}


	@Override
	public ServiceOutcome<String> getAgentUploadedDocument(String pathKey,boolean viewDocument) {
		String base64String = null;
		ServiceOutcome<String> svcSearchResult = new ServiceOutcome<>();

		try {
			if(pathKey != null) {
				if(viewDocument) {
					String getUrl = awsUtils.getPresignedUrl(DIGIVERIFIER_DOC_BUCKET_NAME, pathKey);	
					svcSearchResult.setMessage(getUrl);
					svcSearchResult.setOutcome(true);	
				}
				else {	
					byte[] getbyteArrayFromS3 = awsUtils.getbyteArrayFromS3(DIGIVERIFIER_DOC_BUCKET_NAME, pathKey);
					base64String = Base64.getEncoder().encodeToString(getbyteArrayFromS3);
					svcSearchResult.setMessage(base64String);
					svcSearchResult.setOutcome(true);
				}
			}
		} catch (Exception e) {
			log.info("Exception in getAgentUploadedDocument{ }"+e);
			return null;
		}
		return svcSearchResult;

	}


	@Override
	public ServiceOutcome<VendorChecks> saveConventionalCandidateChecks(String vendorChecks,
			String proofDocumentNew, String addressCheck) {
		ServiceOutcome<VendorChecks> svcSearchResult = new ServiceOutcome<VendorChecks>();
		try {

			Long candidateId = null;

			JSONObject jsonObject = new JSONObject(vendorChecks);

			if (jsonObject.has("candidateId")) {
				//	        	candidateId = Long.parseLong(jsonObject.getString("candidateId"));
				candidateId = jsonObject.getLong("candidateId");
			}
			
			String addressCheckCleaned = addressCheck.replaceAll("[\\[\\]\"\\\\]", "");

			String addressValue = Arrays.stream(addressCheck.replaceAll("[\\[\\]\"\\\\]", "").split(","))
					.map(String::trim)
					.filter(pair -> pair.startsWith("address:"))
					.map(pair -> pair.substring(pair.indexOf(":") + 1))
					.findFirst()
					.orElse(null);

			String cleanedInput = addressCheck.replaceAll("[\\[\\]\"\\\\]", "");

			// Split into individual address pairs
			List<String> addressPairs = Arrays.stream(cleanedInput.split("},\\{"))
					.map(pair -> pair.replace("{", "").replace("}", ""))
					.collect(Collectors.toList());

			// Extract the addresses based on their type
			//		        Map<String, String> addresses = addressPairs.stream()
			//		                .map(pair -> pair.split(","))
			//		                .filter(parts -> parts.length == 2)
			//		                .collect(Collectors.toMap(
			//		                        parts -> parts[0].split(":")[1].trim(), // addressType
			//		                        parts -> parts[1].split(":")[1].trim()  // address
			//		                ));

			System.out.println("addressPairs : "+addressPairs);
			Map<String, String> addresses = addressPairs.stream()
					.map(pair -> {
						// Use regex to find the addressType and address values
						String addressType = pair.replaceAll(".*addressType:([^,]+),.*", "$1").trim();
						String address = pair.replaceAll(".*address:(.*)", "$1").trim();
						return new String[]{addressType, address};
					})
					.filter(parts -> parts.length == 2)
					.collect(Collectors.toMap(
							parts -> parts[0], // addressType
							parts -> parts[1]  // address
							));
			
			Map<String, String> criminal = addressPairs.stream()
					.map(pair -> {
						// Use regex to find the addressType and address values
						String addressType = pair.replaceAll(".*criminal:([^,]+),.*", "$1").trim();
						String address = pair.replaceAll(".*criminalAddress:(.*)", "$1").trim();
						return new String[]{addressType, address};
					})
					.filter(parts -> parts.length == 2)
					.collect(Collectors.toMap(
							parts -> parts[0], // addressType
							parts -> parts[1]  // address
							));

			String presentAddress = addresses.getOrDefault("present", null);
			String permanentAddress = addresses.getOrDefault("permanent", null);
			
			String criminalPresentAddress = criminal.getOrDefault("present", null);
			String criminalPermanentAddress = criminal.getOrDefault("permanent", null);

			// Print the results
//			System.out.println("Present Address: " + presentAddress);
//			System.out.println("Permanent Address: " + permanentAddress);
//			
//			System.out.println("Criminal Present Address: " + criminalPresentAddress);
//			System.out.println("Criminal Permanent Address: " + criminalPermanentAddress);

			List<String> keysList = Arrays.stream(proofDocumentNew.split(","))
					.map(kv -> kv.replaceAll("[\\[\\]\"]", "").trim().split("\\s+")[0])
					.collect(Collectors.toList());
			
//			System.out.println("proofDocumentNew : "+proofDocumentNew);
			Map<String, String> keyValueMap2 = new HashMap<>();
			if(proofDocumentNew != null && !proofDocumentNew.isEmpty()) {	
				String[] parts2 = proofDocumentNew.substring(1, proofDocumentNew.length() - 1).split(",");
				for (String part : parts2) {
					String[] keyValue = part.split(":");
					// Remove quotes from keys and values
					if (keyValue.length >= 2) {
			            // Remove quotes from keys and values
			            String key = keyValue[0].replaceAll("\"", "");
			            String value = keyValue[1].replaceAll("\"", "");
			            keyValueMap2.put(key, value);
			        }
				}

			}

//			System.out.println("KEYLIST : "+keysList);
			Candidate byCandidateId2 = candidateRepository.findByCandidateId(candidateId);
			Orgclientscope byAccountName = orgClientScopeRepository.findByAccountName(byCandidateId2.getAccountName().toLowerCase());
			//	        List<String> checkList = Arrays.asList(byAccountName.getConventionalCandidateCheck().split(","));
//			List<String> checkList = Arrays.stream(byAccountName.getConventionalCandidateCheck().split(","))
//					.filter(s -> !s.trim().equals("Address"))
//					.collect(Collectors.toList());
			List<String> checkList = Arrays.stream(byAccountName.getConventionalCandidateCheck().split(","))
                    .collect(Collectors.toList());
//FILE VALIDATION START
				ArrayList<String> remainingChecks = new ArrayList<>(checkList);
				remainingChecks.removeAll(keysList);
				if (keyValueMap2 != null && !keyValueMap2.isEmpty() && keyValueMap2.containsKey("Criminal present") && !keyValueMap2.containsKey("Criminal permanent")) {
//		            System.out.println("criminal permanent");
		            remainingChecks.add("Criminal permanent");  
				}
				if(keyValueMap2 != null && !keyValueMap2.isEmpty() && keyValueMap2.containsKey("ID Aadhar") && !keyValueMap2.containsKey("ID PAN")) {
		            remainingChecks.add("ID PAN"); 
				}
				else if(keyValueMap2 != null && !keyValueMap2.isEmpty() && !keyValueMap2.containsKey("ID Aadhar") && keyValueMap2.containsKey("ID PAN")) {
		            remainingChecks.add("ID Aadhar"); 
				}
				if (byAccountName.getConventionalCandidateCheck().contains("Address")) {
					if(keyValueMap2 != null && !keyValueMap2.isEmpty() && keyValueMap2.containsKey("Address present") && !keyValueMap2.containsKey("Address permanent")) {
						remainingChecks.add("Address Permanent"); 
					}
				}
//				System.out.println("Remaining Checks: " + remainingChecks);
				if(!remainingChecks.isEmpty()) {
					 String message = remainingChecks + "\nThis Checks Mandatory.";
						svcSearchResult.setMessage(message);
						svcSearchResult.setOutcome(false);
						throw new Exception("Validation failed: Keys mismatch >>>> ");
				}
				//FILE VALIDATION END

//	            String message = remainingChecks + "\nThis Checks Mandatory.";
//				svcSearchResult.setMessage(message);
//				svcSearchResult.setOutcome(false);
//				throw new Exception("Validation failed: Keys mismatch >>>> ");
//			}

				//INPUT VALIDATION FOR ADDRESS AND CRIMINAL START
			if (byAccountName.getConventionalCandidateCheck().contains("Address")) {
				//	        	if( addressValue != null && !addressValue.isEmpty() ) {
				//	        		log.info("Address Check is Not Empty!!!!");
				//	        	}
				if (presentAddress != null && !presentAddress.isEmpty() && permanentAddress != null && !permanentAddress.isEmpty()) {
					log.info("Both present and permanent addresses are available.");
					// Perform the action here
				}
				else {
					log.info("Address Check is Empty!!!");
					//	 	            svcSearchResult.setMessage("Both present and permanent addresses Mandatory.");
					if(permanentAddress.isEmpty() && !keyValueMap2.containsKey("Address permanent")) {	
						svcSearchResult.setMessage("Address Permanent Upload File and Both Address Inputs Mandatory.");
					}else {
						svcSearchResult.setMessage("Both Address Inputs Mandatory.");	
					}
					svcSearchResult.setOutcome(false);
					throw new Exception("Validation failed: Keys mismatch");
				}
			}
			if (byAccountName.getConventionalCandidateCheck().contains("Criminal")) {
				//	        	if( addressValue != null && !addressValue.isEmpty() ) {
				//	        		log.info("Address Check is Not Empty!!!!");
				//	        	}
				if (criminalPresentAddress != null && !criminalPresentAddress.isEmpty() && criminalPermanentAddress != null && !criminalPermanentAddress.isEmpty()) {
					log.info("Both Criminal present and Criminal permanent are available.");
					// Perform the action here
				}
				else {
					log.info("Criminal Check is Empty!!!");
					//	 	            svcSearchResult.setMessage("Both present and permanent addresses Mandatory.");
					if(criminalPermanentAddress.isEmpty() && !keyValueMap2.containsKey("Criminal permanent")) {	
						svcSearchResult.setMessage("Criminal Permanent Upload File and Both Address Inputs Mandatory.");
					}else {
						svcSearchResult.setMessage("Both Criminal Inputs Mandatory.");	
					}
//					svcSearchResult.setMessage("Criminal Permanent Upload File and Both Criminal Inputs Mandatory.");
					svcSearchResult.setOutcome(false);
					throw new Exception("Validation failed: Keys mismatch");
				}
			}
			//INPUT VALIDATION FOR ADDRESS AND CRIMINAL END

			String mergedString = jsonObject.optString("mergedString2");
			mergedString = mergedString.replaceAll("\\\\", "");

			// Create a new JSONObject
			JSONObject jsonObject2 = new JSONObject();

			//	        JSONObject jsonAddressObject = new JSONObject();

			// Parse the mergedString
			String[] keyValuePairs = mergedString.substring(1, mergedString.length() - 1).split(",\\s*");


			for (String pair : keyValuePairs) {
				// Split the pair into key and value
				String[] entry = pair.split(":", 2);
				// Ensure that the entry has at least two elements before proceeding
				if (entry.length >= 2) {
					String key = entry[0].replaceAll("\"", "").trim(); // Remove quotes and trim any leading/trailing whitespace
					String value = entry[1].replaceAll("\"", "").trim(); // Remove quotes and trim any leading/trailing whitespace

					// Remove trailing commas and curly braces from value if they exist
					value = value.replaceAll("[,\\}]*$", "");

					// Check if the key already exists in the JSONObject
					if (jsonObject2.has(key)) {
						// If key exists, get the existing value and append the new value to it
						Object existingValue = jsonObject2.get(key);
						if(!key.equals("documents")) {
							if (existingValue instanceof JSONArray) {
								((JSONArray) existingValue).put(value);
							} else {
								JSONArray newArray = new JSONArray();
								newArray.put(existingValue);
								newArray.put(value);
								jsonObject2.put(key, newArray);
							}
						}
					} else {
						if(!key.equals("documents")) {
							// If key does not exist, add the key-value pair to the JSONObject as an array
							JSONArray newArray = new JSONArray();
							newArray.put(value);
							jsonObject2.put(key, newArray);
						}
					}
				} else {
					// Handle cases where the entry doesn't have enough elements
					//			        System.err.println("Invalid pair format: " + pair);
				}
			}

			//			System.out.println("proof Doc : "+proofDocumentNew);
			String[] parts = proofDocumentNew.substring(1, proofDocumentNew.length() - 1).split(",");
			Map<String, String> keyValueMap = new HashMap<>();
			for (String part : parts) {
				String[] keyValue = part.split(":");
				// Remove quotes from keys and values
				String key = keyValue[0].replaceAll("\"", "");
				String value = keyValue[1].replaceAll("\"", "");
				keyValueMap.put(key, value);
			}
			
			User byUserEmailId = userRepository.findByUserEmailId("digivendor@digiverifier.com");
			Long vendorId = null;
			if(byUserEmailId != null) {
				vendorId = byUserEmailId.getUserId();
			}
			
			if (jsonObject2.has("Education")) {
				log.info("EDUCATION CHECK =====================");
				Object educationValue = jsonObject2.get("Education");

				JSONArray educationArray = jsonObject2.getJSONArray("Education");

				for (int i = 0; i < educationArray.length(); i++) {
					// Get the value at index i
					String educationType = educationArray.getString(i);
					if (educationType.endsWith("}")) {
						educationType = educationType.substring(0, educationType.length() - 1);
					}

					JSONObject jsonEducation = new JSONObject();
					Source source = sourceRepository.findBySourceName("Education");
					Long sourceId = source.getSourceId();
					// String education = jsonEducation.getString("Education");
//					jsonEducation.put("vendorId", "254");
					jsonEducation.put("vendorId", vendorId);
					jsonEducation.put("sourceId", sourceId);
					jsonEducation.put("checkType", "Education " + educationType);
					jsonEducation.put("type", educationType);
					jsonEducation.put("documentname", "Education " + educationType);
					jsonEducation.put("candidateId", candidateId);

					String updatedVendorChecks = jsonEducation.toString();

					//					System.out.println("updatedVendorChecks : " + updatedVendorChecks);

					for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
						//						System.out.println("Value for key : " + entry.getKey() + " : " + entry.getValue());
						//						System.out.println("Education: out " + educationType.toString());

//						if (entry.getKey().contains("Education UG")) {
//							//							 System.out.println("INSIDE EDUCATION::");
//							//				                System.out.println("Value for key : " + entry.getKey());
//						}

						if (entry.getKey().contains("Education "+educationType.trim())) {
							//							System.out.println("Education: In " + educationType.toString());

							byte[] bytes = Base64.getDecoder().decode(entry.getValue());
							//		            	        System.out.println("Bytes Array>>"+bytes);
							saveInitiateVendorChecks(updatedVendorChecks, null, bytes);

						}
					}

				}

			}

			if (jsonObject2.has("Employment")) {
				log.info("EMPLOYMENT CHECK =====================");
				Object educationValue = jsonObject2.get("Employment");

				JSONArray employmentArray = jsonObject2.getJSONArray("Employment");

				for (int i = 0; i < employmentArray.length(); i++) {
					// Get the value at index i
					String employmentType = employmentArray.getString(i);
					employmentType = employmentType.replace("(", "").replace(")", "");
					if (employmentType.endsWith("}")) {
						employmentType = employmentType.substring(0, employmentType.length() - 1);
					}

					JSONObject jsonEmployment = new JSONObject();
					Source source = sourceRepository.findBySourceName("Employment");
					Long sourceId = source.getSourceId();
					// String education = jsonEducation.getString("Education");
					jsonEmployment.put("vendorId", vendorId);
					jsonEmployment.put("sourceId", sourceId);
					jsonEmployment.put("checkType", "Employment " + employmentType);
					jsonEmployment.put("type", employmentType);
					jsonEmployment.put("documentname", "Employment " + employmentType);
					jsonEmployment.put("candidateId", candidateId);

					String updatedVendorChecks = jsonEmployment.toString();
					//					System.out.println("updatedVendorChecks : " + updatedVendorChecks);


					for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
						//						System.out.println("Value for key : " + entry.getKey() + " : " + entry.getValue());
						//						System.out.println("Employment: out " + employmentType.toString());

						if (entry.getKey().contains("Employment "+employmentType.trim())) {
							//							System.out.println("Employment: IN " + employmentType.toString());

							byte[] bytes = Base64.getDecoder().decode(entry.getValue());
							//		            	        System.out.println("Bytes Array>>"+bytes);
							saveInitiateVendorChecks(updatedVendorChecks, null, bytes);

						}
					}

				}
			}

			if (jsonObject2.has("id")) {
				log.info("id items ");
				Object idValue = jsonObject2.get("id");

				JSONArray idArray = jsonObject2.getJSONArray("id");

				for (int i = 0; i < idArray.length(); i++) {
					// Get the value at index i
					String idType = idArray.getString(i);
					if (idType.endsWith("}")) {
						idType = idType.substring(0, idType.length() - 1);
					}
					//					System.out.println("id: " + idType.toString());

					JSONObject jsonId = new JSONObject();
					Source source = sourceRepository.findBySourceName("ID Items");
					Long sourceId = source.getSourceId();
					// String education = jsonEducation.getString("Education");
					jsonId.put("vendorId", vendorId);
					jsonId.put("sourceId", sourceId);
					jsonId.put("checkType", "ID " + idType);
					jsonId.put("type", idType);
					jsonId.put("documentname", "ID " + idType);
					jsonId.put("candidateId", candidateId);

					String updatedVendorChecks = jsonId.toString();

					for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {

						if (entry.getKey().equals("ID " + idType)) {

							byte[] bytes = Base64.getDecoder().decode(entry.getValue());
							//		            	        System.out.println("Bytes Array>>"+bytes);
							saveInitiateVendorChecks(updatedVendorChecks, null, bytes);

						}
					}

				}
			}

			// For Address
			addressCheck = addressCheck.replaceAll("\\\\", "");
			addressCheck = addressCheck.substring(1, addressCheck.length() - 1);

			JsonArray jsonArray = new JsonParser().parse(addressCheck).getAsJsonArray();

			if (addressCheck != null && !addressCheck.isEmpty() && byAccountName.getConventionalCandidateCheck().contains("Address")) {
				//				System.out.println("AddressCheck IN : ");
				JsonArray jsonArrayAddress = new JsonParser().parse(addressCheck).getAsJsonArray();

				// Loop through the JsonArray and extract addressType and address from each
				// JsonObject
				for (int i = 0; i < jsonArrayAddress.size(); i++) {
					JsonObject jsonObjectAddress = jsonArray.get(i).getAsJsonObject();
		            if (jsonObjectAddress.has("addressType")) {
		            	
//		            }
					String addressType = jsonObjectAddress.get("addressType").getAsString();
					String address = jsonObjectAddress.get("address").getAsString();
//										System.out.println("Object " + (i + 1) + ":");
//										System.out.println("addressType: " + addressType);
//										System.out.println("address: " + address);

					JSONObject jsonAddress = new JSONObject();
					Source source = sourceRepository.findBySourceName("Address");
					Long sourceId = source.getSourceId();
					// String education = jsonEducation.getString("Education");
					jsonAddress.put("vendorId", vendorId);
					jsonAddress.put("sourceId", sourceId);
					jsonAddress.put("checkType", "Address " + addressType);
					jsonAddress.put("type", addressType);
					jsonAddress.put("Address", address);
					jsonAddress.put("documentname", "Address " + addressType);
					jsonAddress.put("candidateId", candidateId);

					String updatedVendorChecks = jsonAddress.toString();
//					saveInitiateVendorChecks(updatedVendorChecks, null, null);
					for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
						if (entry.getKey().equals("Address " + addressType)) {
							byte[] bytes = Base64.getDecoder().decode(entry.getValue());
							//		            	        System.out.println("Bytes Array>>"+bytes);
							saveInitiateVendorChecks(updatedVendorChecks, null, bytes);

						}
					}
					if (!keyValueMap.containsKey("Address present")) {
						saveInitiateVendorChecks(updatedVendorChecks, null, null);
					}

				}
			}
			}

			if (jsonObject2.has("criminal")) {
				log.info("criminal");
				Object criminalValue = jsonObject2.get("criminal");

				JSONArray criminalArray = jsonObject2.getJSONArray("criminal");
				JsonArray jsonArrayCriminal = new JsonParser().parse(addressCheck).getAsJsonArray();
				for (int j = 0; j < jsonArrayCriminal.size(); j++) {

					JsonObject jsonObjectCriminal = jsonArray.get(j).getAsJsonObject();
					if(jsonObjectCriminal.has("criminal")) {
						
//					}
					String criminalAddress = jsonObjectCriminal.get("criminalAddress").getAsString();
					
					String criminalType = jsonObjectCriminal.get("criminal").getAsString();
//					String address = jsonObjectAddress.get("address").getAsString();
//				}
					

					JSONObject jsonCriminal = new JSONObject();
					Source source = sourceRepository.findBySourceName("Criminal");
					Long sourceId = source.getSourceId();
					// String education = jsonEducation.getString("Education");
					jsonCriminal.put("vendorId", vendorId);
					jsonCriminal.put("sourceId", sourceId);
					jsonCriminal.put("checkType", "Criminal " + criminalType);
					jsonCriminal.put("type", criminalType);
					jsonCriminal.put("documentname", "Criminal " + criminalType);
					jsonCriminal.put("criminalAddress",criminalAddress);
					jsonCriminal.put("candidateId", candidateId);

					String updatedVendorChecks = jsonCriminal.toString();

					for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {

						if (entry.getKey().equals("Criminal " + criminalType)) {
							byte[] bytes = Base64.getDecoder().decode(entry.getValue());
							//		            	        System.out.println("Bytes Array>>"+bytes);
							saveInitiateVendorChecks(updatedVendorChecks, null, bytes);

						}
					}
					if (!keyValueMap.containsKey("Criminal present")) {
						saveInitiateVendorChecks(updatedVendorChecks, null, null);
					}

				}
			}
			}

//			}
			if (keyValueMap.containsKey("Database ")) {
				log.info("DATABASE");
				JSONObject jsonDatabase = new JSONObject();
				Source source = sourceRepository.findBySourceName("Global Database check");
				Long sourceId = source.getSourceId();
				jsonDatabase.put("vendorId", vendorId);
				jsonDatabase.put("sourceId", sourceId);
				jsonDatabase.put("checkType", source.getSourceName());
				jsonDatabase.put("type", "NA");
				jsonDatabase.put("documentname", "database");
				jsonDatabase.put("candidateId", candidateId);

				String updatedVendorChecks = jsonDatabase.toString();

				for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {

					if (entry.getKey().equals("Database ")) {

						byte[] bytes = Base64.getDecoder().decode(entry.getValue());
						//            	        System.out.println("Bytes Array>>"+bytes);
						saveInitiateVendorChecks(updatedVendorChecks, null, bytes);

					}
				}
			}

			Candidate byCandidateId = candidateRepository.findByCandidateId(candidateId);
			ConventionalCandidateStatus candidateStatus = conventionalCandidateStatusRepository
					.findByCandidateCandidateCode(byCandidateId.getCandidateCode());
			candidateStatus.setStatusMaster(statusMasterRepository.findByStatusCode("CONVENTIONALPENDINGAPPROVAL"));
			log.info("ConventionalCandidateStatus : MOVED TO CONVENTIONALPENDINGAPPROVAL FROM "
					+ candidateStatus.getStatusMaster().getStatusCode() + " => candidateId : "
					+ byCandidateId.getCandidateId());
			candidateStatus.setLastUpdatedOn(new Date());
			candidateStatus = conventionalCandidateStatusRepository.save(candidateStatus);
			conventionalCandidateService.createConventionalCandidateStatusHistory(candidateStatus, "NOTCANDIDATE");
			if (candidateStatus != null) {
				byCandidateId.setSubmittedOn(new Date());
				candidateRepository.save(byCandidateId);
				ConventionalCandidateVerificationState candidateVerificationState = conventionalCandidateVerificationStateRepository.findByCandidateCandidateId(candidateId);
				if(candidateVerificationState == null)
					candidateVerificationState = new ConventionalCandidateVerificationState();
				Date date = new Date();
				ZonedDateTime zonedDateTime = date.toInstant().atZone(ZoneId.systemDefault());
				candidateVerificationState.setCandidate(byCandidateId);
				candidateVerificationState.setCaseInitiationTime(zonedDateTime);
				conventionalCandidateVerificationStateRepository.save(candidateVerificationState);
				emailSentTask.loa(byCandidateId.getCandidateCode());
				svcSearchResult.setMessage("Thank you Form is Submitted");
			}

		} catch (Exception e) {
			log.info(svcSearchResult.getMessage());
			log.info("Exception in saveConventionalCandidateChecks{ }" + e.getMessage());
			svcSearchResult.setMessage(svcSearchResult.getMessage() != null ? svcSearchResult.getMessage() : "Something went Wrong.");
			svcSearchResult.setOutcome(false);
		}

		return svcSearchResult;
	}
	
	public ServiceOutcome<?> getECourtProof(VendorInitiatDto vendorInitiatDto) {
		ServiceOutcome<?> svcSearchResult = new ServiceOutcome<>();

		try {
		       RestTemplate restTemplate = new RestTemplate();

		       ECourtRequestDto requestDto = new ECourtRequestDto();
		        requestDto.setPettitioner(vendorInitiatDto.getCandidateName());
		        requestDto.setFathername(vendorInitiatDto.getFatherName());
		        requestDto.setDob(vendorInitiatDto.getDateOfBirth());
//		        requestDto.setDob(vendorInitiatDto.getAddress());
		        log.info("E court request body {}", requestDto);
		        HttpHeaders headers = new HttpHeaders();
		        headers.set("Content-Type", "application/json");

		        HttpEntity<ECourtRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);
		        ResponseEntity<String> response = restTemplate.exchange(eCourtURL, HttpMethod.POST, requestEntity, String.class);
			    
				JSONObject obj = new JSONObject(response.getBody());
				JSONObject records = null;
				String message = "Something went wrong";
				boolean outcome = false;

				if(obj.has("records"))
					records = obj.getJSONObject("records");
				if(records != null) {
			        ObjectMapper objectMapper = new ObjectMapper();
			        ECourtProofResponseDto responseDto = objectMapper.readValue(records.toString(), ECourtProofResponseDto.class);
			        log.info("E court response and fetched for candidate Id {}, {}", responseDto.getStatus(), vendorInitiatDto.getCandidateId());

			        if(responseDto.getStatus() != null)
				    	  if(responseDto.getStatus().equalsIgnoreCase("Success")) {
				    	      String path = "Candidate/".concat("Generated".concat("/E_COURT_PROOF").concat(".pdf"));
				    	      String base64String = responseDto.getPdf();
				    	      byte[] byteArray = Base64.getDecoder().decode(base64String);
				    	      awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, path, byteArray);
				    	      Content content = new Content();
				    	      content.setCandidateId(vendorInitiatDto.getCandidateId());
				    	      content.setContentCategory(ContentCategory.OTHERS);
//				    	      content.setContentSubCategory(ContentSubCategory.E_COURT_PROOF);
			
				    	      content.setFileType(FileType.PDF);
				    	      content.setContentType(ContentType.ISSUED);
				    	      content.setBucketName(DIGIVERIFIER_DOC_BUCKET_NAME);
				    	      content.setPath(path);
				    	      Content savedObj = contentRepository.save(content);
				    	      log.info("E court proof saved {}", savedObj);
				    	      
				    	      if(savedObj != null) {
				    				Optional<VendorChecks> vendorCheck = vendorChecksRepository.findById(vendorInitiatDto.getVendorcheckId());
				    				if(vendorCheck.isPresent()) {
				    					vendorCheck.get().setECourtProofContentId(savedObj.getContentId());
				    					vendorChecksRepository.save(vendorCheck.get());
				    					
				    					message = savedObj.getContentId().toString();
				    					outcome = true;
				    				}
				    	      }
				    	  }
				}
				
			svcSearchResult.setOutcome(outcome);
			svcSearchResult.setMessage(message);
		} catch (Exception e) {
			log.error("Exception occured in getECourtProof method in USERSERVICEIMPL --> " + e);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something went wrong");
		}
	      
	      return svcSearchResult;
	}

}