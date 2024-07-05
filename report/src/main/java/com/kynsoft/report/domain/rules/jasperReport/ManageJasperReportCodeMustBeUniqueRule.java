package com.kynsoft.report.domain.rules.jasperReport;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;

import java.util.UUID;

public class ManageJasperReportCodeMustBeUniqueRule extends BusinessRule {

    private final IJasperReportTemplateService service;

    private final String code;

    private final UUID id;

    public ManageJasperReportCodeMustBeUniqueRule(IJasperReportTemplateService service, String code, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("code", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase())
        );
        this.service = service;
        this.code = code;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCodeAndNotId(code, id) > 0;
    }

}
