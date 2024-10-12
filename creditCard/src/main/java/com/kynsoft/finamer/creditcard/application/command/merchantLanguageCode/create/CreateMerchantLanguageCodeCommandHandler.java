package com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageLanguageDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.MerchantLanguageCodeDto;
import com.kynsoft.finamer.creditcard.domain.rules.merchantLanguageCode.MerchantLanguageCodeUniqueRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageLanguageService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import com.kynsoft.finamer.creditcard.domain.services.IMerchantLanguageCodeService;
import org.springframework.stereotype.Component;

@Component
public class CreateMerchantLanguageCodeCommandHandler implements ICommandHandler<CreateMerchantLanguageCodeCommand> {

    private final IMerchantLanguageCodeService merchantLanguageCodeService;
    private final IManageMerchantService merchantService;
    private final IManageLanguageService languageService;

    public CreateMerchantLanguageCodeCommandHandler(IMerchantLanguageCodeService merchantLanguageCodeService, IManageMerchantService merchantService, IManageLanguageService languageService) {
        this.merchantLanguageCodeService = merchantLanguageCodeService;
        this.merchantService = merchantService;
        this.languageService = languageService;
    }

    @Override
    public void handle(CreateMerchantLanguageCodeCommand command) {
        ManageMerchantDto merchantDto = this.merchantService.findById(command.getManageMerchant());
        ManageLanguageDto languageDto = this.languageService.findById(command.getManageLanguage());

        RulesChecker.checkRule(new MerchantLanguageCodeUniqueRule(this.merchantLanguageCodeService, merchantDto.getId(), command.getManageLanguage(), command.getId()));

        MerchantLanguageCodeDto merchantLanguageCodeDto = this.merchantLanguageCodeService.create(
                new MerchantLanguageCodeDto(
                        command.getId(), command.getName(), command.getMerchantLanguage(),
                        languageDto, merchantDto
                )
        );
    }
}
