package com.kynsoft.report.applications.query.menu;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.report.applications.query.dbconection.getById.DBConectionResponse;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateType;
import com.kynsoft.report.domain.dto.status.ModuleSystems;
import com.kynsoft.report.domain.dto.status.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportMenuResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private Double menuPosition;
    private Boolean highRisk;
    private ModuleSystems moduleSystems;

    public ReportMenuResponse(JasperReportTemplateDto jasperReportTemplateDto) {
        this.id = jasperReportTemplateDto.getId();
        this.code = jasperReportTemplateDto.getCode();
        this.name = jasperReportTemplateDto.getName();
        this.menuPosition = jasperReportTemplateDto.getMenuPosition();
        this.highRisk = false;//jasperReportTemplateDto.getHighRisk();
        this.moduleSystems = jasperReportTemplateDto.getModuleSystems();
    }

}
