package com.kynsoft.report.domain.dto.status;

import com.kynsoft.report.domain.dto.JasperReportParameterDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JasperReportTemplateWithParamsDto extends JasperReportTemplateDto {
    private List<JasperReportParameterDto> parameters;
}
