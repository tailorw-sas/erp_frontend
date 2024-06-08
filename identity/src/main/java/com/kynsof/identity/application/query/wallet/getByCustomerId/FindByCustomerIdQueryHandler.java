package com.kynsof.identity.application.query.wallet.getByCustomerId;

import com.kynsof.identity.domain.dto.WalletDto;
import com.kynsof.identity.domain.interfaces.service.IWalletService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class FindByCustomerIdQueryHandler implements IQueryHandler<FindByCustomerIdQuery, WalletResponse>  {

    private final IWalletService serviceImpl;

    public FindByCustomerIdQueryHandler(IWalletService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public WalletResponse handle(FindByCustomerIdQuery query) {
        WalletDto walletDto = serviceImpl.findByCustomerId(query.getCustomerId());

        return new WalletResponse(walletDto);
    }
}
