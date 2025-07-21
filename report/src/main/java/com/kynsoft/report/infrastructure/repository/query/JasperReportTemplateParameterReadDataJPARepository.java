package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.JasperReportParameter;
import com.kynsoft.report.infrastructure.entity.JasperReportTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JasperReportTemplateParameterReadDataJPARepository extends JpaRepository<JasperReportParameter, UUID>, JpaSpecificationExecutor<JasperReportParameter> {
    Page<JasperReportParameter> findAll(Specification specification, Pageable pageable);

    @Query("SELECT p FROM JasperReportParameter p WHERE p.jasperReportTemplate.id = :templateId")
    java.util.List<JasperReportParameter> findByTemplateId(@Param("templateId") UUID templateId);

}
