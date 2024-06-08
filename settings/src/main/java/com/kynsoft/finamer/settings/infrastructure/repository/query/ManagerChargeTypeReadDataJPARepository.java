package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManagerChargeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManagerChargeTypeReadDataJPARepository extends JpaRepository<ManagerChargeType, UUID>,
        JpaSpecificationExecutor<ManagerChargeType> {

    Page<ManagerChargeType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManagerChargeType b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

}
