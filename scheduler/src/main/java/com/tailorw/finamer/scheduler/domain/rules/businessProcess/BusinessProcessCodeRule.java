package com.tailorw.finamer.scheduler.domain.rules.businessProcess;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.tailorw.finamer.scheduler.domain.services.IBusinessProcessService;

import java.util.Objects;

public class BusinessProcessCodeRule extends BusinessRule {

    private final String code;
    private final IBusinessProcessService service;

    public BusinessProcessCodeRule(String code, IBusinessProcessService service){
        super(DomainErrorMessage.BUSINESS_PROCESS_CODE_MUST_BE_UNIQUE, new ErrorField("code", DomainErrorMessage.BUSINESS_PROCESS_CODE_MUST_BE_UNIQUE.getReasonPhrase()));
        this.code = code;
        this.service = service;
    }

    @Override
    public boolean isBroken() {
        return Objects.nonNull(service.findByCode(code));
    }
}
