package com.kynsoft.finamer.creditcard.application.query.managerBank.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManagerBankResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.creditcard.domain.services.IManagerBankService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerBankByIdQueryHandler implements IQueryHandler<FindManagerBankByIdQuery, ManagerBankResponse>  {

    private final IManagerBankService service;

    public FindManagerBankByIdQueryHandler(IManagerBankService service) {
        this.service = service;
    }

    @Override
    public ManagerBankResponse handle(FindManagerBankByIdQuery query) {
        ManagerBankDto response = service.findById(query.getId());

        return new ManagerBankResponse(response);
    }
}
