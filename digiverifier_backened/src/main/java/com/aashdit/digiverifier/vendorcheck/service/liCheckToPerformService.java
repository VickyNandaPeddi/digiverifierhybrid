/**
 *
 */
package com.aashdit.digiverifier.vendorcheck.service;

import com.aashdit.digiverifier.common.model.ServiceOutcome;
import com.aashdit.digiverifier.config.superadmin.Enum.ReportType;
import com.aashdit.digiverifier.config.superadmin.dto.DashboardDto;
import com.aashdit.digiverifier.config.superadmin.model.VendorCheckStatusMaster;
import com.aashdit.digiverifier.vendorcheck.dto.*;
import com.aashdit.digiverifier.vendorcheck.model.ConventionalAttributesMaster;
import com.aashdit.digiverifier.vendorcheck.model.ConventionalVendorCandidatesSubmitted;
import com.aashdit.digiverifier.vendorcheck.model.ModeOfVerificationStatusMaster;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * @author ${Nanda Kishore}
 */
@Service
public interface liCheckToPerformService {
    public ServiceOutcome<List<ConventionalVendorCandidatesSubmitted>> findAllConventionalVendorSubmittedCandidatesByDateRange(DashboardDto dashboardDto) throws Exception;

    public ServiceOutcome<List<ConventionalVendorCandidatesSubmitted>> findAllSubmittedCandidatesByDateRangeOnInterimAndFinal(DashboardDto dashboardDto) throws Exception;

    public ServiceOutcome<LicheckRequiredResponseDto> addUpdateLiCheckToPerformData(FetchVendorConventionalCandidateDto licheckDto) throws Exception;

    public ServiceOutcome<List<LicheckRequiredResponseDto>> findAllLiChecksRequired() throws Exception;
    public ServiceOutcome<List<LicheckRequiredResponseDto>> findAllLiChecksRequiredbyCandidateId(String candidateId) throws Exception;

    public ServiceOutcome<List<LicheckRequiredResponseDto>> findAllStopLiChecksRequiredbyCandidateId(String candidateId) throws Exception;

    public ServiceOutcome<List<LicheckRequiredResponseDto>> findAllNewUploadLiChecksRequiredbyCandidateId(String candidateId) throws Exception;

    public ServiceOutcome<List<LicheckRequiredResponseDto>> findAllLiChecksRequiredbyCheckStatus(String checkStatus) throws Exception;

    public ServiceOutcome<SubmittedCandidates> saveConventionalVendorSubmittedCandidates(String vendorId, boolean schedulerCondition) throws Exception;

    public ServiceOutcome<SubmittedCandidates> triggerCandidateDataAndCheckData(String VendorID, String requestId) throws Exception;

    public ServiceOutcome<List<SubmittedCandidates>> findAllConventionalVendorSubmittedCandidates() throws Exception;

    public ServiceOutcome<String> UpdateBGVCheckStatusRowwise(String vendorChecksString, MultipartFile proofDocumentNew, String modeOfVerificationPerformed);

    public ServiceOutcome<ConventionalCandidateDocDto> saveConventionalCandidateDocumentInfo(FetchVendorConventionalCandidateDto fetchVendorConventionalCandidateDto) throws Exception;

    public ServiceOutcome<List<ConventionalCandidateDocDto>> findAllConventionalCandidateDocumentInfo() throws Exception;

    public ServiceOutcome<CandidateuploadS3Documents> findAllfilesUploadedurls(String candidateId, String checkName) throws Exception;

    public ServiceOutcome<String> findUpdateLicheckWithVendorCheck(Long vendorCheckId, Long liCheckId) throws Exception;

    public ServiceOutcome<String> updateLiCheckStatusByVendor(String vendorCheckStatusMasterId, String vendorCheckId, String remarks, String modeOfVericationPerformed) throws Exception;

    public ServiceOutcome<String> updateCandidateStatusByLicheckStatus();

    public ServiceOutcome<String> updateCandidateVerificationStatus(String requestId);

    public ServiceOutcome<List<VendorCheckStatusMaster>> findAllVendorCheckStatus();

    public ServiceOutcome<List<liReportDetails>> generateDocumentConventional(String candidateId, String reportType);

    public ServiceOutcome<String> generateJsonRepsonseByConventionalCandidateId(String candidateId, ReportType reportType, String update);

    public ServiceOutcome<String> generateConventionalCandidateReport(Long candidateCode, ReportType reportType, String update);


    public ServiceOutcome<ConventionalVendorCandidatesSubmitted> findConventionalCandidateByCandidateId(Long candiateId);

    public ServiceOutcome<byte[]> generateConventionalUtilizationReport() throws Exception;

    public ServiceOutcome<List<VendorReferenceDataDto>> generateReferenceDataToVendor(Long candidateId, Long checkName) throws Exception;


    public ServiceOutcome<List<ModeOfVerificationStatusMaster>> findAllModeOfVerifcationPerformed() throws Exception;


    //new code
    ServiceOutcome<ConventionalAttributesMaster> saveConventionalAttributesMaster(ConventionalAttributesMaster conventionalAttributesMaster);

    ServiceOutcome<ConventionalAttributesMaster> getConventionalAttributesMasterById(Long Id);


    public ServiceOutcome<List<ConventionalVendorCandidatesSubmitted>> searchAllCandidate(String searchText);

    public ServiceOutcome<String> getRemarksForValidation(String checkUniqueId);

    public ServiceOutcome<String> reAssignToAnotherVendor(String checkUniqueId,String vendorId);

    public ServiceOutcome<byte[]>  downloadAllFilebyRequestId(String requestId)throws Exception;

    public void deleteData(String startDate, String endDate) throws Exception;

    public ServiceOutcome<String> updateIdentityCheckDisableStatus(String checkUniqueId,String enableStatus);
}
