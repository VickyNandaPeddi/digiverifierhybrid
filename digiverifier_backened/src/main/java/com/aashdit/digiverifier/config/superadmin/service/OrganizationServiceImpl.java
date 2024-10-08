package com.aashdit.digiverifier.config.superadmin.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.apache.commons.lang3.StringEscapeUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;


import com.aashdit.digiverifier.config.superadmin.model.*;
import com.aashdit.digiverifier.config.superadmin.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.aashdit.digiverifier.config.superadmin.util.ClientExcelUtil;
import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.aashdit.digiverifier.config.superadmin.util.ClientCSVUtil;
import com.aashdit.digiverifier.config.superadmin.repository.OrgclientscopeRepository;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.repository.AgentSampleCsvXlsMasterRepository;
import com.aashdit.digiverifier.config.admin.repository.UserRepository;
import com.aashdit.digiverifier.config.candidate.model.Candidate;
import com.aashdit.digiverifier.config.candidate.repository.CandidateRepository;
import com.aashdit.digiverifier.config.candidate.repository.SuspectEmpMasterRepository;
import com.aashdit.digiverifier.config.superadmin.dto.OrganizationDto;
import com.aashdit.digiverifier.config.superadmin.dto.OrganizationEmailTemplateDto;
import com.aashdit.digiverifier.config.superadmin.dto.ServiceConfigurationDto;
import com.aashdit.digiverifier.config.superadmin.dto.ServiceMasterDto;
import com.aashdit.digiverifier.config.superadmin.dto.SourceServiceListDto;
import com.aashdit.digiverifier.config.superadmin.dto.OrgDto;
import com.aashdit.digiverifier.utils.CustomFileValidator;
import com.aashdit.digiverifier.utils.SecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aashdit.digiverifier.config.superadmin.repository.VendorMasterNewRepository;
import com.aashdit.digiverifier.config.superadmin.dto.VendorMasterDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrganizationServiceImpl implements OrganizationService{
	
	@Autowired
	private VendorMasterNewRepository vendorMasterNewRepository;


	@Autowired
	private OrganizationRepository organizationRepository;
	
	@Autowired
	private ServiceMasterRepository serviceRepository;
	
	@Autowired
	private SourceRepository sourceRepository;
	
	@Autowired
	private ServiceSourceMasterRepository serviceSourceMasterRepository;
	
	@Autowired
	private ServiceTypeConfigRepository serviceTypeConfigRepository;
	
	@Autowired
	private ColorRepository colorRepository;
	
	@Autowired
	private ToleranceConfigRepository toleranceConfigRepository;
	
	@Autowired
	private OrganizationConfigRepository organizationConfigRepository;
	
	@Autowired
	private SuspectEmpMasterRepository suspectEmpMasterRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrganizationExecutiveRepository organizationExecutiveRepository;

	@Autowired
	private OrgclientscopeRepository orgclientscopeRepository;

	@Autowired
	private ClientExcelUtil clientexcelUtil;

	@Autowired
	private ClientCSVUtil clientCSVUtil;

	@Autowired
	private VendorCheckStatusMasterRepository vendorCheckStatusMasterRepository;
	
	@Autowired
	private ServiceMasterRepository serviceMasterRepository; 
	
	@Autowired
	private AgentSampleCsvXlsMasterRepository agentSampleCsvXlsMasterRepository;
	
	@Autowired
	private CandidateRepository candidateRepository;
	
	@Autowired
	private OrganizationEmailTemplateRepository organizationEmailTemplateRepository;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private EnvironmentVal envirnmentVal;

	//new code added for client scope upload//--------------------------------------------
	@Transactional
	@Override
	public ServiceOutcome<Boolean> saveclientscopeInformation(MultipartFile file){

		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		try{
			List<Orgclientscope> orgclientscope=null;
			if (ClientCSVUtil.hasScopeCSVFormat(file)){
				orgclientscope =clientCSVUtil.csvToclientscope(file.getInputStream());

			}
			if (ClientExcelUtil.hasScopeExcelFormat(file)){
				orgclientscope =clientexcelUtil.excelToclientscope(file.getInputStream());
			}

			orgclientscopeRepository.saveAll(orgclientscope);
			svcSearchResult.setMessage("File Upload Successfully");
			svcSearchResult.setOutcome(true);
		}catch (Exception ex) {
			throw new RuntimeException("fail to store csv/xls data: " + ex);
			// svcSearchResult.setMessage("Fail to Upload file");
	    	// svcSearchResult.setOutcome(false);
	    }
			
		return svcSearchResult;
	}
	
	
	// @Override
	// public ServiceOutcome<Organization> saveOrganization(String organizationString, MultipartFile file) {
	// 	ServiceOutcome<Organization> svcSearchResult = new ServiceOutcome<Organization>();
	// 	try {
	// 		Organization organization  = new ObjectMapper().readValue(organizationString, Organization.class);
	// 		Organization result = null;
	// 		User user = SecurityHelper.getCurrentUser();
	// 		if (organization.getOrganizationId() != null && !organization.getOrganizationId().equals(0l)) {
	// 			Optional<Organization> organizationObj = organizationRepository.findById(organization.getOrganizationId());
	// 			if (organizationObj.isPresent()) {
	// 				Organization orgObj = organizationObj.get();
	// 				orgObj.setOrganizationName(organization.getOrganizationName());
	// 				orgObj.setOrganizationEmailId(organization.getOrganizationEmailId());
	// 				orgObj.setOrganizationLocation(organization.getOrganizationLocation());
	// 				orgObj.setCustomerName(organization.getCustomerName());
	// 				orgObj.setCustomerPhoneNumber(organization.getCustomerPhoneNumber());
	// 				orgObj.setGstNumber(organization.getGstNumber());
	// 				orgObj.setPocName(organization.getPocName());
	// 				orgObj.setAccountPocEmail(organization.getAccountPocEmail());
	// 				orgObj.setShipmentAddress(organization.getShipmentAddress());
	// 				orgObj.setOrganizationWebsite(organization.getOrganizationWebsite());
	// 				orgObj.setBillingAddress(organization.getBillingAddress());
	// 				orgObj.setAccountsPocPhoneNumber(organization.getAccountsPocPhoneNumber());
	// 				orgObj.setOrganizationLogo(file!=null?file.getBytes():orgObj.getOrganizationLogo());
	// 				orgObj.setAccountsPoc(organization.getAccountsPoc());
	// 				orgObj.setLastUpdatedOn(new Date());
	// 				orgObj.setLastUpdatedBy(user);
	// 				result = organizationRepository.save(orgObj);
	// 				if(result!=null) {
	// 					svcSearchResult.setData(result);
	// 			    	svcSearchResult.setOutcome(true);
	// 			    	svcSearchResult.setMessage("Customer Updated.");
	// 				}else {
	// 					svcSearchResult.setData(null);
	// 			    	svcSearchResult.setOutcome(false);
	// 			    	svcSearchResult.setMessage("Unable to update Customer.");
	// 				}
	// 			}
	// 		}
	// 		else {
	// 			organization.setOrganizationLogo(file!=null?file.getBytes():null);
	// 			organization.setIsActive(Boolean.valueOf(true));
	// 			organization.setCreatedOn(new Date());
	// 			organization.setCreatedBy(user);
	// 			result = organizationRepository.save(organization);
	// 			if (result != null) {
	// 				svcSearchResult.setData(result);
	// 		    	svcSearchResult.setOutcome(true);
	// 		    	svcSearchResult.setMessage("Customer Added.");
	// 			} else {
	// 				svcSearchResult.setData(null);
	// 		    	svcSearchResult.setOutcome(false);
	// 		    	svcSearchResult.setMessage("Unable to save Customer.");
	// 			}
	// 		}

	// 	} catch (Exception ex) {
	// 		log.error("Exception occured in saveOrganization method in OrganizationServiceImpl-->" + ex);
	// 		svcSearchResult.setData(null);
	//     	svcSearchResult.setOutcome(false);
	//     	svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
	// 	}
	// 	return svcSearchResult;
	// }
	
	@Override
	public ServiceOutcome<Organization> saveOrganization(String organizationString, Boolean showValidation,MultipartFile file) {
		ServiceOutcome<Organization> svcSearchResult = new ServiceOutcome<Organization>();
		System.out.println(showValidation+"inside method");
		try {
			Organization organization  = new ObjectMapper().readValue(organizationString, Organization.class);
//			System.out.println("organization : "+organization);
			Organization result = null;
			User user = SecurityHelper.getCurrentUser();
			
			if(file != null) {
		        List<String> allowedFileTypes = new ArrayList<>();
		        allowedFileTypes.add(".jpg");
		        allowedFileTypes.add(".png");
		        allowedFileTypes.add(".jpeg");

		        CustomFileValidator customFileValidator = new CustomFileValidator(file, allowedFileTypes);
		        customFileValidator.validate();
			}
	        
			if (organization.getOrganizationId() != null && !organization.getOrganizationId().equals(0l)) {
				Optional<Organization> organizationObj = organizationRepository.findById(organization.getOrganizationId());
				if (organizationObj.isPresent()) {
					Organization orgObj = organizationObj.get();
					orgObj.setOrganizationName(organization.getOrganizationName());
					orgObj.setOrganizationEmailId(organization.getOrganizationEmailId());
					orgObj.setOrganizationLocation(organization.getOrganizationLocation());
					orgObj.setCustomerName(organization.getCustomerName());
					orgObj.setCustomerPhoneNumber(organization.getCustomerPhoneNumber());
					orgObj.setGstNumber(organization.getGstNumber());
					orgObj.setPanNumber(organization.getPanNumber());
					orgObj.setSaacCode(organization.getSaacCode());
					orgObj.setPocName(organization.getPocName());
					orgObj.setAccountPocEmail(organization.getAccountPocEmail());
					orgObj.setEmailTemplate(organization.getEmailTemplate());
					orgObj.setEmailConfig(organization.getEmailConfig());
					orgObj.setDaysToPurge(organization.getDaysToPurge());
					orgObj.setReportBackupEmail(organization.getReportBackupEmail());
					orgObj.setNoYearsToBeVerified(organization.getNoYearsToBeVerified());
					orgObj.setShowValidation(showValidation);
					orgObj.setShipmentAddress(organization.getShipmentAddress());
					orgObj.setOrganizationWebsite(organization.getOrganizationWebsite());
					orgObj.setBillingAddress(organization.getBillingAddress());
					orgObj.setAccountsPocPhoneNumber(organization.getAccountsPocPhoneNumber());
					orgObj.setOrganizationLogo(file!=null?file.getBytes():orgObj.getOrganizationLogo());
					orgObj.setAccountsPoc(organization.getAccountsPoc());
					orgObj.setCallBackUrl(organization.getCallBackUrl());
					orgObj.setLastUpdatedOn(new Date());
					orgObj.setPasswordPolicy(organization.getPasswordPolicy());
					orgObj.setLastPasswords(organization.getLastPasswords());
					orgObj.setLastUpdatedBy(user);
					orgObj.setIsMFA(organization.getIsMFA());
					orgObj.setTokenUrlForCallBack(organization.getTokenUrlForCallBack());		
					orgObj.setCallBackUrlUser(organization.getCallBackUrlUser());
					orgObj.setCallBackUrlPassword(organization.getCallBackUrlPassword());
					
					result = organizationRepository.save(orgObj);
					
					if(result!=null) {
						svcSearchResult.setData(result);
				    	svcSearchResult.setOutcome(true);
				    	svcSearchResult.setMessage("Customer Updated.");
					}else {
						svcSearchResult.setData(null);
				    	svcSearchResult.setOutcome(false);
				    	svcSearchResult.setMessage("Unable to update Customer.");
					}
				}
			}
			else {
				System.out.println("inside else-----------");
				organization.setShowValidation(showValidation);													
				organization.setOrganizationLogo(file!=null?file.getBytes():null);
				organization.setIsActive(Boolean.valueOf(true));
				organization.setCreatedOn(new Date());
				organization.setCreatedBy(user);		
				result = organizationRepository.save(organization);
				if (result != null) {
					svcSearchResult.setData(result);
			    	svcSearchResult.setOutcome(true);
			    	svcSearchResult.setMessage("Customer Added.");
				} else {
					svcSearchResult.setData(null);
			    	svcSearchResult.setOutcome(false);
			    	svcSearchResult.setMessage("Unable to save Customer.");
				}
			}

		} catch(IllegalArgumentException ex) {
			log.error("IllegalArgumentException occured in saveOrganization method in OrganizationServiceImpl-->" + ex);
			svcSearchResult.setData(null);
	    	svcSearchResult.setOutcome(false);
	    	svcSearchResult.setMessage(ex.getMessage());
		} catch (Exception ex) {
			log.error("Exception occured in saveOrganization method in OrganizationServiceImpl-->" + ex);
			svcSearchResult.setData(null);
	    	svcSearchResult.setOutcome(false);
	    	svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}
	@Override
	public ServiceOutcome<List<Organization>> getAllOrganization() {
		ServiceOutcome<List<Organization>> svcSearchResult = new ServiceOutcome<List<Organization>>();
		try {
			List<Organization> organizationList= organizationRepository.findAll();
			if(!organizationList.isEmpty()) {
				svcSearchResult.setData(organizationList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO ORGANIZATION FOUND");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getAllOrganization method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}
	// @Override
	// public ServiceOutcome<List<OrgDto>> getAllOrganization() {
	// 	ServiceOutcome<List<OrgDto>> svcSearchResult = new ServiceOutcome<List<OrgDto>>();
	// 	System.out.println("inside method");
	// 	List<OrgDto> orgDtoList=new ArrayList<OrgDto>();
	// 	try {
	// 		List<Organization> organizationList= organizationRepository.findAll();
	// 		for(Organization userobj: organizationList) {
	// 		    if(organizationList != null) {
	// 		    	OrgDto userDto = new OrgDto();
	// 		        BeanUtils.copyProperties(userobj, userDto);
	// 		        orgDtoList.add(userDto);
	// 		    }

	// 		}
	// 		if(!orgDtoList.isEmpty()) {
	// 			svcSearchResult.setData(orgDtoList);
	// 			svcSearchResult.setOutcome(true);
	// 			svcSearchResult.setMessage("SUCCESS");
	// 		}else {
	// 			svcSearchResult.setData(null);
	// 			svcSearchResult.setOutcome(false);
	// 			svcSearchResult.setMessage("NO ORGANIZATION FOUND");
	// 		}
	// 	}
	// 	catch(Exception ex)
	// 	{
	// 		log.error("Exception occured in getAllOrganization method in OrganizationServiceImpl-->"+ex);
	// 		svcSearchResult.setData(null);
	// 		svcSearchResult.setOutcome(false);
	// 		svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
	// 	}
	// 	return svcSearchResult;
	// }

	@Override
	public ServiceOutcome<OrgDto> getOrganizationById(Long organizationId) {
		ServiceOutcome<OrgDto> svcSearchResult = new ServiceOutcome<OrgDto>();
		try {
			Optional<Organization> organization= organizationRepository.findById(organizationId);
			Long passwordPolicy = envirnmentVal.getPasswordPolicyDay();
			OrgDto dto = null;
//			if (organization.isPresent()) {
//	            Organization org = organization.get();
//	             dto = modelMapper.map(org, OrgDto.class);
//	            dto.setPasswordPolicyDay(passwordPolicy);
//	        }
			
			if (organization.isPresent()) {
				 Organization org = organization.get();
	             dto = modelMapper.map(org, OrgDto.class);
	            dto.setPasswordPolicyDay(passwordPolicy);
//				svcSearchResult.setData(organization.get());
				svcSearchResult.setData(dto);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO ORGANIZATION FOUND");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getOrganizationById method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Transactional
	@Override
	public ServiceOutcome<Organization> activeAndInactiveOrganizationById(Long organizationId, Boolean isActive) {
		ServiceOutcome<Organization> svcSearchResult = new ServiceOutcome<Organization>();
		try {
			Organization result=null;
			if (organizationId == null || organizationId.equals(0l)) {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage( "Please specify Organization");
			}
			else {
				Optional<Organization> organizationObj = organizationRepository.findById(organizationId);
				if (organizationObj.isPresent()) {
					Organization orgObj = organizationObj.get();
					if (isActive.booleanValue()) {
						orgObj.setIsActive(isActive);
						result = organizationRepository.save(orgObj);
						
						userRepository.deactiveOrActiveUsers(organizationId,false);
						
						
						svcSearchResult.setData(result);
						svcSearchResult.setOutcome(true);
						svcSearchResult.setMessage( "Customer activated successfully.");
					}
					if (!isActive.booleanValue()) {
						orgObj.setIsActive(isActive);
						result = organizationRepository.save(orgObj);
						userRepository.deactiveOrActiveUsers(organizationId,true);
						svcSearchResult.setData(result);
						svcSearchResult.setOutcome(true);
						svcSearchResult.setMessage( "Customer deactivated successfully.");

					}
				}
				else {
					svcSearchResult.setData(null);
					svcSearchResult.setOutcome(false);
					svcSearchResult.setMessage( "No Organization Found");
				}
			}
		} catch (Exception ex) {
			log.error("Exception occured in activeAndInactiveOrganizationById method in OrganizationServiceImpl-->" + ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<List<Source>> getAllSource() {
		ServiceOutcome<List<Source>> svcSearchResult = new ServiceOutcome<List<Source>>();
		try {
			List<Source> sourceList= sourceRepository.findByIsActiveTrue();
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
			log.error("Exception occured in getAllSource method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<List<ServiceMaster>> getAllServicesByOrganizationId(Long organizationId) {
		ServiceOutcome<List<ServiceMaster>> svcSearchResult = new ServiceOutcome<List<ServiceMaster>>();
		try {
			List<ServiceMaster> serviceList= serviceRepository.findByOrganizationOrganizationId(organizationId);
			if(!serviceList.isEmpty()) {
				svcSearchResult.setData(serviceList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO SERVICE FOUND");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getAllServicesByOrganizationId method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	@Transactional
	public ServiceOutcome<ServiceConfigurationDto> saveOrganizationServiceConfiguration(ServiceConfigurationDto serviceConfigurationDto) {

		ServiceOutcome<ServiceConfigurationDto> svcSearchResult = new ServiceOutcome<>();
		List<ServiceTypeConfig> serviceTypeConfigList=new ArrayList<>();
		ServiceConfigurationDto serviceConfigurationDtoObj=new ServiceConfigurationDto();
		System.out.println(serviceConfigurationDto+"serviceConfigurationDto");
		try {
			User user = SecurityHelper.getCurrentUser();
			if(serviceConfigurationDto.getOrganizationId()!= null) {
				List<ServiceTypeConfig> serviceConfigList = serviceTypeConfigRepository.findAllByOrganizationOrganizationId(serviceConfigurationDto.getOrganizationId());
				if(!serviceConfigList.isEmpty()){
					serviceTypeConfigRepository.deleteByOrganizationOrganizationId(serviceConfigurationDto.getOrganizationId());
				}
			    	for(int i=0;i<serviceConfigurationDto.getSourceServiceId().size();i++)
			    	{
			    		ServiceTypeConfig serviceConfig=new ServiceTypeConfig();
			    		serviceConfig.setOrganization(organizationRepository.findById(serviceConfigurationDto.getOrganizationId()).get());
			    		serviceConfig.setServiceSourceMaster(serviceSourceMasterRepository.findById(serviceConfigurationDto.getSourceServiceId().get(i)).get());
			    		serviceConfig.setCreatedOn(new Date());
			    		serviceConfig.setCreatedBy(user);
			    		serviceTypeConfigRepository.save(serviceConfig);
			    	}
				serviceTypeConfigList=serviceTypeConfigRepository.findAllByOrganizationOrganizationId(serviceConfigurationDto.getOrganizationId());
				List<Long> collect = serviceTypeConfigList.stream().map(x -> x.getServiceSourceMaster().getSourceServiceId()).collect(Collectors.toList());
				serviceConfigurationDtoObj.setSourceServiceId(collect);
		    	if(serviceConfigurationDto!=null) {
		    		ToleranceConfig toleranceConfig=null;
		    		if(serviceConfigurationDto.getToleranceConfigId()!=null) {
		    			Optional<ToleranceConfig> toleranceConfigObj = toleranceConfigRepository.findById(serviceConfigurationDto.getToleranceConfigId());
		    			if(Optional.ofNullable(toleranceConfigObj).isPresent()) {
		    				toleranceConfig=toleranceConfigObj.get();
		    				toleranceConfig.setLastUpdatedBy(user);
			    			toleranceConfig.setLastUpdatedOn(new Date());
		    			}
		    		}
		    		else {
		    			toleranceConfig = new ToleranceConfig();
		    			toleranceConfig.setCreatedOn(new Date());
		    			toleranceConfig.setCreatedBy(user);
		    		}
		    		toleranceConfig.setTenure(serviceConfigurationDto.getTenure());
		    		toleranceConfig.setDataRetentionPeriod(serviceConfigurationDto.getDataRetentionPeriod());
		    		toleranceConfig.setAnonymousDataRetentionPeriod(serviceConfigurationDto.getAnonymousDataRetentionPeriod());
		    		toleranceConfig.setDualEmploymentTolerance(serviceConfigurationDto.getDualEmploymentTolerance());
		    		toleranceConfig.setDualEmployment(serviceConfigurationDto.getDualEmployment());
		    		toleranceConfig.setColor(colorRepository.findById(Long.parseLong(serviceConfigurationDto.getColorId().toString())).get());
		    		toleranceConfig.setNumberYrsOfExperience(serviceConfigurationDto.getNumberYrsOfExperience());
		    		toleranceConfig.setNumberOfEmployment(serviceConfigurationDto.getNumberOfEmployment());
		    		toleranceConfig.setNumberOfLatestEducation(serviceConfigurationDto.getNumberOfLatestEducation());
		    		toleranceConfig.setAccessToRelativesBill(serviceConfigurationDto.getAccessToRelativesBill());
		    		toleranceConfig.setOrganization(organizationRepository.findById(serviceConfigurationDto.getOrganizationId()).get());
		    		toleranceConfig=toleranceConfigRepository.save(toleranceConfig);
		    		serviceConfigurationDtoObj.setToleranceConfig(toleranceConfig);
		    		serviceConfigurationDtoObj.setOrganizationId(toleranceConfig.getOrganization().getOrganizationId());
		    		
					try {
						if(serviceConfigurationDto.getInviteExpiryDays() != null) {
							OrganizationConfig orgConfigObj = new OrganizationConfig();
							Optional<OrganizationConfig> organizationConfig = organizationConfigRepository.findByOrganizationId(serviceConfigurationDto.getOrganizationId());
							if(organizationConfig.isPresent()) {
								orgConfigObj = organizationConfig.get();
								orgConfigObj.setInviteExpiryDays(serviceConfigurationDto.getInviteExpiryDays());
							} else {
								orgConfigObj.setOrganizationId(serviceConfigurationDto.getOrganizationId());
								orgConfigObj.setInviteExpiryDays(serviceConfigurationDto.getInviteExpiryDays());
							}
							
							organizationConfigRepository.save(orgConfigObj);
						}
					} catch (Exception e) {
						log.error("Exception occured in organizationConfig save inside saveOrganizationServiceConfiguration method in OrganizationServiceImpl-->" + e);
					}
		    	}
				List<OrganizationExecutive> organizationExecutiveByOrganizationId = organizationExecutiveRepository
						.findAllByOrganizationIdAndOrderByWeightDesc(serviceConfigurationDto.getOrganizationId());
		    	if(organizationExecutiveByOrganizationId.isEmpty()) {
					OrganizationExecutive organizationExecutive = new OrganizationExecutive();
					organizationExecutive.setOrganizationId(serviceConfigurationDto.getOrganizationId());
					organizationExecutive.setExecutiveId(serviceConfigurationDto.getExecutiveId().get(0));
					organizationExecutive.setWeight(serviceConfigurationDto.getWeight().get(0));
					organizationExecutiveRepository.save(organizationExecutive);
					OrganizationExecutive organizationExecutivee = new OrganizationExecutive();
					organizationExecutivee.setOrganizationId(serviceConfigurationDto.getOrganizationId());
					organizationExecutivee.setExecutiveId(serviceConfigurationDto.getExecutiveId().get(1));
					organizationExecutivee.setWeight(serviceConfigurationDto.getWeight().get(1));
					organizationExecutiveRepository.save(organizationExecutivee);
					OrganizationExecutive organizationExecutiveee = new OrganizationExecutive();
					organizationExecutiveee.setOrganizationId(serviceConfigurationDto.getOrganizationId());
					organizationExecutiveee.setExecutiveId(serviceConfigurationDto.getExecutiveId().get(2));
					organizationExecutiveee.setWeight(serviceConfigurationDto.getWeight().get(2));
					organizationExecutiveRepository.save(organizationExecutiveee);
					OrganizationExecutive organizationExecutiveeee = new OrganizationExecutive();
					organizationExecutiveeee.setOrganizationId(serviceConfigurationDto.getOrganizationId());
					organizationExecutiveeee.setExecutiveId(serviceConfigurationDto.getExecutiveId().get(3));
					organizationExecutiveeee.setWeight(serviceConfigurationDto.getWeight().get(3));
					organizationExecutiveRepository.save(organizationExecutiveeee);
		    	} else {
		    	    List<OrganizationExecutive> updatedExecutives = IntStream.range(0, serviceConfigurationDto.getExecutiveId().size())
		    	            .mapToObj(i -> {
		    	                Long executiveId = serviceConfigurationDto.getExecutiveId().get(i);
		    	                OrganizationExecutive organizationExecutive = organizationExecutiveByOrganizationId.stream()
		    	                    .filter(exec -> exec.getExecutiveId().equals(executiveId))
		    	                    .findFirst()
		    	                    .orElse(new OrganizationExecutive());
		    	                organizationExecutive.setOrganizationId(serviceConfigurationDto.getOrganizationId());
		    	                organizationExecutive.setExecutiveId(executiveId);
		    	                organizationExecutive.setWeight(serviceConfigurationDto.getWeight().get(i));
		    	                return organizationExecutive;
		    	            })
		    	            .collect(Collectors.toList());

		    	        organizationExecutiveRepository.saveAll(updatedExecutives);
		    	}
					
		    	svcSearchResult.setData(serviceConfigurationDtoObj);
		    	svcSearchResult.setOutcome(true);
		    	svcSearchResult.setMessage("Configuration Completed.");
			}
			else {
				svcSearchResult.setData(null);
		    	svcSearchResult.setOutcome(false);
		    	svcSearchResult.setMessage("NOT FOUND");
			}
			
		}catch (Exception ex) {
			log.error("Exception occured in saveOrganizationServiceConfiguration method in OrganizationServiceImpl-->" + ex);
			
			svcSearchResult.setData(null);
	    	svcSearchResult.setOutcome(false);
	    	svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<List<Color>> getAllColor() {
		ServiceOutcome<List<Color>> svcSearchResult = new ServiceOutcome<List<Color>>();
		try {
			List<Color> colorList= colorRepository.findAll();
			if(!colorList.isEmpty()) {
				svcSearchResult.setData(colorList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NOT FOUND");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getAllColor method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<List<ServiceTypeConfig>> getServiceTypeConfigByOrgId(Long organizationId) {
		ServiceOutcome<List<ServiceTypeConfig>> svcSearchResult = new ServiceOutcome<List<ServiceTypeConfig>>();
		try {
			List<ServiceTypeConfig> serviceConfigList= serviceTypeConfigRepository.findAllByOrganizationOrganizationId(organizationId);
			if(!serviceConfigList.isEmpty()) {
				svcSearchResult.setData(serviceConfigList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NOT FOUND");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getServiceTypeConfigByOrgId method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<ToleranceConfig> getToleranceConfigByOrgId(Long organizationId) {
		ServiceOutcome<ToleranceConfig> svcSearchResult = new ServiceOutcome<ToleranceConfig>();
		try {
			ToleranceConfig toleranceConfigObj= toleranceConfigRepository.findByOrganizationOrganizationId(organizationId);
			if (toleranceConfigObj != null) {
				svcSearchResult.setData(toleranceConfigObj);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			} else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NOT FOUND ");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getToleranceConfigByOrgId method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<List<SourceServiceListDto>> getSourceServiceList(Long organizationId) {
		ServiceOutcome<List<SourceServiceListDto>> svcSearchResult = new ServiceOutcome<List<SourceServiceListDto>>();
		Double value=0.0d;
		try {
			List<ServiceMaster> serviceList= serviceRepository.findByOrganizationOrganizationIdAndRatePerReportAndRatePerItem(organizationId,value);
			List<SourceServiceListDto> sourceServiceList=new ArrayList<SourceServiceListDto>();
			if(serviceList!=null) {
				for (ServiceMaster serviceMasterObj : serviceList) {
					SourceServiceListDto sourceServiceListObj=new SourceServiceListDto();
					sourceServiceListObj.setSource(serviceMasterObj.getSource());
					List<ServiceSourceMaster> serviceSourceList=serviceSourceMasterRepository.findAllBySourceSourceId(serviceMasterObj.getSource().getSourceId());
					sourceServiceListObj.setServiceSourceMaster(serviceSourceList);
					sourceServiceList.add(sourceServiceListObj);
				} 
			}
			if(!sourceServiceList.isEmpty()) {
				svcSearchResult.setData(sourceServiceList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO SERVICE FOUND");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getSourceServiceList method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<List<ServiceMaster>> saveOrganizationBilling(Long organizationId, List<ServiceMasterDto> serviceMaster) {
		ServiceOutcome<List<ServiceMaster>> svcSearchResult = new ServiceOutcome<List<ServiceMaster>>();
		List<ServiceMaster> serviceList=null;
		ServiceMaster serviceObj=null;
		Double value=0.0d;
		try {
			User user = SecurityHelper.getCurrentUser();
			if(organizationId!=null && !organizationId.equals(0l)) {
				if(serviceMaster.get(0).getServiceId()==null) {
					serviceList=serviceRepository.findByOrganizationOrganizationId(organizationId);
					if(!serviceList.isEmpty()) {
						svcSearchResult.setData(serviceList);
						svcSearchResult.setOutcome(false);
						svcSearchResult.setMessage("Billing Setup Already Save for This Organization");
					}
					else {
						for (ServiceMasterDto serviceMasterObj : serviceMaster) {
							serviceObj=new ServiceMaster();
							serviceObj.setSource(sourceRepository.findById(serviceMasterObj.getSourceId()).get());
							serviceObj.setOrganization(organizationRepository.findById(organizationId).get());
							if(serviceMasterObj.getRatePerReport()!=null) 
								serviceObj.setRatePerReport(serviceMasterObj.getRatePerReport());
							else 
								serviceObj.setRatePerReport(value);
							if(serviceMasterObj.getRatePerItem()!=null) 
								serviceObj.setRatePerItem(serviceMasterObj.getRatePerItem());
							else 
								serviceObj.setRatePerItem(value);
							serviceObj.setIsActive(true);
							serviceObj.setCreatedOn(new Date());
							serviceObj.setCreatedBy(user);
							serviceObj=serviceRepository.save(serviceObj);
							
							svcSearchResult.setMessage("Billing Details Saved Successfully.");
						}
					}
				}
				for (ServiceMasterDto serviceMasterObj : serviceMaster) {
					if(serviceMasterObj.getServiceId()!=null) {
						Optional<ServiceMaster> service=serviceRepository.findById(serviceMasterObj.getServiceId());
						if (service.isPresent()) {
							serviceObj = service.get();
							serviceObj.setRatePerReport(serviceMasterObj.getRatePerReport());
							serviceObj.setRatePerItem(serviceMasterObj.getRatePerItem());
							serviceObj.setLastUpdatedOn(new Date());
							serviceObj.setLastUpdatedBy(user);
							serviceObj=serviceRepository.save(serviceObj);
							
							svcSearchResult.setMessage("Billing Details Updated Successfully.");
						}
					}
				}
				serviceList=serviceRepository.findByOrganizationOrganizationId(organizationId);
				Double amount=serviceList.stream().filter(a -> a.getRatePerItem() != null && a.getRatePerItem() > 0).mapToDouble(ServiceMaster::getRatePerItem).sum();	
				Double repAmount=serviceList.stream().filter(a -> a.getRatePerReport() != null && a.getRatePerReport() > 0).mapToDouble(ServiceMaster::getRatePerReport).sum();	
				Optional<Organization> organization=organizationRepository.findById(organizationId);
				if(organization.isPresent()) {
					Organization org=organization.get();
					org.setTotal(amount+repAmount);
					organizationRepository.save(org);
				}
				svcSearchResult.setData(serviceList);
				svcSearchResult.setOutcome(true);
			}
		else {
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
	}
	catch (Exception ex) {
		log.error("Exception occured in saveOrganizationBilling method in OrganizationServiceImpl-->" + ex);
		svcSearchResult.setData(null);
		svcSearchResult.setOutcome(false);
		svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
	}
	return svcSearchResult;
	}


	@Override
	public ServiceOutcome<List<OrganizationDto>> getOrganizationListAfterBilling() {
		ServiceOutcome<List<OrganizationDto>> svcSearchResult = new ServiceOutcome<List<OrganizationDto>>();
		try {
			StringBuilder query = new StringBuilder();
			query.append("select distinct (sm.organization_id),org.organization_name ");
			query.append("from t_dgv_service_master sm ");
			query.append("join t_dgv_organization_master org on org.organization_id = sm.organization_id where org.is_active =true;");
			
			Query resultQuery = entityManager.createNativeQuery(query.toString()); 
			
			List<Object[]> organizationObjectList =  resultQuery.getResultList();
			List<OrganizationDto> organizationList = organizationObjectList.stream().map(OrganizationDto::new).collect(Collectors.toList());
			if(!organizationList.isEmpty()) {
				svcSearchResult.setData(organizationList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NOT FOUND");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getServiceTypeConfigByOrgId method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}
	
	@Override
	public List<OrganizationExecutive> getOrganizationExecutiveByOrganizationId(Long organizationId) {
		return organizationExecutiveRepository.findAllByOrganizationIdAndOrderByWeightDesc(organizationId);
	}

	@Override
	public ServiceOutcome<Boolean> getShowvalidation (Long organizationId) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<Boolean>();
		Boolean result=null;
		try{
			// OrganizationDto organizationObj : organization;
			Optional<Organization> organizationObj = organizationRepository.findById(organizationId);
				if (organizationObj.isPresent()) {
					Organization orgObj = organizationObj.get();
					result = organizationObj.get().getShowValidation();
				}
				if(result!=null) {
					svcSearchResult.setOutcome(result);
				}
				System.out.println(result+"________------------------------");
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getShowvalidation method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}



		return svcSearchResult;
	}
	

	@Override
	public ServiceOutcome<List<VendorMasterNew>> saveVendorChecks(Long userId, List<VendorMasterDto> vendorMasterNew) {
		ServiceOutcome<List<VendorMasterNew>> svcSearchResult = new ServiceOutcome<List<VendorMasterNew>>();
		List<VendorMasterNew> serviceList=null;
		System.out.println(vendorMasterNew+"vendorMasterNew");
		VendorMasterNew vendorObj=null; 
		Double value=0.0d;
		try{
			VendorMasterNew result = null;
            
			User user = SecurityHelper.getCurrentUser();
            System.out.println("----------------------------------------------checking33----------------------"+vendorMasterNew.get(0).getVendorId());
            if(vendorMasterNew.get(0).getVendorId() == null ){
				System.out.println("--------------inside save ---------------------");
				
				for (VendorMasterDto vendorMasterObj : vendorMasterNew) {
					vendorObj = new VendorMasterNew();
					
					// vendorObj.setSource(source);
					vendorObj.setSource(sourceRepository.findById(vendorMasterObj.getSourceId()).get());
					if(vendorMasterObj.getRatePerItem()!=null) 
						vendorObj.setRatePerItem(vendorMasterObj.getRatePerItem());
						
					else 
						vendorObj.setRatePerItem(value);
						
					if(vendorMasterObj.getTatPerItem()!=null) 
						vendorObj.setTatPerItem(vendorMasterObj.getTatPerItem());
						
					else 
						vendorObj.setTatPerItem(value);
						
					vendorObj.setIsActive(true);
					vendorObj.setCreatedOn(new Date());
					vendorObj.setCreatedBy(user);
					System.out.println("-------------------------------------userid---------------------------"+userId);
					vendorObj.setUserId(userId);				
					vendorObj.setLastUpdatedOn(new Date());
					vendorObj.setLastUpdatedBy(user);
					System.out.println("------------------check3---------------------------------"+vendorObj);
					result=vendorMasterNewRepository.save(vendorObj);
					svcSearchResult.setMessage("VendorCheck Details Saved Successfully.");
					
				}
			}
			System.out.println("----------------------------------------------checking2----------------------"+vendorMasterNew.get(0).getVendorId());
			for (VendorMasterDto vendorMasterObj : vendorMasterNew) {
					System.out.println("--------------inside for---------------------");
					if(vendorMasterObj.getVendorId()!=null) {
						Optional<VendorMasterNew> service=vendorMasterNewRepository.findById(vendorMasterObj.getVendorId());
						System.out.println("--------------inside update---------------------"+service);
						if (service.isPresent()) {
							vendorObj = service.get();
							vendorObj.setRatePerItem(vendorMasterObj.getRatePerItem());
							vendorObj.setTatPerItem(vendorMasterObj.getTatPerItem());
							vendorObj.setLastUpdatedOn(new Date());
							vendorObj.setLastUpdatedBy(user);
							vendorObj=vendorMasterNewRepository.save(vendorObj);
							
							svcSearchResult.setMessage("Vendor Details Updated Successfully.");
						}
					}
				}
				
            
            
			// System.out.println("------------------check2---------------------------------"+typeof(vendorMasterNew.get(0).getSourceId()));
        }
        catch (Exception ex) {
            log.error("Exception occured in saveVendorChecks method in OrganizationServiceImpl-->" + ex);
            svcSearchResult.setData(null);
            svcSearchResult.setOutcome(false);
            svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
        }
	System.out.println("----------------------------------------------checking4444----------------------"+vendorMasterNew.get(0).getVendorId());
    return svcSearchResult;

    }

	@Override
	public ServiceOutcome<List<VendorMasterNew>> getAllVendorServicesUserId(Long userId) {
		ServiceOutcome<List<VendorMasterNew>> svcSearchResult = new ServiceOutcome<List<VendorMasterNew>>();
		try {
			List<VendorMasterNew> serviceList= vendorMasterNewRepository.findByUserId(userId);
			if(!serviceList.isEmpty()) {
				svcSearchResult.setData(serviceList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NO SERVICE FOUND");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getAllVendorServicesUserId method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}

	@Override
	public ServiceOutcome<List<VendorCheckStatusMaster>> getAllVenorcheckStatus() {
		ServiceOutcome<List<VendorCheckStatusMaster>> svcSearchResult = new ServiceOutcome<List<VendorCheckStatusMaster>>();
		try {
			List<VendorCheckStatusMaster> vendorCheckStatusMasterList= vendorCheckStatusMasterRepository.findAll();
			if(!vendorCheckStatusMasterList.isEmpty()) {
				svcSearchResult.setData(vendorCheckStatusMasterList);
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("SUCCESS");
			}else {
				svcSearchResult.setData(null);
				svcSearchResult.setOutcome(false);
				svcSearchResult.setMessage("NOT FOUND");
			}
		}
		catch(Exception ex)
		{
			log.error("Exception occured in getAllVenorcheckStatus method in OrganizationServiceImpl-->"+ex);
			svcSearchResult.setData(null);
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
		}
		return svcSearchResult;
	}
	
	@Override
	@Transactional
	public ServiceOutcome<String> deleteOrg(Long orgId) {
		ServiceOutcome svcSearchResult = new ServiceOutcome<>();
		try {
	        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=0").executeUpdate();
		    
//	        List<Long> userIds = userRepository.getUserIdsByOrgId(orgId);
	        List<Long> candidateIds = candidateRepository.findCandidateIdsByOrgId(orgId);
			candidateRepository.deleteAllByOrgId(candidateIds);
//	        log.info("candidate ids before flush {}", candidateIds);

	        // Ensure the previous deletion is complete before proceeding
	        entityManager.flush();
//	        log.info("candidate ids after flush {}", candidateIds);
	        candidateRepository.deleteByOrgId(orgId);
//	        log.info("candidate ids after candidate delete {}", candidateIds);

	        
			toleranceConfigRepository.deleteByOrganizationOrganizationId(orgId);
			
			List<Long> organizationExecutives= organizationExecutiveRepository.findAllByOrganizationId(orgId);
			organizationExecutiveRepository.deleteAllById(organizationExecutives);
			serviceMasterRepository.deleteByOrganizationOrganizationId(orgId);
			serviceTypeConfigRepository.deleteByOrganizationOrganizationId(orgId);
			userRepository.deleteByOrganizationOrganizationId(orgId);
			agentSampleCsvXlsMasterRepository.deleteByOrganizationOrganizationId(orgId);
			organizationConfigRepository.deleteByOrganizationId(orgId);
			suspectEmpMasterRepository.removeAllSuspectEmpByOrgId(orgId);
			organizationExecutiveRepository.deleteByOrganizationId(orgId);
			organizationRepository.deleteById(orgId);
			
	        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS=1").executeUpdate();
			
			svcSearchResult.setData("Success");
			svcSearchResult.setOutcome(true);
			svcSearchResult.setMessage("Delete Successfull");
		} catch (Exception e) {
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
			log.error("Exception occured in deleteOrg method in OrganizationServiceImpl --> ", e);
		}
		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<Boolean> saveNUpdateOrgEmailTemplates(
			OrganizationEmailTemplateDto organizationEmailTemplateDto) {
		ServiceOutcome<Boolean> svcSearchResult = new ServiceOutcome<>();
		try {
			if(organizationEmailTemplateDto!=null && organizationEmailTemplateDto.getEmailTemplateId()!=null && organizationEmailTemplateDto.getEmailTemplateId()!=0) {
				log.info("Updting email template for Template ID::{}", organizationEmailTemplateDto.getEmailTemplateId());
				OrganizationEmailTemplate organizationEmailTemplate=organizationEmailTemplateRepository.getByEmailTempId(organizationEmailTemplateDto.getEmailTemplateId());
				
				if(organizationEmailTemplate!=null) {
					organizationEmailTemplate.setCandidateInviteEmailSub(organizationEmailTemplateDto.getInviteMailSub());
					organizationEmailTemplate.setCandidateInviteEmailTemp(organizationEmailTemplateDto.getInviteMailContent());
					organizationEmailTemplate.setCandidateLoaEmailSub(organizationEmailTemplateDto.getLoaMailSub());		
					organizationEmailTemplate.setCandidateLoaEmailTemp(organizationEmailTemplateDto.getLoaMailContent());
					organizationEmailTemplate.setCwfCopyRight(organizationEmailTemplateDto.getCwfCopyright());					
					if(organizationEmailTemplateDto.getCwflogo() != null) {
						organizationEmailTemplate.setCwfLogo(organizationEmailTemplateDto.getCwflogo());	
					}
					
					organizationEmailTemplateRepository.save(organizationEmailTemplate);
					
					svcSearchResult.setData(true);
					svcSearchResult.setMessage("Templates Updated Successfully..");
					svcSearchResult.setOutcome(true);
					svcSearchResult.setStatus("Success");
				}else {
					svcSearchResult.setData(false);
					svcSearchResult.setMessage("Data not Fount To Update..");
					svcSearchResult.setOutcome(false);
					svcSearchResult.setStatus("Fail");
				}
			}else {
				log.info("New Email templates Storing for ORG ID::{}",organizationEmailTemplateDto.getOrgId());			
				
				OrganizationEmailTemplate organizationEmailTemplate= new OrganizationEmailTemplate();
				organizationEmailTemplate.setOrganizationId(organizationEmailTemplateDto.getOrgId());
				organizationEmailTemplate.setCandidateInviteEmailSub(organizationEmailTemplateDto.getInviteMailSub());
				organizationEmailTemplate.setCandidateInviteEmailTemp(organizationEmailTemplateDto.getInviteMailContent());
				organizationEmailTemplate.setCandidateLoaEmailSub(organizationEmailTemplateDto.getLoaMailSub());		
				organizationEmailTemplate.setCandidateLoaEmailTemp(organizationEmailTemplateDto.getLoaMailContent());
				organizationEmailTemplate.setCwfCopyRight(organizationEmailTemplateDto.getCwfCopyright());
				organizationEmailTemplate.setCreatedDate(new Date());
				
				if(organizationEmailTemplateDto.getCwflogo() != null) {
					organizationEmailTemplate.setCwfLogo(organizationEmailTemplateDto.getCwflogo());
				}

				organizationEmailTemplateRepository.save(organizationEmailTemplate);
				svcSearchResult.setData(true);
				svcSearchResult.setMessage("Templates Stored Successfully..");
				svcSearchResult.setOutcome(true);
				svcSearchResult.setStatus("Success");
			}
			
		} catch (Exception e) {
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
			log.error("Exception occured in saveNUpdateOrgEmailTemplates method in OrganizationServiceImpl --> "+ e);
		
		}
		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<OrganizationEmailTemplateDto> getOrgEmailTemplates(Long orgId) {
		ServiceOutcome<OrganizationEmailTemplateDto> svcSearchResult = new ServiceOutcome<>();
		OrganizationEmailTemplateDto organizationEmailTemplateDto=new OrganizationEmailTemplateDto();
		try {
			
			OrganizationEmailTemplate organizationEmailTemplate=organizationEmailTemplateRepository.getByOrganizationId(orgId);
			if(organizationEmailTemplate!=null) {				
				organizationEmailTemplateDto.setEmailTemplateId(organizationEmailTemplate.getEmailTempId());
				organizationEmailTemplateDto.setInviteMailContent(organizationEmailTemplate.getCandidateInviteEmailTemp());
				organizationEmailTemplateDto.setInviteMailSub(organizationEmailTemplate.getCandidateInviteEmailSub());
				organizationEmailTemplateDto.setLoaMailContent(organizationEmailTemplate.getCandidateLoaEmailTemp());
				organizationEmailTemplateDto.setLoaMailSub(organizationEmailTemplate.getCandidateLoaEmailSub());
				organizationEmailTemplateDto.setOrgId(orgId);
				organizationEmailTemplateDto.setCwfCopyright(organizationEmailTemplate.getCwfCopyRight());
				organizationEmailTemplateDto.setCwflogo(organizationEmailTemplate.getCwfLogo());
				
				svcSearchResult.setData(organizationEmailTemplateDto);
				svcSearchResult.setStatus("Success");
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Records get successfully.");
			}else {
				log.info("No records present for this ORG");
				svcSearchResult.setData(organizationEmailTemplateDto);
				svcSearchResult.setStatus("Success");
				svcSearchResult.setOutcome(true);
				svcSearchResult.setMessage("Records Not Present.");
			}
			
		}catch (Exception e) {
			svcSearchResult.setData(organizationEmailTemplateDto);
			svcSearchResult.setStatus("Fail");
			svcSearchResult.setOutcome(false);
			svcSearchResult.setMessage("Something Went Wrong, Please Try After Sometimes.");
			log.error("Exception occured in getOrgEmailTemplates method in OrganizationServiceImpl --> "+ e);
		
		}
		return svcSearchResult;
	}


	@Override
	public ServiceOutcome<?> getEmailTemplateForReview(Long orgId, boolean invitationMail, boolean loaMail) {

		ServiceOutcome<?> svcSearchResult = new ServiceOutcome<>();

		try {

			OrganizationEmailTemplate organizationEmailTemplate=organizationEmailTemplateRepository.getByOrganizationId(orgId);

			LocalDateTime currentDateTime = LocalDateTime.now();

			// Add three days
			LocalDateTime newDateTime = currentDateTime.plusDays(3);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String currentFormatted = currentDateTime.format(formatter);
			String currentDatePlusOne = newDateTime.format(formatter);

			String emailContent = null;
			String loaContent =null;

			if(organizationEmailTemplate!=null && organizationEmailTemplate.getCandidateLoaEmailTemp()!=null) {


				if(loaMail) {
					String loaTemplate = organizationEmailTemplate.getCandidateLoaEmailTemp();
					String loaTemplateEscaped = StringEscapeUtils.escapeHtml4(loaTemplate);
					loaTemplate = loaTemplate.replace("\n", "<br/>");
					loaContent = "<!DOCTYPE html>"+
							"<html>"+
							"<head>"+
							"<style>a:link, a:visited {"
							+ "  background-color: #36f439;"
							+ "  color: white;"
							+ "  padding: 15px 25px;"
							+ "  text-align: center;"
							+ "  text-decoration: none;"
							+ "  display: inline-block;"
							+ "}"
							+ "body {\r\n"
							+ "            font-size: 15px; /* Adjust the size as needed */\r\n"
							+ "            text-align: justify;\r\n"
							+ "        }"
							+ "a:hover, a:active {"
							+ "  background-color: red;"
							+ "}"
							+ ".center {" + "  margin: auto;" + "width: 90%;" + "border: 0px solid #73AD21;" + "text-align: left; " + "padding: 20px;" + "}"
							+ "</style>"
							+ "</head>"
							+ "<body>"
							+ ""
							+ "<div class=\"center\">"  // Fix: Use double quotes to enclose class attribute
							+ "<p style=\"font-family: 'Verdana', sans-serif; font-size: 14px; color: #1a1a1a; line-height: 1.4; font-weight: normal;\">Dear <strong>xyz,</strong></p>"
							+ "<br/>"
							+ "<p style=\"font-family: 'Verdana', sans-serif; font-size: 14px; color: #1a1a1a; line-height: 1.4; font-weight: normal;\">Further to your consent to perform background verification, please find attached the copy of accepted Letter of Authorization.\r\n"+ "</p>"
							+ "<br/>"
							+ "<p style=\"font-family: 'Verdana', sans-serif; font-size: 14px; color: #1a1a1a; line-height: 1.4; font-weight: normal;\">" + loaTemplateEscaped + "</p>" 
							+ "<br/>"
							+ "</div>"
							+ "</body>"
							+ "</html>";

					svcSearchResult.setStatus("Success");
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage(loaContent);

				}

				if(invitationMail) {

					String inviteTemplate = organizationEmailTemplate.getCandidateInviteEmailTemp();
					List<String> targetBoldWords = List.of("Note", "xyz");
					String updateInviteTemplate =makeWordsBold(inviteTemplate, targetBoldWords);
					updateInviteTemplate = inviteTemplate.replace("\n", "<br/>");


					emailContent = 	"<!DOCTYPE html>"+
							"<html>"+
							"<head>"+
							"<style>"+
							""
							+".center {" + "  margin: auto;" + "width: 90%;"+ "border: 0px solid #73AD21;"+ "text-align: left; "+"padding: 20px;"+ "}"
							+ "</style>"
							+"</head>"
							+"<body>"
							+"<div class="+"center"+">" 
							+"<p style=\"font-family: 'Arial', sans-serif; font-size: 14px; color: #333; line-height: 1.5;\">Dear xyz,<br>"
							+"<br>"
							+"<p style=\"font-family: 'Arial', sans-serif; font-size: 14px; color: #333; line-height: 1.5;\">" + updateInviteTemplate + "</p>"
							+"<br>"
							+"<button type='button' style='background: #00AE68; color: #fff; font-weight: 500; padding: 8px 20px; font-size: 14px; border-radius: 5px; box-shadow: 0px 2px 2px #00000052'>Start Verification Process</button>"
							+"<p style=\"font-family: 'Arial', sans-serif; font-size: 14px; color: #333; line-height: 1.5;\">Note: This link will expire on "+currentDatePlusOne+"</p>"
							+ "</div>"
							+"</body>"
							+"</html>";

					svcSearchResult.setStatus("Success");
					svcSearchResult.setOutcome(true);
					svcSearchResult.setMessage(emailContent);
				}
			}						

		} catch (Exception e) {
			log.error("Exception occured in getEmailTemplateForReview method in OrganizationServiceImpl --> "+ e);
		}

		return svcSearchResult;
	}
	
	private static String makeWordsBold(String originalString, List<String> targetWords) {
		String resultString = originalString;
		for (String targetWord : targetWords) {
			resultString = resultString.replaceAll("(?i)\\b" + targetWord + "\\b", "<b>" + targetWord + "</b>");
		}
		return resultString;
	}
}
