package com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation.ManageBankReconciliationResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankReconciliationDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageBankReconciliationService;
import org.springframework.stereotype.Component;

@Component
public class FindManageBankReconciliationByIdQueryHandler implements IQueryHandler<FindManageBankReconciliationByIdQuery, ManageBankReconciliationResponse> {

    private final IManageBankReconciliationService bankReconciliationService;

    public FindManageBankReconciliationByIdQueryHandler(IManageBankReconciliationService bankReconciliationService) {
        this.bankReconciliationService = bankReconciliationService;
    }

    @Override
    public ManageBankReconciliationResponse handle(FindManageBankReconciliationByIdQuery query) {

        ManageBankReconciliationDto bankReconciliationDto = this.bankReconciliationService.findById(query.getId());

        return new ManageBankReconciliationResponse(bankReconciliationDto);
    }
}
