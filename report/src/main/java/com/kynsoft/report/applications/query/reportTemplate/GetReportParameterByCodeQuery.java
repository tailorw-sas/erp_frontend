package com.kynsoft.report.applications.query.reportTemplate;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class GetReportParameterByCodeQuery implements IQuery {

    private final String reportCode;

    public GetReportParameterByCodeQuery(String reportCode) {
        this.reportCode = reportCode;
    }

}
