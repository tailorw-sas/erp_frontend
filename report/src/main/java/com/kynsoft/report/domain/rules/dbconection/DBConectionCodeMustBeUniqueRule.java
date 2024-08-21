package com.kynsoft.report.domain.rules.dbconection;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.report.domain.services.IDBConectionService;

import java.util.UUID;

public class DBConectionCodeMustBeUniqueRule extends BusinessRule {

    private final IDBConectionService service;

    private final String code;

    private final UUID id;

    public DBConectionCodeMustBeUniqueRule(IDBConectionService service, String code, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("id", DomainErrorMessage.ITEM_ALREADY_EXITS.getReasonPhrase()
                ));
        this.service = service;
        this.code = code;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByCodeAndNotId(code, id) > 0;
    }
}
