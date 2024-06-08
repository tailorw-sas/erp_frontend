package com.kynsof.identity.infrastructure.repository.query;

import com.kynsof.identity.infrastructure.identity.Permission;
import com.kynsof.identity.infrastructure.identity.UserPermissionBusiness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserPermissionBusinessReadDataJPARepository extends JpaRepository<UserPermissionBusiness, UUID>, JpaSpecificationExecutor<UserPermissionBusiness> {

    Page<UserPermissionBusiness> findAll(Specification specification, Pageable pageable);

    @Query("SELECT upb FROM UserPermissionBusiness upb WHERE upb.user.id = :userId AND upb.business.id = :businessId AND upb.deleted = false")
    List<UserPermissionBusiness> findByUserAndBusiness(UUID userId, UUID businessId);

    @Query("SELECT COUNT(upb) FROM UserPermissionBusiness upb WHERE upb.user.id = :userId AND upb.business.id = :businessId")
    Long countByUserAndBusiness(UUID userId, UUID businessId);

    @Query("SELECT COUNT(upb) FROM UserPermissionBusiness upb WHERE upb.user.id = :userId AND upb.business.id = :businessId AND upb.deleted = false")
    Long countByUserAndBusinessAndNotDeleted(UUID userId, UUID businessId);

    @Query("SELECT p FROM UserPermissionBusiness upb JOIN upb.permission p WHERE upb.user.id = :userId AND upb.business.id = :businessId AND upb.deleted = false")
    Set<Permission> findPermissionsByUserIdAndBusinessId(UUID userId, UUID businessId);

   // @Query("SELECT p FROM UserPermissionBusiness upb JOIN upb.permission p WHERE upb.user.id = :userId AND upb.business.id = :businessId AND upb.deleted = false")
    List<UserPermissionBusiness> findUserPermissionBusinessByUserId(UUID userId);
}
