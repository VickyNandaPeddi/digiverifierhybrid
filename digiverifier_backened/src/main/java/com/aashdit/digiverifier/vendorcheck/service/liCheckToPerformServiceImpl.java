/**
 *
 */
package com.aashdit.digiverifier.vendorcheck.service;

import com.aashdit.digiverifier.client.securityDetails.DigilockerClient;
import com.aashdit.digiverifier.common.ContentRepository;
import com.aashdit.digiverifier.common.enums.ContentCategory;
import com.aashdit.digiverifier.common.enums.ContentSubCategory;
import com.aashdit.digiverifier.common.enums.ContentType;
import com.aashdit.digiverifier.common.enums.FileType;
import com.aashdit.digiverifier.common.model.Content;
import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.admin.dto.VendorInitiatDto;
import com.aashdit.digiverifier.config.admin.dto.VendorUploadChecksDto;
import com.aashdit.digiverifier.config.admin.dto.VendorcheckdashbordtDto;
import com.aashdit.digiverifier.config.admin.model.User;
import com.aashdit.digiverifier.config.admin.model.VendorChecks;
import com.aashdit.digiverifier.config.admin.model.VendorUploadChecks;
import com.aashdit.digiverifier.config.admin.repository.UserRepository;
import com.aashdit.digiverifier.config.admin.repository.VendorChecksRepository;
import com.aashdit.digiverifier.config.admin.repository.VendorUploadChecksRepository;
import com.aashdit.digiverifier.config.admin.service.UserService;
import com.aashdit.digiverifier.config.candidate.dto.ExecutiveSummaryDto;
import com.aashdit.digiverifier.config.candidate.model.*;
import com.aashdit.digiverifier.config.candidate.repository.*;
import com.aashdit.digiverifier.config.candidate.service.CandidateService;
import com.aashdit.digiverifier.config.superadmin.Enum.ConventionalVerificationStatus;
import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.dto.DashboardDto;
import com.aashdit.digiverifier.config.superadmin.model.*;
import com.aashdit.digiverifier.config.superadmin.repository.SourceRepository;
import com.aashdit.digiverifier.config.superadmin.repository.VendorCheckStatusMasterRepository;
import com.aashdit.digiverifier.config.superadmin.repository.VendorMasterNewRepository;
import com.aashdit.digiverifier.config.superadmin.service.OrganizationService;
import com.aashdit.digiverifier.config.superadmin.service.PdfService;
import com.aashdit.digiverifier.config.superadmin.service.ReportService;
import com.aashdit.digiverifier.email.dto.EmailProperties;
import com.aashdit.digiverifier.globalConfig.EnvironmentVal;
import com.aashdit.digiverifier.utils.DateUtil;
import com.aashdit.digiverifier.utils.*;
import com.aashdit.digiverifier.vendorcheck.dto.*;
import com.aashdit.digiverifier.vendorcheck.model.*;
import com.aashdit.digiverifier.vendorcheck.repository.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.aashdit.digiverifier.digilocker.service.DigilockerServiceImpl.DIGIVERIFIER_DOC_BUCKET_NAME;

/**
 * @author ${Nanda Kishore}
 */
