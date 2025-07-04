package com.kynsoft.report.domain.dto;

import com.kynsoft.report.domain.dto.status.ModuleSystems;
import com.kynsoft.report.domain.dto.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JasperReportTemplateDto {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String file;
    private JasperReportTemplateType type;
    private Status status;
    private LocalDateTime createdAt;
    private Double menuPosition;
    private DBConectionDto dbConectionDto;
    private ModuleSystems moduleSystems; // Campo para el módulo del sistema
    private String query;

    public JasperReportTemplateDto(UUID id, String templateCode, String templateName, 
                                   String templateDescription, String templateContentUrl, 
                                   JasperReportTemplateType type,
                                    Double menuPosition,
                                   Status status, DBConectionDto dbConection, String query
                                   ) {
        this.id = id;
        this.code = templateCode;
        this.name = templateName;
        this.description = templateDescription;
        this.file = templateContentUrl;
        this.type = type;
        this.menuPosition = menuPosition;
        this.status = status;
        this.dbConectionDto = dbConection;
        this.query = query;
    }
}
