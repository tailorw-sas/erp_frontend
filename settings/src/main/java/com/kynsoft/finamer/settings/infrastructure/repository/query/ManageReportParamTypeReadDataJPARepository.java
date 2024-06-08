package com.kynsoft.finamer.settings.infrastructure.repository.query;

import com.kynsoft.finamer.settings.infrastructure.identity.ManageReportParamType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManageReportParamTypeReadDataJPARepository extends JpaRepository<ManageReportParamType, UUID>,
        JpaSpecificationExecutor<ManageReportParamType> {

    Page<ManageReportParamType> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(b) FROM ManageReportParamType b WHERE b.name = :name AND b.id <> :id")
    Long countByNameAndNotId(@Param("name") String name, @Param("id") UUID id);
}
