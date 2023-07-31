package com.aashdit.digiverifier.config.admin.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.admin.dto.UserDto;
import com.aashdit.digiverifier.config.admin.model.ConventionalAttributesMaster;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.dto.VendorInitiatDto;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.admin.dto.AgentInvitationSentDto;

public interface UserService {

	ServiceOutcome<UserDto> saveUser(UserDto user);

	ServiceOutcome<List<UserDto>> getUserByOrganizationIdAndUser(Long organizationId, User user);

	ServiceOutcome<UserDto> getUserById(Long userId);

	ServiceOutcome<User> activeAndInactiveUserById(Long userId, Boolean isActive);

	ServiceOutcome<User> findByUsername(String userName);

	ServiceOutcome<User> saveUserLoginData(User user);

	ServiceOutcome<User> getAdminDetailsForOrganization(Long organizationId);

	ServiceOutcome<List<User>> getAgentSupervisorList(Long organizationId);

	ServiceOutcome<Boolean> saveAgentInformation(MultipartFile file);

	ServiceOutcome<UserDto> getUserProfile();

	ServiceOutcome<List<User>> getAdminList();

	ServiceOutcome<User> activeNInAtiveAdmin(Long userId, Boolean isActive);

	ServiceOutcome<List<User>> getAgentList(Long organizationId);

	ServiceOutcome<User> getUserByUserId(Long userId);

	ServiceOutcome<List<User>> getUsersByRoleCode(String roleCode);

	void logoutUserAfter5Mins();

	ServiceOutcome<List<User>> getVendorList(Long vendorId);

	// ServiceOutcome<VendorInitiatDto> saveInitiateVendorChecks(VendorInitiatDto vendorInitiatDto);

	ServiceOutcome<VendorChecks> saveInitiateVendorChecks(String vendorChecks, MultipartFile proofDocumentNew);

	ServiceOutcome<VendorChecks> saveproofuploadVendorChecks(String vendorChecks,String vendorAttributesValue, MultipartFile proofDocumentNew);

	

	ServiceOutcome<List<VendorChecks>> getVendorCheckDetails(Long candidateId);

	ServiceOutcome<List<VendorChecks>> getallVendorCheckDetails(Long vendorId);

	ServiceOutcome<Boolean> invitationSent(AgentInvitationSentDto agentInvitationSentDto);


    ServiceOutcome<ConventionalAttributesMaster> getConventionalAttributesMasterById(Long Id);

    ServiceOutcome<ConventionalAttributesMaster> findBySourceName(String sourceName);

	ServiceOutcome<ConventionalAttributesMaster> saveConventionalAttributesMaster(
			ConventionalAttributesMaster conventionalAttributesMaster);    


	

}
