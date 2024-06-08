package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageEmployeeGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ManageEmployeeGroupReadDataJPARepository extends JpaRepository<ManageEmployeeGroup, UUID>,
        JpaSpecificationExecutor<ManageEmployeeGroup> {

    Page<ManageEmployeeGroup> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageEmployeeGroup b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);

}
