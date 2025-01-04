package com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageBankReconciliationService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageBankReconciliationQueryHandler implements IQueryHandler<GetSearchManageBankReconciliationQuery, PaginatedResponse> {

    private final IManageBankReconciliationService bankReconciliationService;

    public GetSearchManageBankReconciliationQueryHandler(IManageBankReconciliationService bankReconciliationService) {
        this.bankReconciliationService = bankReconciliationService;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageBankReconciliationQuery query) {
        return this.bankReconciliationService.search(query.getPageable(),query.getFilter());
    }
}
