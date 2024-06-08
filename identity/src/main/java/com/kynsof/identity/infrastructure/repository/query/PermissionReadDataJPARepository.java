package com.kynsof.identity.infrastructure.repository.query;

import com.kynsof.identity.infrastructure.identity.Permission;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionReadDataJPARepository extends JpaRepository<Permission, UUID>, JpaSpecificationExecutor<Permission> {
    Page<Permission> findAll(Specification specification, Pageable pageable);

    @Query("SELECT p FROM Permission p WHERE p.module.id = :moduleId")
    Set<Permission> findByModuleId(UUID moduleId);

    @Query("SELECT p FROM Permission p JOIN p.userPermissionBusinesses urb JOIN urb.business b " +
            "WHERE p.module.id = :moduleId AND b.id = :businessId AND urb.deleted = false")
    List<Permission> findByModuleIdAndBusinessId(@Param("moduleId") UUID moduleId, @Param("businessId") UUID businessId);

    @Query("SELECT COUNT(b) FROM Permission b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

}
