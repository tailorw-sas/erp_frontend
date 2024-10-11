package com.kynsoft.report.infrastructure.entity;

import com.kynsof.share.core.domain.BaseEntity;
import com.kynsoft.report.domain.dto.JasperReportParameterDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "jasper_report_parameter")
public class JasperReportParameter extends BaseEntity {

    private String paramName;

    private String type;

    private String module;

    private String service;

    private String label;

    // Relación de muchos a uno con JasperReportTemplate
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jasper_report_template_id", nullable = false)
    private JasperReportTemplate jasperReportTemplate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public JasperReportParameter(JasperReportParameterDto jasperReportParameterDto) {
        this.id = jasperReportParameterDto.getId();
        this.paramName = jasperReportParameterDto.getParamName();
        this.type = jasperReportParameterDto.getType();
        this.module = jasperReportParameterDto.getModule();
        this.service = jasperReportParameterDto.getService();
        this.label = jasperReportParameterDto.getLabel();
        this.jasperReportTemplate = new JasperReportTemplate(jasperReportParameterDto.getJasperReportTemplate());
    }

    public JasperReportParameterDto toAggregate() {
        return new JasperReportParameterDto(
                id,
                paramName,
                type,
                module,
                service,
                label,
                jasperReportTemplate.toAggregate()
        );
    }
}