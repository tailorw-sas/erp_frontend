package com.kynsoft.report.infrastructure.entity;

import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateType;
import com.kynsof.share.core.domain.BaseEntity;
import com.kynsoft.report.domain.dto.status.ModuleSystems;
import com.kynsoft.report.domain.dto.status.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private ModuleSystems moduleSystems;
    private Double menuPosition;

    private String query;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "db_conection_id", nullable = true)
    private DBConection dbConection;

    @OneToMany(mappedBy = "jasperReportTemplate", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<JasperReportParameter> parametersList;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updateAt;

    public JasperReportTemplate(JasperReportTemplateDto jasperReportTemplateDto) {
        this.id = jasperReportTemplateDto.getId();
        this.code = jasperReportTemplateDto.getCode();
        this.name = jasperReportTemplateDto.getName();
        this.description = jasperReportTemplateDto.getDescription();
        this.file = jasperReportTemplateDto.getFile();
        this.type = jasperReportTemplateDto.getType();
        this.menuPosition = jasperReportTemplateDto.getMenuPosition();
        this.status = jasperReportTemplateDto.getStatus();
        this.dbConection = jasperReportTemplateDto.getDbConectionDto() != null ? new DBConection(jasperReportTemplateDto.getDbConectionDto()) : null;
        this.moduleSystems = jasperReportTemplateDto.getModuleSystems();
    }

    public JasperReportTemplateDto toAggregate() {
        String templateContentUrlS = file != null ? file : null;
        DBConectionDto connectionDto = dbConection != null ? dbConection.toAggregate() : null;
        return new JasperReportTemplateDto(
                id,
                code,
                name,
                description,
                templateContentUrlS,
                type,
                status,
                createdAt,
                menuPosition,
                connectionDto,
                moduleSystems,
                query
        );
    }

}
