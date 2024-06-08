package com.kynsoft.report.applications.query.reportTemplate;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetReportParameterByCodeResponse implements IResponse {

    private String parameters;


    public GetReportParameterByCodeResponse(String parameters) {
        this.parameters = parameters;
    }

}
