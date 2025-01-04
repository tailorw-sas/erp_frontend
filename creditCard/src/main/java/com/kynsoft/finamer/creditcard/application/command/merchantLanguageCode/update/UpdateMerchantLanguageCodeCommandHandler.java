package com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManageLanguageDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.MerchantLanguageCodeDto;
import com.kynsoft.finamer.creditcard.domain.rules.merchantLanguageCode.MerchantLanguageCodeUniqueRule;
import com.kynsoft.finamer.creditcard.domain.services.IManageLanguageService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import com.kynsoft.finamer.creditcard.domain.services.IMerchantLanguageCodeService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateMerchantLanguageCodeCommandHandler implements ICommandHandler<UpdateMerchantLanguageCodeCommand> {

    private final IMerchantLanguageCodeService merchantLanguageCodeService;
    private final IManageMerchantService merchantService;
    private final IManageLanguageService languageService;

    public UpdateMerchantLanguageCodeCommandHandler(IMerchantLanguageCodeService merchantLanguageCodeService, IManageMerchantService merchantService, IManageLanguageService languageService) {
        this.merchantLanguageCodeService = merchantLanguageCodeService;
        this.merchantService = merchantService;
        this.languageService = languageService;
    }

    @Override
    public void handle(UpdateMerchantLanguageCodeCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "ID cannot be null."));

        MerchantLanguageCodeDto dto = this.merchantLanguageCodeService.findById(command.getId());
        ManageMerchantDto merchantDto = this.merchantService.findById(command.getManageMerchant());
        ManageLanguageDto languageDto = this.languageService.findById(command.getManageLanguage());

        RulesChecker.checkRule(new MerchantLanguageCodeUniqueRule(
                this.merchantLanguageCodeService,
                merchantDto.getId(),
                command.getManageLanguage(),
                command.getId()
        ));

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setMerchantLanguage, command.getMerchantLanguage(), dto.getMerchantLanguage(), update::setUpdate);
        updateEntity(dto::setManageMerchant, command.getManageMerchant(), dto.getManageMerchant() != null ? dto.getManageMerchant().getId() : null, this.merchantService::findById, update::setUpdate);
        updateEntity(dto::setManageLanguage, command.getManageLanguage(), dto.getManageLanguage() != null ? dto.getManageLanguage().getId() : null, this.languageService::findById, update::setUpdate);

        if (update.getUpdate() > 0){
            this.merchantLanguageCodeService.update(dto);
        }
    }

    private <T> void updateEntity(Consumer<T> setter, UUID newValue, UUID oldValue, EntityFinder<T> finder, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            T entity = finder.findById(newValue);
            setter.accept(entity);
            update.accept(1);
        }
    }

    @FunctionalInterface
    private interface EntityFinder<T> {
        T findById(UUID id);
    }
}
