package com.kynsoft.finamer.settings.application.query.manageBankAccount.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageBankAccountResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.settings.domain.services.IManageBankAccountService;
import org.springframework.stereotype.Component;

@Component
public class FindManageBankAccountByIdQueryHandler implements IQueryHandler<FindManageBankAccountByIdQuery, ManageBankAccountResponse> {

    private final IManageBankAccountService service;

    public FindManageBankAccountByIdQueryHandler(IManageBankAccountService service) {
        this.service = service;
    }

    @Override
    public ManageBankAccountResponse handle(FindManageBankAccountByIdQuery query) {
        ManageBankAccountDto dto = service.findById(query.getId());
        return new ManageBankAccountResponse(dto);
    }
}
