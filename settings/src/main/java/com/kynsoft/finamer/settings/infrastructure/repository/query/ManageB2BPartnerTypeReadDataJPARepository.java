package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageB2BPartnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManageB2BPartnerTypeReadDataJPARepository extends JpaRepository<ManageB2BPartnerType, UUID>,
        JpaSpecificationExecutor<ManageB2BPartnerType> {

    Page<ManageB2BPartnerType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageB2BPartnerType b WHERE b.code = :code AND b.id <> :id AND b.deleted = false")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

}