@Service
@Slf4j
public class liCheckToPerformServiceImpl implements liCheckToPerformService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DigilockerClient digilockerClient;
    @Autowired
    private ConventionalCandidateRepository conventionalCandidateRepository;
    @Autowired
    FileUtil fileUtil;
    @Autowired
    AWSConfig awsConfig;
    @Autowired
    AmazonS3 s3Client;
    @Autowired
    private AwsUtils awsUtils;
    @Autowired
    private LiCheckToPerformRepository liCheckToPerformRepository;
    @Autowired
    private ConventionalCandidatesSubmittedRepository conventionalCandidatesSubmittedRepository;
    @Autowired
    private ConventionalCandidateDocumentInfoRepository conventionalCandidateDocumentInfoRepository;
    @Autowired
    EnvironmentVal environmentVal;
    @Autowired
    VendorCheckStatusMasterRepository vendorCheckStatusMasterRepository;

    @Autowired
    CandidateRepository candidateRepository;
    @Autowired
    private PdfService pdfService;

    @Autowired
    VendorChecksRepository vendorChecksRepository;
    @Autowired
    StatusMasterRepository statusMasterRepository;
    @Autowired
    SourceRepository sourceRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private ContentRepository contentRepository;
    @Autowired
    private VendorUploadChecksRepository vendorUploadChecksRepository;
    @Autowired
    private ReportService reportService;
    @Autowired
    CandidateService candidateService;
    @Autowired
    ConventionalAttributesMasterRepository conventionalAttributesMasterRepository;
    @Autowired
    LicheckHistoryRepository licheckHistoryRepository;

    @Transactional
    public String updateBgvCheckRowwiseonProgress(Long requestID, Long checkUniqueId) {
        List<liReportDetails> liReportDetails = new ArrayList<>();
        try {
            log.info("updateBgvCheckRowwise  for Progress() starts");
            ArrayList<UpdateSubmittedCandidatesResponseDto> updateSubmittedCandidatesResponseDtos = new ArrayList<>();
            ArrayList<liChecksDetails> liChecksDetails = new ArrayList<>();
            UpdateSubmittedCandidatesResponseDto conventionalVendorCandidatesSubmitted = new UpdateSubmittedCandidatesResponseDto();
            ConventionalVendorCandidatesSubmitted candidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(String.valueOf(requestID));
            conventionalVendorCandidatesSubmitted.setCandidateID(String.valueOf(candidatesSubmitted.getCandidateId()));
            conventionalVendorCandidatesSubmitted.setName(candidatesSubmitted.getName());
            conventionalVendorCandidatesSubmitted.setPSNO(candidatesSubmitted.getPsNo());
            conventionalVendorCandidatesSubmitted.setRequestID(candidatesSubmitted.getRequestId());
            conventionalVendorCandidatesSubmitted.setVendorName(candidatesSubmitted.getVendorId());
            com.aashdit.digiverifier.vendorcheck.dto.liReportDetails liReportDetails1 = new liReportDetails();
            liReportDetails1.setReportFileExtention("");
            liReportDetails1.setReportFileName("");
            liReportDetails1.setReportAttachment("");
            liReportDetails1.setReportStatus("");
            liReportDetails1.setReportType("");
            ConventionalVendorliChecksToPerform byCheckUniqueId = liCheckToPerformRepository.findByCheckUniqueId(checkUniqueId);
            com.aashdit.digiverifier.vendorcheck.dto.liChecksDetails liChecksDetails1 = new liChecksDetails();
            liChecksDetails1.setCheckCode(Math.toIntExact(byCheckUniqueId.getCheckCode()));
            liChecksDetails1.setCheckName(byCheckUniqueId.getCheckName());
            liChecksDetails1.setCheckRemarks(byCheckUniqueId.getCheckRemarks());
            liChecksDetails1.setCheckStatus(Math.toIntExact((byCheckUniqueId.getCheckStatus().getVendorCheckStatusMasterId())));
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            liChecksDetails1.setCompletedDate(date);
            liChecksDetails1.setModeOfVerficationPerformed(byCheckUniqueId.getModeOfVerificationRequired());
            liChecksDetails1.setModeOfVerficationRequired(byCheckUniqueId.getModeOfVerificationRequired());
            liChecksDetails.add(liChecksDetails1);
            liReportDetails1.setVendorReferenceID(String.valueOf(candidatesSubmitted.getApplicantId()));
            liReportDetails.add(liReportDetails1);
            conventionalVendorCandidatesSubmitted.setLiReportDetails(liReportDetails);
            conventionalVendorCandidatesSubmitted.setLiChecksDetails(liChecksDetails);
            updateSubmittedCandidatesResponseDtos.add(conventionalVendorCandidatesSubmitted);
            //hitting the update request to third party api
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", environmentVal.getMtGrantType());
            map.add("username", environmentVal.getMtUsername());
            map.add("password", environmentVal.getMtPassword());
            HttpHeaders tokenHeader = new HttpHeaders();
            tokenHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            ResponseEntity<String> responseEntity = null;
            HttpEntity<MultiValueMap<String, String>> requestBodyFormUrlEncoded = new HttpEntity<>(map, tokenHeader);
            responseEntity = restTemplate.postForEntity(environmentVal.getConventionalVendorToken(), requestBodyFormUrlEncoded, String.class);
            JSONObject tokenObject = new JSONObject(responseEntity.getBody());
            String access_token = tokenObject.getString("access_token");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + access_token);
            headers.set("Content-Type", "application/json");
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<UpdateSubmittedCandidatesResponseDto>> liCheckDtoHttpEntity = new HttpEntity<>(updateSubmittedCandidatesResponseDtos, headers);
            ResponseEntity<String> icheckRepsonse = restTemplate.exchange(environmentVal.getConventionalUpdateBgvCheckStatusRowwise(), HttpMethod.POST, liCheckDtoHttpEntity, String.class);
            log.info("acknoledge response " + icheckRepsonse);
            log.info("updateBgvCheckRowwiseonProgress ENDS");
        } catch (Exception e) {
            log.error("updateBgvCheckRowwiseonProgress" + e.getMessage());
        }
        log.info("updateBgvCheckRowwise  for Progress() ends");
        return "progress Bgvcheckupdate";
    }


    public ServiceOutcome<String> UpdateBGVCheckStatusRowwise(String vendorChecksString, MultipartFile proofDocumentNew, String modeOfVerificationPerformed) {

        List<liReportDetails> liReportDetails = new ArrayList<>();
        String responedata = "";
        ServiceOutcome<String> updateRowwiseStatusServiceOutcome = new ServiceOutcome<>();
        try {
            log.info("updateAcknoledgementRowwise() for other status  starts");
            ArrayList<UpdateSubmittedCandidatesResponseDto> updateSubmittedCandidatesResponseDtos = new ArrayList<>();
            ArrayList<liChecksDetails> liChecksDetails = new ArrayList<>();
            VendorcheckdashbordtDto vendorcheckdashbordtDto = new ObjectMapper().readValue(vendorChecksString, VendorcheckdashbordtDto.class);
            VendorChecks vendorCheckss = vendorChecksRepository.findByVendorcheckId(vendorcheckdashbordtDto.getVendorcheckId());
            ConventionalVendorliChecksToPerform conventionalVendorliChecksToPerform = liCheckToPerformRepository.findByVendorChecksVendorcheckId(vendorCheckss.getVendorcheckId());
            updateLiCheckStatusByVendor(vendorcheckdashbordtDto.getStatus(), String.valueOf(vendorCheckss.getVendorcheckId()), vendorcheckdashbordtDto.getRemarks(), conventionalVendorliChecksToPerform.getModeOfVerificationPerformed());
//            byte[] vendorProof = proofDocumentNew.getBytes();
//            String base64String = Base64.getEncoder().encodeToString(vendorProof);
            conventionalVendorliChecksToPerform.setModeOfVerificationPerformed(conventionalVendorliChecksToPerform.getModeOfVerificationPerformed());
            com.aashdit.digiverifier.vendorcheck.dto.liReportDetails liReportDetails1 = new liReportDetails();
            ConventionalVendorCandidatesSubmitted conventinalCandidate = conventionalCandidatesSubmittedRepository.findByRequestId(conventionalVendorliChecksToPerform.getRequestId());
            UpdateSubmittedCandidatesResponseDto conventionalVendorCandidatesSubmitted = new UpdateSubmittedCandidatesResponseDto();
            conventionalVendorCandidatesSubmitted.setCandidateID(String.valueOf(conventinalCandidate.getCandidateId()));
            conventionalVendorCandidatesSubmitted.setName(conventinalCandidate.getName());
            conventionalVendorCandidatesSubmitted.setPSNO(conventinalCandidate.getPsNo());
            conventionalVendorCandidatesSubmitted.setRequestID(conventinalCandidate.getRequestId());
            conventionalVendorCandidatesSubmitted.setVendorName(conventinalCandidate.getVendorId());
            Candidate candidate = candidateRepository.findByConventionalRequestId(Long.valueOf(conventionalVendorCandidatesSubmitted.getRequestID()));
            ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted1 = conventionalCandidatesSubmittedRepository.findByRequestId(conventionalVendorCandidatesSubmitted.getRequestID());
            com.aashdit.digiverifier.vendorcheck.dto.liChecksDetails liChecksDetails1 = new liChecksDetails();
            liChecksDetails1.setCheckCode(Math.toIntExact(conventionalVendorliChecksToPerform.getCheckCode()));
            liChecksDetails1.setCheckName(conventionalVendorliChecksToPerform.getCheckName());
            liChecksDetails1.setCheckRemarks(conventionalVendorliChecksToPerform.getCheckRemarks());
            liChecksDetails1.setCheckStatus(Math.toIntExact((conventionalVendorliChecksToPerform.getCheckStatus().getVendorCheckStatusMasterId())));
            String pattern = "dd/MM/yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            String date = simpleDateFormat.format(new Date());
            liChecksDetails1.setCompletedDate(date);
            liChecksDetails1.setModeOfVerficationPerformed(conventionalVendorliChecksToPerform.getModeOfVerificationPerformed());
            liChecksDetails1.setModeOfVerficationRequired(conventionalVendorliChecksToPerform.getModeOfVerificationRequired());
            liChecksDetails.add(liChecksDetails1);
            liReportDetails1.setVendorReferenceID(String.valueOf(conventionalVendorCandidatesSubmitted1.getApplicantId()));
            if (liChecksDetails1.getCheckStatus() == 4 || liChecksDetails1.getCheckStatus() == 5 || liChecksDetails1.getCheckStatus() == 6) {
                ServiceOutcome<String> stringServiceOutcome = generateConventionalCandidateReport(candidate.getCandidateId(), ReportType.INTERIM, "DONT");
                Content content = contentRepository.findByCandidateIdAndCreatedOn(candidate.getCandidateId());
                String bucketName = content.getBucketName();
                String path = content.getPath();
                String[] split = path.split("/");
                String filename = split[split.length - 1];
                String fileExtension = filename.substring(filename.length() - 4, filename.length());
                liReportDetails1.setReportFileExtention(fileExtension);
                liReportDetails1.setReportFileName(filename);
                try {
                    byte[] bytes = awsUtils.getbyteArrayFromS3(bucketName, path);
                    String base64String = Base64.getEncoder().encodeToString(bytes);
                    liReportDetails1.setReportAttachment(base64String);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                liReportDetails1.setReportStatus(String.valueOf(liChecksDetails1.getCheckStatus()));
                liReportDetails1.setReportType("1");
                liReportDetails.add(liReportDetails1);

            }
            conventionalVendorCandidatesSubmitted.setLiReportDetails(liReportDetails);
            conventionalVendorCandidatesSubmitted.setLiChecksDetails(liChecksDetails);
            updateSubmittedCandidatesResponseDtos.add(conventionalVendorCandidatesSubmitted);
            //hitting the update request to third party api
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", environmentVal.getMtGrantType());
            map.add("username", environmentVal.getMtUsername());
            map.add("password", environmentVal.getMtPassword());
            HttpHeaders tokenHeader = new HttpHeaders();
            tokenHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            ResponseEntity<String> responseEntity = null;
            HttpEntity<MultiValueMap<String, String>> requestBodyFormUrlEncoded = new HttpEntity<>(map, tokenHeader);
            responseEntity = restTemplate.postForEntity(environmentVal.getConventionalVendorToken(), requestBodyFormUrlEncoded, String.class);
            JSONObject tokenObject = new JSONObject(responseEntity.getBody());
            String access_token = tokenObject.getString("access_token");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + access_token);
            headers.set("Content-Type", "application/json");
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<UpdateSubmittedCandidatesResponseDto>> liCheckDtoHttpEntity = new HttpEntity<>(updateSubmittedCandidatesResponseDtos, headers);
            ResponseEntity<String> icheckRepsonse = restTemplate.exchange(environmentVal.getConventionalUpdateBgvCheckStatusRowwise(), HttpMethod.POST, liCheckDtoHttpEntity, String.class);
            responedata = icheckRepsonse.getBody();
            log.info("Update Status triggered , LTM Response" + icheckRepsonse);
            updateRowwiseStatusServiceOutcome.setData(responedata);
            updateRowwiseStatusServiceOutcome.setMessage(responedata);
            updateRowwiseStatusServiceOutcome.setStatus(String.valueOf(icheckRepsonse.getStatusCode()));
            updateRowwiseStatusServiceOutcome.setOutcome(true);


        } catch (Exception e) {
            log.error("updateAcknoledgementRowwise()" + e.getMessage());
            updateRowwiseStatusServiceOutcome.setData(e.getMessage());
            updateRowwiseStatusServiceOutcome.setMessage(e.getMessage());
            updateRowwiseStatusServiceOutcome.setStatus("400");
            updateRowwiseStatusServiceOutcome.setOutcome(false);
        }
        log.info("updateAcknoledgementRowwise ends");
        return updateRowwiseStatusServiceOutcome;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServiceOutcome<LicheckRequiredResponseDto> addUpdateLiCheckToPerformData(FetchVendorConventionalCandidateDto licheckDto) throws Exception {
        // TODO Auto-generated method stub
        try {
            String bgvresponse = "";
            log.info("addUpdateLiCheckToPerformData() starts");
            ServiceOutcome<LicheckRequiredResponseDto> svcOutcome = new ServiceOutcome<LicheckRequiredResponseDto>();
            List<ConventionalVendorliChecksToPerform> mailConventionalVendorliChecksToPerform = new ArrayList<>();
            if (licheckDto.getLicheckId() == null) {
                User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);
                //To generate token first
                MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
                map.add("grant_type", environmentVal.getMtGrantType());
                map.add("username", environmentVal.getMtUsername());
                map.add("password", environmentVal.getMtPassword());
                HttpHeaders tokenHeader = new HttpHeaders();
                tokenHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                ResponseEntity<String> responseEntity = null;
                HttpEntity<MultiValueMap<String, String>> requestBodyFormUrlEncoded = new HttpEntity<>(map, tokenHeader);
                responseEntity = restTemplate.postForEntity(environmentVal.getConventionalVendorToken(), requestBodyFormUrlEncoded, String.class);
                JSONObject tokenObject = new JSONObject(responseEntity.getBody());
                String access_token = tokenObject.getString("access_token");
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + access_token);
                headers.set("Content-Type", "application/json");
                ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(licheckDto.getRequestId());
                FetchVendorConventionalCandidateDto fetchVendorConventionalCandidateDto = new FetchVendorConventionalCandidateDto(conventionalVendorCandidatesSubmitted.getRequestId(), String.valueOf(conventionalVendorCandidatesSubmitted.getCandidateId()), conventionalVendorCandidatesSubmitted.getPsNo(), conventionalVendorCandidatesSubmitted.getVendorId(), conventionalVendorCandidatesSubmitted.getRequestType());
                HttpEntity<FetchVendorConventionalCandidateDto> liCheckDtoHttpEntity = new HttpEntity<>(fetchVendorConventionalCandidateDto, headers);
                ResponseEntity<String> icheckRepsonse = restTemplate.exchange(environmentVal.getConventionalVendorFetchVendorChecks(), HttpMethod.POST, liCheckDtoHttpEntity, String.class);
//                log.info("Response from lICheck response TOKEN API " + icheckRepsonse);
                String message = icheckRepsonse.getBody(); //.get("message").toString().replaceAll("=", ":")
                JSONObject obj1 = new JSONObject(message);
                if (obj1.isNull("FastTrackStatus") == false) {
                    conventionalVendorCandidatesSubmitted.setFastTrack(obj1.getString("FastTrackStatus"));
                    ConventionalVendorCandidatesSubmitted save = conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                    log.info("saved the fasttrack status  to the candidate" + save.getCandidateId());
                }


                if (obj1.isNull("liChecksToPerform") == false) {
                    JSONObject liChecksToPerform = obj1.getJSONObject("liChecksToPerform");
                    if (liChecksToPerform.isNull("liChecksRequired") == false) {
                        JSONArray liChecksRequired = liChecksToPerform.getJSONArray("liChecksRequired");
                        List<JSONObject> collect = IntStream.range(0, liChecksRequired.length()).mapToObj(index -> ((JSONObject) liChecksRequired.get(index))).collect(Collectors.toList());
                        //by request id
                        List<ConventionalVendorliChecksToPerform> byCandidateId = liCheckToPerformRepository.findByRequestId(fetchVendorConventionalCandidateDto.getRequestId());
                        if (fetchVendorConventionalCandidateDto.getRequestType().equalsIgnoreCase("InsufficiencyClearance")) {
                            for (JSONObject licheckReq : collect) {
                                String checkUniqueId = licheckReq.getString("Check_Unique_ID");
                                ConventionalVendorliChecksToPerform liChecksToPerform1 = liCheckToPerformRepository.findByCheckUniqueId(Long.valueOf(checkUniqueId));
                                if (liChecksToPerform1 != null) {
                                    LicheckHistory licheckHistory = new LicheckHistory();
                                    VendorCheckStatusMaster byCheckStatusCode = vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(2l);
                                    liChecksToPerform1.setCheckCode(licheckReq.getLong("CheckCode"));
                                    liChecksToPerform1.setCheckUniqueId(licheckReq.getLong("Check_Unique_ID"));
                                    liChecksToPerform1.setCheckName(licheckReq.getString("CheckName"));
                                    liChecksToPerform1.setCheckStatus(byCheckStatusCode);
                                    liChecksToPerform1.setCandidateId(obj1.getString("RequestID"));
                                    liChecksToPerform1.setCheckRemarks(licheckReq.getString("CheckRemarks"));
                                    liChecksToPerform1.setModeOfVerificationRequired(licheckReq.getString("ModeOfVerficationRequired"));
                                    //setting the mode on initial
                                    liChecksToPerform1.setModeOfVerificationPerformed(licheckReq.getString("ModeOfVerficationRequired"));
                                    liChecksToPerform1.setCompletedDate(licheckReq.getString("CompletedDate"));
                                    liChecksToPerform1.setCreatedBy(user);
                                    liChecksToPerform1.setCreatedOn(new Date());
                                    liChecksToPerform1.setCandidateId(obj1.getString("CandidateID"));
                                    liChecksToPerform1.setRequestId(obj1.getString("RequestID"));
                                    if (licheckReq.getString("DatetoComplete").isEmpty() == false) {
                                        liChecksToPerform1.setDateToComplete(licheckReq.getString("DatetoComplete"));
                                    }
                                    ConventionalVendorliChecksToPerform saved = liCheckToPerformRepository.save(liChecksToPerform1);
                                    mailConventionalVendorliChecksToPerform.add(saved);
                                    log.info("added insufficiency licheck" + saved.getCheckUniqueId());
                                    String s = updateBgvCheckRowwiseonProgress(Long.valueOf(liChecksToPerform1.getRequestId()), liChecksToPerform1.getCheckUniqueId());
                                    log.info("sent status of inprogess for resubmitted licheck" + checkUniqueId);
                                    LicheckRequiredResponseDto licheckRequiredResponseDto = new LicheckRequiredResponseDto(saved.getId(), saved.getCheckCode(), saved.getCheckName(), saved.getCheckStatus().getCheckStatusCode(), saved.getCheckRemarks(), saved.getModeOfVerificationRequired(), saved.getModeOfVerificationPerformed(), saved.getCompletedDate());
                                    log.info("addUpdateLiCheckToPerformData() adding licheck with insufficiency data validating with check unique id");
                                }
                            }
                            log.info("reverting the candidate status to  original status");
                            ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted1 = conventionalCandidatesSubmittedRepository.findByRequestId(String.valueOf(obj1.getLong("RequestID")));
                            conventionalVendorCandidatesSubmitted1.setRequestType(conventionalVendorCandidatesSubmitted1.getOldRequestType());
                            conventionalVendorCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted1);
                            log.info("acknoledged after all check get added for insufficiency starts");
                            acknoledgeAfterSavedCandidate(obj1.getLong("RequestID"));
                            log.info("acknoledged after all check get added for insufficiency ends");
                            log.info("email sending for insuff starts");
                            emailSentTask.sendEmailOnRaisedInsufficiency(conventionalVendorCandidatesSubmitted, mailConventionalVendorliChecksToPerform, "kavitha@digiverifier.com", "nandakishore.p@digiverifier.com");
                            log.info("email sending for insuff ends");

                        } else {
                            for (JSONObject licheckReq : collect) {
                                if (byCandidateId.isEmpty()) {
                                    String checkUniqueId = licheckReq.getString("Check_Unique_ID");
                                    ConventionalVendorliChecksToPerform byCheckUniqueId = liCheckToPerformRepository.findByCheckUniqueId(Long.valueOf(checkUniqueId));
                                    if (byCheckUniqueId == null) {
                                        VendorCheckStatusMaster byCheckStatusCode = vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(7l);
                                        ConventionalVendorliChecksToPerform liChecksToPerform1 = new ConventionalVendorliChecksToPerform();
                                        liChecksToPerform1.setCheckCode(licheckReq.getLong("CheckCode"));
                                        liChecksToPerform1.setCheckUniqueId(licheckReq.getLong("Check_Unique_ID"));
                                        liChecksToPerform1.setCheckName(licheckReq.getString("CheckName"));
                                        liChecksToPerform1.setCheckStatus(byCheckStatusCode);
                                        liChecksToPerform1.setRequestId(fetchVendorConventionalCandidateDto.getRequestId());
                                        liChecksToPerform1.setCheckRemarks(licheckReq.getString("CheckRemarks"));
                                        liChecksToPerform1.setModeOfVerificationRequired(licheckReq.getString("ModeOfVerficationRequired"));
                                        liChecksToPerform1.setModeOfVerificationPerformed(licheckReq.getString("ModeOfVerficationRequired"));
                                        liChecksToPerform1.setCompletedDate(licheckReq.getString("CompletedDate"));
                                        liChecksToPerform1.setCreatedBy(user);
                                        liChecksToPerform1.setCreatedOn(new Date());
                                        liChecksToPerform1.setCandidateId(obj1.getString("CandidateID"));
                                        liChecksToPerform1.setRequestId(obj1.getString("RequestID"));
                                        if (licheckReq.getString("DatetoComplete").isEmpty() == false) {
                                            liChecksToPerform1.setDateToComplete(licheckReq.getString("DatetoComplete"));
                                        }
                                        ConventionalVendorliChecksToPerform saved = liCheckToPerformRepository.save(liChecksToPerform1);
                                        LicheckHistory licheckHistory = new LicheckHistory();
                                        licheckHistory.setCandidateId(Long.valueOf(saved.getCandidateId()));
                                        licheckHistory.setCheckName(saved.getCheckName());
                                        licheckHistory.setCheckUniqueId(saved.getCheckUniqueId());
                                        licheckHistory.setCreatedBy(saved.getCreatedBy().getUserName());
                                        licheckHistory.setCreatedOn(new Date());
                                        licheckHistory.setCheckStatus(saved.getCheckStatus().getCheckStatusCode());
                                        licheckHistory.setRequestType(conventionalVendorCandidatesSubmitted.getRequestType());
                                        licheckHistory.setCandidateStatus(conventionalVendorCandidatesSubmitted.getStatus().getStatusCode());
                                        licheckHistory.setRequestId(Long.valueOf(saved.getRequestId()));
                                        LicheckHistory save = licheckHistoryRepository.save(licheckHistory);
                                        log.info("licheck history NEWUPLOAD SAVED" + save.getId());
                                        LicheckRequiredResponseDto licheckRequiredResponseDto = new LicheckRequiredResponseDto(saved.getId(), saved.getCheckCode(), saved.getCheckName(), saved.getCheckStatus().getCheckStatusCode(), saved.getCheckRemarks(), saved.getModeOfVerificationRequired(), saved.getModeOfVerificationPerformed(), saved.getCompletedDate());
                                        svcOutcome.setData(licheckRequiredResponseDto);
                                    }
                                }
                                //for new upload
                                if (!byCandidateId.isEmpty()) {
                                    byCandidateId.forEach(lidata -> {
                                        VendorCheckStatusMaster byCheckStatusCode = vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(7l);
                                        String checkUniqueId = licheckReq.getString("Check_Unique_ID");
                                        ConventionalVendorliChecksToPerform byCheckUniqueId = liCheckToPerformRepository.findByCheckUniqueId(Long.valueOf(checkUniqueId));
                                        if (byCheckUniqueId == null) {
                                            log.info("addUpdateLiCheckToPerformData() adding new licheck other than insufficiency data ");
                                            ConventionalVendorliChecksToPerform liChecksToPerform1 = new ConventionalVendorliChecksToPerform();
                                            liChecksToPerform1.setCheckCode(licheckReq.getLong("CheckCode"));
                                            liChecksToPerform1.setCheckName(licheckReq.getString("CheckName"));
                                            liChecksToPerform1.setCheckUniqueId(licheckReq.getLong("Check_Unique_ID"));
                                            liChecksToPerform1.setCheckStatus(byCheckStatusCode);
//                                    liChecksToPerform1.setResubmitted("NOTRESUBMITTED");
                                            liChecksToPerform1.setCheckRemarks(licheckReq.getString("CheckRemarks"));
                                            liChecksToPerform1.setModeOfVerificationRequired(licheckReq.getString("ModeOfVerficationRequired"));
                                            liChecksToPerform1.setModeOfVerificationPerformed(licheckReq.getString("ModeOfVerficationRequired"));
                                            liChecksToPerform1.setCompletedDate(licheckReq.getString("CompletedDate"));
                                            liChecksToPerform1.setCreatedBy(user);
                                            liChecksToPerform1.setCreatedOn(new Date());
                                            liChecksToPerform1.setCandidateId(obj1.getString("CandidateID"));
                                            liChecksToPerform1.setRequestId(obj1.getString("RequestID"));
                                            if (licheckReq.getString("DatetoComplete").isEmpty() == false) {
                                                liChecksToPerform1.setDateToComplete(licheckReq.getString("DatetoComplete"));
                                            }
                                            ConventionalVendorliChecksToPerform saved = liCheckToPerformRepository.save(liChecksToPerform1);
                                            LicheckRequiredResponseDto licheckRequiredResponseDto = new LicheckRequiredResponseDto(saved.getId(), saved.getCheckCode(), saved.getCheckName(), saved.getCheckStatus().getCheckStatusCode(), saved.getCheckRemarks(), saved.getModeOfVerificationRequired(), saved.getModeOfVerificationPerformed(), saved.getCompletedDate());
                                            svcOutcome.setData(licheckRequiredResponseDto);
                                            log.info("added licheck with new upload as status");
                                        }
                                    });
                                }
                            }
                            log.info("acknoledged after all check get added normal case starts");
                            acknoledgeAfterSavedCandidate(obj1.getLong("RequestID"));
                            log.info("acknoledged after all check get added normal case  ends");
                        }
                    }
                } else {
                    log.info("no licheck from mintree");
                }
            } else {
                //for setting vendor data in licheck
                log.info("addUpdateLiCheckToPerformData() updating licheck with vendor id and soruce id starts");
                log.info("starts of the updation");
                if (liCheckToPerformRepository.existsById(licheckDto.getLicheckId())) {
                    LicheckHistory licheckHistory = new LicheckHistory();
                    log.info("inside if condidtion");
                    ConventionalVendorliChecksToPerform conventionalVendorliChecksToPerform = liCheckToPerformRepository.findById(licheckDto.getLicheckId()).get();
                    ConventionalVendorCandidatesSubmitted byRequestId = conventionalCandidatesSubmittedRepository.findByRequestId(conventionalVendorliChecksToPerform.getRequestId());
                    licheckHistory.setRequestType(byRequestId.getRequestType());
                    licheckHistory.setCandidateStatus(byRequestId.getStatus().getStatusCode());
                    log.info("the licheck id is  :" + licheckDto.getLicheckId());
                    VendorCheckStatusMaster byCheckStatusCode = vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(2l);
                    log.info("status data is  :" + 2l);
                    Source source = sourceRepository.findById(licheckDto.getSourceId()).get();
                    log.info("source data   :" + source);
                    conventionalVendorliChecksToPerform.setCheckStatus(byCheckStatusCode);
                    log.info("setted the status  :" + conventionalVendorliChecksToPerform.getCheckStatus());
                    conventionalVendorliChecksToPerform.setSource(source);
                    conventionalVendorliChecksToPerform.setSourceName(licheckDto.getSourceName());
                    conventionalVendorliChecksToPerform.setVendorName(licheckDto.getVendorName());
                    log.info("Before updating the licheck");
                    ConventionalVendorliChecksToPerform updatedLiChecksToPerform = liCheckToPerformRepository.save(conventionalVendorliChecksToPerform);
                    licheckHistory.setCandidateId(Long.valueOf(updatedLiChecksToPerform.getCandidateId()));
                    licheckHistory.setCheckName(updatedLiChecksToPerform.getCheckName());
                    licheckHistory.setCheckUniqueId(updatedLiChecksToPerform.getCheckUniqueId());
                    licheckHistory.setCreatedBy(updatedLiChecksToPerform.getCreatedBy().getUserName());
                    licheckHistory.setCreatedOn(new Date());
                    licheckHistory.setCheckStatus(updatedLiChecksToPerform.getCheckStatus().getCheckStatusCode());
                    licheckHistory.setRequestId(Long.valueOf(updatedLiChecksToPerform.getRequestId()));
                    LicheckHistory save = licheckHistoryRepository.save(licheckHistory);
                    log.info("licheck history INPROGRESS SAVED" + save.getId());
                    log.info("acknoledge  for onprogress starts");
                    bgvresponse = updateBgvCheckRowwiseonProgress(Long.valueOf(conventionalVendorliChecksToPerform.getRequestId()), conventionalVendorliChecksToPerform.getCheckUniqueId());
                    log.info("acknoledge  for onprogress ends");
                    log.info("addUpdateLiCheckToPerformData() updating licheck with vendor id and soruce id ends ");
                }

            }

            log.info("addUpdateLiCheckToPerformData() ends ");
            return svcOutcome;

        } catch (Exception e) {
            throw new Exception("unable to  save ");
        }

    }

    public ServiceOutcome<List<LicheckRequiredResponseDto>> findAllLiChecksRequired() throws Exception {
        ServiceOutcome<List<LicheckRequiredResponseDto>> serviceOutcome = new ServiceOutcome<List<LicheckRequiredResponseDto>>();
        try {
            User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);
            ArrayList<LicheckRequiredResponseDto> licheckRequiredResponseDtos = new ArrayList<>();

            List<ConventionalVendorliChecksToPerform> allLichecks = liCheckToPerformRepository.findAll();

            for (ConventionalVendorliChecksToPerform licheck : allLichecks) {
                LicheckRequiredResponseDto licheckRequiredResponseDto = new LicheckRequiredResponseDto();
                licheckRequiredResponseDto.setId(licheck.getId());
                licheckRequiredResponseDto.setCheckCode(licheck.getCheckCode());
                licheckRequiredResponseDto.setCandidateId(licheck.getCandidateId());
                licheckRequiredResponseDto.setCheckName(licheck.getCheckName());
                licheckRequiredResponseDto.setCheckRemarks(licheck.getCheckRemarks());
                licheckRequiredResponseDto.setCheckStatus(licheck.getCheckStatus().getCheckStatusCode());
                licheckRequiredResponseDto.setCompletedDateTime(licheck.getCompletedDate());
                licheckRequiredResponseDto.setCreatedBy(licheck.getCreatedBy().getUserName());
                licheckRequiredResponseDto.setCreatedOn(licheck.getCreatedOn());
                licheckRequiredResponseDto.setModeOfVerificationPerformed(licheck.getModeOfVerificationPerformed());
                licheckRequiredResponseDto.setModeOfVerificationRequired(licheck.getModeOfVerificationRequired());
                if (licheck.getVendorChecks() != null) {
                    licheckRequiredResponseDto.setDocumentName(licheck.getVendorChecks().getDocumentname());
                    licheckRequiredResponseDto.setVendorId(licheck.getVendorChecks().getVendorcheckId());
                }
                if (licheck.getRequestId().isEmpty() == false) {
                    licheckRequiredResponseDto.setRequestID(licheck.getRequestId());
                }
                if (licheck.getSource() != null) {
                    licheckRequiredResponseDto.setSourceId(licheck.getSource().getSourceId());
                }

                if (licheck.getSourceName() != null) {
                    licheckRequiredResponseDto.setSourceName(licheck.getSourceName());
                }
                if (licheck.getVendorName() != null) {
                    licheckRequiredResponseDto.setVendorName(licheck.getVendorName());
                }

                licheckRequiredResponseDtos.add(licheckRequiredResponseDto);
            }

            serviceOutcome.setData(licheckRequiredResponseDtos);
            serviceOutcome.setOutcome(true);

            return serviceOutcome;
        } catch (Exception e) {
            log.error(e.getMessage());
            serviceOutcome.setOutcome(false);
            serviceOutcome.setMessage(e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    //by request id
    public ServiceOutcome<List<LicheckRequiredResponseDto>> findAllLiChecksRequiredbyCandidateId(String requestId) throws Exception {
        ServiceOutcome<List<LicheckRequiredResponseDto>> serviceOutcome = new ServiceOutcome<List<LicheckRequiredResponseDto>>();
        ArrayList<LicheckRequiredResponseDto> licheckRequiredResponseDtos = null;
        try {
            User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);

            licheckRequiredResponseDtos = new ArrayList<>();
            List<ConventionalVendorliChecksToPerform> allLichecks = liCheckToPerformRepository.findByRequestId(requestId);
            for (ConventionalVendorliChecksToPerform licheck : allLichecks) {
                LicheckRequiredResponseDto licheckRequiredResponseDto = new LicheckRequiredResponseDto();
                licheckRequiredResponseDto.setId(licheck.getId());
                licheckRequiredResponseDto.setCheckCode(licheck.getCheckCode());
                licheckRequiredResponseDto.setCandidateId(licheck.getCandidateId());
                licheckRequiredResponseDto.setCheckName(licheck.getCheckName());
                licheckRequiredResponseDto.setCheckRemarks(licheck.getCheckRemarks());
                licheckRequiredResponseDto.setCheckStatus(licheck.getCheckStatus().getCheckStatusCode());
                licheckRequiredResponseDto.setCompletedDateTime(licheck.getCompletedDate());
                licheckRequiredResponseDto.setCreatedBy(licheck.getCreatedBy().getUserName());
                licheckRequiredResponseDto.setCreatedOn(licheck.getCreatedOn());
                licheckRequiredResponseDto.setCheckUniqueId(licheck.getCheckUniqueId());
                ModeOfVerificationStatusMaster modeOfVerificationStatusMasterPerformed = modeOfVerificationStatusMasterRepository.findById(Long.valueOf(licheck.getModeOfVerificationPerformed())).get();
                licheckRequiredResponseDto.setModeOfVerificationPerformed(modeOfVerificationStatusMasterPerformed.getModeOfVerification());
                ModeOfVerificationStatusMaster modeOfVerificationStatusMasterRequired = modeOfVerificationStatusMasterRepository.findById(Long.valueOf(licheck.getModeOfVerificationRequired())).get();
                licheckRequiredResponseDto.setModeOfVerificationRequired(modeOfVerificationStatusMasterRequired.getModeOfVerification());
                if (licheck.getVendorChecks() != null) {
                    licheckRequiredResponseDto.setDocumentName(licheck.getVendorChecks().getDocumentname());
                    licheckRequiredResponseDto.setVendorId(licheck.getVendorChecks().getVendorcheckId());
                }
                if (licheck.getRequestId().isEmpty() == false) {
                    licheckRequiredResponseDto.setRequestID(licheck.getRequestId());
                    Candidate byConventionalRequestId = candidateRepository.findByConventionalRequestId(Long.valueOf(licheck.getRequestId()));
                    if (byConventionalRequestId != null) {
                        licheckRequiredResponseDto.setCandidateBasicId(String.valueOf(byConventionalRequestId.getCandidateId()));
                    }
                }
                if (licheck.getSource() != null) {
                    licheckRequiredResponseDto.setSourceId(licheck.getSource().getSourceId());
                }

                if (licheck.getSourceName() != null) {
                    licheckRequiredResponseDto.setSourceName(licheck.getSourceName());
                }
                if (licheck.getVendorName() != null) {
                    licheckRequiredResponseDto.setVendorName(licheck.getVendorName());
                }

                if (licheck.getDateToComplete() != null) {
                    licheckRequiredResponseDto.setFastTrackDateTime(String.valueOf(licheck.getDateToComplete()));
                }
                if (licheck.getStopCheck() != null) {
                    licheckRequiredResponseDto.setStopCheckStatus(licheck.getStopCheck());
                }
                ServiceOutcome<CandidateuploadS3Documents> allfilesUploadedurls = findAllfilesUploadedurls(licheck.getRequestId(), licheck.getCheckName());
                if (allfilesUploadedurls.getData() != null) {
                    licheckRequiredResponseDto.setCandidateuploadS3Documents(allfilesUploadedurls.getData());
                    licheckRequiredResponseDto.setDocumentName(licheckRequiredResponseDto.getCandidateuploadS3Documents().getDocumentName());
                }
                if (licheck.getDisabled() != null) {
                    licheckRequiredResponseDto.setDisableStatus(licheck.getDisabled());
                }
                licheckRequiredResponseDtos.add(licheckRequiredResponseDto);
            }

            serviceOutcome.setData(licheckRequiredResponseDtos);
            serviceOutcome.setOutcome(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            serviceOutcome.setData(licheckRequiredResponseDtos);
        }
        return serviceOutcome;
    }

    public ServiceOutcome<List<LicheckRequiredResponseDto>> findAllStopLiChecksRequiredbyCandidateId(String requestId) throws Exception {
        ServiceOutcome<List<LicheckRequiredResponseDto>> serviceOutcome = new ServiceOutcome<List<LicheckRequiredResponseDto>>();
        ArrayList<LicheckRequiredResponseDto> licheckRequiredResponseDtos = null;
        try {
            User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);
            licheckRequiredResponseDtos = new ArrayList<>();
            List<ConventionalVendorliChecksToPerform> allLichecks = liCheckToPerformRepository.findByRequestId(requestId);
            allLichecks = allLichecks.stream().filter(p -> p.getStopCheck() != null).collect(Collectors.toList());
            for (ConventionalVendorliChecksToPerform licheck : allLichecks) {
                LicheckRequiredResponseDto licheckRequiredResponseDto = new LicheckRequiredResponseDto();
                licheckRequiredResponseDto.setId(licheck.getId());
                licheckRequiredResponseDto.setCheckCode(licheck.getCheckCode());
                licheckRequiredResponseDto.setCandidateId(licheck.getCandidateId());
                licheckRequiredResponseDto.setCheckName(licheck.getCheckName());
                licheckRequiredResponseDto.setCheckRemarks(licheck.getCheckRemarks());
                licheckRequiredResponseDto.setCheckStatus(licheck.getCheckStatus().getCheckStatusCode());
                licheckRequiredResponseDto.setCompletedDateTime(licheck.getCompletedDate());
                if (licheck.getCreatedBy() != null) {
                    licheckRequiredResponseDto.setCreatedBy(licheck.getCreatedBy().getUserName());
                }
                licheckRequiredResponseDto.setCreatedOn(licheck.getCreatedOn());
                licheckRequiredResponseDto.setCheckUniqueId(licheck.getCheckUniqueId());
                ModeOfVerificationStatusMaster modeOfVerificationStatusMasterPerformed = modeOfVerificationStatusMasterRepository.findById(Long.valueOf(licheck.getModeOfVerificationPerformed())).get();
                licheckRequiredResponseDto.setModeOfVerificationPerformed(modeOfVerificationStatusMasterPerformed.getModeOfVerification());
                ModeOfVerificationStatusMaster modeOfVerificationStatusMasterRequired = modeOfVerificationStatusMasterRepository.findById(Long.valueOf(licheck.getModeOfVerificationRequired())).get();
                licheckRequiredResponseDto.setModeOfVerificationRequired(modeOfVerificationStatusMasterRequired.getModeOfVerification());
                if (licheck.getVendorChecks() != null) {
                    licheckRequiredResponseDto.setDocumentName(licheck.getVendorChecks().getDocumentname());
                    licheckRequiredResponseDto.setVendorId(licheck.getVendorChecks().getVendorcheckId());
                }
                if (licheck.getRequestId().isEmpty() == false) {
                    licheckRequiredResponseDto.setRequestID(licheck.getRequestId());
                    Candidate byConventionalRequestId = candidateRepository.findByConventionalRequestId(Long.valueOf(licheck.getRequestId()));
                    if (byConventionalRequestId != null) {
                        licheckRequiredResponseDto.setCandidateBasicId(String.valueOf(byConventionalRequestId.getCandidateId()));
                    }
                }
                if (licheck.getSource() != null) {
                    licheckRequiredResponseDto.setSourceId(licheck.getSource().getSourceId());
                }

                if (licheck.getSourceName() != null) {
                    licheckRequiredResponseDto.setSourceName(licheck.getSourceName());
                }
                if (licheck.getVendorName() != null) {
                    licheckRequiredResponseDto.setVendorName(licheck.getVendorName());
                }

                if (licheck.getDateToComplete() != null) {
                    licheckRequiredResponseDto.setFastTrackDateTime(String.valueOf(licheck.getDateToComplete()));
                }
                if (licheck.getStopCheck() != null) {
                    licheckRequiredResponseDto.setStopCheckStatus(licheck.getStopCheck());
                }
                if (licheck.getDisabled() != null) {
                    licheckRequiredResponseDto.setDisableStatus(licheck.getDisabled());
                }

                ServiceOutcome<CandidateuploadS3Documents> allfilesUploadedurls = findAllfilesUploadedurls(licheck.getRequestId(), licheck.getCheckName());
                if (allfilesUploadedurls.getData() != null) {
                    licheckRequiredResponseDto.setCandidateuploadS3Documents(allfilesUploadedurls.getData());
                    licheckRequiredResponseDto.setDocumentName(licheckRequiredResponseDto.getCandidateuploadS3Documents().getDocumentName());
                }
                licheckRequiredResponseDtos.add(licheckRequiredResponseDto);
            }

            serviceOutcome.setData(licheckRequiredResponseDtos);
            serviceOutcome.setOutcome(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            serviceOutcome.setData(licheckRequiredResponseDtos);
        }
        return serviceOutcome;
    }

    @Autowired
    UserService userService;


    @Transactional
    public ServiceOutcome<List<LicheckRequiredResponseDto>> findAllNewUploadLiChecksRequiredbyCandidateId(String requestId) throws Exception {
        ServiceOutcome<List<LicheckRequiredResponseDto>> serviceOutcome = new ServiceOutcome<List<LicheckRequiredResponseDto>>();
        ArrayList<LicheckRequiredResponseDto> licheckRequiredResponseDtos = null;
        try {
            User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);

            licheckRequiredResponseDtos = new ArrayList<>();
            List<ConventionalVendorliChecksToPerform> allLichecks = liCheckToPerformRepository.findByRequestId(requestId);
            List<ConventionalVendorliChecksToPerform> collect = allLichecks.stream().filter(licheck -> licheck.getCheckStatus().getVendorCheckStatusMasterId() == 7l).collect(Collectors.toList());
            List<Source> all = sourceRepository.findAll();
            List<Long> excludedSourceIds = Arrays.asList(1l, 2l, 3l, 4l, 5l, 6l, 9l, 10l);
            all.removeIf(source -> excludedSourceIds.contains(source.getSourceId()));
            for (ConventionalVendorliChecksToPerform licheck : collect) {
                LicheckRequiredResponseDto licheckRequiredResponseDto = new LicheckRequiredResponseDto();
                licheckRequiredResponseDto.setId(licheck.getId());
                licheckRequiredResponseDto.setCheckCode(licheck.getCheckCode());
                licheckRequiredResponseDto.setCandidateId(licheck.getCandidateId());
                licheckRequiredResponseDto.setCheckName(licheck.getCheckName());
                licheckRequiredResponseDto.setCheckRemarks(licheck.getCheckRemarks());
                licheckRequiredResponseDto.setCompletedDateTime(licheck.getCompletedDate());
                if (licheck.getCreatedBy() != null) {
                    licheckRequiredResponseDto.setCreatedBy(licheck.getCreatedBy().getUserName());
                }
                licheckRequiredResponseDto.setCreatedOn(licheck.getCreatedOn());
                licheckRequiredResponseDto.setCheckUniqueId(licheck.getCheckUniqueId());
                ModeOfVerificationStatusMaster modeOfVerificationStatusMasterPerformed = modeOfVerificationStatusMasterRepository.findById(Long.valueOf(licheck.getModeOfVerificationPerformed())).get();
                licheckRequiredResponseDto.setModeOfVerificationPerformed(modeOfVerificationStatusMasterPerformed.getModeOfVerification());
                ModeOfVerificationStatusMaster modeOfVerificationStatusMasterRequired = modeOfVerificationStatusMasterRepository.findById(Long.valueOf(licheck.getModeOfVerificationRequired())).get();
                licheckRequiredResponseDto.setModeOfVerificationRequired(modeOfVerificationStatusMasterRequired.getModeOfVerification());
                if (licheck.getRequestId().isEmpty() == false) {
                    licheckRequiredResponseDto.setRequestID(licheck.getRequestId());
                    Candidate byConventionalRequestId = candidateRepository.findByConventionalRequestId(Long.valueOf(licheck.getRequestId()));
                    if (byConventionalRequestId != null) {
                        licheckRequiredResponseDto.setCandidateBasicId(String.valueOf(byConventionalRequestId.getCandidateId()));
                    }
                }
                Source desiredSource = all.stream().filter(source -> licheckRequiredResponseDto.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains(source.getSourceName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase())).findFirst().orElse(null);
                if (desiredSource != null) {
                    licheckRequiredResponseDto.setSourceId(desiredSource.getSourceId());
                }
                if (desiredSource != null) {
                    licheckRequiredResponseDto.setSourceName(desiredSource.getSourceName().trim());
                }
                ServiceOutcome<CandidateuploadS3Documents> allfilesUploadedurls = findAllfilesUploadedurls(licheck.getRequestId(), licheck.getCheckName());
                if (allfilesUploadedurls.getData() != null) {
                    licheckRequiredResponseDto.setCandidateuploadS3Documents(allfilesUploadedurls.getData());
                    licheckRequiredResponseDto.setDocumentName(licheckRequiredResponseDto.getCandidateuploadS3Documents().getDocumentName());
                }
                if (licheck.getDisabled() != null) {
                    licheckRequiredResponseDto.setDisableStatus(licheck.getDisabled());
                }
                if (licheck.getDateToComplete() != null) {
                    licheckRequiredResponseDto.setFastTrackDateTime(String.valueOf(licheck.getDateToComplete()));
                }

                if (licheck.getStopCheck() != null) {
                    licheckRequiredResponseDto.setStopCheckStatus(licheck.getStopCheck());
                }
                if (licheck.getVendorChecks() == null) {
                    VendorInitiatDto vendorInitiatDto = new VendorInitiatDto();
                    vendorInitiatDto.setCandidateId(Long.valueOf(licheck.getRequestId()));
                    vendorInitiatDto.setDocumentname(licheckRequiredResponseDto.getDocumentName());
                    if (licheckRequiredResponseDto.getCandidateuploadS3Documents() != null) {
                        vendorInitiatDto.setDocumentUrl(licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    }
                    vendorInitiatDto.setLicheckId(String.valueOf(licheck.getId()));
                    vendorInitiatDto.setSourceId(licheckRequiredResponseDto.getSourceId());
                    vendorInitiatDto.setSourceName(licheckRequiredResponseDto.getSourceName());
                    if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("Passport".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName() + vendor.getUserLastName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("Cibil".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());

                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("EMPLOYMENT".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());

                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("ofac".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());

                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("Criminal".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("Globaldatabase".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("Address".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("Education".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("Reference".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("Drug".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("Employment-Physicalvisit".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("PAN".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("Aadhar".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    } else if (licheck.getCheckName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase().contains("DrivingLicence".replaceAll("[^a-zA-Z0-9]", "").toLowerCase())) {
                        User vendor = userRepository.findById(72l).get();
                        vendorInitiatDto.setVendorId(vendor.getUserId());
                        vendorInitiatDto.setVendorName(vendor.getUserFirstName());
                        String vendorUploadData = new ObjectMapper().writeValueAsString(vendorInitiatDto);
                        userService.saveInitiateVendorChecks(vendorUploadData, null, (licheckRequiredResponseDto.getCandidateuploadS3Documents() == null) ? null : licheckRequiredResponseDto.getCandidateuploadS3Documents().getPathkey());
                    }
                }
                if (licheck.getVendorName() != null) {
                    licheckRequiredResponseDto.setVendorName(licheck.getVendorName());
                }
                licheckRequiredResponseDto.setCheckStatus(licheck.getCheckStatus().getCheckStatusCode());

                if (licheck.getCheckStatus().getVendorCheckStatusMasterId() == 7l) {
                    licheckRequiredResponseDtos.add(licheckRequiredResponseDto);
                }
            }
            serviceOutcome.setData(licheckRequiredResponseDtos);
            serviceOutcome.setOutcome(true);
        } catch (Exception e) {
            log.error(e.getMessage());
            serviceOutcome.setData(licheckRequiredResponseDtos);
        }
        return serviceOutcome;
    }

//    public ServiceOutcome<List<LicheckRequiredResponseDto>> findAllLiChecksRequiredbyCandidateId(String candidateId) throws Exception {
//        try {
//
//            User user = SecurityHelper.getCurrentUser();
//            ServiceOutcome<List<LicheckRequiredResponseDto>> serviceOutcome = new ServiceOutcome<List<LicheckRequiredResponseDto>>();
//            List<LicheckRequiredResponseDto> allLiCheckResponses = liCheckToPerformRepository.findAllLiCheckResponseByCandidateId(candidateId);
//
//            if (allLiCheckResponses.isEmpty()) {
//                ArrayList<LicheckRequiredResponseDto> emptyLicheckResponse = new ArrayList<>();
//
//                serviceOutcome.setData(emptyLicheckResponse);
//            }
//            allLiCheckResponses.forEach(res -> {
//                if (res.getVendorId() != null) {
//                    Long vendorId = res.getVendorId();
//                    VendorChecks vendorChecks = vendorChecksRepository.findById(vendorId).get();
//                    res.setDoucmentName(vendorChecks.getDocumentname());
//                }
//                res.setCreatedBy(user.getUserName());
//                ConventionalVendorliChecksToPerform conventionalVendorliChecksToPerform = liCheckToPerformRepository.findById(res.getCheckCode()).get();
//                res.setVendorName(conventionalVendorliChecksToPerform.getVendorName());
//                res.setSourceName(conventionalVendorliChecksToPerform.getSourceName());
//
////                Candidate byConventionalCandidateId = candidateRepository.findByConventionalCandidateId(Long.parseLong(res.getCandidateId()));
////                res.setCandidateId(byConventionalCandidateId.getCandidateId().toString());
//
//            });
////
//            serviceOutcome.setData(allLiCheckResponses);
//            return serviceOutcome;
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//    }

    public ServiceOutcome<List<LicheckRequiredResponseDto>> findAllLiChecksRequiredbyCheckStatus(String checkStatus) throws Exception {
        try {

            User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);

            ServiceOutcome<List<LicheckRequiredResponseDto>> serviceOutcome = new ServiceOutcome<List<LicheckRequiredResponseDto>>();
            List<LicheckRequiredResponseDto> allLiCheckResponses = new ArrayList<>();

            if (checkStatus.equalsIgnoreCase("NEWUPLOAD")) {
                allLiCheckResponses = liCheckToPerformRepository.findAllLiCheckResponseByCheckStatus(7l);
            }
            if (checkStatus.equalsIgnoreCase("QCPENDING")) {
                allLiCheckResponses = liCheckToPerformRepository.findAllLiCheckResponseByCheckStatus(8l);
            }
            if (checkStatus.equalsIgnoreCase("CLEAR")) {
                allLiCheckResponses = liCheckToPerformRepository.findAllLiCheckResponseByCheckStatus(1l);
            }
            if (checkStatus.equalsIgnoreCase("INPROGRESS")) {
                allLiCheckResponses = liCheckToPerformRepository.findAllLiCheckResponseByCheckStatus(2l);
            }
            if (checkStatus.equalsIgnoreCase("INSUFFICIENCY")) {
                allLiCheckResponses = liCheckToPerformRepository.findAllLiCheckResponseByCheckStatus(3l);
            }
            if (checkStatus.equalsIgnoreCase("MAJORDISCREPANCY")) {
                allLiCheckResponses = liCheckToPerformRepository.findAllLiCheckResponseByCheckStatus(4l);
            }
            if (checkStatus.equalsIgnoreCase("MINORDISCREPANCY")) {
                allLiCheckResponses = liCheckToPerformRepository.findAllLiCheckResponseByCheckStatus(5l);
            }
            if (checkStatus.equalsIgnoreCase("UNABLETOVERIFY")) {
                allLiCheckResponses = liCheckToPerformRepository.findAllLiCheckResponseByCheckStatus(6l);
            }


            allLiCheckResponses.forEach(res -> {

                res.setCreatedBy(user.getUserName());
                ConventionalVendorliChecksToPerform conventionalVendorliChecksToPerform = liCheckToPerformRepository.findById(res.getCheckCode()).get();
                res.setVendorName(conventionalVendorliChecksToPerform.getVendorName());
                res.setSourceName(conventionalVendorliChecksToPerform.getSourceName());
                if (conventionalVendorliChecksToPerform.getRequestId().isEmpty() == false) {
                    res.setRequestID(conventionalVendorliChecksToPerform.getRequestId());
                }

                if (res.getVendorId() != null) {
                    VendorChecks vendorChecks = vendorChecksRepository.findById(res.getVendorId()).get();
                    res.setDocumentName(vendorChecks.getDocumentname());
                }
            });
            if (allLiCheckResponses.isEmpty()) {
                serviceOutcome.setData(new ArrayList<>());
            }
            serviceOutcome.setData(allLiCheckResponses);
            return serviceOutcome;
        } catch (Exception e) {
            log.error(e.getMessage());

            throw new Exception(e.getMessage());
        }
    }

    @Autowired
    ConventionalCandidateDrugInfoRepository conventionalCandidateDrugInfoRepository;

    @Transactional
    public String acknoledgeAfterSavedCandidate(Long requestId) {
        try {
            log.info("acknoledgeAfterSavedCandidate() starts");
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", environmentVal.getMtGrantType());
            map.add("username", environmentVal.getMtUsername());
            map.add("password", environmentVal.getMtPassword());

            HttpHeaders tokenHeader = new HttpHeaders();
            tokenHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            ResponseEntity<String> responseEntity = null;
            HttpEntity<MultiValueMap<String, String>> requestBodyFormUrlEncoded = new HttpEntity<>(map, tokenHeader);
            responseEntity = restTemplate.postForEntity(environmentVal.getConventionalVendorToken(), requestBodyFormUrlEncoded, String.class);
            JSONObject tokenObject = new JSONObject(responseEntity.getBody());
            String access_token = tokenObject.getString("access_token");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + access_token);
            headers.set("Content-Type", "application/json");
            headers.setContentType(MediaType.APPLICATION_JSON);
            ConventionalVendorCandidatesSubmitted conventionalCandidate = conventionalCandidatesSubmittedRepository.findByRequestId(String.valueOf(requestId));
            AcknoledgementDto acknoledgementDto = new AcknoledgementDto();
            acknoledgementDto.setCandidateID(String.valueOf(conventionalCandidate.getCandidateId()));
            acknoledgementDto.setPSNO(conventionalCandidate.getPsNo());
            acknoledgementDto.setRequestID(conventionalCandidate.getRequestId());
            acknoledgementDto.setVENDORID(conventionalCandidate.getVendorId());
            acknoledgementDto.setVendorReferenceID(String.valueOf(conventionalCandidate.getApplicantId()));
            ArrayList<AcknoledgementDto> acknoledgementDtos = new ArrayList<>();
            acknoledgementDtos.add(acknoledgementDto);
            HttpEntity<List<AcknoledgementDto>> acknoledgementDtoHttpEntity = new HttpEntity<>(acknoledgementDtos, headers);
            ResponseEntity<String> acknoledgementData = restTemplate.exchange(environmentVal.getConventionalUpdateBGVRequestAcknowledgement(), HttpMethod.POST, acknoledgementDtoHttpEntity, String.class);
            log.info("candidate Added  response Ackonledgement" + acknoledgementData);
            log.info("acknoledgeAfterSavedCandidate() ends");
        } catch (Exception e) {
            log.info("acknoledgeAfterSavedCandidate() exception" + e.getMessage());
        }
        return "Acknoledged";
    }

    @Autowired
    EmailSentTask emailSentTask;

    @Override
    @Transactional
    public ServiceOutcome<SubmittedCandidates> saveConventionalVendorSubmittedCandidates(String VendorID, boolean schedularConfdition) throws Exception {
        ServiceOutcome<SubmittedCandidates> serviceOutcome = new ServiceOutcome<>();
        List<ConventionalVendorCandidatesSubmitted> emailSendCandidateList = new ArrayList<>();
        try {
            log.info("saveConventionalVendorSubmittedCandidates() starts");
            ServiceOutcome<LicheckRequiredResponseDto> svcOutcome = new ServiceOutcome<LicheckRequiredResponseDto>();
            User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);
            //To generate token first
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", environmentVal.getMtGrantType());
            map.add("username", environmentVal.getMtUsername());
            map.add("password", environmentVal.getMtPassword());

            HttpHeaders tokenHeader = new HttpHeaders();
            tokenHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            ResponseEntity<String> responseEntity = null;
            HttpEntity<MultiValueMap<String, String>> requestBodyFormUrlEncoded = new HttpEntity<>(map, tokenHeader);
            responseEntity = restTemplate.postForEntity(environmentVal.getConventionalVendorToken(), requestBodyFormUrlEncoded, String.class);
            JSONObject tokenObject = new JSONObject(responseEntity.getBody());
            String access_token = tokenObject.getString("access_token");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + access_token);
            headers.set("Content-Type", "application/json");
            HttpEntity<String> vendorIdHttp = new HttpEntity<>(VendorID, headers);
            ResponseEntity<String> candidateResponse = restTemplate.exchange(environmentVal.getConventionalVendorFetchVendorRequestDetails(), HttpMethod.POST, vendorIdHttp, String.class);
            String message = candidateResponse.getBody();
            JSONArray obj1 = new JSONArray(message);
            List<JSONArray> list = Arrays.asList(obj1);
            List<JSONObject> collect = IntStream.range(0, obj1.length()).mapToObj(index -> ((JSONObject) obj1.get(index))).collect(Collectors.toList());
            List<ConventionalVendorCandidatesSubmitted> all = conventionalCandidatesSubmittedRepository.findAll();
            for (JSONObject candidate : collect) {
                Long candidateId = candidate.getLong("CandidateID");
                Long requestID = candidate.getLong("RequestID");
                Boolean candidateExists = conventionalCandidatesSubmittedRepository.existsByRequestId(String.valueOf(requestID));
                if (candidate.getString("RequestType").equalsIgnoreCase("InsufficiencyClearance") == true) {
                    ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(String.valueOf(requestID));
                    if (conventionalVendorCandidatesSubmitted.getRequestType().equalsIgnoreCase("InsufficiencyClearance") == false) {
                        conventionalVendorCandidatesSubmitted.setOldRequestType(conventionalVendorCandidatesSubmitted.getRequestType());
                        conventionalVendorCandidatesSubmitted.setRequestType("InsufficiencyClearance");
                        log.info("saving the oldrequest type");
                        ConventionalVendorCandidatesSubmitted savedSubmittedCandidates = conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                    }
                }
                if (candidateExists == false) {
                    ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = new ConventionalVendorCandidatesSubmitted();
                    conventionalVendorCandidatesSubmitted.setCandidateId(candidate.getLong("CandidateID"));
                    conventionalVendorCandidatesSubmitted.setVendorId(candidate.getString("VendorID"));
                    conventionalVendorCandidatesSubmitted.setName(candidate.getString("Name"));
                    conventionalVendorCandidatesSubmitted.setPsNo(candidate.getString("PSNO"));
                    conventionalVendorCandidatesSubmitted.setRequestId(candidate.getString("RequestID"));
                    conventionalVendorCandidatesSubmitted.setRequestType(candidate.getString("RequestType"));
                    StatusMaster newupload = statusMasterRepository.findByStatusCode("NEWUPLOAD");
                    conventionalVendorCandidatesSubmitted.setStatus(newupload);
                    conventionalVendorCandidatesSubmitted.setCreatedBy(user);
                    conventionalVendorCandidatesSubmitted.setCreatedOn(new Date());
                    Random rnd = new Random();
                    int n = 100000 + rnd.nextInt(900000);
                    conventionalVendorCandidatesSubmitted.setApplicantId(n);
                    ConventionalVendorCandidatesSubmitted savedSubmittedCandidates = conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                    emailSendCandidateList.add(savedSubmittedCandidates);
                    log.info("saveConventionalVendorSubmittedCandidates() saved with candidateId" + savedSubmittedCandidates.getCandidateId());
                }
                if (candidate.getString("RequestType").equalsIgnoreCase("STOPBGV") == true) {
                    ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(String.valueOf(requestID));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date currentDate = new Date();
                    String formattedDate = dateFormat.format(currentDate);
                    conventionalVendorCandidatesSubmitted.setStopCheckRecivedDate(formattedDate);
                    log.info("saving the stop bgv" + conventionalVendorCandidatesSubmitted.getRequestId());
                    ConventionalVendorCandidatesSubmitted savedSubmittedCandidates = conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                    FetchVendorConventionalCandidateDto fetchVendorConventionalCandidateDto = new FetchVendorConventionalCandidateDto(conventionalVendorCandidatesSubmitted.getRequestId(), String.valueOf(conventionalVendorCandidatesSubmitted.getCandidateId()), conventionalVendorCandidatesSubmitted.getPsNo(), conventionalVendorCandidatesSubmitted.getVendorId(), conventionalVendorCandidatesSubmitted.getRequestType());
                    HttpEntity<FetchVendorConventionalCandidateDto> liCheckDtoHttpEntity = new HttpEntity<>(fetchVendorConventionalCandidateDto, headers);
                    ResponseEntity<String> icheckRepsonse = restTemplate.exchange(environmentVal.getConventionalVendorFetchVendorChecks(), HttpMethod.POST, liCheckDtoHttpEntity, String.class);
                    String responseMessage = icheckRepsonse.getBody();
                    JSONObject checkresponse = new JSONObject(responseMessage);
                    if (checkresponse.isNull("liStopChecks") == false) {
                        JSONObject liStopChecks = checkresponse.getJSONObject("liStopChecks");
                        if (liStopChecks.isNull("liChecksToStop") == false) {
                            JSONArray liChecksToStop = liStopChecks.getJSONArray("liChecksToStop");
                            List<JSONObject> stopCollect = IntStream.range(0, liChecksToStop.length()).mapToObj(index -> ((JSONObject) liChecksToStop.get(index))).collect(Collectors.toList());
                            for (JSONObject licheckReq : stopCollect) {
                                Long checkUniqueId = licheckReq.getLong("Check_Unique_ID");

                                ConventionalVendorliChecksToPerform byCheckUniqueId = liCheckToPerformRepository.findByCheckUniqueId(checkUniqueId);
                                if (byCheckUniqueId != null) {
                                    if (byCheckUniqueId.getCheckStatus().getVendorCheckStatusMasterId() != 1l) {
                                        byCheckUniqueId.setStopCheck("TRUE");
                                        ConventionalVendorliChecksToPerform save = liCheckToPerformRepository.save(byCheckUniqueId);
                                        log.info("stopped check by check unique id" + save.getCheckUniqueId());
                                    }
                                }
                                acknoledgeAfterSavedCandidate(checkresponse.getLong("RequestID"));
                                log.info("acknoledged after all check get stopped for Stopped checks");
                            }
                        }
                    }
                }
//                emailSentTask.sendEmail(String.valueOf(conventionalVendorCandidatesSubmitted.getCandidateId()),conventionalVendorCandidatesSubmitted.getName(),"nandakishore.p@digiverifier.com","nandakishore.p@digiverifier.com");

//                emailSentTask.sendEmail(String.valueOf("2675966"), "nanda Kishore", "nandakishore.p@digiverifier.com", "nandakishore.p@digiverifier.com");

                log.info("saveConventionalVendorSubmittedCandidates() ends");
                if (schedularConfdition == true) {
                    log.info("scheduler for request id    :" + requestID + "   :   starts");
                    findConventionalCandidateByCandidateId(requestID);
                    log.info("scheduler for request id    :" + requestID + "   :   ends");
                    log.info("scheduler for vendorinitiation    :" + requestID + "   :   starts");
                    findAllNewUploadLiChecksRequiredbyCandidateId(String.valueOf(requestID));
                    log.info("scheduler for vendorinitiation    :" + requestID + "   :   ends");

                }

            }
            if (emailSendCandidateList.isEmpty() == false) {
                emailSentTask.sendEmailOnSaveCandidates(emailSendCandidateList, "kavitha@digiverifier.com", "nandakishore.p@digiverifier.com;giridharan.bk@digiverifier.com");
            }
        } catch (Exception e) {
            serviceOutcome.setMessage(e.getMessage());
            serviceOutcome.setOutcome(false);
            serviceOutcome.setStatus(e.getMessage());
            log.error("exception occured in saveConventionalVendorSubmittedCandidates()" + e.getMessage());
        }
        return serviceOutcome;

    }

    public ServiceOutcome<SubmittedCandidates> triggerCandidateDataAndCheckData(String VendorID, String triggerRequestId) throws Exception {
        ServiceOutcome<SubmittedCandidates> serviceOutcome = new ServiceOutcome<>();
        try {
            log.info("saveConventionalVendorSubmittedCandidates() starts");
            ServiceOutcome<LicheckRequiredResponseDto> svcOutcome = new ServiceOutcome<LicheckRequiredResponseDto>();
            User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);
            //To generate token first
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", environmentVal.getMtGrantType());
            map.add("username", environmentVal.getMtUsername());
            map.add("password", environmentVal.getMtPassword());
            HttpHeaders tokenHeader = new HttpHeaders();
            tokenHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            ResponseEntity<String> responseEntity = null;
            HttpEntity<MultiValueMap<String, String>> requestBodyFormUrlEncoded = new HttpEntity<>(map, tokenHeader);
            responseEntity = restTemplate.postForEntity(environmentVal.getConventionalVendorToken(), requestBodyFormUrlEncoded, String.class);
            JSONObject tokenObject = new JSONObject(responseEntity.getBody());
            String access_token = tokenObject.getString("access_token");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + access_token);
            headers.set("Content-Type", "application/json");
            HttpEntity<String> vendorIdHttp = new HttpEntity<>(VendorID, headers);
            ResponseEntity<String> candidateResponse = restTemplate.exchange(environmentVal.getConventionalVendorFetchVendorRequestDetails(), HttpMethod.POST, vendorIdHttp, String.class);
            String message = candidateResponse.getBody();
            JSONArray obj1 = new JSONArray(message);
            List<JSONArray> list = Arrays.asList(obj1);
            List<JSONObject> collect = IntStream.range(0, obj1.length()).mapToObj(index -> ((JSONObject) obj1.get(index))).collect(Collectors.toList());
            List<ConventionalVendorCandidatesSubmitted> all = conventionalCandidatesSubmittedRepository.findAll();
            if (collect.isEmpty() == true) {
                serviceOutcome.setOutcome(false);
                serviceOutcome.setMessage(null);
                serviceOutcome.setData(new SubmittedCandidates());
            }
            for (JSONObject candidate : collect) {
                Long candidateId = candidate.getLong("CandidateID");
                Long requestID = candidate.getLong("RequestID");
                long triggre = Long.parseLong(triggerRequestId);
                if (triggre == requestID) {
                    Boolean candidateExists = conventionalCandidatesSubmittedRepository.existsByRequestId(String.valueOf(requestID));
                    if (candidate.getString("RequestType").equalsIgnoreCase("InsufficiencyClearance") == true) {
                        ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(String.valueOf(requestID));
                        if (conventionalVendorCandidatesSubmitted.getRequestType().equalsIgnoreCase("InsufficiencyClearance") == false) {
                            conventionalVendorCandidatesSubmitted.setOldRequestType(conventionalVendorCandidatesSubmitted.getRequestType());
                            conventionalVendorCandidatesSubmitted.setRequestType("InsufficiencyClearance");
                            log.info("saving the oldrequest type");
                            ConventionalVendorCandidatesSubmitted savedSubmittedCandidates = conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                        }
                    }
                    log.info("scheduler for request id    :" + requestID + "   :   starts");
                    findConventionalCandidateByCandidateId(requestID);
                    log.info("scheduler for request id    :" + requestID + "   :   ends");
                    serviceOutcome.setOutcome(true);
                    serviceOutcome.setMessage("Insufficiency Resumittted, No need Of Remarks");

                } else {
                    serviceOutcome.setOutcome(false);
                    serviceOutcome.setMessage(null);
                    serviceOutcome.setData(new SubmittedCandidates());
                }
            }

        } catch (Exception e) {
            serviceOutcome.setMessage(e.getMessage());
//            serviceOutcome.setOutcome(false);
            serviceOutcome.setStatus(e.getMessage());
            log.error("exception occured in saveConventionalVendorSubmittedCandidates()" + e.getMessage());
        }
        return serviceOutcome;

    }

    public String addConvetionalCandidateData(String requestID) {
        try {
            log.info("addConvetionalCandidateData() starts");
            ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(String.valueOf(requestID));
            FetchVendorConventionalCandidateDto fetchVendorConventionalCandidateDto = new FetchVendorConventionalCandidateDto();
            fetchVendorConventionalCandidateDto.setCandidateID(String.valueOf(conventionalVendorCandidatesSubmitted.getCandidateId()));
            fetchVendorConventionalCandidateDto.setRequestId(conventionalVendorCandidatesSubmitted.getRequestId());
            fetchVendorConventionalCandidateDto.setPsno(conventionalVendorCandidatesSubmitted.getPsNo());
            fetchVendorConventionalCandidateDto.setVendorId(conventionalVendorCandidatesSubmitted.getVendorId());
            fetchVendorConventionalCandidateDto.setRequestType(conventionalVendorCandidatesSubmitted.getRequestType());
            Boolean conventionalDocexists = conventionalCandidateDocumentInfoRepository.existsByRequestID(String.valueOf(requestID));

//            if (conventionalVendorCandidatesSubmitted.getRequestType().equalsIgnoreCase("InsufficiencyClearance")) {
//                try {
//                    log.info("docuemtns saving for insufficiency");
//                    saveConventionalCandidateDocumentInfo(fetchVendorConventionalCandidateDto);
//                    log.info("docuements saved for insufficiency");
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            if (conventionalDocexists == false) {
//                try {
//                    log.info("candidate doc no exists saving ");
//                    saveConventionalCandidateDocumentInfo(fetchVendorConventionalCandidateDto);
//                    log.info("candidate doc saved ");
//
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
            ServiceOutcome<List> listServiceOutcome = candidateService.saveConventionalCandidateInformation(fetchVendorConventionalCandidateDto);

        } catch (Exception e) {
            log.info("addConvetionalCandidateData() exception " + e.getMessage());
        }
        log.info("addConvetionalCandidateData() ends");
        return "";
    }

    @Override
    public ServiceOutcome<List<SubmittedCandidates>> findAllConventionalVendorSubmittedCandidates() throws Exception {
        ServiceOutcome<List<SubmittedCandidates>> listServiceOutcome = new ServiceOutcome<>();
        User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);
        ;

        try {
            List<SubmittedCandidates> allSubmittedCandidates = conventionalCandidatesSubmittedRepository.findAllSubmittedCandidates();
            allSubmittedCandidates.forEach(resp -> {
                resp.setCreatedBy(user.getUserName());
            });
            if (allSubmittedCandidates.isEmpty()) {
                listServiceOutcome.setData(null);
            }
            listServiceOutcome.setData(allSubmittedCandidates);
        } catch (Exception e) {
            log.error(e.getMessage());

            throw new Exception(e.getMessage());

        }
        return listServiceOutcome;
    }


    @Override
    public ServiceOutcome<ConventionalCandidateDocDto> saveConventionalCandidateDocumentInfo(FetchVendorConventionalCandidateDto fetchVendorConventionalCandidateDto) throws Exception {
        ServiceOutcome<ConventionalCandidateDocDto> svcOutcome = new ServiceOutcome<ConventionalCandidateDocDto>();
        try {
            log.info("saveConventionalCandidateDocumentInfo starts()");
            User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);

            //To generate token first
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", environmentVal.getMtGrantType());
            map.add("username", environmentVal.getMtUsername());
            map.add("password", environmentVal.getMtPassword());

            HttpHeaders tokenHeader = new HttpHeaders();
            tokenHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            ResponseEntity<String> responseEntity = null;
            HttpEntity<MultiValueMap<String, String>> requestBodyFormUrlEncoded = new HttpEntity<>(map, tokenHeader);
            responseEntity = restTemplate.postForEntity(environmentVal.getConventionalVendorToken(), requestBodyFormUrlEncoded, String.class);
            JSONObject tokenObject = new JSONObject(responseEntity.getBody());
            String access_token = tokenObject.getString("access_token");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + access_token);
            headers.set("Content-Type", "application/json");
            HttpEntity<FetchVendorConventionalCandidateDto> liCheckDtoHttpEntity = new HttpEntity<>(fetchVendorConventionalCandidateDto, headers);
            ResponseEntity<String> icheckRepsonse = restTemplate.exchange(environmentVal.getConventionalVendorFetchVendorChecks(), HttpMethod.POST, liCheckDtoHttpEntity, String.class);
//            log.info("Response from lICheck response  API " + icheckRepsonse);
            String message = icheckRepsonse.getBody(); //.get("message").toString().replaceAll("=", ":")
            JSONObject obj1 = new JSONObject(message);
            JSONObject liCandidateInformation = obj1.getJSONObject("liCandidateInformation");
            if (liCandidateInformation.isNull("liCandidateDocumentInfo") == false) {
                JSONArray liCandidateDocumentInfo = liCandidateInformation.getJSONArray("liCandidateDocumentInfo");
                //getting the json document array and getting the individual document
                List<JSONObject> collect = IntStream.range(0, liCandidateDocumentInfo.length()).mapToObj(index -> ((JSONObject) liCandidateDocumentInfo.get(index))).collect(Collectors.toList());
                for (JSONObject jsonObject : collect) {
                    if (fetchVendorConventionalCandidateDto.getRequestType().equalsIgnoreCase("InsufficiencyClearance") == false) {
                        Boolean existsByRequestID = conventionalCandidateDocumentInfoRepository.existsByRequestID(obj1.getString("RequestID"));
                        if (existsByRequestID == false) {
                            ConventionalCandidateDocumentInfo conventionalCandidateDocumentInfo = new ConventionalCandidateDocumentInfo();
                            conventionalCandidateDocumentInfo.setCandidateId(obj1.getString("CandidateID"));
                            conventionalCandidateDocumentInfo.setDocumentName(jsonObject.getString("DocumentName"));
                            conventionalCandidateDocumentInfo.setFileType(jsonObject.getString("FileType"));
                            conventionalCandidateDocumentInfo.setResubmitted(false);
                            conventionalCandidateDocumentInfo.setRequestID(obj1.getString("RequestID"));
                            byte[] data = DatatypeConverter.parseBase64Binary(jsonObject.getString("DocumentAttachment"));
                            //unzipping and creating
                            Path resourcePath = Paths.get("src", "main", "resources", "temp");
                            File file3 = resourcePath.toFile();
                            String absolutePath = file3.getAbsolutePath();
                            if (file3.exists() == false) {
                                file3.mkdir();
                            }
                            String separator = File.separator;
                            String pathtocreate = absolutePath + separator + obj1.getString("RequestID");
                            log.info("pathserparator" + pathtocreate);
                            File pathofcreate = new File(pathtocreate);
                            if (pathofcreate.exists() == false) {
                                pathofcreate.mkdir();
                            }
                            //unzipped the data to  a file
                            String slash = fileUtil.unzip(data, pathofcreate.toString());
                            if (slash.isEmpty() == false) {
                                if (!file3.exists() || !file3.isDirectory()) {
                                    throw new Exception("The folder does not exist or is not a directory.");
                                }

                                File[] files = pathofcreate.listFiles();
                                if (files == null) {
                                    throw new Exception("The folder does not contain enough files.");
                                }
                                File folder = new File(pathtocreate.toString() + separator + slash);


                                if (!folder.exists() || !folder.isDirectory()) {
                                    throw new Exception("The folder does not exist or is not a directory.");
                                }
                                File[] filesdolder = folder.listFiles();
                                if (files == null || files.length < 1) {
                                    throw new Exception("The folder does not contain enough files.");
                                }

                                String folderKey = "Candidate/Convetional/" + obj1.getString("RequestID") + "/" + "New";
                                ObjectMetadata metadata = new ObjectMetadata();
                                String precisedUrlOfFolder = awsUtils.uploadEmptyFolderAndGeneratePrecisedUrl(DIGIVERIFIER_DOC_BUCKET_NAME, folderKey);
                                // Upload the files to the folder
                                for (File file : filesdolder) {
                                    String key = folderKey + file.getName();
                                    PutObjectRequest request = new PutObjectRequest(DIGIVERIFIER_DOC_BUCKET_NAME, key, file);
                                    s3Client.putObject(request);
                                }
                                conventionalCandidateDocumentInfo.setCreatedBy(user);
                                conventionalCandidateDocumentInfo.setDocumentUrl(folderKey);
                                conventionalCandidateDocumentInfo.setCreatedOn(new Date());
                                ConventionalCandidateDocumentInfo save = conventionalCandidateDocumentInfoRepository.save(conventionalCandidateDocumentInfo);
                                ConventionalCandidateDocDto conventionalCandidateDocDto = new ConventionalCandidateDocDto(save.getDocumentName(), save.getDocumentUrl(), save.getFileType());
                                svcOutcome.setData(conventionalCandidateDocDto);
//                            FileUtils.deleteDirectory(file3);
                            } else {
                                if (!file3.exists() || !file3.isDirectory()) {
                                    throw new Exception("The folder does not exist or is not a directory.");
                                }

                                File[] files = file3.listFiles();
                                if (files == null) {
                                    throw new Exception("The folder does not contain enough files.");
                                }
                                File folder = new File(file3.toString() + separator + obj1.getString("RequestID"));

                                if (!folder.exists() || !folder.isDirectory()) {
                                    throw new Exception("The folder does not exist or is not a directory.");
                                }
                                File[] filesdolder = folder.listFiles();
                                if (files == null || files.length < 1) {
                                    throw new Exception("The folder does not contain enough files.");
                                }

                                String folderKey = "Candidate/Convetional/" + obj1.getString("RequestID") + "/" + "New";
                                ObjectMetadata metadata = new ObjectMetadata();
                                String precisedUrlOfFolder = awsUtils.uploadEmptyFolderAndGeneratePrecisedUrl(DIGIVERIFIER_DOC_BUCKET_NAME, folderKey);
                                // Upload the files to the folder
                                for (File file : filesdolder) {
                                    String key = folderKey + file.getName();
                                    PutObjectRequest request = new PutObjectRequest(DIGIVERIFIER_DOC_BUCKET_NAME, key, file);
                                    s3Client.putObject(request);
                                }
                                conventionalCandidateDocumentInfo.setCreatedBy(user);
                                conventionalCandidateDocumentInfo.setDocumentUrl(folderKey);
                                conventionalCandidateDocumentInfo.setCreatedOn(new Date());
                                ConventionalCandidateDocumentInfo save = conventionalCandidateDocumentInfoRepository.save(conventionalCandidateDocumentInfo);
                                ConventionalCandidateDocDto conventionalCandidateDocDto = new ConventionalCandidateDocDto(save.getDocumentName(), save.getDocumentUrl(), save.getFileType());
                                svcOutcome.setData(conventionalCandidateDocDto);
//                            FileUtils.deleteDirectory(file3);
                            }
                        }

                    } else {
                        log.info("for insufficiency  clearance data adding doucment in s3");
                        ConventionalCandidateDocumentInfo conventionalCandidateDocumentInfo = conventionalCandidateDocumentInfoRepository.findByRequestIdForInsufficiency(obj1.getString("RequestID"));
                        if (conventionalCandidateDocumentInfo.isResubmitted() == false) {
                            conventionalCandidateDocumentInfo.setCandidateId(obj1.getString("CandidateID"));
                            conventionalCandidateDocumentInfo.setDocumentName(jsonObject.getString("DocumentName"));
                            conventionalCandidateDocumentInfo.setFileType(jsonObject.getString("FileType"));
                            conventionalCandidateDocumentInfo.setRequestID(obj1.getString("RequestID"));
                            byte[] data = DatatypeConverter.parseBase64Binary(jsonObject.getString("DocumentAttachment"));
                            //unzipping and creating
                            Path resourcePath = Paths.get("src", "main", "resources", "temp");
                            File file3 = resourcePath.toFile();
                            String absolutePath = file3.getAbsolutePath();
                            if (file3.exists() == false) {
                                file3.mkdir();
                            }
                            String separator = File.separator;
                            String pathtocreate = absolutePath + separator + obj1.getString("RequestID");
                            log.info("pathserparator" + pathtocreate);
                            File pathofcreate = new File(pathtocreate);
                            if (pathofcreate.exists() == false) {
                                pathofcreate.mkdir();
                            }
                            //unzipped the data to  a file
                            String slash = fileUtil.unzip(data, pathofcreate.toString());
                            if (slash.isEmpty() == false) {
                                if (!file3.exists() || !file3.isDirectory()) {
                                    throw new Exception("The folder does not exist or is not a directory.");
                                }

                                File[] files = pathofcreate.listFiles();
                                if (files == null) {
                                    throw new Exception("The folder does not contain enough files.");
                                }
                                File folder = new File(pathtocreate.toString() + separator + slash);


                                if (!folder.exists() || !folder.isDirectory()) {
                                    throw new Exception("The folder does not exist or is not a directory.");
                                }
                                File[] filesdolder = folder.listFiles();
                                if (files == null || files.length < 1) {
                                    throw new Exception("The folder does not contain enough files.");
                                }
                                List<ConventionalCandidateDocumentInfo> byCandidateId = conventionalCandidateDocumentInfoRepository.findByRequestID(obj1.getString("RequestID"));
                                // Upload the files to the folder
                                byCandidateId.forEach(byCandidateId1 -> {
                                    for (File file : filesdolder) {
                                        String key = byCandidateId1.getDocumentName() + file.getName() + "Resubmitted";
                                        PutObjectRequest request = new PutObjectRequest(DIGIVERIFIER_DOC_BUCKET_NAME, key, file);
                                        s3Client.putObject(request);
                                    }
                                    conventionalCandidateDocumentInfo.setCreatedBy(user);
                                    conventionalCandidateDocumentInfo.setResubmitted(true);
                                    conventionalCandidateDocumentInfo.setDocumentUrl(byCandidateId1.getDocumentUrl());
                                    conventionalCandidateDocumentInfo.setCreatedOn(new Date());
                                    ConventionalCandidateDocumentInfo save = conventionalCandidateDocumentInfoRepository.save(conventionalCandidateDocumentInfo);
                                    ConventionalCandidateDocDto conventionalCandidateDocDto = new ConventionalCandidateDocDto(save.getDocumentName(), save.getDocumentUrl(), save.getFileType());
                                    svcOutcome.setData(conventionalCandidateDocDto);

                                });
                            }
                        }
                    }
                }
            }
            log.info("saveConventionalCandidateDocumentInfo(ends)");
        } catch (Exception e) {
            log.error("exception occured in saveConventionalCandidateDocumentInfo()" + e.getMessage());

        }
        return svcOutcome;
    }

    @Override
    public ServiceOutcome<List<ConventionalCandidateDocDto>> findAllConventionalCandidateDocumentInfo() throws Exception {
        ServiceOutcome<List<ConventionalCandidateDocDto>> listServiceOutcome = new ServiceOutcome<>();

        try {
            User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);
            ;

            List<ConventionalCandidateDocDto> allConventionalCandidateDocs = conventionalCandidateDocumentInfoRepository.findAllConventionalCandidateDocs();
            if (allConventionalCandidateDocs.isEmpty()) {
                listServiceOutcome.setData(null);
            }
            allConventionalCandidateDocs.forEach(resp -> {
                resp.setCreatedBy(user.getUserName());
            });
            listServiceOutcome.setData(allConventionalCandidateDocs);
        } catch (Exception e) {
            log.error(e.getMessage());

            throw new Exception(e.getMessage());

        }
        return listServiceOutcome;
    }

    @Override
    public ServiceOutcome<CandidateuploadS3Documents> findAllfilesUploadedurls(String requestId, String checkName) throws Exception {
        ServiceOutcome<CandidateuploadS3Documents> listServiceOutcome = new ServiceOutcome<>();
        ArrayList<CandidateuploadS3Documents> candidateuploadS3Documents = new ArrayList<>();
        List<ConventionalCandidateDocumentInfo> filteredList = new ArrayList<>();
        try {
            List<ConventionalCandidateDocumentInfo> documentsList = conventionalCandidateDocumentInfoRepository.findByRequestID(requestId);
            String modifiedCheckName = checkName.replaceAll("[\\s-]", "").toLowerCase();
            List<ConventionalCandidateDocumentInfo> matchingDocuments = documentsList.stream().filter(documentInfo -> {
                String documentUrl = documentInfo.getDocumentUrl();
                String[] parts = documentUrl.split("/");
                String lastPart = parts[parts.length - 1].toLowerCase();
                lastPart = lastPart = lastPart.replaceAll("\\s", "");
                boolean checkDocMatch;
                checkDocMatch = modifiedCheckName.contains(lastPart.toLowerCase()) || modifiedCheckName.toLowerCase().contains(lastPart.toLowerCase());
                if (checkDocMatch == false) {
                    if (lastPart.contains("education")) {
                        if (modifiedCheckName.toLowerCase().contains("undergraduate")) {
                            checkDocMatch = true;
                        } else if (modifiedCheckName.toLowerCase().contains("postgraduate")) {
                            // Handle postgraduate case
                            checkDocMatch = true;
                        } else if (modifiedCheckName.toLowerCase().contains("diploma")) {
                            // Handle diploma case
                            checkDocMatch = true;
                        } else if (modifiedCheckName.toLowerCase().contains("highschool")) {
                            // Handle high school case
                            checkDocMatch = true;
                        } else if (modifiedCheckName.toLowerCase().contains("highest")) {
                            // Handle highest education case
                            checkDocMatch = true;
                        } else {
                            ConventionalCandidateCafEducation byConventionalRequestId = conventionalCafCandidateEducationRepository.findByConventionalRequestId(Long.valueOf(requestId));

                        }
                    }
                }
                return checkDocMatch;
            }).collect(Collectors.toList());

            matchingDocuments.forEach(matchingDocument -> {
                // Render or process the matching document data as needed
                log.info("Matching document found:");
                log.info("Document URL: " + matchingDocument.getDocumentUrl());
                CandidateuploadS3Documents candidateuploadS3Documents1 = new CandidateuploadS3Documents();
                candidateuploadS3Documents1.setDocumentName(matchingDocument.getDocumentName());
                candidateuploadS3Documents1.setPathkey(matchingDocument.getDocumentUrl());
                String presignedUrl = awsUtils.getPresignedUrl(DIGIVERIFIER_DOC_BUCKET_NAME, matchingDocument.getDocumentUrl());
                candidateuploadS3Documents1.setDocumentUrl(presignedUrl);
//                candidateuploadS3Documents.add(candidateuploadS3Documents1);
                listServiceOutcome.setData(candidateuploadS3Documents1);
            });

        } catch (Exception e) {
            log.error("in generate  precised url method");
        }
        return listServiceOutcome;
    }
//    public ServiceOutcome<List<CandidateuploadS3Documents>> findAllfilesUploadedurls(String requestId) throws Exception {
//
//        ServiceOutcome<List<CandidateuploadS3Documents>> listServiceOutcome = new ServiceOutcome<>();
//        ArrayList<CandidateuploadS3Documents> candidateuploadS3Documents = new ArrayList<>();
//        try {
//            //by request id
//            List<ConventionalCandidateDocumentInfo> byCandidateId = conventionalCandidateDocumentInfoRepository.findByRequestID(requestId);
//
//            for (ConventionalCandidateDocumentInfo conventionalCandidateDocumentInfo : byCandidateId) {
//                String url = conventionalCandidateDocumentInfo.getDocumentUrl();
//                ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(DIGIVERIFIER_DOC_BUCKET_NAME).withPrefix(url);
//                ObjectListing objectListing = s3Client.listObjects(listObjectsRequest);
//                List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
//                List<S3ObjectSummary> objectSummaries1 = objectListing.getObjectSummaries();
//                List<String> pdfFiles = new ArrayList<>();
//                for (S3ObjectSummary objectSummary : objectSummaries) {
//                    String key = objectSummary.getKey();
//                    if (key.endsWith(".pdf")) {
//                        pdfFiles.add(key);
//                        String presignedUrl = awsUtils.getPresignedUrl(DIGIVERIFIER_DOC_BUCKET_NAME, key);
//                        CandidateuploadS3Documents candidateuploadS3Documents1 = new CandidateuploadS3Documents();
//                        String[] split = key.split("/");
//                        candidateuploadS3Documents1.setDocumentName(split[split.length - 1]);
//                        candidateuploadS3Documents1.setPathkey(key);
//                        candidateuploadS3Documents1.setDocumentUrl(presignedUrl);
//                        candidateuploadS3Documents.add(candidateuploadS3Documents1);
//                    } else {
//                        log.info("not a pdf");
//                        pdfFiles.add(key);
//                        log.info(key);
//                        String presignedUrl = awsUtils.getPresignedUrl(DIGIVERIFIER_DOC_BUCKET_NAME, key);
//                        CandidateuploadS3Documents candidateuploadS3Documents1 = new CandidateuploadS3Documents();
//                        String[] split = key.split("/");
//                        candidateuploadS3Documents1.setDocumentName(split[split.length - 1]);
//                        candidateuploadS3Documents1.setPathkey(key);
//                        candidateuploadS3Documents1.setDocumentUrl(presignedUrl);
//                        candidateuploadS3Documents.add(candidateuploadS3Documents1);
//                    }
//                }
//
//                listServiceOutcome.setData(candidateuploadS3Documents);
//
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//        return listServiceOutcome;
//
//    }

    @Autowired
    private ModeOfVerificationStatusMasterRepository modeOfVerificationStatusMasterRepository;

    @Transactional
    public ServiceOutcome<String> updateLiCheckStatusByVendor(String vendorCheckStatusMasterId, String vendorCheckId, String remarks, String modeOfVericationPerformed) throws Exception {
        ServiceOutcome<String> serviceOutcome = new ServiceOutcome<>();
        try {
            LicheckHistory licheckHistory = new LicheckHistory();
            log.info("updateing vendorcheck with remarks");
            ConventionalVendorliChecksToPerform byVendorChecksVendorcheckId = liCheckToPerformRepository.findByVendorChecksVendorcheckId(Long.valueOf(vendorCheckId));
            VendorCheckStatusMaster byVendorCheckStatusMasterId = vendorCheckStatusMasterRepository.findByVendorCheckStatusMasterId(Long.valueOf(vendorCheckStatusMasterId));
            ModeOfVerificationStatusMaster modeOfVerificationStatusMaster = modeOfVerificationStatusMasterRepository.findById(Long.valueOf(modeOfVericationPerformed)).get();
            byVendorChecksVendorcheckId.setCheckStatus(byVendorCheckStatusMasterId);
            byVendorChecksVendorcheckId.setModeOfVerificationPerformed(String.valueOf(modeOfVerificationStatusMaster.getModeTypeCode()));
            byVendorChecksVendorcheckId.setCheckRemarks(remarks);
            ConventionalVendorliChecksToPerform updatedStatusCode = liCheckToPerformRepository.save(byVendorChecksVendorcheckId);
            ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(byVendorChecksVendorcheckId.getRequestId());
            StatusMaster pendingapproval = statusMasterRepository.findByStatusCode("PENDINGAPPROVAL");
            conventionalVendorCandidatesSubmitted.setStatus(pendingapproval);
            ConventionalVendorCandidatesSubmitted updated = conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
            licheckHistory.setCandidateId(Long.valueOf(updatedStatusCode.getCandidateId()));
            licheckHistory.setCheckName(updatedStatusCode.getCheckName());
            licheckHistory.setCheckUniqueId(updatedStatusCode.getCheckUniqueId());
            licheckHistory.setCreatedBy(updatedStatusCode.getCreatedBy().getUserName());
            licheckHistory.setCreatedOn(new Date());
            licheckHistory.setCheckStatus(updatedStatusCode.getCheckStatus().getCheckStatusCode());
            licheckHistory.setCandidateStatus(conventionalVendorCandidatesSubmitted.getStatus().getStatusCode());
            licheckHistory.setRequestType(updated.getRequestType());
            licheckHistory.setRequestId(Long.valueOf(updatedStatusCode.getRequestId()));
            LicheckHistory save = licheckHistoryRepository.save(licheckHistory);
            log.info("licheck history  CHANGED STATUS " + save.getId());
            log.info("update vendor updloaded candidate status");
            serviceOutcome.setData("updated with VendorCheckStatusId" + vendorCheckStatusMasterId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return serviceOutcome;
    }

    @Transactional
    public ServiceOutcome<String> findUpdateLicheckWithVendorCheck(Long vendorCheckId, Long liCheckId) throws Exception {
        ServiceOutcome<String> serviceOutcome = new ServiceOutcome<>();
        try {
            VendorChecks vendorChecks = vendorChecksRepository.findByVendorcheckId(vendorCheckId);
            ConventionalVendorliChecksToPerform conventionalVendorliChecksToPerform = liCheckToPerformRepository.findById(liCheckId).get();
            conventionalVendorliChecksToPerform.setVendorChecks(vendorChecks);
            ConventionalVendorliChecksToPerform save1 = liCheckToPerformRepository.save(conventionalVendorliChecksToPerform);
            liCheckToPerformRepository.flush();
            ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(conventionalVendorliChecksToPerform.getRequestId());
            if (conventionalVendorCandidatesSubmitted.getStatus().getStatusCode().equalsIgnoreCase("NEWUPLOAD")) {
                StatusMaster inprogress = statusMasterRepository.findByStatusCode("INPROGRESS");
                conventionalVendorCandidatesSubmitted.setStatus(inprogress);
                ConventionalVendorCandidatesSubmitted save = conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                log.info("updated candidate status");
            }
            updateBgvCheckRowwiseonProgress(Long.valueOf(conventionalVendorliChecksToPerform.getRequestId()), conventionalVendorliChecksToPerform.getCheckUniqueId());
//            log.info(" Not updated candidate status");
            serviceOutcome.setData(String.valueOf(vendorCheckId));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return serviceOutcome;
    }

    @Transactional
    public ServiceOutcome<String> updateCandidateStatusByLicheckStatus() {
        ServiceOutcome<String> serviceOutcome = new ServiceOutcome<String>();
        try {
            List<ConventionalVendorCandidatesSubmitted> candidatesSubmitteds = conventionalCandidatesSubmittedRepository.findAll();
            for (ConventionalVendorCandidatesSubmitted candidatesSubmitted : candidatesSubmitteds) {
                if (liCheckToPerformRepository.existsByRequestId(String.valueOf(candidatesSubmitted.getRequestId())) == true) {
                    List<ConventionalVendorliChecksToPerform> liChecks = liCheckToPerformRepository.findByRequestId(String.valueOf(candidatesSubmitted.getRequestId()));
                    Map<String, Long> statusCountMap = liChecks.stream().collect(Collectors.toMap(da -> String.valueOf(da.getCheckStatus().getVendorCheckStatusMasterId()), v -> 1L, Long::sum));
                    ArrayList<String> keydata = new ArrayList<>();
                    statusCountMap.forEach((k, v) -> keydata.add(k));
                    if (candidatesSubmitted.getStatus().getStatusMasterId() != 8l && candidatesSubmitted.getStatus().getStatusMasterId() != 13l) {
                        if (keydata.stream().anyMatch("7"::equals)) {
                            ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(candidatesSubmitted.getRequestId());
                            StatusMaster newupload = statusMasterRepository.findByStatusCode("NEWUPLOAD");
                            conventionalVendorCandidatesSubmitted.setStatus(newupload);
                            conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                        }
                        if (keydata.stream().anyMatch("2"::equals)) {
                            ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(candidatesSubmitted.getRequestId());
                            StatusMaster inprogress = statusMasterRepository.findByStatusCode("INPROGRESS");
                            conventionalVendorCandidatesSubmitted.setStatus(inprogress);
                            conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                        }
                        boolean matchFound = keydata.stream().anyMatch(str -> str.equals("7") || str.equals("2"));
                        boolean otherMatchFound = keydata.stream().anyMatch(str -> !str.equals("7") && !str.equals("2"));


                        if (otherMatchFound) {
                            ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(candidatesSubmitted.getRequestId());
                            StatusMaster pendingapproval = statusMasterRepository.findByStatusCode("PENDINGAPPROVAL");
                            conventionalVendorCandidatesSubmitted.setStatus(pendingapproval);
                            conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                        }
                    }

                }
            }

            serviceOutcome.setData("data came");
            return serviceOutcome;
        } catch (Exception e) {
            log.error(e.getMessage());

        }
        return serviceOutcome;
    }

    @Transactional
    public ServiceOutcome<String> updateCandidateVerificationStatus(String requestID) {
        ServiceOutcome<String> serviceOutcome = new ServiceOutcome<String>();
        try {
            ConventionalVendorCandidatesSubmitted candidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(requestID);
            if (liCheckToPerformRepository.existsByRequestId(String.valueOf(candidatesSubmitted.getRequestId())) == true) {
                List<ConventionalVendorliChecksToPerform> liChecks = liCheckToPerformRepository.findByRequestId(String.valueOf(candidatesSubmitted.getRequestId()));
                List<ConventionalVendorliChecksToPerform> collect = liChecks.stream().filter(licheck -> licheck.getCheckStatus().getVendorCheckStatusMasterId() != 7l && licheck.getCheckStatus().getVendorCheckStatusMasterId() != 2l).collect(Collectors.toList());


//                log.info("completed checks" + collect.toString());
                Map<String, Long> statusCountMap = collect.stream().collect(Collectors.toMap(da -> String.valueOf(da.getCheckStatus().getVendorCheckStatusMasterId()), v -> 1L, Long::sum));
                ArrayList<String> keydata = new ArrayList<>();
                statusCountMap.forEach((k, v) -> keydata.add(k));
                keydata.stream().sorted();
                if (keydata.stream().anyMatch("6"::equalsIgnoreCase)) {
                    ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(candidatesSubmitted.getRequestId());
                    conventionalVendorCandidatesSubmitted.setVerificationStatus("UNABLETOVERIFIY");
                    conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                } else if (keydata.stream().anyMatch("4"::equalsIgnoreCase)) {
                    ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(candidatesSubmitted.getRequestId());
                    conventionalVendorCandidatesSubmitted.setVerificationStatus("MAJORDISCREPANCY");
                    conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                } else if (keydata.stream().anyMatch("5"::equalsIgnoreCase)) {
                    ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(candidatesSubmitted.getRequestId());
                    conventionalVendorCandidatesSubmitted.setVerificationStatus("MINORDISCREPANCY");
                    conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                } else if (keydata.stream().anyMatch("3"::equalsIgnoreCase)) {
                    ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(candidatesSubmitted.getRequestId());
                    conventionalVendorCandidatesSubmitted.setVerificationStatus("INSUFFICIENCY");
                    conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                } else if (keydata.stream().anyMatch("2"::equalsIgnoreCase)) {
                    ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(candidatesSubmitted.getRequestId());
                    conventionalVendorCandidatesSubmitted.setVerificationStatus("INPROGRESS");
                    conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                } else if (keydata.stream().allMatch("1"::equalsIgnoreCase)) {
                    ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(candidatesSubmitted.getRequestId());
                    conventionalVendorCandidatesSubmitted.setVerificationStatus("CLEAR");
                    conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted);
                }
            }


            serviceOutcome.setData("data came");
            return serviceOutcome;
        } catch (Exception e) {
            log.error(e.getMessage());

        }
        return serviceOutcome;
    }

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
    @Autowired
    ConventionalVendorCandidatesSubmittedRepository conventionalVendorCandidatesSubmittedRepository;

    public ServiceOutcome<List<ConventionalVendorCandidatesSubmitted>> findAllConventionalVendorSubmittedCandidatesByDateRange(DashboardDto dashboardDto) throws Exception {
        ServiceOutcome<List<ConventionalVendorCandidatesSubmitted>> listServiceOutcome = new ServiceOutcome<>();
//         User user = (SecurityHelper.getCurrentUser()!=null)?SecurityHelper.getCurrentUser():userRepository.findByUserId(53l);;
        String strToDate = "";
        String strFromDate = "";
        List<ConventionalVendorCandidatesSubmitted> candidatesSubmittedList = new ArrayList<ConventionalVendorCandidatesSubmitted>();
        try {
            strToDate = dashboardDto.getToDate() != null ? dashboardDto.getToDate() : ApplicationDateUtils.getStringTodayAsDDMMYYYY();
            strFromDate = dashboardDto.getFromDate() != null ? dashboardDto.getFromDate() : ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 7);
            Date startDate = formatter.parse(strFromDate + " 00:00:00");
            Date endDate = formatter.parse(strToDate + " 23:59:59");
            User user = userRepository.findById(dashboardDto.getUserId()).get();
            User byOrganizationAndRoleId = userRepository.findByOrganizationAndRoleId(user.getOrganization().getOrganizationId(), user.getRole().getRoleId(), user.getUserId());
            if (byOrganizationAndRoleId != null) {
                candidatesSubmittedList = conventionalVendorCandidatesSubmittedRepository.findAllByUserIdAndDateRange(startDate, endDate);
            }
            if (candidatesSubmittedList.isEmpty() == false) {
                if (dashboardDto.getStatus() == null) {
                    dashboardDto.setStatus("NEWUPLOAD");
                }
                if (dashboardDto.getStatus().equalsIgnoreCase("NEWUPLOAD")) {
                    listServiceOutcome.setData(candidatesSubmittedList);
                    if (candidatesSubmittedList.isEmpty()) {
                        listServiceOutcome.setData(new ArrayList<>());
                    }
                } else if (dashboardDto.getStatus().equalsIgnoreCase("STOPBGV")) {
                    List<ConventionalVendorCandidatesSubmitted> collect = candidatesSubmittedList.stream().filter(cand -> cand.getStopCheckRecivedDate() != null).collect(Collectors.toList());
                    listServiceOutcome.setData(collect);
                    if (collect.isEmpty()) {
                        listServiceOutcome.setData(new ArrayList<>());
                    }
                } else {
                    List<ConventionalVendorCandidatesSubmitted> collect = candidatesSubmittedList.stream().filter(cand -> cand.getStatus().getStatusCode().equalsIgnoreCase(dashboardDto.getStatus())).collect(Collectors.toList());
                    listServiceOutcome.setData(collect);
                    if (collect.isEmpty()) {
                        listServiceOutcome.setData(new ArrayList<>());
                    }
                }
            } else {
                listServiceOutcome.setData(new ArrayList<ConventionalVendorCandidatesSubmitted>());
            }


        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return listServiceOutcome;
    }

    public ServiceOutcome<List<ConventionalVendorCandidatesSubmitted>> findAllSubmittedCandidatesByDateRangeOnInterimAndFinal(DashboardDto dashboardDto) throws Exception {
        ServiceOutcome<List<ConventionalVendorCandidatesSubmitted>> listServiceOutcome = new ServiceOutcome<>();
        String strToDate = "";
        String strFromDate = "";
        List<ConventionalVendorCandidatesSubmitted> candidatesSubmittedList = new ArrayList<ConventionalVendorCandidatesSubmitted>();
        try {
            strToDate = dashboardDto.getToDate() != null ? dashboardDto.getToDate() : ApplicationDateUtils.getStringTodayAsDDMMYYYY();
            strFromDate = dashboardDto.getFromDate() != null ? dashboardDto.getFromDate() : ApplicationDateUtils.subtractNoOfDaysFromDateAsDDMMYYYY(new SimpleDateFormat("dd/MM/yyyy").parse(strToDate), 7);
            Date startDate = formatter.parse(strFromDate + " 00:00:00");
            Date endDate = formatter.parse(strToDate + " 23:59:59");
            User user = userRepository.findById(dashboardDto.getUserId()).get();
            if (user.getRole().getRoleCode().equalsIgnoreCase("ROLE_ADMIN") || user.getRole().getRoleCode().equalsIgnoreCase("ROLE_PARTNERADMIN")) {
                candidatesSubmittedList = conventionalVendorCandidatesSubmittedRepository.findAllByUserIdAndDateRange(startDate, endDate);
            }
            if (candidatesSubmittedList.isEmpty() == false) {
                if (dashboardDto.getStatus() == null) {
                    dashboardDto.setStatus("NEWUPLOAD");
                }
                if (dashboardDto.getStatus().equalsIgnoreCase("NEWUPLOAD")) {
                    listServiceOutcome.setData(candidatesSubmittedList);
                    if (candidatesSubmittedList.isEmpty()) {
                        listServiceOutcome.setData(null);
                    }
                } else {
                    List<ConventionalVendorCandidatesSubmitted> collect = candidatesSubmittedList.stream().filter(cand -> cand.getStatus().getStatusCode().equalsIgnoreCase(dashboardDto.getStatus())).collect(Collectors.toList());
                    listServiceOutcome.setData(collect);
                    if (collect.isEmpty()) {
                        listServiceOutcome.setData(null);
                    }
                }
            } else {
                listServiceOutcome.setData(new ArrayList<ConventionalVendorCandidatesSubmitted>());
            }


        } catch (Exception e) {
            log.error(e.getMessage());

        }
        return listServiceOutcome;
    }


    public ServiceOutcome<List<VendorCheckStatusMaster>> findAllVendorCheckStatus() {
        ServiceOutcome<List<VendorCheckStatusMaster>> vendorCheckStatusMaster = new ServiceOutcome<>();
        try {
            List<VendorCheckStatusMaster> all = vendorCheckStatusMasterRepository.findAll();
            vendorCheckStatusMaster.setData(all);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return vendorCheckStatusMaster;

    }

    public ServiceOutcome<List<liReportDetails>> generateDocumentConventional(String requestID, String reportType) {
        ServiceOutcome<List<liReportDetails>> listServiceOutcome = new ServiceOutcome<>();
        try {
            List<liChecksDetails> liChecksDetails = liCheckToPerformRepository.findAllUpdateLiCheckResponseByRequestId(requestID);
            log.info("enter to generate doc *******************************");
            Candidate candidate = candidateRepository.findByConventionalRequestId(Long.valueOf(requestID));
//            CandidateAddComments candidateAddComments=candidateAddCommentRepository.findByCandidateCandidateId(candidate.getCandidateId());
//            log.info(candidate.getCandidateId()+"*******************************"+validateCandidateStatus(candidate.getCandidateId()));
//            if(validateCandidateStatus(candidate.getCandidateId())){
            log.info("enter if *******************************");
            List<VendorUploadChecksDto> vendordocDtoList = new ArrayList<VendorUploadChecksDto>();
            VendorUploadChecksDto vendorUploadChecksDto = null;
            // candidate Basic detail
            ConventionalCandidateReportDto candidateReportDTO = new ConventionalCandidateReportDto();
            candidateReportDTO.setName(candidate.getCandidateName());
            candidateReportDTO.setApplicantId(candidate.getApplicantId());
            candidateReportDTO.setDob(candidate.getDateOfBirth());
            candidateReportDTO.setContactNo(candidate.getContactNumber());
            candidateReportDTO.setEmailId(candidate.getEmailId());
            candidateReportDTO.setApplicantId("124ffa");
            candidateReportDTO.setProject("No Project");
            candidateReportDTO.setExperience("Fresher");
            candidateReportDTO.setCaseInitiationDate("22/12/2023");
            candidateReportDTO.setFinalReportDate("22/1/2023");
//            candidateReportDTO.setVerificationStatus(VerificationStatus.RED);
//           candidateReportDTO.setExperience(candidate.getIsFresher() ? "Fresher" : "Experience");
//                candidateReportDTO.setReportType(reportType);
            Organization organization = candidate.getOrganization();
            candidateReportDTO.setOrganizationName(organization.getOrganizationName());
            candidateReportDTO.setProject(organization.getOrganizationName());
            candidateReportDTO.setOrganizationLocation(organization.getOrganizationLocation());
            candidateReportDTO.setOrganizationLogo(organization.getLogoUrl());
            candidateReportDTO.setFinalReportDate(new Date().toString());

//            List<VendorChecks> vendorList = vendorChecksRepository.findAllByCandidateCandidateId(candidate.getCandidateId());
//            for (VendorChecks vendorChecks : vendorList) {
//
//                User user = userRepository.findByUserId(vendorChecks.getVendorId());
//                VendorUploadChecks vendorChecksss = vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
//                if (vendorChecksss != null) {
//                    vendorUploadChecksDto = new VendorUploadChecksDto(user.getUserFirstName(), vendorChecksss.getVendorChecks().getVendorcheckId(), vendorChecksss.getVendorUploadedDocument(), vendorChecksss.getDocumentname(), vendorChecksss.getAgentColor().getColorName(), vendorChecksss.getAgentColor().getColorHexCode(), null);
//                    vendordocDtoList.add(vendorUploadChecksDto);
//                }
//            }
//            candidateReportDTO.setVendorProofDetails(vendordocDtoList);


            File report = FileUtil.createUniqueTempFile("report", ".pdf");
            //here we are setting the pdf file where resides in the resources  folder
            String htmlStr = pdfService.parseThymeleafTemplateForConventionalCandidate("conventional_pdf", candidateReportDTO);
            pdfService.generatePdfFromHtml(htmlStr, report);


        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return listServiceOutcome;
    }


    @Autowired
    OrganizationService organizationService;
    @Autowired
    @Lazy
    private EmailProperties emailProperties;
    @Autowired
    CandidateCafAddressRepository candidateCafAddressRepository;

    //not touched
    public ServiceOutcome<String> generateConventionalCandidateReport(Long candidateId, ReportType reportType, String updated) {
        ServiceOutcome<String> stringServiceOutcome = new ServiceOutcome<>();
        try {
            log.info("enter to generate doc *******************************");
//            Candidate candidate = candidateService.findCandidateByCandidateCode(candidateCode);
            Candidate candidate = candidateRepository.findById(candidateId).get();
            ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(String.valueOf(candidate.getConventionalRequestId()));
            ConventionalCandidate byConventionalRequestId = conventionalCandidateRepository.findByConventionalRequestId(Long.valueOf(conventionalVendorCandidatesSubmitted.getRequestId()));
            if (candidate != null) {
                log.info("enter if *******************************");
                List<VendorUploadChecksDto> vendordocDtoList = new ArrayList<VendorUploadChecksDto>();
                // candidate Basic detailx`
                ConventionalCandidateReportDto candidateReportDTO = new ConventionalCandidateReportDto();
                candidateReportDTO.setCandidateId(String.valueOf(conventionalVendorCandidatesSubmitted.getCandidateId()));
                candidateReportDTO.setApplicantId(String.valueOf(conventionalVendorCandidatesSubmitted.getApplicantId()));
                candidateReportDTO.setOrganizationName(candidate.getOrganization().getOrganizationName());
                candidateReportDTO.setOrganizationLogo(Arrays.toString(candidate.getOrganization().getOrganizationLogo()));
                candidateReportDTO.setRequestId(conventionalVendorCandidatesSubmitted.getRequestId());
                if (byConventionalRequestId != null) {
                    candidateReportDTO.setAddress(byConventionalRequestId.getBirthPlace());
                }
                if (conventionalVendorCandidatesSubmitted.getVerificationStatus() != null) {
                    if (conventionalVendorCandidatesSubmitted.getVerificationStatus().equalsIgnoreCase("UNABLETOVERIFIY")) {
                        candidateReportDTO.setVerificationStatus(ConventionalVerificationStatus.UNABLETOVERIFIY);
                    }
                    if (conventionalVendorCandidatesSubmitted.getVerificationStatus().equalsIgnoreCase("MAJORDISCREPANCY")) {
                        candidateReportDTO.setVerificationStatus(ConventionalVerificationStatus.MAJORDISCREPANCY);
                    }
                    if (conventionalVendorCandidatesSubmitted.getVerificationStatus().equalsIgnoreCase("MINORDISCREPANCY")) {
                        candidateReportDTO.setVerificationStatus(ConventionalVerificationStatus.MINORDISCREPANCY);
                    }
                    if (conventionalVendorCandidatesSubmitted.getVerificationStatus().equalsIgnoreCase("INSUFFICIENCY")) {
                        candidateReportDTO.setVerificationStatus(ConventionalVerificationStatus.INSUFFICIENCY);
                    }
                    if (conventionalVendorCandidatesSubmitted.getVerificationStatus().equalsIgnoreCase("INPROGRESS")) {
                        candidateReportDTO.setVerificationStatus(ConventionalVerificationStatus.INPROGRESS);
                    }
                    if (conventionalVendorCandidatesSubmitted.getVerificationStatus().equalsIgnoreCase("CLEAR")) {
                        candidateReportDTO.setVerificationStatus(ConventionalVerificationStatus.CLEAR);
                    }
                } else {
                    candidateReportDTO.setVerificationStatus(ConventionalVerificationStatus.NAN);
                }
                candidateReportDTO.setName(candidate.getCandidateName());
                candidateReportDTO.setReferenceId(candidate.getApplicantId());
                candidateReportDTO.setDob(candidate.getDateOfBirth());
                candidateReportDTO.setContactNo(candidate.getContactNumber());
                candidateReportDTO.setEmailId(candidate.getEmailId());
                candidateReportDTO.setReportType(reportType);
                Organization organization = candidate.getOrganization();
                candidateReportDTO.setProject(organization.getOrganizationName());
                candidateReportDTO.setOrganizationLocation(organization.getOrganizationLocation());
                candidateReportDTO.setOrganizationLogo(organization.getLogoUrl());
                candidateReportDTO.setComments("");
                log.info("request id" + String.valueOf(candidate.getConventionalRequestId()));
                List<ConventionalVendorliChecksToPerform> byCandidateId = liCheckToPerformRepository.findByRequestId(String.valueOf(candidate.getConventionalRequestId()));
                log.warn("check data outside report" + byCandidateId);
                if (byCandidateId.isEmpty() == false) {
                    List<ConventionalVendorliChecksToPerform> collect1 = byCandidateId.stream().filter(licheck -> licheck.getCheckStatus().getVendorCheckStatusMasterId() != 7l && licheck.getCheckStatus().getVendorCheckStatusMasterId() != 2l).collect(Collectors.toList());
//                    log.warn("check data inside report" + collect1.toString());
                    candidateReportDTO.setLiChecksDetails(collect1);
                } else {
                    candidateReportDTO.setLiChecksDetails(new ArrayList<>());
                }
                CandidateVerificationState candidateVerificationState = candidateService.getCandidateVerificationStateByCandidateId(candidate.getCandidateId());
                boolean hasCandidateVerificationStateChanged = false;
                if (Objects.isNull(candidateVerificationState)) {
                    candidateVerificationState = new CandidateVerificationState();
                    candidateVerificationState.setCandidate(candidate);
                    final ZoneId id = ZoneId.systemDefault();
                    candidateVerificationState.setCaseInitiationTime(ZonedDateTime.ofInstant(candidate.getCreatedOn().toInstant(), id));
                }
                switch (reportType) {
                    case FINAL:
                        if (updated.equalsIgnoreCase("UPDATE")) {
                            candidateVerificationState.setFinalReportTime(ZonedDateTime.now());
                        }
                        break;
                    case INTERIM:
                        if (updated.equalsIgnoreCase("UPDATE")) {
                            candidateVerificationState.setInterimReportTime(ZonedDateTime.now());
                        }
                        break;
                }
                candidateVerificationState = candidateService.addOrUpdateCandidateVerificationStateByCandidateId(candidate.getCandidateId(), candidateVerificationState);
//                Content byCandidateIdAndLastUpdatedOnMax = contentRepository.findByCandidateIdAndLastUpdatedOnMax(candidateId);
//                candidateReportDTO.setFinalReportDate(String.valueOf(byCandidateIdAndLastUpdatedOnMax.getLastUpdatedOn()));
                candidateReportDTO.setFinalReportDate(DateUtil.convertToString(candidateVerificationState.getFinalReportTime()));
                candidateReportDTO.setInterimReportDate(DateUtil.convertToString(candidateVerificationState.getInterimReportTime()));
                candidateReportDTO.setCaseInitiationDate(DateUtil.convertToString(ZonedDateTime.now()));
                Long organizationId = organization.getOrganizationId();
                List<OrganizationExecutive> organizationExecutiveByOrganizationId = organizationService.getOrganizationExecutiveByOrganizationId(organizationId);
                List<ExecutiveSummaryDto> executiveSummaryDtos = new ArrayList<>();
                //            organizationExecutiveByOrganizationId.stream().forEach(organizationExecutive -> {
//                switch (organizationExecutive.getExecutive().getName()) {
//
//                    // log.info(organizationExecutive.getExecutive());
//                    case EDUCATION:
//                        log.info("inside EDUCATION *******************************");
//                        List<CandidateCafEducationDto> candidateCafEducationDtos = candidateService.getAllCandidateEducationByCandidateId(
//                                candidate.getCandidateId());
//                        List<EducationVerificationDTO> educationVerificationDTOS = candidateCafEducationDtos.stream()
//                                .map(candidateCafEducationDto -> {
//                                    EducationVerificationDTO educationVerificationDTO = new EducationVerificationDTO();
//                                    educationVerificationDTO.setVerificationStatus(VerificationStatus.valueOf(candidateCafEducationDto.getColorColorCode()));
//                                    educationVerificationDTO.setSource(SourceEnum.DIGILOCKER);
//                                    educationVerificationDTO.setDegree(candidateCafEducationDto.getCourseName());
//                                    educationVerificationDTO.setUniversity(
//                                            candidateCafEducationDto.getBoardOrUniversityName());
//                                    return educationVerificationDTO;
//                                }).collect(Collectors.toList());
//                        candidateReportDTO.setEducationVerificationDTOList(educationVerificationDTOS);
//                        List<String> redArray = new ArrayList<>();
//                        ;
//                        List<String> amberArray = new ArrayList<>();
//                        ;
//                        List<String> greenArray = new ArrayList<>();
//                        ;
//                        String status = null;
//                        for (EducationVerificationDTO s : educationVerificationDTOS) {
//                            if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
//                                redArray.add("count");
//                            } else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)) {
//                                amberArray.add("count");
//                            } else {
//                                greenArray.add("count");
//                            }
//                        }
//                        if (redArray.size() > 0) {
//                            status = VerificationStatus.RED.toString();
//                        } else if (amberArray.size() > 0) {
//                            status = VerificationStatus.AMBER.toString();
//                        } else {
//                            status = VerificationStatus.GREEN.toString();
//                        }
//                        candidateReportDTO.setEducationConsolidatedStatus(status);
//                        break;
//                    case IDENTITY:
//                        log.info("inside identity *******************************");
//                        // verify from digilocker and itr
//                        List<IDVerificationDTO> idVerificationDTOList = new ArrayList<>();
//                        IDVerificationDTO aadhaarIdVerificationDTO = new IDVerificationDTO();
//                        aadhaarIdVerificationDTO.setName(candidate.getAadharName());
//                        aadhaarIdVerificationDTO.setIDtype(IDtype.AADHAAR.label);
//                        aadhaarIdVerificationDTO.setIdNo(candidate.getAadharNumber());
//                        aadhaarIdVerificationDTO.setSourceEnum(SourceEnum.DIGILOCKER);
//                        aadhaarIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
//                        idVerificationDTOList.add(aadhaarIdVerificationDTO);
//
//                        IDVerificationDTO panIdVerificationDTO = new IDVerificationDTO();
//                        panIdVerificationDTO.setName(candidate.getCandidateName());
//                        panIdVerificationDTO.setIDtype(IDtype.PAN.label);
//                        panIdVerificationDTO.setIdNo(candidate.getPanNumber());
//                        panIdVerificationDTO.setSourceEnum(SourceEnum.DIGILOCKER);
//                        panIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
//                        idVerificationDTOList.add(panIdVerificationDTO);
//
//                        List<CandidateEPFOResponse> uanList = candidateEPFOResponseRepository.findByCandidateId(
//                                candidate.getCandidateId());
//                        for (CandidateEPFOResponse candidateEPFOResponse : uanList) {
//                            IDVerificationDTO uanIdVerificationDTO = new IDVerificationDTO();
//                            uanIdVerificationDTO.setName(candidate.getCandidateName());
//                            uanIdVerificationDTO.setIDtype(IDtype.UAN.label);
//                            uanIdVerificationDTO.setIdNo(candidateEPFOResponse.getUan());
//                            uanIdVerificationDTO.setSourceEnum(SourceEnum.EPFO);
//                            uanIdVerificationDTO.setVerificationStatus(VerificationStatus.GREEN);
//                            idVerificationDTOList.add(uanIdVerificationDTO);
//                        }
//                        List<String> redArray_id = new ArrayList<>();
//                        ;
//                        List<String> amberArray_id = new ArrayList<>();
//                        ;
//                        List<String> greenArray_id = new ArrayList<>();
//                        ;
//                        String status_id = null;
//                        for (IDVerificationDTO s : idVerificationDTOList) {
//                            if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
//                                redArray_id.add("count");
//                            } else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)) {
//                                amberArray_id.add("count");
//                            } else {
//                                greenArray_id.add("count");
//                            }
//                        }
//                        if (redArray_id.size() > 0) {
//                            status_id = VerificationStatus.RED.toString();
//                        } else if (amberArray_id.size() > 0) {
//                            status_id = VerificationStatus.AMBER.toString();
//                        } else {
//                            status_id = VerificationStatus.GREEN.toString();
//                        }
//
//                        log.info("befor epfo *******************************");
//                        candidateReportDTO.setIdVerificationDTOList(idVerificationDTOList);
//                        candidateReportDTO.setIdConsolidatedStatus(status_id);
//                        PanCardVerificationDto panCardVerificationDto = new PanCardVerificationDto();
//                        panCardVerificationDto.setInput(candidate.getPanNumber());
//                        panCardVerificationDto.setOutput(candidate.getPanNumber());
//                        panCardVerificationDto.setSource(SourceEnum.DIGILOCKER);
//                        panCardVerificationDto.setVerificationStatus(VerificationStatus.GREEN);
//                        candidateReportDTO.setPanCardVerification(panCardVerificationDto);
//                        executiveSummaryDtos.add(new ExecutiveSummaryDto(ExecutiveName.IDENTITY, "Pan", VerificationStatus.GREEN));
////
//                        AadharVerificationDTO aadharVerification = new AadharVerificationDTO();
//                        aadharVerification.setAadharNo(candidate.getAadharNumber());
//                        aadharVerification.setName(candidate.getAadharName());
//                        aadharVerification.setFatherName(candidate.getAadharFatherName());
//                        aadharVerification.setDob(candidate.getAadharDob());
//                        aadharVerification.setSource(SourceEnum.DIGILOCKER);
//                        candidateReportDTO.setAadharCardVerification(aadharVerification);
//                        executiveSummaryDtos.add(new ExecutiveSummaryDto(ExecutiveName.IDENTITY, "Aadhar", VerificationStatus.GREEN));
//                        break;
//                    case EMPLOYMENT:
//                        log.info("empy *******************************");
//                        List<CandidateCafExperience> candidateCafExperienceList = candidateService.getCandidateExperienceByCandidateId(candidate.getCandidateId());
//
//                        Collections.sort(candidateCafExperienceList, new Comparator<CandidateCafExperience>() {
//                            @Override
//                            public int compare(CandidateCafExperience o1, CandidateCafExperience o2) {
//                                return o1.getInputDateOfJoining().compareTo(o2.getInputDateOfJoining());
//                            }
//                        });
//                        Collections.reverse(candidateCafExperienceList);
//                        cleanDate(candidateCafExperienceList);
//                        List<CandidateCafExperience> candidateExperienceFromItrEpfo = candidateService.getCandidateExperienceFromItrAndEpfoByCandidateId(
//                                candidate.getCandidateId(), true);
//                        cleanDate(candidateExperienceFromItrEpfo);
//                        ServiceOutcome<ToleranceConfig> toleranceConfigByOrgId = organizationService.getToleranceConfigByOrgId(
//                                organizationId);
//                        // log.info(candidateCafExperienceList+"candidateCafExperienceList");
//                        if (!candidateCafExperienceList.isEmpty()) {
//                            log.info("inside exp if");
//                            // validate experience and tenure
//                            List<EmploymentVerificationDto> employmentVerificationDtoList = validateAndCompareExperience(candidateCafExperienceList, candidateExperienceFromItrEpfo, toleranceConfigByOrgId.getData());
//                            employmentVerificationDtoList.sort(Comparator.comparing(EmploymentVerificationDto::getDoj).reversed());
//                            candidateReportDTO.setEmploymentVerificationDtoList(employmentVerificationDtoList);
//
//                            List<EmploymentTenureVerificationDto> employmentTenureDtoList = validateAndCompareExperienceTenure(employmentVerificationDtoList, candidateExperienceFromItrEpfo, toleranceConfigByOrgId.getData());
//                            employmentTenureDtoList.sort(Comparator.comparing(EmploymentTenureVerificationDto::getDoj).reversed());
//                            candidateReportDTO.setEmploymentTenureVerificationDtoList(employmentTenureDtoList);
//                            List<String> redArray_emp = new ArrayList<>();
//                            ;
//                            List<String> amberArray_emp = new ArrayList<>();
//                            ;
//                            List<String> greenArray_emp = new ArrayList<>();
//                            ;
//                            String status_emp = null;
//                            for (EmploymentTenureVerificationDto s : employmentTenureDtoList) {
//                                if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
//                                    redArray_emp.add("count");
//                                } else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)) {
//                                    amberArray_emp.add("count");
//                                } else {
//                                    greenArray_emp.add("count");
//                                }
//                            }
//                            if (redArray_emp.size() > 0) {
//                                status_emp = VerificationStatus.RED.toString();
//                            } else if (amberArray_emp.size() > 0) {
//                                status_emp = VerificationStatus.AMBER.toString();
//                            } else {
//                                status_emp = VerificationStatus.GREEN.toString();
//                            }
//                            candidateReportDTO.setEmploymentConsolidatedStatus(status_emp);
//                            candidateCafExperienceList.sort(Comparator.comparing(CandidateCafExperience::getInputDateOfJoining).reversed());
//                            candidateReportDTO.setInputExperienceList(candidateCafExperienceList);
//                            EPFODataDto epfoDataDto = new EPFODataDto();
//
//                            Optional<CandidateEPFOResponse> canditateItrEpfoResponseOptional = candidateEPFOResponseRepository.findByCandidateId(
//                                    candidate.getCandidateId()).stream().findFirst();
//                            if (canditateItrEpfoResponseOptional.isPresent()) {
//                                String epfoResponse = canditateItrEpfoResponseOptional.get().getEPFOResponse();
//                                try {
//                                    ObjectMapper objectMapper = new ObjectMapper();
//                                    JsonNode arrNode = objectMapper.readTree(epfoResponse).get("message");
//                                    List<EpfoDataResDTO> epfoDatas = new ArrayList<>();
//                                    if (arrNode.isArray()) {
//                                        for (final JsonNode objNode : arrNode) {
//                                            EpfoDataResDTO epfoData = new EpfoDataResDTO();
//                                            epfoData.setName(objNode.get("name").asText());
//                                            epfoData.setUan(objNode.get("uan").asText());
//                                            epfoData.setCompany(objNode.get("company").asText());
//                                            epfoData.setDoe(objNode.get("doe").asText());
//                                            epfoData.setDoj(objNode.get("doj").asText());
//                                            epfoDatas.add(epfoData);
//                                        }
//                                    }
//
//                                    epfoDataDto.setCandidateName(epfoDatas.stream().map(EpfoDataResDTO::getName).filter(StringUtils::isNotEmpty).findFirst().orElse(null));
//                                    epfoDataDto.setUANno(canditateItrEpfoResponseOptional.get().getUan());
//                                    epfoDataDto.setEpfoDataList(epfoDatas);
//                                    candidateReportDTO.setEpfoData(epfoDataDto);
//                                } catch (JsonProcessingException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//
//                            List<ITRData> itrDataList = itrDataRepository.findAllByCandidateCandidateCodeOrderByFiledDateDesc(
//                                    candidateCode);
//                            ITRDataDto itrDataDto = new ITRDataDto();
//                            itrDataDto.setItrDataList(itrDataList);
//                            candidateReportDTO.setItrData(itrDataDto);
//
//                            // log.info(candidateReportDTO.getEmploymentVerificationDtoList()+"candidateReportDTO");
//                            for (EmploymentVerificationDto employmentVerificationDto : candidateReportDTO.getEmploymentVerificationDtoList()) {
//                                // log.info("inside for"+employmentVerificationDto+"emppppp"+candidateReportDTO);
//                                executiveSummaryDtos.add(new ExecutiveSummaryDto(ExecutiveName.EMPLOYMENT, employmentVerificationDto.getInput(), employmentVerificationDto.getVerificationStatus()));
//                            }
//
//
//                        }
//
//                        break;
//                    case ADDRESS:
//
//                        List<CandidateCafAddressDto> candidateAddress = candidateService.getCandidateAddress(candidate);
//                        log.info("ADDRESS**************" + candidateAddress);
//                        List<AddressVerificationDto> collect = candidateAddress.stream().map(candidateCafAddressDto -> {
//                            AddressVerificationDto addressVerificationDto = new AddressVerificationDto();
//                            addressVerificationDto.setInput(candidateCafAddressDto.getCandidateAddress());
//                            addressVerificationDto.setVerificationStatus(VerificationStatus.GREEN);
//                            addressVerificationDto.setSource(SourceEnum.DIGILOCKER);
//                            List<String> type = new ArrayList<>();
//                            // 	if(candidateCafAddressDto.getIsAssetDeliveryAddress()) {
//                            // 		type.add("Communication");
//                            // 	}  if(candidateCafAddressDto.getIsPresentAddress()) {
//                            // 		type.add("Present");
//
//                            // 	}  if(candidateCafAddressDto.getIsPermanentAddress()) {
//                            // 		type.add("Premanent");
//                            // 	}
//                            // 	addressVerificationDto.setType(String.join(", ", type));
//                            return addressVerificationDto;
//                        }).collect(Collectors.toList());
//                        List<String> redArray_addr = new ArrayList<>();
//                        ;
//                        List<String> amberArray_addr = new ArrayList<>();
//                        ;
//                        List<String> greenArray_addr = new ArrayList<>();
//                        ;
//                        String status_addr = null;
//                        for (AddressVerificationDto s : collect) {
//                            if (s.getVerificationStatus().equals(VerificationStatus.RED)) {
//                                redArray_addr.add("count");
//                            } else if (s.getVerificationStatus().equals(VerificationStatus.AMBER)) {
//                                amberArray_addr.add("count");
//                            } else {
//                                greenArray_addr.add("count");
//                            }
//                        }
//                        if (redArray_addr.size() > 0) {
//                            status_addr = VerificationStatus.RED.toString();
//                        } else if (amberArray_addr.size() > 0) {
//                            status_addr = VerificationStatus.AMBER.toString();
//                        } else {
//                            status_addr = VerificationStatus.GREEN.toString();
//                        }
//                        candidateReportDTO.setAddressConsolidatedStatus(status_addr);
//                        candidateReportDTO.setAddressVerificationDtoList(collect);
//                        log.info("candidateReportDTO**************" + candidateReportDTO);
//                        break;
//                    case CRIMINAL:
//                        break;
//                    case REFERENCE_CHECK_1:
//                        break;
//                    case REFERENCE_CHECK_2:
//                        break;
//                }
//
//                log.info("switch  *******************************");
//                candidateReportDTO.setExecutiveSummaryList(executiveSummaryDtos);
//                log.info("switch end *******************************");
//
//            });
                // log.info("before pdf*******************************"+candidateReportDTO);

//                List<VendorChecks> vendorList = vendorChecksRepository.findAllByCandidateCandidateId(candidate.getCandidateId());
//                for (VendorChecks vendorChecks : vendorList) {
//
//                    User user = userRepository.findByUserId(vendorChecks.getVendorId());
//                    VendorUploadChecks vendorChecksss = vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
//                    if (vendorChecksss != null) {
//                        vendorUploadChecksDto = new VendorUploadChecksDto(user.getUserFirstName(), vendorChecksss.getVendorChecks().getVendorcheckId(), vendorChecksss.getVendorUploadedDocument(), vendorChecksss.getDocumentname(), vendorChecksss.getAgentColor().getColorName(), vendorChecksss.getAgentColor().getColorHexCode(), null);
//                        vendordocDtoList.add(vendorUploadChecksDto);
//                    }
//                }
//                candidateReportDTO.setVendorProofDetails(vendordocDtoList);
//                ArrayList<VendorAttributeDto> vendorAttributeDtos = new ArrayList<>();
//                VendorAttributeDto vendorAttributeDto =null;
                List<VendorChecks> vendorList = vendorChecksRepository.findAllByCandidateCandidateId(candidate.getCandidateId());
//                for (VendorChecks vendorChecks : vendorList) {
//                    User user = userRepository.findByUserId(vendorChecks.getVendorId());
//                    VendorUploadChecks vendorChecksss = vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
//                    if (vendorChecksss != null) {
//                        log.info("interim document list with  document name    : " + vendorChecksss.getDocumentname());
//                        vendorAttributeDto.setSourceName(vendorChecksss.getVendorChecks().getSource().getSourceName());
//                        vendorAttributeDto.setVendorAttirbuteValue(vendorChecksss.getVendorAttirbuteValue());
//                        vendorAttributeDtos.add(vendorAttributeDto);
//                        VendorUploadChecksDto vendorUploadChecksDto = new VendorUploadChecksDto(user.getUserFirstName(), vendorChecksss.getVendorChecks().getVendorcheckId(), vendorChecksss.getVendorUploadedDocument(), vendorChecksss.getDocumentname(), vendorChecksss.getAgentColor().getColorName(), vendorChecksss.getAgentColor().getColorHexCode(), null, vendorAttributeDtos);
//                        vendordocDtoList.add(vendorUploadChecksDto);
//                        log.info("vendor docdto data " + vendordocDtoList.toString());
//                    }
//                }

//                for (VendorChecks vendorChecks : vendorList) {
//                    User user = userRepository.findByUserId(vendorChecks.getVendorId());
//                    VendorUploadChecks vendorChecksss = vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
//                    if (vendorChecksss != null) {
////                        VendorUploadChecksDto vendorUploadChecksDto = new VendorUploadChecksDto();
////                        vendorUploadChecksDto.setAgentColor(vendorChecksss.getAgentColor().getColorName());
////                        vendorUploadChecksDto.setVendorChecks(vendorChecksss.getVendorChecks().getVendorcheckId());
////                        vendorUploadChecksDto.setUserFirstName(user.getUserFirstName());
////                        vendorUploadChecksDto.setDocumentname(vendorChecksss.getDocumentname());
////                        vendorUploadChecksDto.setDocument(vendorChecksss.getVendorUploadedDocument());
////                        vendorUploadChecksDto.setColorHexCode(vendorChecksss.getAgentColor().getColorHexCode());
//                        //vendor attributes
//                        ArrayList<VendorAttributeDto> vendorAttributeDtos = new ArrayList<>();
//                        VendorAttributeDto vendorAttributeDto = new VendorAttributeDto();
//                        vendorAttributeDto.setSourceName(vendorChecksss.getVendorChecks().getSource().getSourceName());
//                        vendorAttributeDto.setVendorAttirbuteValue(vendorChecksss.getVendorAttirbuteValue());
//                        log.info("vendor attributes data" + vendorAttributeDto);
//                        vendorAttributeDtos.add(vendorAttributeDto);
//                        VendorUploadChecksDto vendorUploadChecksDto = new VendorUploadChecksDto(user.getUserFirstName(), vendorChecksss.getVendorChecks().getVendorcheckId(), vendorChecksss.getVendorUploadedDocument(), vendorChecksss.getDocumentname(), vendorChecksss.getAgentColor().getColorName(), vendorChecksss.getAgentColor().getColorHexCode(), null);
//                        vendorUploadChecksDto.setVendorAttirbuteValue(vendorAttributeDtos);
//                        vendordocDtoList.add(vendorUploadChecksDto);
//                        log.info("venord data" + vendorUploadChecksDto);
//                    }
//                }

                VendorUploadChecksDto vendorUploadChecksDto = null;
//                for (VendorChecks vendorChecks : vendorList) {
//                    User user = userRepository.findByUserId(vendorChecks.getVendorId());
//                    VendorUploadChecks vendorChecksss = vendorUploadChecksRepository
//                            .findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
//                    if (vendorChecksss != null) {
//                        vendorUploadChecksDto = new VendorUploadChecksDto(user.getUserFirstName(),
//                                vendorChecksss.getVendorChecks().getVendorcheckId(),
//                                vendorChecksss.getVendorUploadedDocument(), vendorChecksss.getDocumentname(),
//                                vendorChecksss.getAgentColor().getColorName(),
//                                vendorChecksss.getAgentColor().getColorHexCode(), null);
//                        vendordocDtoList.add(vendorUploadChecksDto);
//                    }
//                }

//
//
//                List<Source> sourceList = sourceRepository.findByIsActiveTrue();
//                if (sourceList.size() > 0) {
//                    sourceList.forEach(source -> {
//                        List<VendorChecks> vendorChecks =  vendorChecksRepository
//                                .findByCandidateCandidateIdAndSourceSourceId(candidate.getCandidateId(),
//                                        source.getSourceId());
//
//                        if (vendorChecks.size() > 0) {
//                            VendorChecks vendorCheck = vendorChecks.get(vendorChecks.size() - 1);
//                            VendorUploadChecks byVendorChecksVendorcheckId = vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorCheck.getVendorcheckId());
//                            log.info("vendor check agent attributes {}", byVendorChecksVendorcheckId.getVendorAttirbuteValue());
//                            Map<String, String> checkMap = new LinkedHashMap<>();
//
//                            VendorUploadChecks vendorUploadChecks = vendorUploadChecksRepository
//                                    .findByVendorChecksVendorcheckId(vendorCheck.getVendorcheckId());
//
//                            if (vendorUploadChecks != null) {
//                                byVendorChecksVendorcheckId.getVendorAttirbuteValue()
//                                        .addAll(vendorUploadChecks.getVendorAttirbuteValue());
//                            }
//
//                            byVendorChecksVendorcheckId.getVendorAttirbuteValue().forEach(temp -> {
//                                String[] arr = temp.split("=");
//                                checkMap.put(arr[0], arr[1]);
//                            });
//
//                            if (source.getSourceId() == Long.valueOf(3))
//                                candidateReportDTO.(checkMap);
//                            if (source.getSourceId() == Long.valueOf(6))
//                                candidateReportDTO.setCriminalCheck(checkMap);
//                            if (source.getSourceId() == Long.valueOf(2))
//                                candidateReportDTO.setEducationCheck(checkMap);
//                        }
//                    });
//                }

                //mycode
                for (VendorChecks vendorChecks : vendorList) {
                    User user = userRepository.findByUserId(vendorChecks.getVendorId());
                    VendorUploadChecks vendorChecksss = vendorUploadChecksRepository.findByVendorChecksVendorcheckId(vendorChecks.getVendorcheckId());
                    if (vendorChecksss != null) {
                        // Set vendor attributes
                        ArrayList<VendorAttributeDto> vendorAttributeDtos = new ArrayList<>();
                        VendorAttributeDto vendorAttributeDto = new VendorAttributeDto();
                        vendorAttributeDto.setSourceName(vendorChecksss.getVendorChecks().getSource().getSourceName());
                        vendorAttributeDto.setVendorAttirbuteValue(vendorChecksss.getVendorAttirbuteValue());
                        vendorAttributeDtos.add(vendorAttributeDto);
                        vendorUploadChecksDto = new VendorUploadChecksDto(user.getUserFirstName(), vendorChecksss.getVendorChecks().getVendorcheckId(), vendorChecksss.getVendorUploadedDocument(), vendorChecksss.getDocumentname(), vendorChecksss.getAgentColor().getColorName(), vendorChecksss.getAgentColor().getColorHexCode(), null);
//                        if (vendorUploadChecksDto.getDocument() != null) {
//                            File tempFile = File.createTempFile(vendorUploadChecksDto.getDocumentname(), ".pdf");
//                            FileOutputStream fos = new FileOutputStream(tempFile);
//                            fos.write(vendorUploadChecksDto.getDocument());
//                            fos.close();
//                            log.info("temp file" + tempFile);
//                        }
                        vendorUploadChecksDto.setVendorAttirbuteValue(vendorAttributeDtos);
                        vendordocDtoList.add(vendorUploadChecksDto);

                    }
                }
                candidateReportDTO.setVendorProofDetails(vendordocDtoList);
//                log.info("candidate report dtp" + candidateReportDTO.toString());
                log.info("after*****************update**************");
                Date createdOn = candidate.getCreatedOn();
                log.info("after *****************date**************");
                File report = FileUtil.createUniqueTempFile("report", ".pdf");
                String htmlStr = pdfService.parseThymeleafTemplateForConventionalCandidate("conventional_pdf", candidateReportDTO);

                pdfService.generatePdfFromHtml(htmlStr, report);
                List<Content> contentList = contentRepository.findAllByCandidateIdAndContentTypeIn(candidate.getCandidateId(), Arrays.asList(ContentType.ISSUED, ContentType.AGENT_UPLOADED));

                List<File> files = contentList.stream().map(content -> {
                    log.info("**************************");
                    File uniqueTempFile = FileUtil.createUniqueTempFile(candidateId + "_issued_" + content.getContentId().toString(), ".pdf");
                    awsUtils.getFileFromS3(content.getBucketName(), content.getPath(), uniqueTempFile);
                    return uniqueTempFile;
                }).collect(Collectors.toList());

                List<String> vendorFilesURLs_paths = vendordocDtoList.stream().filter(vendor -> vendor.getDocument() != null && vendor.getDocument().length > 0).map(vendor -> {
                    byte[] data = vendor.getDocument();
                    Random rand = new Random();
                    long random12Digits = 100000000000L + (long) (rand.nextDouble() * 900000000000L);
                    System.out.println(random12Digits);
                    String vendorFilesTemp = "Candidate/".concat(new Date().toString()).concat(candidateId + "/Generated" + random12Digits);
                    log.info("vendortemp file Path" + vendorFilesTemp);
                    String s = awsUtils.uploadFileAndGetPresignedUrl_bytes(DIGIVERIFIER_DOC_BUCKET_NAME, vendorFilesTemp, data);
                    return vendorFilesTemp;
                }).collect(Collectors.toList());

                List<File> vendorfiles = vendorFilesURLs_paths.stream().map(content -> {
                    log.info("**************************");
                    File uniqueTempFile = FileUtil.createUniqueTempFile(content, ".pdf");
                    awsUtils.getFileFromS3(DIGIVERIFIER_DOC_BUCKET_NAME, content, uniqueTempFile);
                    return uniqueTempFile;
                }).collect(Collectors.toList());

                try {
//                    log.info("entry to generate try*************************");
                    File mergedFile = FileUtil.createUniqueTempFile(String.valueOf(candidateId), ".pdf");
                    List<InputStream> collect = new ArrayList<>();
                    collect.add(FileUtil.convertToInputStream(report));
                    collect.addAll(files.stream().map(FileUtil::convertToInputStream).collect(Collectors.toList()));
                    collect.addAll(vendorfiles.stream().map(FileUtil::convertToInputStream).collect(Collectors.toList()));
//                    vendordocDtoList = vendordocDtoList.stream().filter(p -> p.getDocument() != null).collect(Collectors.toList());
//                    vendordocDtoList.forEach(vendor -> {
//                        collect.add(new ByteArrayInputStream(vendor.getDocument()));
//                    });
                    PdfUtil.mergePdfFiles(collect, new FileOutputStream(mergedFile.getPath()));
                    log.info("merged file path" + mergedFile.getPath());
                    String path = "Candidate/".concat(candidateId + "/Generated".concat("/").concat(reportType.name()).concat(".pdf"));
                    String pdfUrl = awsUtils.uploadFileAndGetPresignedUrl(DIGIVERIFIER_DOC_BUCKET_NAME, path, mergedFile);
                    Content content = new Content();
                    content.setCandidateId(candidate.getCandidateId());
                    content.setContentCategory(ContentCategory.OTHERS);
                    content.setContentSubCategory(ContentSubCategory.PRE_APPROVAL);
                    // log.info(content+"*******************************************content");
                    if (reportType.name().equalsIgnoreCase("PRE_OFFER")) {
                        content.setContentSubCategory(ContentSubCategory.PRE_APPROVAL);
                    } else if (reportType.name().equalsIgnoreCase("FINAL")) {
                        content.setContentSubCategory(ContentSubCategory.FINAL);
                    }
                    content.setFileType(FileType.PDF);
                    content.setContentType(ContentType.GENERATED);
                    content.setBucketName(DIGIVERIFIER_DOC_BUCKET_NAME);
                    content.setCreatedOn(new Date());
                    content.setPath(path);
//                    if (update.equalsIgnoreCase("UPDATE")) {
//                        log.info("report time updated ");
//                        content.setLastUpdatedOn(new Date());
//                    }
                    contentRepository.save(content);
//                    String reportTypeStr = reportType.label;
//                    Email email = new Email();
//                    email.setSender(emailProperties.getDigiverifierEmailSenderId());
//                    User agent = candidate.getCreatedBy();
//                    email.setReceiver(agent.getUserEmailId());
//                    email.setTitle("DigiVerifier " + reportTypeStr + " report - " + candidate.getCandidateName());
//
//                    email.setAttachmentName(candidateCode + " " + reportTypeStr + ".pdf");
//                    email.setAttachmentFile(mergedFile);

//                email.setContent(String.format(emailContent, agent.getUserFirstName(), candidate.getCandidateName(), reportTypeStr));
//                emailSentTask.send(email);
                    // delete files
                    files.stream().forEach(file -> file.delete());

                    stringServiceOutcome.setData(pdfUrl);
                    mergedFile.delete();
                    report.delete();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                log.info("enter else");
                throw new RuntimeException("unable to generate document for this candidate");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return stringServiceOutcome;
    }


    @Transactional
    public ServiceOutcome<String> generateJsonRepsonseByConventionalCandidateId(String requestId, ReportType reportType, String update) {

        ServiceOutcome<String> listServiceOutcome = new ServiceOutcome<>();
        List<liReportDetails> liReportDetails = new ArrayList<>();
        try {
            ArrayList<UpdateSubmittedCandidatesResponseDto> updateSubmittedCandidatesResponseDtos = new ArrayList<>();
            com.aashdit.digiverifier.vendorcheck.dto.liReportDetails liReportDetails1 = new liReportDetails();
            ConventionalVendorCandidatesSubmitted conventinalCandidate = conventionalCandidatesSubmittedRepository.findByRequestId(requestId);
            updateCandidateVerificationStatus(requestId);
            UpdateSubmittedCandidatesResponseDto conventionalVendorCandidatesSubmitted = new UpdateSubmittedCandidatesResponseDto();
            conventionalVendorCandidatesSubmitted.setCandidateID(String.valueOf(conventinalCandidate.getCandidateId()));
            conventionalVendorCandidatesSubmitted.setName(conventinalCandidate.getName());
            conventionalVendorCandidatesSubmitted.setPSNO(conventinalCandidate.getPsNo());
            conventionalVendorCandidatesSubmitted.setRequestID(conventinalCandidate.getRequestId());
            conventionalVendorCandidatesSubmitted.setVendorName(conventinalCandidate.getVendorId());
            List<liChecksDetails> allLiCheckResponseByCandidateId = liCheckToPerformRepository.findAllUpdateLiCheckResponseByRequestId(String.valueOf(conventionalVendorCandidatesSubmitted.getRequestID()));
//            log.info("licheck data" + allLiCheckResponseByCandidateId);
            allLiCheckResponseByCandidateId.stream().forEach(lichec -> {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = dateFormat.format(new Date());
                lichec.setCompletedDate(formattedDate.toString());
                if (lichec.getModeOfVerficationPerformed() == null) {
                    lichec.setModeOfVerficationPerformed("3");
                }
            });
//            List<liChecksDetails> collect = allLiCheckResponseByCandidateId.stream().filter(licheck -> licheck.getCheckStatus() != 7l && licheck.getCheckStatus() != 2l && licheck.getModeOfVerficationPerformed() != null).collect(Collectors.toList());
//            log.info("licheck data" + allLiCheckResponseByCandidateId);
            List<liChecksDetails> collect = allLiCheckResponseByCandidateId.stream().filter(licheck -> (licheck.getCheckStatus() == 1l || licheck.getCheckStatus() == 2l) && licheck.getModeOfVerficationPerformed() != null).collect(Collectors.toList());
            Candidate candidate = candidateRepository.findByConventionalRequestId(Long.valueOf(conventionalVendorCandidatesSubmitted.getRequestID()));
            ServiceOutcome<String> stringServiceOutcome = generateConventionalCandidateReport(candidate.getCandidateId(), reportType, update);
            String reportUploadedPrecisedUrl = stringServiceOutcome.getData();
            if (reportUploadedPrecisedUrl != null) {
                listServiceOutcome.setData(reportUploadedPrecisedUrl);
                List<Content> allByCandidateId = contentRepository.findAllByCandidateId(candidate.getCandidateId());
                allByCandidateId.forEach(content -> {
                    String bucketName = content.getBucketName();
                    String path = content.getPath();
                    String[] split = path.split("/");
                    String filename = split[split.length - 1];
                    String fileExtension = filename.substring(filename.length() - 4, filename.length());
                    liReportDetails1.setReportFileExtention(fileExtension);
                    liReportDetails1.setReportFileName(filename);
                    try {
                        byte[] bytes = awsUtils.getbyteArrayFromS3(bucketName, path);
                        String base64String = Base64.getEncoder().encodeToString(bytes);
                        liReportDetails1.setReportAttachment(base64String);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                });

            }

            ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted1 = conventionalCandidatesSubmittedRepository.findByRequestId(conventionalVendorCandidatesSubmitted.getRequestID());

            if (reportType.label.equalsIgnoreCase("INTERIM")) {
                liReportDetails1.setReportType("1");
            }
            if (reportType.label.equalsIgnoreCase("FINAL")) {
                liReportDetails1.setReportType("3");
            }
            if (reportType.label.equalsIgnoreCase("Supplimentry")) {
                liReportDetails1.setReportType("2");
            }

            if (conventionalVendorCandidatesSubmitted1.getVerificationStatus().equalsIgnoreCase("CLEAR")) {
                liReportDetails1.setReportStatus("1");
            }

            if (conventionalVendorCandidatesSubmitted1.getVerificationStatus().equalsIgnoreCase("INPROGRESS")) {
                liReportDetails1.setReportStatus("2");
            }

            if (conventionalVendorCandidatesSubmitted1.getVerificationStatus().equalsIgnoreCase("INSUFFICIENCY")) {
                liReportDetails1.setReportStatus("3");
            }
            if (conventionalVendorCandidatesSubmitted1.getVerificationStatus().equalsIgnoreCase("MAJORDISCREPANCY")) {
                liReportDetails1.setReportStatus("4");
            }

            if (conventionalVendorCandidatesSubmitted1.getVerificationStatus().equalsIgnoreCase("MINORDISCREPANCY")) {
                liReportDetails1.setReportStatus("5");
            }

            if (conventionalVendorCandidatesSubmitted1.getVerificationStatus().equalsIgnoreCase("UNABLETOVERIFIY")) {
                liReportDetails1.setReportStatus("6");
            }

            liReportDetails1.setVendorReferenceID(String.valueOf(conventionalVendorCandidatesSubmitted1.getApplicantId()));
            liReportDetails.add(liReportDetails1);
            conventionalVendorCandidatesSubmitted.setLiReportDetails(liReportDetails);
            conventionalVendorCandidatesSubmitted.setLiChecksDetails(collect);
            updateSubmittedCandidatesResponseDtos.add(conventionalVendorCandidatesSubmitted);

//            listServiceOutcome.setData(conventionalVendorCandidatesSubmitted);
            //hitting the update request to third party api
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", environmentVal.getMtGrantType());
            map.add("username", environmentVal.getMtUsername());
            map.add("password", environmentVal.getMtPassword());

            HttpHeaders tokenHeader = new HttpHeaders();
            tokenHeader.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            ResponseEntity<String> responseEntity = null;
            HttpEntity<MultiValueMap<String, String>> requestBodyFormUrlEncoded = new HttpEntity<>(map, tokenHeader);
            responseEntity = restTemplate.postForEntity(environmentVal.getConventionalVendorToken(), requestBodyFormUrlEncoded, String.class);
            JSONObject tokenObject = new JSONObject(responseEntity.getBody());
            String access_token = tokenObject.getString("access_token");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + access_token);
            headers.set("Content-Type", "application/json");
            headers.setContentType(MediaType.APPLICATION_JSON);
//            String acKnoledgementUrl = "https://LTIiVerifyTestAPI.azurewebsites.net/VendorUpdateService/UpdateBGVRequestAcknowledgement";
//
//            ConventionalVendorCandidatesSubmitted conventionalCandidate = conventionalCandidatesSubmittedRepository.findById(Long.valueOf(conventionalVendorCandidatesSubmitted.getCandidateID())).get();
//
//            AcknoledgementDto acknoledgementDto = new AcknoledgementDto();
//            acknoledgementDto.setCandidateID(String.valueOf(conventionalCandidate.getCandidateId()));
//            acknoledgementDto.setPSNO(conventionalVendorCandidatesSubmitted.getPSNO());
//            acknoledgementDto.setRequestID(conventionalCandidate.getRequestId());
//            acknoledgementDto.setVENDORID(conventionalCandidate.getVendorId());
//            acknoledgementDto.setVendorReferenceID(String.valueOf(conventionalCandidate.getApplicantId()));
//
//            ArrayList<AcknoledgementDto> acknoledgementDtos = new ArrayList<>();
//            acknoledgementDtos.add(acknoledgementDto);
//            HttpEntity<List<AcknoledgementDto>> acknoledgementDtoHttpEntity = new HttpEntity<>(acknoledgementDtos, headers);
//            ResponseEntity<String> acknoledgementData = restTemplate.exchange(acKnoledgementUrl, HttpMethod.POST, acknoledgementDtoHttpEntity, String.class);
//            log.info("Acknoledged api");
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
//            UpdateSubmittedCandidatesResponseDto candidateDetails = objectMapper.readValue("", UpdateSubmittedCandidatesResponseDto.class);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

//
//            Object jsonObj = objectMapper.readValue((DataInput) updateSubmittedCandidatesResponseDtos, Object.class);
//
            if (update.equalsIgnoreCase("UPDATE")) {

                HttpEntity<List<UpdateSubmittedCandidatesResponseDto>> liCheckDtoHttpEntity = new HttpEntity<>(updateSubmittedCandidatesResponseDtos, headers);
                ResponseEntity<String> icheckRepsonse = restTemplate.exchange(environmentVal.getConventionalUpdateBgvCheckStatusRowwise(), HttpMethod.POST, liCheckDtoHttpEntity, String.class);
//                log.info("Response from lICheck response  API " + icheckRepsonse);
                listServiceOutcome.setMessage(icheckRepsonse.getBody());
                if (reportType.label.equalsIgnoreCase("INTERIM")) {
                    StatusMaster interimreport = statusMasterRepository.findByStatusCode("INTERIMREPORT");
                    log.info("interim updated");
                    conventionalVendorCandidatesSubmitted1.setStatus(interimreport);
                    ConventionalVendorCandidatesSubmitted updatedToInterim = conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted1);
                }

                if (reportType.label.equalsIgnoreCase("FINAL")) {
                    StatusMaster interimreport = statusMasterRepository.findByStatusCode("FINALREPORT");
                    log.info("final report updated");
                    conventionalVendorCandidatesSubmitted1.setStatus(interimreport);
                    ConventionalVendorCandidatesSubmitted updatedToInterim = conventionalCandidatesSubmittedRepository.save(conventionalVendorCandidatesSubmitted1);
                }
            }
            listServiceOutcome.setOutcome(true);
        } catch (Exception e) {
            listServiceOutcome.setOutcome(false);
            listServiceOutcome.setMessage(" Interim Report Generation Failed");
            log.error(e.getMessage());
        }
        return listServiceOutcome;


    }


    public ServiceOutcome<ConventionalVendorCandidatesSubmitted> findConventionalCandidateByCandidateId(Long requestId) {
        ServiceOutcome<ConventionalVendorCandidatesSubmitted> conventionalVendorCandidatesSubmittedServiceOutcome = new ServiceOutcome<>();
        try {
            boolean b = conventionalCandidatesSubmittedRepository.existsByRequestId(String.valueOf(requestId));
            if (b == true) {
                ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(String.valueOf(requestId));
                FetchVendorConventionalCandidateDto fetchVendorConventionalCandidateDto = new FetchVendorConventionalCandidateDto(conventionalVendorCandidatesSubmitted.getRequestId(), String.valueOf(conventionalVendorCandidatesSubmitted.getCandidateId()), conventionalVendorCandidatesSubmitted.getPsNo(), conventionalVendorCandidatesSubmitted.getVendorId());
                addConvetionalCandidateData(conventionalVendorCandidatesSubmitted.getRequestId());
                addUpdateLiCheckToPerformData(fetchVendorConventionalCandidateDto);
                conventionalVendorCandidatesSubmittedServiceOutcome.setData(conventionalVendorCandidatesSubmitted);
                conventionalVendorCandidatesSubmittedServiceOutcome.setOutcome(true);
            }

        } catch (Exception e) {
            conventionalVendorCandidatesSubmittedServiceOutcome.setData(null);
            conventionalVendorCandidatesSubmittedServiceOutcome.setOutcome(false);
            log.error("in findConventionalCandidateByCandidateId " + e.getMessage());
        }
        return conventionalVendorCandidatesSubmittedServiceOutcome;
    }

    @Autowired
    CandidateVerificationStateRepository candidateVerificationStateRepository;
    @Autowired
    VendorMasterNewRepository vendorMasterNewRepository;

    @Transactional
    public ServiceOutcome<List<ReportUtilizationDto>> generateJsonResponse() throws Exception {

        ServiceOutcome<List<ReportUtilizationDto>> serviceOutcome = new ServiceOutcome<>();

        ArrayList<ReportUtilizationDto> reportUtilizationDtos = new ArrayList<>();
        try {
            List<VendorChecks> all = vendorChecksRepository.findAllVendorChecksInVendorUploadChecks();
            List<Long> vendorIdList = new ArrayList<>();
            for (VendorChecks vendorChecks : all) {
                ReportUtilizationDto reportUtilizationDto = new ReportUtilizationDto();
                Long vendorId = vendorChecks.getVendorId();
                reportUtilizationDto.setVendorId(vendorId);
                User user = userRepository.findById(vendorId).get();
                reportUtilizationDto.setVendorName(user.getUserFirstName());
                Long vendorcheckId = vendorChecks.getVendorcheckId();
                VendorChecks byVendorcheckId = vendorChecksRepository.findByVendorcheckId(vendorcheckId);
                User caseInitatedBy = userRepository.findById(byVendorcheckId.getCreatedBy().getUserId()).get();
                reportUtilizationDto.setCaseInititatedBy(caseInitatedBy.getUserName());
                ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(String.valueOf(byVendorcheckId.getCandidate().getConventionalRequestId()));
                reportUtilizationDto.setUrnRefNo(String.valueOf(conventionalVendorCandidatesSubmitted.getApplicantId()));
                reportUtilizationDto.setCandidateName(byVendorcheckId.getCandidateName());
                reportUtilizationDto.setCaseAssignedDate(String.valueOf(byVendorcheckId.getCreatedOn()));
                reportUtilizationDto.setReportCode(conventionalVendorCandidatesSubmitted.getVerificationStatus());
                reportUtilizationDto.setCheckName(byVendorcheckId.getSource().getSourceName());
                CandidateVerificationState canidateVerificationData = candidateVerificationStateRepository.findByCandidateCandidateId(byVendorcheckId.getCandidate().getCandidateId());
                if (canidateVerificationData != null) {
                    reportUtilizationDto.setReportSubmittedDate(String.valueOf(canidateVerificationData.getInterimReportTime()));
                }
                vendorIdList.add(vendorId);
                reportUtilizationDtos.add(reportUtilizationDto);
            }
            for (ReportUtilizationDto reportUtilizationDto : reportUtilizationDtos) {
                for (Long vendorId : vendorIdList) {
                    List<VendorChecks> allGroupByVendorCheckId = vendorChecksRepository.findAllGroupByVendorCheckId(vendorId);
                    if (reportUtilizationDto.getVendorId().equals(vendorId)) {
                        ArrayList<ChecksDto> checksDtos = new ArrayList<>();
                        for (VendorChecks checks : allGroupByVendorCheckId) {
                            ConventionalVendorliChecksToPerform byVendorChecksVendorcheckId = liCheckToPerformRepository.findByVendorChecksVendorcheckId(checks.getVendorcheckId());
                            ChecksDto checksDto = new ChecksDto();
                            checksDto.setColorCode(checks.getVendorCheckStatusMaster().getCheckStatusCode());
                            checksDto.setCourceName(checks.getSource().getSourceName());
                            VendorMasterNew byVendorId = vendorMasterNewRepository.findByVendorId(checks.getVendorId());
                            if (byVendorId != null) {
                                checksDto.setPerUnitPrice(byVendorId.getRatePerItem());
                                checksDto.setQuantity((long) allGroupByVendorCheckId.size());
                                checksDto.setTotalCode(String.valueOf(checksDto.getPerUnitPrice() * checksDto.getQuantity()));
                            }
                            checksDtos.add(checksDto);
                        }
                        reportUtilizationDto.setChecksDtos(checksDtos);
                    }

                }
                serviceOutcome.setData(reportUtilizationDtos);
            }
            // Create a new workbook
            Workbook workbook = new XSSFWorkbook();
            // Create a new sheet
            Sheet sheet = workbook.createSheet("Report Excel Data");
            sheet.setDefaultColumnWidth(12);
            // candidate cell style
//            int desiredCellWidth = 10;
            CellStyle headerCellStyle = workbook.createCellStyle();
//            headerCellStyle.setShrinkToFit(true);
            headerCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontName("Arial");
            headerFont.setFontHeightInPoints((short) 10);
            headerCellStyle.setFont(headerFont);
            //licheck cellstyle
            CellStyle headerCellStyle2 = workbook.createCellStyle();
            headerCellStyle2.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerCellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont2 = workbook.createFont();
            headerFont2.setBold(true);
            headerFont2.setFontName("Arial");
            headerFont.setFontHeightInPoints((short) 10);
            headerCellStyle2.setFont(headerFont);
            //for the lichek heading -leaving the row


            List<ReportUtilizationDto> data = serviceOutcome.getData();
            int startcellno = 7;
            int endcellNo = 10;
            Row headerRow1 = sheet.createRow(3);
            for (ReportUtilizationDto datum : data) {
                ReportUtilizationDto reportUtilizationDto1 = datum;
                for (ChecksDto checksDto : reportUtilizationDto1.getChecksDtos()) {
                    for (int j = 0; j < reportUtilizationDto1.getChecksDtos().size(); j++) {
                        // Merge cells for office data
                        CellRangeAddress officeDataMergeRegion = new CellRangeAddress(3, 3, startcellno, endcellNo);
                        sheet.addMergedRegion(officeDataMergeRegion);
                        // Place data within merged region
                        List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
                        for (CellRangeAddress region : mergedRegions) {
                            if (region.equals(officeDataMergeRegion)) {
                                for (int row = region.getFirstRow(); row <= region.getLastRow(); row++) {
                                    Row mergedRow = sheet.getRow(row);
                                    Cell mergedCell = mergedRow.getCell(region.getFirstColumn());
                                    if (mergedCell == null) {
                                        mergedCell = mergedRow.createCell(region.getFirstColumn());
                                    }
                                    mergedCell.setCellValue(reportUtilizationDto1.getCheckName());

                                }
                            }
                        }

                        startcellno = startcellno + 5;
                        endcellNo = endcellNo + 5;
                    }
                }
            }


            // Create a header row
            Row headerRow = sheet.createRow(4);
            headerRow.createCell(0).setCellValue("Vendor Name");
            headerRow.createCell(1).setCellValue("Case Initiated by (DigiVerifier Spoc))");
            headerRow.createCell(2).setCellValue("URN / Ref No.");
            headerRow.createCell(3).setCellValue("Candidate Name");
            headerRow.createCell(4).setCellValue("Case Assigned Date");
            headerRow.createCell(5).setCellValue("Report Submitted Date");
            headerRow.createCell(6).setCellValue("Report Color Code");
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                cell.setCellStyle(headerCellStyle);
            }
            int rowNum = 5;


            for (ReportUtilizationDto datum : serviceOutcome.getData()) {

                Row dataRow = sheet.createRow(rowNum);
                rowNum++;
                dataRow.createCell(0).setCellValue(datum.getVendorName());
                dataRow.createCell(1).setCellValue(datum.getCaseInititatedBy());
                dataRow.createCell(2).setCellValue(datum.getUrnRefNo());
                dataRow.createCell(3).setCellValue(datum.getCandidateName());
                dataRow.createCell(4).setCellValue(datum.getCaseAssignedDate());
                dataRow.createCell(5).setCellValue(datum.getReportSubmittedDate());
                dataRow.createCell(6).setCellValue(datum.getReportCode());

                if (datum.getChecksDtos() != null) {
                    headerRow.createCell(7).setCellValue("Course Name");
                    headerRow.createCell(8).setCellValue("Qty");
                    headerRow.createCell(9).setCellValue("Price Per Unit");
                    headerRow.createCell(10).setCellValue("Color Code");
                    headerRow.createCell(11).setCellValue("Total Amount");
                    for (int i = 7; i < headerRow.getLastCellNum(); i++) {
                        Cell cell = headerRow.getCell(i);
                        cell.setCellStyle(headerCellStyle2);
                    }
                    for (int i = 1; i <= datum.getChecksDtos().size(); i++) {
                        if (i == 1) {
                            ChecksDto checksDto = datum.getChecksDtos().get(i - 1);
                            dataRow.createCell(7).setCellValue(checksDto.getCourceName());
                            dataRow.createCell(8).setCellValue(checksDto.getQuantity());
                            dataRow.createCell(9).setCellValue(checksDto.getPerUnitPrice());
                            dataRow.createCell(10).setCellValue(checksDto.getColorCode());
                            dataRow.createCell(11).setCellValue(checksDto.getTotalCode());
                        }
                        if (i != 1) {

                            Row dataRow1 = sheet.createRow(rowNum++);
                            ChecksDto checksDto = datum.getChecksDtos().get(i - 1);
                            dataRow1.createCell(7).setCellValue(checksDto.getCourceName());
                            dataRow1.createCell(8).setCellValue(checksDto.getQuantity());
                            dataRow1.createCell(9).setCellValue(checksDto.getPerUnitPrice());
                            dataRow1.createCell(10).setCellValue(checksDto.getColorCode());
                            dataRow1.createCell(11).setCellValue(checksDto.getTotalCode());

                        }

                    }


                }


            }
// Auto-size columns
            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            File nanda = FileUtil.createUniqueTempFile("nanda", ".xlsx");

            FileOutputStream fileOutputStream = new FileOutputStream(nanda);
            workbook.write(fileOutputStream);
            byte[] fileContent = Files.readAllBytes(Paths.get(nanda.getAbsolutePath()));
            String base64String = Base64.getEncoder().encodeToString(fileContent);
//            log.info(base64String);
            ;
//            reportUtilizationDtos.get(0).setExcelBase64(base64String);
            serviceOutcome.setMessage(base64String);
        } catch (Exception e) {
            log.error("error in generate response  :" + e.getMessage());
        }
        return serviceOutcome;
    }


    @Autowired
    ConventionalCandidateReferenceInfoRepository conventionalCandidateReferenceInfoRepository;

    @Autowired
    ConventionalCandidateExperienceRepository conventionalCandidateExperienceRepository;
    @Autowired
    ConventionalCafCandidateEducationRepository conventionalCafCandidateEducationRepository;
    @Autowired
    ConventionCafAddressRepository conventionCafAddressRepository;
    @Autowired
    CandidateCafExperienceRepository candidateCafExperienceRepository;

    @Autowired
    CandidateCafEducationRepository candidateCafEducationRepository;

    //not touched
    @Transactional
    public ServiceOutcome<List<VendorReferenceDataDto>> generateReferenceDataToVendor(Long candidateId, Long checkSourceId) throws Exception {
        ServiceOutcome<List<VendorReferenceDataDto>> serviceOutcome = new ServiceOutcome<>();
        try {
            ArrayList<VendorReferenceDataDto> vendorReferenceDataDtos = new ArrayList<>();
            VendorChecks vc = vendorChecksRepository.findByCandidateIdAndSourceID(candidateId, checkSourceId);
            String sourceName = vc.getSource().getSourceName();
            String[] words = sourceName.split(" ");
            Stream<String> wordStream = Arrays.stream(words);
            if (wordStream.anyMatch(p -> p.equalsIgnoreCase("REFERENCE"))) {
                Boolean aBoolean = conventionalCandidateReferenceInfoRepository.existsByConventionalCandiateId(vc.getCandidate().getConventionalCandidateId());
                if (true) {
                    List<ConventionalCandidateReferenceInfo> byConventionalCandiateId = conventionalCandidateReferenceInfoRepository.findByConventionalCandiateId(vc.getCandidate().getConventionalCandidateId());
                    ArrayList<ConventionalCandidateReferenceDto> candidateReferenceDtos = new ArrayList<>();
                    byConventionalCandiateId.forEach(data -> {
                        ConventionalCandidateReferenceDto candidateReferenceDto = new ConventionalCandidateReferenceDto();
//                        candidateReferenceDto.setReferenceId(data.getReferenceId());
                        candidateReferenceDto.setReferenceNumber(data.getReferenceNumber());
                        candidateReferenceDto.setProfessionalRelation(data.getProfessionalRelation());
                        candidateReferenceDto.setName(data.getName());
                        candidateReferenceDto.setDesignation(data.getDesignation());
                        candidateReferenceDto.setCompanyName(data.getCompanyName());
//                        candidateReferenceDto.setContactNumber(data.getContactNumber());
//                        candidateReferenceDto.setEmailId(data.getEmailId());
                        candidateReferenceDto.setInsufficiencyRemarks(data.getInsufficiencyRemarks());
                        candidateReferenceDto.setDurationKnown(data.getDurationKnown());
                        candidateReferenceDtos.add(candidateReferenceDto);
                    });
                    VendorReferenceDataDto vendorReferenceDataDto = new VendorReferenceDataDto();
                    vendorReferenceDataDto.setContactNumber(vc.getCandidate().getContactNumber());
                    vendorReferenceDataDto.setDateOfBirth(vc.getCandidate().getDateOfBirth());
                    vendorReferenceDataDto.setEmailId(vc.getCandidate().getEmailId());
                    ConventionalCandidate byConventionalRequestId = conventionalCandidateRepository.findByConventionalRequestId(vc.getCandidate().getConventionalRequestId());
                    vendorReferenceDataDto.setFatherName(byConventionalRequestId.getFatherName());
                    vendorReferenceDataDto.setGender(byConventionalRequestId.getGender());
                    vendorReferenceDataDto.setCandidateId(String.valueOf(vc.getCandidate().getConventionalCandidateId()));
                    vendorReferenceDataDto.setCheckName(vc.getSource().getSourceName());
                    vendorReferenceDataDto.setVendorReferenceData(candidateReferenceDtos);
                    vendorReferenceDataDtos.add(vendorReferenceDataDto);

                }

            } else if (sourceName.contains("EMPLOYMENT")) {
                Boolean aBoolean = conventionalCandidateExperienceRepository.existsByConventionalCandidateId(vc.getCandidate().getConventionalCandidateId());
                if (true) {
                    List<ConventionalCandidateExperience> byConventionalCandidateId = conventionalCandidateExperienceRepository.findByConventionalCandidateId(vc.getCandidate().getConventionalCandidateId());
                    ArrayList<ConventionalExperienceDto> candidateCafExperienceDtos = new ArrayList<>();
                    byConventionalCandidateId.forEach(data -> {
                        ConventionalExperienceDto convetionalExperienceDto = new ConventionalExperienceDto();
                        convetionalExperienceDto.setSuperiorName(data.getSuperiorName());
                        convetionalExperienceDto.setDesignation(data.getDesignation());
                        convetionalExperienceDto.setDuration(data.getDuration());
                        convetionalExperienceDto.setSuperiorDesignation(data.getSuperiorDesignation());
                        convetionalExperienceDto.setSuperiorEmailID(data.getSuperiorEmailID());
                        convetionalExperienceDto.setEmployeeCode(data.getEmployeeCode());
                        convetionalExperienceDto.setEmploymentType(data.getEmploymentType());
                        convetionalExperienceDto.setGrossSalary(data.getGrossSalary());
                        convetionalExperienceDto.setHrContactNumber(data.getHrContactNumber());
                        convetionalExperienceDto.setHrEmailId(data.getHrEmailId());
                        convetionalExperienceDto.setHrName(data.getHrName());
                        convetionalExperienceDto.setInsufficiencyRemarks(data.getInsufficiencyRemarks());
                        convetionalExperienceDto.setLastSalary(data.getLastSalary());
                        convetionalExperienceDto.setSuperiorContactNumber(data.getSuperiorContactNumber());
                        convetionalExperienceDto.setSuperiorName(data.getSuperiorName());
                        Optional<CandidateCafExperience> byId = candidateCafExperienceRepository.findById(data.getCandidateCafExperience());
                        if (byId.isPresent()) {
                            convetionalExperienceDto.setCandidateEmployerName(byId.get().getCandidateEmployerName());
                            convetionalExperienceDto.setInputDateOfJoining(byId.get().getInputDateOfJoining());
                            convetionalExperienceDto.setInputDateOfExit(byId.get().getInputDateOfExit());
                        }
                        candidateCafExperienceDtos.add(convetionalExperienceDto);
                    });


                    VendorReferenceDataDto vendorReferenceDataDto = new VendorReferenceDataDto();
                    vendorReferenceDataDto.setContactNumber(vc.getCandidate().getContactNumber());
                    vendorReferenceDataDto.setDateOfBirth(vc.getCandidate().getDateOfBirth());
                    vendorReferenceDataDto.setEmailId(vc.getCandidate().getEmailId());
                    ConventionalCandidate byConventionalRequestId = conventionalCandidateRepository.findByConventionalRequestId(vc.getCandidate().getConventionalRequestId());
                    vendorReferenceDataDto.setFatherName(byConventionalRequestId.getFatherName());
                    vendorReferenceDataDto.setGender(byConventionalRequestId.getGender());
                    vendorReferenceDataDto.setCandidateId(String.valueOf(vc.getCandidate().getConventionalCandidateId()));
                    vendorReferenceDataDto.setCheckName(vc.getSource().getSourceName());
                    vendorReferenceDataDto.setVendorReferenceData(candidateCafExperienceDtos);
                    vendorReferenceDataDtos.add(vendorReferenceDataDto);
                }

            } else if (sourceName.contains("EDUCATION")) {
                Boolean aBoolean = conventionalCafCandidateEducationRepository.existsByConventionalCandidateId(vc.getCandidate().getConventionalCandidateId());
                if (true) {
                    List<ConventionalCandidateCafEducation> byConventionalCandidateId = conventionalCafCandidateEducationRepository.findByConventionalCandidateId(vc.getCandidate().getConventionalCandidateId());
                    ArrayList<ConventionalEducationDto> candidateCafEducationDtos = new ArrayList<>();
                    byConventionalCandidateId.forEach(data -> {
                        ConventionalEducationDto conventionalEducationDto = new ConventionalEducationDto();
                        conventionalEducationDto.setDegreeType(data.getDegreeType());
                        conventionalEducationDto.setEducationType(data.getEducationType());
                        conventionalEducationDto.setEndDate(data.getEndDate());
                        conventionalEducationDto.setStartDate(data.getStartDate());
                        conventionalEducationDto.setInsufficiecyRemarks(data.getInsufficiencyRemarks());
                        Optional<CandidateCafEducation> byId = candidateCafEducationRepository.findById(data.getCandidateCafEducationId());
                        if (byId.isPresent()) {
                            conventionalEducationDto.setSchoolOrCollegeName(byId.get().getSchoolOrCollegeName());
                            conventionalEducationDto.setQualificationName(byId.get().getQualificationMaster().getQualificationName());
                            conventionalEducationDto.setBoardOrUniversityName(byId.get().getBoardOrUniversityName());
                        }
                        candidateCafEducationDtos.add(conventionalEducationDto);
                    });
                    VendorReferenceDataDto vendorReferenceDataDto = new VendorReferenceDataDto();
                    vendorReferenceDataDto.setContactNumber(vc.getCandidate().getContactNumber());
                    vendorReferenceDataDto.setDateOfBirth(vc.getCandidate().getDateOfBirth());
                    vendorReferenceDataDto.setEmailId(vc.getCandidate().getEmailId());
                    ConventionalCandidate byConventionalRequestId = conventionalCandidateRepository.findByConventionalRequestId(vc.getCandidate().getConventionalRequestId());
                    vendorReferenceDataDto.setFatherName(byConventionalRequestId.getFatherName());
                    vendorReferenceDataDto.setGender(byConventionalRequestId.getGender());
                    vendorReferenceDataDto.setCandidateId(String.valueOf(vc.getCandidate().getConventionalCandidateId()));
                    vendorReferenceDataDto.setCheckName(vc.getSource().getSourceName());
                    vendorReferenceDataDto.setVendorReferenceData(candidateCafEducationDtos);
                    vendorReferenceDataDtos.add(vendorReferenceDataDto);
                }


            } else if (sourceName.contains("ADDRESS")) {
                Boolean aBoolean = conventionCafAddressRepository.existsByConventionalCandidateId(vc.getCandidate().getConventionalCandidateId());


                if (true) {
                    List<ConventionalCafAddress> byConventionalCandidateId = conventionCafAddressRepository.findByConventionalCandidateId(vc.getCandidate().getConventionalCandidateId());
                    ArrayList<ConventionalAddressDto> conventionalAddressDtos = new ArrayList<>();

                    byConventionalCandidateId.forEach(data -> {
                        ConventionalAddressDto conventionalAddressDto = new ConventionalAddressDto();
                        conventionalAddressDto.setAddressType(data.getAddressType());
                        conventionalAddressDto.setInsufficiencyRemarks(data.getInsufficiencyRemarks());
                        conventionalAddressDto.setStayToDate(data.getStayToDate());
                        conventionalAddressDto.setStayFromDate(data.getStayFromDate());
                        conventionalAddressDto.setHouseType(data.getHouseType());
                        Optional<CandidateCafAddress> byId = candidateCafAddressRepository.findById(data.getCandidateCafAddressId());
                        if (byId.isPresent()) {
                            conventionalAddressDto.setPincode(String.valueOf(byId.get().getPinCode()));
                            conventionalAddressDto.setCandidateAddress(byId.get().getCandidateAddress());
                            conventionalAddressDto.setCity(byId.get().getCity());
                            conventionalAddressDto.setState(byId.get().getState());

                        }
                        conventionalAddressDtos.add(conventionalAddressDto);

                    });

                    VendorReferenceDataDto vendorReferenceDataDto = new VendorReferenceDataDto();
                    vendorReferenceDataDto.setContactNumber(vc.getCandidate().getContactNumber());
                    vendorReferenceDataDto.setDateOfBirth(vc.getCandidate().getDateOfBirth());
                    vendorReferenceDataDto.setEmailId(vc.getCandidate().getEmailId());
                    ConventionalCandidate byConventionalRequestId = conventionalCandidateRepository.findByConventionalRequestId(vc.getCandidate().getConventionalRequestId());
                    vendorReferenceDataDto.setFatherName(byConventionalRequestId.getFatherName());
                    vendorReferenceDataDto.setGender(byConventionalRequestId.getGender());
                    vendorReferenceDataDto.setCandidateId(String.valueOf(vc.getCandidate().getConventionalCandidateId()));
                    vendorReferenceDataDto.setCheckName(vc.getSource().getSourceName());
                    vendorReferenceDataDto.setVendorReferenceData(conventionalAddressDtos);
                    vendorReferenceDataDtos.add(vendorReferenceDataDto);
                }
            } else {
                log.info("NO Reference Data Found For  this Candidate");
                VendorReferenceDataDto vendorReferenceDataDto = new VendorReferenceDataDto();
                vendorReferenceDataDto.setContactNumber(vc.getCandidate().getContactNumber());
                vendorReferenceDataDto.setDateOfBirth(vc.getCandidate().getDateOfBirth());
                vendorReferenceDataDto.setEmailId(vc.getCandidate().getEmailId());
                ConventionalCandidate byConventionalRequestId = conventionalCandidateRepository.findByConventionalRequestId(vc.getCandidate().getConventionalRequestId());
                vendorReferenceDataDto.setFatherName(byConventionalRequestId.getFatherName());
                vendorReferenceDataDto.setGender(byConventionalRequestId.getGender());
                vendorReferenceDataDto.setCandidateId(String.valueOf(vc.getCandidate().getConventionalCandidateId()));
                vendorReferenceDataDto.setCheckName(vc.getSource().getSourceName());
                vendorReferenceDataDto.setVendorReferenceData(null);
                vendorReferenceDataDtos.add(vendorReferenceDataDto);

            }
//


            serviceOutcome.setData(vendorReferenceDataDtos);
            serviceOutcome.setMessage("fetched response sucessfully");
            serviceOutcome.setOutcome(true);
            List<VendorReferenceDataDto> vendorData = serviceOutcome.getData();
            File nanda = FileUtil.createUniqueTempFile("nanda", ".xlsx");
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");
            int rowNum = 0;
            CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = sheet.getWorkbook().createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Candidate ID");
            headerRow.getCell(0).setCellStyle(headerStyle);
            headerRow.createCell(1).setCellValue("Check Name");
            headerRow.getCell(1).setCellStyle(headerStyle);
            headerRow.createCell(2).setCellValue("Date Of Birth");
            headerRow.getCell(2).setCellStyle(headerStyle);
            headerRow.createCell(3).setCellValue("Father Name");
            headerRow.getCell(3).setCellStyle(headerStyle);
            headerRow.createCell(4).setCellValue("Gender");
            headerRow.getCell(4).setCellStyle(headerStyle);
            headerRow.createCell(5).setCellValue("Contact Number");
            headerRow.getCell(5).setCellStyle(headerStyle);
            headerRow.createCell(6).setCellValue("Email Id");
            headerRow.getCell(6).setCellStyle(headerStyle);
            for (VendorReferenceDataDto dto : vendorReferenceDataDtos) {
                List vendorReferenceData = dto.getVendorReferenceData();
                if (vendorReferenceData != null) {
                    for (int i = 0; i < vendorReferenceData.size(); i++) {
                        if (vendorReferenceData.get(i) instanceof ConventionalAddressDto) {
                            ConventionalAddressDto conventionalAddressDto = (ConventionalAddressDto) vendorReferenceData.get(i);
                            // Assuming you want to set the field names of ConventionalAddressDto as headers
                            Field[] fields = ConventionalAddressDto.class.getDeclaredFields();
                            for (int j = 0; j < fields.length; j++) {
                                String fieldName = fields[j].getName();
                                headerRow.createCell(7 + j).setCellValue(fieldName);
                                headerRow.getCell(7 + j).setCellStyle(headerStyle);
                            }
                        } else if (vendorReferenceData.get(i) instanceof ConventionalEducationDto) {
                            ConventionalEducationDto conventionalEducationDto = (ConventionalEducationDto) vendorReferenceData.get(i);
                            // Assuming you want to set the field names of ConventionalAddressDto as headers
                            Field[] fields = ConventionalEducationDto.class.getDeclaredFields();
                            for (int j = 0; j < fields.length; j++) {
                                String fieldName = fields[j].getName();
                                headerRow.createCell(7 + j).setCellValue(fieldName);
                                headerRow.getCell(7 + j).setCellStyle(headerStyle);
                            }
                        } else if (vendorReferenceData.get(i) instanceof ConventionalExperienceDto) {
                            ConventionalExperienceDto conventionalExperienceDto = (ConventionalExperienceDto) vendorReferenceData.get(i);
                            // Assuming you want to set the field names of ConventionalAddressDto as headers
                            Field[] fields = ConventionalExperienceDto.class.getDeclaredFields();
                            for (int j = 0; j < fields.length; j++) {
                                String fieldName = fields[j].getName();
                                headerRow.createCell(7 + j).setCellValue(fieldName);
                                headerRow.getCell(7 + j).setCellStyle(headerStyle);
                            }
                        } else if (vendorReferenceData.get(i) instanceof ConventionalCandidateReferenceDto) {
                            ConventionalCandidateReferenceDto conventionalCandidateReferenceDto = (ConventionalCandidateReferenceDto) vendorReferenceData.get(i);
                            // Assuming you want to set the field names of ConventionalAddressDto as headers
                            Field[] fields = ConventionalCandidateReferenceDto.class.getDeclaredFields();
                            for (int j = 0; j < fields.length; j++) {
                                String fieldName = fields[j].getName();
                                headerRow.createCell(7 + j).setCellValue(fieldName);
                                headerRow.getCell(7 + j).setCellStyle(headerStyle);
                            }
                        } else {
                            headerRow.createCell(7).setCellValue("Vendor Reference Data");
                            headerRow.getCell(7).setCellStyle(headerStyle);
                        }
                    }
                } else {
                    headerRow.createCell(7).setCellValue("VendorReference Data");
                    headerRow.getCell(7).setCellStyle(headerStyle);
                }
            }
//            sheet.setColumnWidth(0, 15 * 756); // 15 characters
//            sheet.setColumnWidth(1, 70 * 756); // 70 characters
            for (int j = 7; j < headerRow.getLastCellNum(); j++) {
                sheet.setColumnWidth(j, 15 * 756); // 15 characters for other columns
            }
            int datarowNo = 1;
            for (VendorReferenceDataDto dto : vendorReferenceDataDtos) {
                Row dataRow = null;
                if (dto.getVendorReferenceData() != null) {
                    List vendorReferenceData = dto.getVendorReferenceData();
                    if (vendorReferenceData != null) {
                        for (int i = 0; i < vendorReferenceData.size(); i++) {
                            dataRow = sheet.createRow(datarowNo);
                            dataRow.createCell(0).setCellValue(dto.getCandidateId());
                            dataRow.createCell(1).setCellValue(dto.getCheckName());
                            dataRow.createCell(2).setCellValue(dto.getDateOfBirth());
                            dataRow.createCell(3).setCellValue(dto.getFatherName());
                            dataRow.createCell(4).setCellValue(dto.getGender());
                            dataRow.createCell(5).setCellValue(dto.getContactNumber());
                            dataRow.createCell(6).setCellValue(dto.getEmailId());
                            datarowNo = datarowNo + 1;
                            System.out.println("datarow" + datarowNo);
                            if (vendorReferenceData.get(i) instanceof ConventionalAddressDto) {
                                ConventionalAddressDto conventionalAddressDto = (ConventionalAddressDto) vendorReferenceData.get(i);
                                // Assuming you want to set the field names of ConventionalAddressDto as headers
                                Field[] fields = ConventionalAddressDto.class.getDeclaredFields();
                                for (int j = 0; j < fields.length; j++) {
                                    Field field = fields[j];
                                    String fieldName = field.getName();
                                    // Use reflection to get the field's value from the dto
                                    try {
                                        field.setAccessible(true); // Allow access to private fields
                                        Object value = field.get(conventionalAddressDto);
                                        String valueAsString = (value != null) ? value.toString() : "";
                                        dataRow.createCell(7 + j).setCellValue(valueAsString);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else if (vendorReferenceData.get(i) instanceof ConventionalEducationDto) {
                                ConventionalEducationDto conventionalEducationDto = (ConventionalEducationDto) vendorReferenceData.get(i);
                                // Assuming you want to set the field names of ConventionalAddressDto as headers
                                Field[] fields = ConventionalEducationDto.class.getDeclaredFields();
                                for (int j = 0; j < fields.length; j++) {
                                    Field field = fields[j];
                                    String fieldName = field.getName();
                                    // Use reflection to get the field's value from the dto
                                    try {
                                        field.setAccessible(true); // Allow access to private fields
                                        Object value = field.get(conventionalEducationDto);
                                        String valueAsString = (value != null) ? value.toString() : "";
                                        dataRow.createCell(7 + j).setCellValue(valueAsString);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else if (vendorReferenceData.get(i) instanceof ConventionalExperienceDto) {
                                ConventionalExperienceDto conventionalExperienceDto = (ConventionalExperienceDto) vendorReferenceData.get(i);
                                // Assuming you want to set the field names of ConventionalAddressDto as headers
                                Field[] fields = ConventionalExperienceDto.class.getDeclaredFields();
                                for (int j = 0; j < fields.length; j++) {
                                    Field field = fields[j];
                                    String fieldName = field.getName();
                                    // Use reflection to get the field's value from the dto
                                    try {
                                        field.setAccessible(true); // Allow access to private fields
                                        Object value = field.get(conventionalExperienceDto);
                                        String valueAsString = (value != null) ? value.toString() : "";
                                        dataRow.createCell(7 + j).setCellValue(valueAsString);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else if (vendorReferenceData.get(i) instanceof ConventionalCandidateReferenceDto) {
                                ConventionalCandidateReferenceDto conventionalCandidateReferenceDto = (ConventionalCandidateReferenceDto) vendorReferenceData.get(i);
                                // Assuming you want to set the field names of ConventionalAddressDto as headers
                                Field[] fields = ConventionalCandidateReferenceDto.class.getDeclaredFields();
                                for (int j = 0; j < fields.length; j++) {
                                    Field field = fields[j];
                                    String fieldName = field.getName();
                                    // Use reflection to get the field's value from the dto
                                    try {
                                        field.setAccessible(true); // Allow access to private fields
                                        Object value = field.get(conventionalCandidateReferenceDto);
                                        String valueAsString = (value != null) ? value.toString() : "";
                                        dataRow.createCell(7 + j).setCellValue(valueAsString);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } else {
                                dataRow.createCell(2).setCellValue("null");
                            }
                        }
                    }
                } else {
                    dataRow = sheet.createRow(datarowNo);
                    dataRow.createCell(0).setCellValue(dto.getCandidateId());
                    dataRow.createCell(1).setCellValue(dto.getCheckName());
                    dataRow.createCell(2).setCellValue(dto.getDateOfBirth());
                    dataRow.createCell(3).setCellValue(dto.getFatherName());
                    dataRow.createCell(4).setCellValue(dto.getGender());
                    dataRow.createCell(5).setCellValue(dto.getContactNumber());
                    dataRow.createCell(6).setCellValue(dto.getEmailId());
                    dataRow.createCell(7).setCellValue("NO DATA");
                }
                for (int j = 7; j < dataRow.getLastCellNum(); j++) {
                    sheet.setColumnWidth(j, 15 * 256); // 15 characters for other columns
                }
            }
            FileOutputStream fileOutputStream = new FileOutputStream(nanda);
            workbook.write(fileOutputStream);
            byte[] fileContent = Files.readAllBytes(Paths.get(nanda.getAbsolutePath()));
            String base64String = Base64.getEncoder().encodeToString(fileContent);
            serviceOutcome.setMessage(base64String);


        } catch (Exception e) {
            log.info(e.getMessage());
            serviceOutcome.setOutcome(false);

            serviceOutcome.setMessage(e.getMessage());
        }


        return serviceOutcome;
    }

    //    @Transactional


    public ServiceOutcome<byte[]> generateConventionalUtilizationReport() throws Exception {
        ServiceOutcome<byte[]> serviceOutcome = new ServiceOutcome<>();
        List<ReportUtilizationDto> reportUtilizationDtos = new ArrayList<>();

        try {
            File nanda = FileUtil.createUniqueTempFile("utilreport", ".xlsx");
            Workbook workbook = new XSSFWorkbook();
            List<Source> allSources = sourceRepository.findAll();
            allSources = allSources.stream().filter(p -> p.getSourceId() != 3l).collect(Collectors.toList());
            for (Source source : allSources) {
                source.setSourceName(source.getSourceName().replace("PROFFESSIONAL", "PROF").replace("REFERENCE", "REF"));
                String modifiedSourceName = source.getSourceName().replaceAll("[\\s-]+", "");
                List<ReportUtilizationVendorDto> allVendorCandidateAndSourceId = vendorChecksRepository.findAllVendorCandidateAndSourceId(source.getSourceId());
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
                headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                CellStyle headerCellStyle2 = workbook.createCellStyle();
                headerCellStyle2.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                headerCellStyle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                Sheet sheet = workbook.createSheet(source.getSourceName());
                log.info("source name  :" + source.getSourceName());
                int rowno = 1;
                int datarowNo = 2;
                for (ReportUtilizationVendorDto reportUtilizationVendorDto : allVendorCandidateAndSourceId) {
                    Long candidateId = reportUtilizationVendorDto.getCandidateId();
                    Long vendorId = reportUtilizationVendorDto.getVendorId();

//                        if (sheetExists == false) {
                    sheet.setDefaultColumnWidth(25);
                    Row headerRow = sheet.createRow(rowno);
                    headerRow.createCell(0).setCellValue("Vendor Name");
                    headerRow.createCell(1).setCellValue("Case Initiated by (DigiVerifier Spoc))");
                    headerRow.createCell(2).setCellValue("URN / Ref No.");
                    headerRow.createCell(3).setCellValue("Candidate Name");
                    headerRow.createCell(4).setCellValue("Case Assigned Date");
                    headerRow.createCell(5).setCellValue("Report Status");
                    headerRow.createCell(6).setCellValue("Report Color Code");
                    for (int i = 0; i <= 6; i++) {
                        Cell cell = headerRow.getCell(i);
                        cell.setCellStyle(headerCellStyle);
                    }
                    headerRow.createCell(7).setCellValue("Detail Name");
                    headerRow.createCell(8).setCellValue("Qty");
                    headerRow.createCell(9).setCellValue("Price Per Unit");
                    headerRow.createCell(10).setCellValue("Color Code");
                    headerRow.createCell(11).setCellValue("Total Amount");
                    for (int i = 7; i <= 11; i++) {
                        Cell cell = headerRow.getCell(i);
                        cell.setCellStyle(headerCellStyle2);
                    }

                    List<VendorChecks> byCandidateIdANdVendorIdAndCandidateId = vendorChecksRepository.findByCandidateIdANdVendorIdAndCandidateId(vendorId, candidateId, source.getSourceId());
                    for (VendorChecks vendorChecks : byCandidateIdANdVendorIdAndCandidateId) {
                        datarowNo = datarowNo + 1;
                        Row dataRow = sheet.createRow(datarowNo);
                        User user = userRepository.findById(vendorId).get();
                        dataRow.createCell(0).setCellValue((user.getUserFirstName() != null) ? user.getUserFirstName() : "NA");
                        if (vendorChecks.getCreatedBy() != null) {
                            User caseInitatedBy = userRepository.findById(vendorChecks.getCreatedBy().getUserId()).get();
                            dataRow.createCell(1).setCellValue((String.valueOf(caseInitatedBy.getUserName()) != null) ? caseInitatedBy.getUserName() : "NA");
                        }
                        ConventionalVendorCandidatesSubmitted conventionalVendorCandidatesSubmitted = conventionalCandidatesSubmittedRepository.findByRequestId(String.valueOf(vendorChecks.getCandidate().getConventionalRequestId()));
                        dataRow.createCell(2).setCellValue((conventionalVendorCandidatesSubmitted != null) ? conventionalVendorCandidatesSubmitted.getApplicantId() : 0l);
                        dataRow.createCell(3).setCellValue((vendorChecks != null) ? conventionalVendorCandidatesSubmitted.getName() : "NA");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = dateFormat.format(vendorChecks.getCreatedOn());
                        dataRow.createCell(4).setCellValue((vendorChecks.getCreatedOn() != null) ? dateFormat.format(vendorChecks.getCreatedOn()) : dateFormat.format(new Date()));
                        CandidateVerificationState canidateVerificationData = candidateVerificationStateRepository.findByCandidateCandidateId(vendorChecks.getCandidate().getCandidateId());
                        dataRow.createCell(5).setCellValue((canidateVerificationData != null) ? String.valueOf(canidateVerificationData.getInterimReportTime()) : "NA");
                        dataRow.createCell(6).setCellValue((conventionalVendorCandidatesSubmitted != null) ? conventionalVendorCandidatesSubmitted.getStatus().getStatusName() : "NA");
                        dataRow.createCell(7).setCellValue((vendorChecks.getSource() != null) ? vendorChecks.getSource().getSourceName() : "NA");
                        dataRow.createCell(8).setCellValue((byCandidateIdANdVendorIdAndCandidateId != null) ? byCandidateIdANdVendorIdAndCandidateId.size() : 0l);
                        dataRow.createCell(10).setCellValue((vendorChecks.getVendorCheckStatusMaster() != null) ? vendorChecks.getVendorCheckStatusMaster().getCheckStatusCode() : "NA");
                        List<VendorMasterNew> byUserId = vendorMasterNewRepository.findByUserId(vendorId);
                        if (byUserId.isEmpty() == false) {
                            byUserId.forEach(data -> {
                                dataRow.createCell(9).setCellValue((data != null) ? data.getRatePerItem() : 4l);
                                if (byCandidateIdANdVendorIdAndCandidateId.isEmpty() == false) {
                                    dataRow.createCell(11).setCellValue((data != null) ? byCandidateIdANdVendorIdAndCandidateId.size() * data.getRatePerItem() : 0);
                                }
                            });
                        }

                    }
                }


            }


//            File nanda = FileUtil.createUniqueTempFile("nanda", ".xlsx");
//            FileOutputStream fileOutputStream = new FileOutputStream(nanda);
//            workbook.write(fileOutputStream);
//            byte[] fileContent = Files.readAllBytes(Paths.get(nanda.getAbsolutePath()));
//            System.out.println("absoulute oath" + nanda.getAbsolutePath());
//            System.out.println("" + nanda.getPath());
//            serviceOutcome.setMessage(nanda.getAbsolutePath());
            FileOutputStream fileOutputStream = new FileOutputStream(nanda);
            workbook.write(fileOutputStream);
            byte[] fileContent = Files.readAllBytes(Paths.get(nanda.getAbsolutePath()));
            String base64String = Base64.getEncoder().encodeToString(fileContent);
            serviceOutcome.setMessage(base64String);
        } catch (Exception e) {
            log.error("error in generate response  :" + e.getMessage());


        }
        return serviceOutcome;
    }

    public ServiceOutcome<List<ModeOfVerificationStatusMaster>> findAllModeOfVerifcationPerformed() throws Exception {
        ServiceOutcome<List<ModeOfVerificationStatusMaster>> listServiceOutcome = new ServiceOutcome<>();

        try {
            List<ModeOfVerificationStatusMaster> all = modeOfVerificationStatusMasterRepository.findAll();
            if (all.isEmpty()) {
                listServiceOutcome.setData(new ArrayList<>());
            }
            listServiceOutcome.setData(all);
            listServiceOutcome.setOutcome(true);
            listServiceOutcome.setStatus("200");


        } catch (Exception e) {
            log.info("LICheck to  perfom serviceImpl ::  findAllModeOfVerifcationPerformed" + e.getMessage());
        }
        return listServiceOutcome;
    }

//    @Autowired
//    private ConventionalReportVendorRepository conventionalReportVendorRepository;

//    @Override
//    @Transactional
//    public ServiceOutcome<ConventionalReportVendor> addConventionalVendorReportAttributes() {
//        ServiceOutcome<ConventionalReportVendor> conventionalReportVendorServiceOutcome = new ServiceOutcome<>();
//        try {
//            log.info("addConventionalVendorReportAttributes() starts");
//
//            ConventionalReportVendor conventionalReportVendor = new ConventionalReportVendor();
//            conventionalReportVendor.setCandidateId(944l);
//            ArrayList<String> pancardCheckAttributes = new ArrayList<>();
//            pancardCheckAttributes.add("Name in the Pan Card");
//            pancardCheckAttributes.add("Date of Birth");
//            pancardCheckAttributes.add("Father Name");
//            pancardCheckAttributes.add("Remarks");
//            conventionalReportVendor.setIdentityCheckPancard(pancardCheckAttributes);
//            conventionalReportVendorRepository.save(conventionalReportVendor);
//
//
//            log.info("addConventionalVendorReportAttributes() ends");
//        } catch (Exception e) {
//            log.info("addConventionalVendorReportAttributes() exception " + e.getMessage());
//        }
//
//        return conventionalReportVendorServiceOutcome;
//    }

    @Override
    public ServiceOutcome<ConventionalAttributesMaster> saveConventionalAttributesMaster(ConventionalAttributesMaster conventionalAttributesMaster) {
        ServiceOutcome<ConventionalAttributesMaster> svcSearchResult = new ServiceOutcome<ConventionalAttributesMaster>();
        try {
            ConventionalAttributesMaster save = conventionalAttributesMasterRepository.save(conventionalAttributesMaster);
            svcSearchResult.setData(save);
        } catch (Exception ex) {

            log.error("Exception occured in saveConventionalAttributesMaster method in userServiceImpl-->" + ex);
        }
        return svcSearchResult;


    }


    @Override
    public ServiceOutcome<ConventionalAttributesMaster> getConventionalAttributesMasterById(Long vendorCheckId) {

        ServiceOutcome<ConventionalAttributesMaster> svcSearchResult = new ServiceOutcome<ConventionalAttributesMaster>();
        try {
            VendorChecks byVendorcheckId = vendorChecksRepository.findByVendorcheckId(vendorCheckId);

            List<ConventionalAttributesMaster> all = conventionalAttributesMasterRepository.findAll();
            List<ConventionalAttributesMaster> matchingEntities = all.stream().filter(attr -> attr.getSourceIds().contains(byVendorcheckId.getSource().getSourceId())).collect(Collectors.toList());


            svcSearchResult.setMessage("Fetched Data");
            svcSearchResult.setData(matchingEntities.get(0));

        } catch (Exception e) {

        }
        return svcSearchResult;

    }

    //    public ServiceOutcome<DashboardDto> searchAllCandidate(String searchText) {
//        ServiceOutcome<DashboardDto> svcSearchResult = new ServiceOutcome<>();
//        log.info("Search text {}",searchText);
//        List<CandidateDetailsDto> candidateDtoList = new ArrayList<CandidateDetailsDto>();
//        LocalDate currentDate = LocalDate.now();
//        List<Candidate> searchResult = candidateRepository.searchAllCandidate(userId, searchAllcandidate.getUserSearchInput(),createdOnDate,currentDate);
//        for (Candidate candidate : searchResult) {
//            CandidateDetailsDto candidateDto = this.modelMapper.map(candidate, CandidateDetailsDto.class);
//            candidateDto.setCreatedOn(formatter.format(candidate.getCreatedOn()));
//            candidateDto.setSubmittedOn(
//                    candidate.getSubmittedOn() != null ? formatter.format(candidate.getSubmittedOn()) : null);
//            CandidateEmailStatus candidateEmailStatus = candidateEmailStatusRepository
//                    .findByCandidateCandidateCode(candidate.getCandidateCode());
//            if (candidateEmailStatus != null) {
//                candidateDto.setDateOfEmailInvite(candidateEmailStatus.getDateOfEmailInvite() != null
//                        ? formatter.format(candidateEmailStatus.getDateOfEmailInvite())
//                        : null);
//                candidateDto.setDateOfEmailFailure(candidateEmailStatus.getDateOfEmailFailure() != null
//                        ? formatter.format(candidateEmailStatus.getDateOfEmailFailure())
//                        : null);
//                candidateDto.setDateOfEmailExpire(candidateEmailStatus.getDateOfEmailExpire() != null
//                        ? formatter.format(candidateEmailStatus.getDateOfEmailExpire())
//                        : null);
//                candidateDto.setDateOfEmailReInvite(candidateEmailStatus.getDateOfEmailReInvite() != null
//                        ? formatter.format(candidateEmailStatus.getDateOfEmailReInvite())
//                        : null);
//            }
//
//            Long candidateId = candidate.getCandidateId();
//            log.info("Candidate: {}",candidateId);
//
//
//            CandidateStatus candidateStatus = candidateStatusRepository
//                    .findByCandidateCandidateCode(candidate.getCandidateCode());
//            candidateDto.setCandidateStatusName(candidateStatus.getStatusMaster().getStatusName());
//
//            List<ContentDTO> contentDTOList = contentService
//                    .getContentListByCandidateId(candidate.getCandidateId());
//            log.info(contentDTOList + "--------contentdtolist-------");
//            candidateDto.setContentDTOList(contentDTOList);
//
//            candidateDtoList.add(candidateDto);
//            log.info("candidateDateDTOLIST::::============== {}",candidateDtoList.toString());
//
//        }
//
//        DashboardDto dashboardDtoObj = new DashboardDto(null, null, null, null, null,
//                userId, null, candidateDtoList);
//        if (!candidateDtoList.isEmpty()) {
//            svcSearchResult.setData(dashboardDtoObj);
//            svcSearchResult.setOutcome(true);
//            svcSearchResult.setMessage("Candidate list fetched successfully.");
//        } else {
//            svcSearchResult.setData(null);
//            svcSearchResult.setOutcome(false);
//            svcSearchResult.setMessage("NO Candidate FOUND");
//        }
//
//        return svcSearchResult;
//    }
    public ServiceOutcome<List<ConventionalVendorCandidatesSubmitted>> searchAllCandidate(String searchText) {
        ServiceOutcome<List<ConventionalVendorCandidatesSubmitted>> svcSearchResult = new ServiceOutcome<>();
        try {
            User user = (SecurityHelper.getCurrentUser() != null) ? SecurityHelper.getCurrentUser() : userRepository.findByUserId(53l);

            Long userId = user.getUserId();
            User byOrganizationAndRoleId = userRepository.findByOrganizationAndRoleId(user.getOrganization().getOrganizationId(), user.getRole().getRoleId(), user.getUserId());
            Date createdOnDate = byOrganizationAndRoleId.getCreatedOn();
            log.info("CreatedOnDate {}", createdOnDate);
            Date currentDate = new Date();
            log.info("Current Date: {}", currentDate);
            List<ConventionalVendorCandidatesSubmitted> searchResult = new ArrayList<>();
            List<StatusMaster> all = statusMasterRepository.findAll();
            Optional<StatusMaster> matchedStatus = null;
            String upperCase = searchText.trim().replaceAll("\\s+", "").toUpperCase();
            if ("QCPENDING".contains(upperCase)) {
                searchText = "PENDINGAPPROVAL";
                String finalSearchText = searchText;
                matchedStatus = all.stream().filter(statusMaster -> statusMaster.getStatusCode().contains(finalSearchText.trim().replaceAll("\\s+", "").toUpperCase())).findFirst();
            } else {
                String finalSearchText1 = searchText;
                matchedStatus = all.stream().filter(statusMaster -> statusMaster.getStatusCode().contains(finalSearchText1.trim().replaceAll("\\s+", "").toUpperCase())).findFirst();
            }
            if (matchedStatus.isPresent()) {
                searchResult = conventionalCandidatesSubmittedRepository.searchAllCandidateStatus(String.valueOf(matchedStatus.get().getStatusMasterId()), createdOnDate, currentDate);
            } else {
                searchResult = conventionalCandidatesSubmittedRepository.searchAllCandidate(searchText, createdOnDate, currentDate);
            }
            if (searchResult.isEmpty() == true) {
                svcSearchResult.setData(new ArrayList<ConventionalVendorCandidatesSubmitted>());
            } else {
                svcSearchResult.setData(searchResult);
            }
        } catch (Exception e) {
            log.error("inside search data" + e.getMessage());
        }
        return svcSearchResult;
    }


    public ServiceOutcome<String> getRemarksForValidation(String checkuniqueId) {
        ServiceOutcome<String> stringServiceOutcome = new ServiceOutcome<>();
        try {
            ConventionalVendorliChecksToPerform byCheckUniqueId = liCheckToPerformRepository.findByCheckUniqueId(Long.valueOf(checkuniqueId));
            stringServiceOutcome.setData(byCheckUniqueId.getCheckRemarks());
        } catch (Exception e) {

        }
        return stringServiceOutcome;
    }

    public ServiceOutcome<String> reAssignToAnotherVendor(String checkUniqueId, String vendorId) {
        ServiceOutcome<String> stringServiceOutcome = new ServiceOutcome<>();
        try {
            ConventionalVendorliChecksToPerform byCheckUniqueId = liCheckToPerformRepository.findByCheckUniqueId(Long.valueOf(checkUniqueId));
            if (byCheckUniqueId != null) {
                if (byCheckUniqueId.getVendorChecks() != null) {
                    User byUserId = userRepository.findByUserId(Long.valueOf(vendorId));
                    byCheckUniqueId.setVendorName(byUserId.getUserFirstName() + byUserId.getUserLastName());
                    ConventionalVendorliChecksToPerform updateLichekVendor = liCheckToPerformRepository.save(byCheckUniqueId);
                    VendorChecks byVendorcheckId = vendorChecksRepository.findByVendorcheckId(byCheckUniqueId.getVendorChecks().getVendorcheckId());
                    byVendorcheckId.setVendorId(byUserId.getUserId());
                    VendorChecks updatedVendorCheckVendor = vendorChecksRepository.save(byVendorcheckId);
                    log.info("updated vendor Sucessfully");
                    stringServiceOutcome.setData("Updated VendorSucessfully");
                    stringServiceOutcome.setMessage("Updated VendorSucessfully");
                    stringServiceOutcome.setOutcome(true);
                    stringServiceOutcome.setStatus("200");
                }
            }
        } catch (Exception e) {
            stringServiceOutcome.setData("Not Able To Update  Vendor");
            stringServiceOutcome.setMessage("Not Able To Update  Vendor");
            stringServiceOutcome.setOutcome(false);
            stringServiceOutcome.setStatus("200");
        }
        return stringServiceOutcome;
    }


    public ServiceOutcome<byte[]> downloadAllFilebyRequestId(String requestId) throws Exception {
        ServiceOutcome<byte[]> serviceOutcome = new ServiceOutcome<>();
        File nanda = File.createTempFile("nanda", ".zip");
        String pathkey = "Candidate/Convetional/" + requestId;
        System.out.println("padfs" + pathkey);
        List<S3ObjectSummary> objectSummaries = s3Client.listObjects(DIGIVERIFIER_DOC_BUCKET_NAME, pathkey).getObjectSummaries();
        try (ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(nanda)) {
            for (S3ObjectSummary objectSummary : objectSummaries) {
                S3Object s3Object = s3Client.getObject(DIGIVERIFIER_DOC_BUCKET_NAME, objectSummary.getKey());
                String fileName = objectSummary.getKey();
                ZipArchiveEntry zipEntry = new ZipArchiveEntry(fileName);
                zipOut.putArchiveEntry(zipEntry);
                IOUtils.copy(s3Object.getObjectContent(), zipOut);
                zipOut.closeArchiveEntry();
                s3Object.close();
            }
            Path zipFilePath = Paths.get(nanda.getPath());
            byte[] data = Files.readAllBytes(zipFilePath);
            serviceOutcome.setData(data);
//            String base64String = Base64.getEncoder().encodeToString(data);
            zipOut.finish();
            serviceOutcome.setMessage("");
            serviceOutcome.setOutcome(true);
        } catch (IOException e) {
            serviceOutcome.setOutcome(false);
            log.info(e.getMessage());

        }
        return serviceOutcome;
    }

    public void deleteData(String startDateStr, String endDateStr) throws ParseException, SQLException {
        List<String> queries = new ArrayList<>();
        String dateFormatPattern = "dd/MM/yyyy";
//        startDateStr = "10/08/2023";
//        endDateStr = "20/08/2023";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        String t_dgv_vendor_uploaded_checks = "DELETE FROM t_dgv_vendor_uploaded_checks WHERE vendor_check_id IN (SELECT vendor_check_id FROM t_dgv_vendor_checks WHERE candidate_id IN (SELECT candidate_id FROM t_dgv_candidate_basic WHERE conventional_request_id IN (SELECT request_id FROM t_dgv_conventional_candidate_request WHERE created_on BETWEEN ? AND ?)))";
        queries.add(t_dgv_vendor_uploaded_checks);
        String t_dgv_conventional_vendorchecks_to_perform = "DELETE FROM t_dgv_conventional_vendorchecks_to_perform WHERE vendor_check IN (SELECT vendor_check_id FROM t_dgv_vendor_checks WHERE candidate_id IN (SELECT candidate_id FROM t_dgv_candidate_basic WHERE conventional_request_id IN (SELECT request_id FROM t_dgv_conventional_candidate_request WHERE created_on BETWEEN ? AND ?)))";
        queries.add(t_dgv_conventional_vendorchecks_to_perform);
        String t_dgv_vendor_checks = "DELETE FROM t_dgv_vendor_checks WHERE candidate_id IN (SELECT candidate_id FROM t_dgv_candidate_basic WHERE conventional_request_id IN (SELECT request_id FROM t_dgv_conventional_candidate_request WHERE created_on BETWEEN ? AND ?))";
        queries.add(t_dgv_vendor_checks);
        String t_dgv_candidate_caf_address = "DELETE FROM t_dgv_candidate_caf_address WHERE candidate_id IN (SELECT candidate_id FROM t_dgv_candidate_basic WHERE conventional_request_id IN (SELECT request_id FROM t_dgv_conventional_candidate_request WHERE created_on BETWEEN ? AND ?))";
        queries.add(t_dgv_candidate_caf_address);
        String t_dgv_candidate_caf_education = "DELETE FROM t_dgv_candidate_caf_education WHERE candidate_id IN (SELECT candidate_id FROM t_dgv_candidate_basic WHERE conventional_request_id IN (SELECT request_id FROM t_dgv_conventional_candidate_request WHERE created_on BETWEEN ? AND ?))";
        queries.add(t_dgv_candidate_caf_education);
        String t_dgv_candidate_caf_experience = "DELETE FROM t_dgv_candidate_caf_experience WHERE candidate_id IN (SELECT candidate_id FROM t_dgv_candidate_basic WHERE conventional_request_id IN (SELECT request_id FROM t_dgv_conventional_candidate_request WHERE created_on BETWEEN ? AND ?))";
        queries.add(t_dgv_candidate_caf_experience);
        String t_dgv_candidate_verification_state = "DELETE FROM t_dgv_candidate_verification_state WHERE candidate_id IN (SELECT candidate_id from t_dgv_candidate_basic where conventional_request_id in (select request_id from t_dgv_conventional_candidate_request where created_on BETWEEN ? AND ?))";
        queries.add(t_dgv_candidate_verification_state);
        String t_dgv_content = "DELETE FROM t_dgv_content WHERE candidate_id IN (SELECT candidate_id from t_dgv_candidate_basic where conventional_request_id in (select request_id from t_dgv_conventional_candidate_request where created_on BETWEEN ? and ?))";
        queries.add(t_dgv_content);
        String t_dgv_conventioanl_candidate_caf_address = "delete from t_dgv_conventioanl_candidate_caf_address where conventional_requestid in (select request_id from t_dgv_conventional_candidate_request where created_on between ? and ?)";
        queries.add(t_dgv_conventioanl_candidate_caf_address);
        String t_dgv_conventional_candidate_caf_education = "delete from t_dgv_conventional_candidate_caf_education where conventional_requestid in (select request_id from t_dgv_conventional_candidate_request where created_on between ? and ?)";
        queries.add(t_dgv_conventional_candidate_caf_education);
        String t_dgv_conventional_candidate_basic = "delete from t_dgv_conventional_candidate_basic where conventional_request_id in (select request_id from t_dgv_conventional_candidate_request where created_on between ? and ?)";
        queries.add(t_dgv_conventional_candidate_basic);
        String t_dgv_conventional_candidate_document_info = "delete from t_dgv_conventional_candidate_document_info where request_id in (select request_id from t_dgv_conventional_candidate_request where created_on between ? and ?)";
        queries.add(t_dgv_conventional_candidate_document_info);
        String t_dgv_conventional_candidate_drug_info = "delete from t_dgv_conventional_candidate_drug_info where conventional_requestid in (select request_id from t_dgv_conventional_candidate_request where created_on between ? and ?)";
        queries.add(t_dgv_conventional_candidate_drug_info);
        String t_dgv_conventional_candidate_reference_info = "delete from t_dgv_conventional_candidate_reference_info where conventional_requestid in (select request_id from t_dgv_conventional_candidate_request where created_on between ? and ?)";
        queries.add(t_dgv_conventional_candidate_reference_info);
        String t_dgv_candidate_basic = "delete from t_dgv_candidate_basic where conventional_request_id in (select request_id from t_dgv_conventional_candidate_request where created_on between ? and ?)";
        queries.add(t_dgv_candidate_basic);
        String t_dgv_conventional_candidate_request = "delete from t_dgv_conventional_candidate_request where created_on between ? and ?";
        queries.add(t_dgv_conventional_candidate_request);
        Date startDate = dateFormat.parse(startDateStr);
        Date endDate = dateFormat.parse(endDateStr);
        System.out.println(startDate);

        try (Connection connection = DriverManager.getConnection(environmentVal.getDatasourceUrl(), "digiverifier", "62DFrjznH1Hu")) {
            System.out.println("url" + environmentVal.getDatasourceUrl());
            for (String query : queries) {
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    System.out.println(query);
                    statement.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
                    statement.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
                    int i = statement.executeUpdate();
                    System.out.println("statement executed " + 1);
                } catch (SQLException e) {
                    e.printStackTrace();
                    log.info("Exception in JDBC delete in delete database existing" + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Exception in JDBC connection");
            // Handle any exceptions that may occur when establishing a database connection
        }
    }

    public ServiceOutcome<String> updateIdentityCheckDisableStatus(String checkUniqueId, String disableStatus) {
        ServiceOutcome<String> stringServiceOutcome = new ServiceOutcome<>();
        try {
            ConventionalVendorliChecksToPerform byCheckUniqueId = liCheckToPerformRepository.findByCheckUniqueId(Long.valueOf(checkUniqueId));
            if (byCheckUniqueId != null) {
                byCheckUniqueId.setDisabled(disableStatus);
                ConventionalVendorliChecksToPerform save = liCheckToPerformRepository.save(byCheckUniqueId);
                stringServiceOutcome.setData(disableStatus + " for " + save.getCheckName() + " Done ..");
                stringServiceOutcome.setOutcome(true);
            }
        } catch (Exception e) {
            log.info("exception for updateIdentityCheckDisableStatus :" + e.getMessage());
            stringServiceOutcome.setData("Enable/Disable not possible now");
            stringServiceOutcome.setOutcome(false);
        }
        return stringServiceOutcome;
    }
}

