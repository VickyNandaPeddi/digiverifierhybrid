package com.aashdit.digiverifier.config.admin.controller;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.admin.dto.UserDto;
import com.aashdit.digiverifier.config.admin.model.ConventionalAttributesMaster;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.service.UserService;
import com.aashdit.digiverifier.config.superadmin.dto.DashboardDto;
import com.aashdit.digiverifier.config.superadmin.model.Source;
import com.aashdit.digiverifier.utils.AwsUtils;
import com.aashdit.digiverifier.utils.SecurityHelper;

import io.swagger.v3.oas.annotations.Operation;

import com.aashdit.digiverifier.config.admin.dto.VendorInitiatDto;
import com.aashdit.digiverifier.config.admin.dto.VendorcheckdashbordtDto;
import com.aashdit.digiverifier.config.admin.dto.vendorChecksDto;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import org.springframework.http.MediaType;

//import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.aashdit.digiverifier.config.admin.dto.AgentInvitationSentDto;
import com.aashdit.digiverifier.config.admin.dto.SearchAllVendorCheckDTO;

@RestController
@RequestMapping(value = "/api/user")
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	@Operation(summary ="Save and  Update ConventionalAttributesMaster")
	@PostMapping("/saveConventionalAttributesMaster")
	public ResponseEntity<ServiceOutcome<ConventionalAttributesMaster>> saveConventionalAttributesMaster(
			@RequestBody ConventionalAttributesMaster conventionalAttributesMaster,
			@RequestHeader("Authorization") String authorization) {

		System.out.println("ConventionalAttributesMaster**************" + conventionalAttributesMaster);
		ServiceOutcome<ConventionalAttributesMaster> svcSearchResult = userService
				.saveConventionalAttributesMaster(conventionalAttributesMaster);
		return new ResponseEntity<ServiceOutcome<ConventionalAttributesMaster>>(svcSearchResult, HttpStatus.OK);

	}

	@Operation(summary ="Save User Information")
	@PostMapping(path = "/saveNUpdateUser")
	public ResponseEntity<ServiceOutcome<UserDto>> saveNUpdateUser(@RequestBody UserDto user,
			@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<UserDto> svcSearchResult = userService.saveUser(user);
		return new ResponseEntity<ServiceOutcome<UserDto>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get user By specific Organization Id")
	@GetMapping("/getUserByOrganizationId/{organizationId}")
	public ResponseEntity<ServiceOutcome<List<UserDto>>> getUserByOrganizationId(@PathVariable Long organizationId,
			@RequestHeader("Authorization") String authorization) {
		User user = SecurityHelper.getCurrentUser();
		ServiceOutcome<List<UserDto>> svcSearchResult = userService.getUserByOrganizationIdAndUser(organizationId,
				user);
		return new ResponseEntity<ServiceOutcome<List<UserDto>>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get user By specific Organization Id and userId")
	@GetMapping("/getUserByOrganizationIdAndUserId/{organizationId}/{userId}")
	public ResponseEntity<ServiceOutcome<List<UserDto>>> getUserByOrganizationIdAndUserId(@PathVariable Long userId,
			@PathVariable Long organizationId, @RequestHeader("Authorization") String authorization) {
		ServiceOutcome<User> user = userService.getUserByUserId(userId);
		ServiceOutcome<List<UserDto>> svcSearchResult = userService.getUserByOrganizationIdAndUser(organizationId,
				user.getData());
		return new ResponseEntity<ServiceOutcome<List<UserDto>>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get user By specific User Id")
	@GetMapping("/getUserById/{userId}")
	public ResponseEntity<ServiceOutcome<UserDto>> getUserById(@PathVariable Long userId,
			@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<UserDto> svcSearchResult = userService.getUserById(userId);
		return new ResponseEntity<ServiceOutcome<UserDto>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Active/Inactive user By specific User Id")
	@PutMapping("/activeNInAtiveUser/{userId}/{isActive}")
	public ResponseEntity<ServiceOutcome<User>> activeNInAtiveUser(@PathVariable("userId") Long userId,
			@PathVariable("isActive") Boolean isActive, @RequestHeader("Authorization") String authorization) {
		ServiceOutcome<User> svcSearchResult = userService.activeAndInactiveUserById(userId, isActive);
		return new ResponseEntity<ServiceOutcome<User>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get Admin Details for the Organization")
	@GetMapping(path = "/getAdminDetailsForOrganization/{organizationId}")
	public ResponseEntity<ServiceOutcome<User>> getAdminDetailsForOrganization(
			@PathVariable("organizationId") Long organizationId, @RequestHeader("Authorization") String authorization)
			throws Exception {
		ServiceOutcome<User> svcSearchResult = userService.getAdminDetailsForOrganization(organizationId);
		return new ResponseEntity<ServiceOutcome<User>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get Agent Supervisor List For The Organization")
	@GetMapping(path = "/getAgentSupervisorList/{organizationId}")
	public ResponseEntity<ServiceOutcome<List<User>>> getAgentSupervisorList(
			@PathVariable("organizationId") Long organizationId, @RequestHeader("Authorization") String authorization)
			throws Exception {
		ServiceOutcome<List<User>> svcSearchResult = userService.getAgentSupervisorList(organizationId);
		return new ResponseEntity<ServiceOutcome<List<User>>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Upload Agent Information file CSV Or XLS")
	@PostMapping("/uploadAgent")
	public ResponseEntity<ServiceOutcome<Boolean>> uploadAgentFile(@RequestParam("file") MultipartFile file,
			@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try {
			System.out.println("inside controller");
			svcSearchResult = userService.saveAgentInformation(file);
			return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
		} catch (Exception ex) {
			log.error("Exception occured in uploadCandidateFile method in UploadCandidateController-->" + ex);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("User with the provided email address already exists.");
			return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
		}

	}

	@Operation(summary ="Get User Profile")
	@GetMapping("/getUserProfile")
	public ResponseEntity<ServiceOutcome<UserDto>> getUserProfile(
			@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<UserDto> svcSearchResult = userService.getUserProfile();
		return new ResponseEntity<ServiceOutcome<UserDto>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get List of Admins")
	@GetMapping(path = "/getAdminList")
	public ResponseEntity<ServiceOutcome<List<User>>> getAdminList(@RequestHeader("Authorization") String authorization)
			throws Exception {
		ServiceOutcome<List<User>> svcSearchResult = userService.getAdminList();
		return new ResponseEntity<ServiceOutcome<List<User>>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Active/Inactive admin By specific User Id")
	@PutMapping("/activeNInAtiveAdmin/{userId}/{isActive}")
	public ResponseEntity<ServiceOutcome<User>> activeNInAtiveAdmin(@PathVariable("userId") Long userId,
			@PathVariable("isActive") Boolean isActive, @RequestHeader("Authorization") String authorization) {
		ServiceOutcome<User> svcSearchResult = userService.activeNInAtiveAdmin(userId, isActive);
		return new ResponseEntity<ServiceOutcome<User>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get Agent  List For The Organization")
	@GetMapping(path = "/getAgentList/{organizationId}")
	public ResponseEntity<ServiceOutcome<List<User>>> getAgentList(@PathVariable("organizationId") Long organizationId,
			@RequestHeader("Authorization") String authorization) throws Exception {
		ServiceOutcome<List<User>> svcSearchResult = userService.getAgentList(organizationId);
		return new ResponseEntity<ServiceOutcome<List<User>>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get user By roleCode")
	@GetMapping("/getUsersByRoleCode/{roleCode}")
	public ResponseEntity<ServiceOutcome<List<User>>> getUserByRoleCode(@PathVariable String roleCode,
			@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<List<User>> svcSearchResult = userService.getUsersByRoleCode(roleCode);
		return new ResponseEntity<ServiceOutcome<List<User>>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get Vendor  List For The Organization")
	@GetMapping(path = "/getVendorList/{organizationId}")
	public ResponseEntity<ServiceOutcome<List<User>>> getVendorList(@PathVariable("organizationId") Long organizationId,
			@RequestHeader("Authorization") String authorization) throws Exception {
		ServiceOutcome<List<User>> svcSearchResult = userService.getVendorList(organizationId);
		return new ResponseEntity<ServiceOutcome<List<User>>>(svcSearchResult, HttpStatus.OK);

	}

	// @Operation(summary ="Save And Update Organization Information")
	// @PostMapping(path = "/saveInitiateVendorChecks/")
	// public ResponseEntity<ServiceOutcome<VendorInitiatDto>>
	// saveInitiateVendorChecks(@RequestBody VendorInitiatDto
	// vendorInitiatDto,@RequestHeader("Authorization") String authorization) {
	// System.out.println(vendorInitiatDto+"***************************************************");
	// ServiceOutcome<VendorInitiatDto> svcSearchResult =
	// userService.saveInitiateVendorChecks(vendorInitiatDto);
	// return new ResponseEntity<ServiceOutcome<VendorInitiatDto>>(svcSearchResult,
	// HttpStatus.OK);
	// }

	@Operation(summary ="Get vendor checks details")
	@GetMapping("/getVendorCheckDetails/{organizationId}")
	public ResponseEntity<?> getVendorCheckDetails(@RequestHeader("Authorization") String authorization,
			@PathVariable("organizationId") Long candidateId) {
		ServiceOutcome<List<VendorInitiatDto>> svcSearchResult = userService.getVendorCheckDetails(candidateId);
		return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get vendor checks details")
	@PostMapping("/getVendorCheck/{vendorId}")
	public ResponseEntity<?> getallVendorCheckDetails(@RequestHeader("Authorization") String authorization,
			@PathVariable("vendorId") Long vendorId,@RequestBody Map<String, Object> requestBody) {
		
		 Map<String, String> dateSearchFilter = (Map<String, String>) requestBody.get("dateSearchFilter");
		    int pageNumber = (int) requestBody.get("pageNumber");
		    int pageSize = (int) requestBody.get("pageSize");
		    String vendorCheckDashboardStatusCode = (String) requestBody.get("vendorCheckDashboardStatusCode");

//		    System.out.println("vendorCheckDashboardStatusCode ============= :::::::"+vendorCheckDashboardStatusCode);
		    log.info("Date Search Filter:: {}", dateSearchFilter);

		
		log.info("PageNumber In con:: {}"+pageNumber);
		log.info("PageSize:: In con {}"+pageSize);
		ServiceOutcome<List<VendorcheckdashbordtDto>> svcSearchResult = userService.getallVendorCheckDetails(vendorId,dateSearchFilter,pageNumber,pageSize,vendorCheckDashboardStatusCode);
		return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}

	// @Operation(summary ="Get vendor checks details")
	// @GetMapping("/getallVendorChecks/{vendorId}")
	// public ResponseEntity<?>
	// getallVendorCheckDetails(@RequestHeader("Authorization") String
	// authorization,@PathVariable("organizationId") Long vendorId) {
	// ServiceOutcome<List<VendorChecks>> svcSearchResult =
	// userService.getallVendorCheckDetails(vendorId);
	// return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	// }

	@Operation(summary ="Save And Update vendordashboard Information")
	@PostMapping(path = "/saveproofuploadVendorChecks", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<ServiceOutcome<VendorChecks>> saveproofuploadVendorChecks(@RequestParam String vendorchecks,
			@RequestParam String vendorAttributesValue, @RequestHeader("Authorization") String authorization,
			@RequestParam(value = "file", required = false) MultipartFile proofDocumentNew) {
		System.out.println(vendorchecks + "***************************************************");
		ServiceOutcome<VendorChecks> svcSearchResult = userService.saveproofuploadVendorChecks(vendorchecks,
				vendorAttributesValue, proofDocumentNew);
		return new ResponseEntity<ServiceOutcome<VendorChecks>>(svcSearchResult, HttpStatus.OK);

	}

	@Operation(summary ="Save And Update vendordashboard Information")
	@PostMapping(path = "/saveInitiateVendorChecks", consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<ServiceOutcome<VendorChecks>> saveInitiateVendorChecks(@RequestParam String vendorchecks,
			@RequestHeader("Authorization") String authorization,
			@RequestParam(value = "file", required = false) MultipartFile proofDocumentNew) {
		System.out.println(vendorchecks + "***************************************************");
		ServiceOutcome<VendorChecks> svcSearchResult = userService.saveInitiateVendorChecks(vendorchecks,
				proofDocumentNew,null);
		return new ResponseEntity<ServiceOutcome<VendorChecks>>(svcSearchResult, HttpStatus.OK);

	}

	@Operation(summary ="Send Email For Candidate")
	@PostMapping("/invitationSent")
	public ResponseEntity<ServiceOutcome<Boolean>> invitationSent(
			@RequestBody AgentInvitationSentDto agentInvitationSentDto,
			@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<Boolean> svcSearchResult = userService.invitationSent(agentInvitationSentDto);
		return new ResponseEntity<ServiceOutcome<Boolean>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get By Id ConventionalAttributesMaster")
	@GetMapping("/getConventionalAttributesMaster/{id}")
	public ResponseEntity<ServiceOutcome<ConventionalAttributesMaster>> getConventionalAttributesMasterById(
			@PathVariable("id") Long id,@RequestParam(value = "type", required = false) String type, @RequestHeader("Authorization") String authorization) {
		ServiceOutcome<ConventionalAttributesMaster> svcSearchResult = userService
				.getConventionalAttributesMasterById(id,type);
		return new ResponseEntity<ServiceOutcome<ConventionalAttributesMaster>>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get Source By SourceName")
	@GetMapping("/getSourceBySourceName/{sourceName}")
	public ResponseEntity<?> findBySourceName(@PathVariable String sourceName,
			@RequestHeader("Authorization") String authorization) {
		System.out.println("sourceName===>" + sourceName);
		ServiceOutcome<ConventionalAttributesMaster> svcSearchResult = userService.findBySourceName(sourceName);
		return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary = "OverAll search By vendorId for vendorDashboard")
	@PostMapping("/searchByVendorId")
	public ResponseEntity<ServiceOutcome<List<VendorChecks>>> getAllSearchDataForVendor(@RequestBody SearchAllVendorCheckDTO searchAllVendorCheck,@RequestHeader("Authorization") String authorization){
		ServiceOutcome<List<VendorChecks>> svcSearchResult = userService.getAllSearchDataForVendor(searchAllVendorCheck);
		return new ResponseEntity<ServiceOutcome<List<VendorChecks>>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Get CandidateList Status And Count")
	@PostMapping("/getVendorCheckStatusAndCount")
	public ResponseEntity<ServiceOutcome<VendorcheckdashbordtDto>> getVendorCheckStatusAndCount(@RequestHeader("Authorization") String authorization,@RequestBody VendorcheckdashbordtDto VendorDashboardDto) {
		
//		log.info("VendorcheckdashbordtDto ================ {}"+VendorDashboardDto.getFromDate());
//		log.info("VendorcheckdashbordtDto ================ {}"+VendorDashboardDto.getToDate());
//		log.info("VendorcheckdashbordtDto ================ {}"+VendorDashboardDto.getUserId());
		
		ServiceOutcome<VendorcheckdashbordtDto> svcSearchResult=  userService.getVendorCheckStatusAndCount(VendorDashboardDto);
		return new ResponseEntity<ServiceOutcome<VendorcheckdashbordtDto>>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary = "UPADTE VENDOR")
	@PostMapping("/updateVendor")
	public ResponseEntity<ServiceOutcome<VendorcheckdashbordtDto>> updateVendor(@RequestBody VendorcheckdashbordtDto updateVendor){
		ServiceOutcome<VendorcheckdashbordtDto> svcSearchResult=  userService.updateVendor(updateVendor);
		return new ResponseEntity<ServiceOutcome<VendorcheckdashbordtDto>>(svcSearchResult, HttpStatus.OK);	
	}
	
	@Operation(summary = "stop Check")
	@PostMapping("/stopCheck")
	public ResponseEntity<ServiceOutcome<VendorcheckdashbordtDto>> stopCheck(@RequestBody VendorcheckdashbordtDto stopCheck){
		ServiceOutcome<VendorcheckdashbordtDto> svcSearchResult=  userService.stopCheck(stopCheck);
		return new ResponseEntity<ServiceOutcome<VendorcheckdashbordtDto>>(svcSearchResult, HttpStatus.OK);

	}
	
	@Operation(summary = "Add new Check to Source table")
	@PostMapping("/addChecks")
	public ResponseEntity<ServiceOutcome<?>> addCheck(@RequestHeader("Authorization") String authorization,@RequestBody String addCheckData){
		
		ServiceOutcome<?> svcSearchResult=  userService.addChecks(addCheckData);
		
		
        return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary ="Get All Source")
	@GetMapping("/getAllSource")
	public ResponseEntity<?> getAllSource(@RequestHeader("Authorization") String authorization) {
		ServiceOutcome<List<Source>> svcSearchResult= userService.getAllSource();
		return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}
	
	@Operation(summary = "Delete Check by sourceId")
	@PostMapping("/deleteCheckBySourceId")
	public ResponseEntity<ServiceOutcome<Source>> deleteCheckById(@RequestBody Long sourceId){
		
		ServiceOutcome<Source> svcSearchResult = userService.deleteCheckById(sourceId);
		
		return new ResponseEntity<ServiceOutcome<Source>>(svcSearchResult,HttpStatus.OK);
		
	}
	
	@Operation(summary = "raiseInSufficieny")
	@PostMapping("/raiseInsufficiency")
	public ResponseEntity<?> raiseInsufficieny(@RequestBody String vendorCheckIdAndRemarks){
		
		
		ServiceOutcome<?> svcSearchResult = userService.inSufficiencyRemarks(vendorCheckIdAndRemarks);
		
        return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}

	@Operation(summary ="Get Files From Resource")
	@GetMapping("/getFilesFromResource/{uploadFor}/{uploadType}")
	public ResponseEntity<Resource> getFilesFromResource(@RequestHeader("Authorization") String authorization,
			@PathVariable("uploadFor") String uploadFor, @PathVariable("uploadType") String uploadType) {
		return userService.getFilesFromResource(uploadFor,uploadType);
	}
	
	@PostMapping("/getAgentUploadedProof")
	public ResponseEntity<ServiceOutcome<?>> getAgentUploadedDocument(@RequestBody String documentPathKey) {
	    ServiceOutcome<?> svcSearchResult = userService.getAgentUploadedDocument(documentPathKey);
        return new ResponseEntity<>(svcSearchResult, HttpStatus.OK);
	}
}
