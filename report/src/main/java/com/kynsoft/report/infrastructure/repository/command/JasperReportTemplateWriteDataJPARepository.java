package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.JasperReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JasperReportTemplateWriteDataJPARepository extends JpaRepository<JasperReportTemplate, UUID> {
}
