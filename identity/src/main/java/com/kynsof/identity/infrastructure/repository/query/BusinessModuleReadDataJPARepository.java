package com.kynsof.identity.infrastructure.repository.query;

import com.kynsof.identity.infrastructure.identity.BusinessModule;
import com.kynsof.identity.infrastructure.identity.ModuleSystem;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BusinessModuleReadDataJPARepository extends JpaRepository<BusinessModule, UUID>, JpaSpecificationExecutor<BusinessModule> {
    Page<BusinessModule> findAll(Specification specification, Pageable pageable);

    @Query("SELECT bm FROM BusinessModule bm WHERE bm.business.id = :businessId AND deleted = false")
    List<BusinessModule> findByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT bm.module FROM BusinessModule bm WHERE bm.business.id = :businessId")
    List<ModuleSystem> findModulesByBusinessId(@Param("businessId") UUID businessId);

    @Query("SELECT m FROM BusinessModule bm JOIN bm.module m WHERE bm.business.id = :businessId")
    List<ModuleSystem> findModuleSystemByBusinessId(UUID businessId);

    @Query("SELECT COUNT(b) FROM BusinessModule b WHERE b.business.id = :businessId AND b.module.id = :moduleId AND deleted = false")
    Long countByBussinessIdAndModuleId(@Param("businessId") UUID businessId, @Param("moduleId") UUID moduleId);

}
