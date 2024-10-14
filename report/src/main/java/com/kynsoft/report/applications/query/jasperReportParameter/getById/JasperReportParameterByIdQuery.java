package com.kynsoft.report.applications.query.jasperReportParameter.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class JasperReportParameterByIdQuery implements IQuery {

    private final UUID id;

    public JasperReportParameterByIdQuery(UUID id) {
        this.id = id;
    }
}
