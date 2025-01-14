package com.kynsoft.finamer.payment.application.query.manageBankAccount.getByAccountNumberAndHotel;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageBankAccountResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import org.springframework.stereotype.Component;

@Component
public class FindManageBankAccountByIdQueryHandler implements IQueryHandler<FindManageBankAccountByIdQuery, ManageBankAccountResponse>  {

    private final IManageBankAccountService service;

    public FindManageBankAccountByIdQueryHandler(IManageBankAccountService service) {
        this.service = service;
    }

    @Override
    public ManageBankAccountResponse handle(FindManageBankAccountByIdQuery query) {
        ManageBankAccountDto bankAccountDto = service.findManageBankAccountByCodeAndHotelCode(query.getAccountNumber(), query.getHotelCode());

        return new ManageBankAccountResponse(bankAccountDto);
    }
}
