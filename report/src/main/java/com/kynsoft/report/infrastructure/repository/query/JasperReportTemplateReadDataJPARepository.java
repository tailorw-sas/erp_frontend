package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.JasperReportTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JasperReportTemplateReadDataJPARepository extends JpaRepository<JasperReportTemplate, UUID>, JpaSpecificationExecutor<JasperReportTemplate> {
    Page<JasperReportTemplate> findAll(Specification specification, Pageable pageable);

    Optional<JasperReportTemplate> findByCode(String code);

    @Query("SELECT COUNT(b) FROM JasperReportTemplate b WHERE b.code = :code AND b.id <> :id")
    Long countByCodeAndNotId(@Param("code") String code, @Param("id") UUID id);
}
