package com.kynsoft.report.domain.rules.dbconection;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.services.IDBConectionService;

import java.util.UUID;

public class DBConectionOldPasswordCheckRule extends BusinessRule {

    private final IDBConectionService service;

    private final String password;

    private final UUID id;

    public DBConectionOldPasswordCheckRule(IDBConectionService service, String password, UUID id) {
        super(
                DomainErrorMessage.PASSWORD_MISMATCH,
                new ErrorField("password", DomainErrorMessage.PASSWORD_MISMATCH.getReasonPhrase())
        );
        this.service = service;
        this.password = password;
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        DBConectionDto dto = this.service.findById(id);
        String oldPassword = dto.getPassword();
        return !oldPassword.equals(password);
    }

}
