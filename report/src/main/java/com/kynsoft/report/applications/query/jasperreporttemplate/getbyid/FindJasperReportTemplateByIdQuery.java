package com.kynsoft.report.applications.query.jasperreporttemplate.getbyid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindJasperReportTemplateByIdQuery implements IQuery {

    private final UUID id;

    public FindJasperReportTemplateByIdQuery(UUID id) {
        this.id = id;
    }

}
