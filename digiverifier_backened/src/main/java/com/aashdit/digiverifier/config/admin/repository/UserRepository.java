package com.aashdit.digiverifier.config.admin.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.aashdit.digiverifier.vendorcheck.model.ConventionalVendorCandidatesSubmitted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aashdit.digiverifier.config.admin.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByOrganizationOrganizationIdAndCreatedByUserId(Long organizationId, Long userId);

    User findByEmployeeId(String employeeId);

    @Query("select u from User u where u.organization.organizationId=?1 and (?2 in (2,3,5,6)) and u.userId=?3")
    User findByOrganizationAndRoleId(Long organizationId,Long roleId,Long userId);
    User findByUserName(String userName);

    User findByUserEmailId(String userEmailId);

    List<User> findAllByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue(Long organizationId, Long roleId);

    List<User> findAllByAgentSupervisorUserId(Long userId);

    User findByOrganizationOrganizationIdAndRoleRoleIdAndIsActiveTrue(Long organizationId, Long roleId);

    List<User> findAllByOrganizationOrganizationId(Long organizationId);

    User findByUserId(Long userId);

    @Modifying
    @Query("Update User set isUserBlocked=:isActive where organization.organizationId=:organizationId")
    void deactiveOrActiveUsers(@Param("organizationId") Long organizationId, @Param("isActive") Boolean isActive);

    List<User> findByRoleRoleCode(String roleCode);

    List<User> findAllByAgentSupervisorUserIdAndRoleRoleIdAndIsActiveTrue(Long userId, Long roleId);

    List<User> findAllByRoleRoleIdAndIsActiveTrue(Long roleId);

    List<User> findByIsActiveTrue();

    @Modifying
    @Query("Update User set isLoggedIn=FALSE")
    void logoutUserAfter5Mins();
}
