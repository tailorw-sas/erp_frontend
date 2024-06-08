package com.kynsof.identity.application.query.walletTransaction.search;

import com.kynsof.identity.domain.interfaces.service.IWalletTransactionService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.stereotype.Component;

@Component
public class GetSearchWalletTransactionQueryHandler implements IQueryHandler<GetSearchWalletTransactionQuery, PaginatedResponse>{
    private final IWalletTransactionService service;
    
    public GetSearchWalletTransactionQueryHandler(IWalletTransactionService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchWalletTransactionQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
