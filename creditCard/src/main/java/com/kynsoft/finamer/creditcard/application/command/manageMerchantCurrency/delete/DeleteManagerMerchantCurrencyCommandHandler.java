package com.kynsoft.finamer.creditcard.application.command.manageMerchantCurrency.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantCurrencyDto;
import com.kynsoft.finamer.creditcard.domain.services.IManagerMerchantCurrencyService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerMerchantCurrencyCommandHandler implements ICommandHandler<DeleteManagerMerchantCurrencyCommand> {

    private final IManagerMerchantCurrencyService service;

    public DeleteManagerMerchantCurrencyCommandHandler(IManagerMerchantCurrencyService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerMerchantCurrencyCommand command) {
        ManagerMerchantCurrencyDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
