package com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.MerchantLanguageCodeDto;
import com.kynsoft.finamer.creditcard.domain.services.IMerchantLanguageCodeService;
import org.springframework.stereotype.Component;

@Component
public class DeleteMerchantLanguageCodeCommandHandler implements ICommandHandler<DeleteMerchantLanguageCodeCommand> {

    private final IMerchantLanguageCodeService merchantLanguageCodeService;

    public DeleteMerchantLanguageCodeCommandHandler(IMerchantLanguageCodeService merchantLanguageCodeService) {
        this.merchantLanguageCodeService = merchantLanguageCodeService;
    }

    @Override
    public void handle(DeleteMerchantLanguageCodeCommand command) {
        MerchantLanguageCodeDto merchantLanguageCodeDto = this.merchantLanguageCodeService.findById(command.getId());

        this.merchantLanguageCodeService.delete(merchantLanguageCodeDto);
    }
}
