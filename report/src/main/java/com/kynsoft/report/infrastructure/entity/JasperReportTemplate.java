package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateType;
import com.kynsof.share.core.domain.BaseEntity;
import com.kynsoft.report.domain.dto.status.Status;
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
@Table(name = "jasper_report_template")
public class JasperReportTemplate extends BaseEntity {

    private String code;
    private String name;
    private String description;
    private String file;
    @Enumerated(EnumType.STRING)
    private JasperReportTemplateType type;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String parameters;

    private Double parentIndex;
    private Double menuPosition;
    private String lanPath;
    private Boolean web;
    private Boolean subMenu;
    private Boolean sendEmail;
    private Boolean internal;
    private Boolean highRisk;
    private Boolean visible;
    private Boolean cancel;
    private String rootIndex;
    private String language;

    private String query;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "db_conection_id", nullable = true)
    private DBConection dbConection;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public JasperReportTemplate(JasperReportTemplateDto jasperReportTemplateDto) {
        this.id = jasperReportTemplateDto.getId();
        this.code = jasperReportTemplateDto.getCode();
        this.name = jasperReportTemplateDto.getName();
        this.description = jasperReportTemplateDto.getDescription();
        this.file = jasperReportTemplateDto.getFile();
        this.type = jasperReportTemplateDto.getType();
        this.parameters = jasperReportTemplateDto.getParameters();

        this.parentIndex = jasperReportTemplateDto.getParentIndex();
        this.menuPosition = jasperReportTemplateDto.getMenuPosition();
        this.lanPath = jasperReportTemplateDto.getLanPath();
        this.web = jasperReportTemplateDto.getWeb();
        this.subMenu = jasperReportTemplateDto.getSubMenu();
        this.sendEmail = jasperReportTemplateDto.getSendEmail();
        this.internal = jasperReportTemplateDto.getInternal();
        this.highRisk = jasperReportTemplateDto.getHighRisk();
        this.visible = jasperReportTemplateDto.getVisible();
        this.cancel = jasperReportTemplateDto.getCancel();
        this.rootIndex = jasperReportTemplateDto.getRootIndex();
        this.language = jasperReportTemplateDto.getLanguage();
        this.status = jasperReportTemplateDto.getStatus();
        this.dbConection = jasperReportTemplateDto.getDbConection() != null ? new DBConection(jasperReportTemplateDto.getDbConection()) : null;
        this.query = jasperReportTemplateDto.getQuery();
    }

    public JasperReportTemplateDto toAggregate() {
        String templateContentUrlS = file != null ? file : null;
        DBConectionDto conectionDto = dbConection != null ? dbConection.toAggregate() : null;
        return new JasperReportTemplateDto(
                id,
                code,
                name,
                description,
                templateContentUrlS,
                type,
                status,
                parameters,
                createdAt,
                parentIndex,
                menuPosition,
                lanPath,
                web,
                subMenu,
                sendEmail,
                internal,
                highRisk,
                visible,
                cancel,
                rootIndex,
                language,
                conectionDto,
                query
        );
    }

}
