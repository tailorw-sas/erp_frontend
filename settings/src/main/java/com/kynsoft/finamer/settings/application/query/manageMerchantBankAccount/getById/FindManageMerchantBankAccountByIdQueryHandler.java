package com.kynsoft.finamer.settings.application.query.manageMerchantBankAccount.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageMerchantBankAccountResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantBankAccountDto;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantBankAccountService;
import org.springframework.stereotype.Component;

@Component
public class FindManageMerchantBankAccountByIdQueryHandler implements IQueryHandler<FindManageMerchantBankAccountByIdQuery, ManageMerchantBankAccountResponse> {

    private final IManageMerchantBankAccountService service;

    public FindManageMerchantBankAccountByIdQueryHandler(IManageMerchantBankAccountService service) {
        this.service = service;
    }

    @Override
    public ManageMerchantBankAccountResponse handle(FindManageMerchantBankAccountByIdQuery query) {
        ManageMerchantBankAccountDto dto = service.findById(query.getId());

        return new ManageMerchantBankAccountResponse(dto);
    }
}
