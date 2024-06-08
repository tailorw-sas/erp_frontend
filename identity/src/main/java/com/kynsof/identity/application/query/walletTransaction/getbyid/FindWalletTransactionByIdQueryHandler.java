package com.kynsof.identity.application.query.walletTransaction.getbyid;

import com.kynsof.identity.domain.dto.WalletTransactionDto;
import com.kynsof.identity.domain.interfaces.service.IWalletTransactionService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class FindWalletTransactionByIdQueryHandler implements IQueryHandler<FindWalletTransactionByIdQuery, WalletTransactionByIdResponse>  {

    private final IWalletTransactionService service;

    public FindWalletTransactionByIdQueryHandler(IWalletTransactionService service) {
        this.service = service;
    }

    @Override
    public WalletTransactionByIdResponse handle(FindWalletTransactionByIdQuery query) {
        WalletTransactionDto response = service.findById(query.getId());

        return new WalletTransactionByIdResponse(response);
    }
}
