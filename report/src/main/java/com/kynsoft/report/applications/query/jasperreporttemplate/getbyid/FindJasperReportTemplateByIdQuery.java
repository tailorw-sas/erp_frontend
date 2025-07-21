package com.kynsoft.report.applications.query.jasperreporttemplate.getbyid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindJasperReportTemplateByIdQuery implements IQuery {

    private final UUID id;
    private final boolean includeParameters;

    public FindJasperReportTemplateByIdQuery(UUID id, boolean includeParameters) {
        this.id = id;
        this.includeParameters = includeParameters;
    }

}
