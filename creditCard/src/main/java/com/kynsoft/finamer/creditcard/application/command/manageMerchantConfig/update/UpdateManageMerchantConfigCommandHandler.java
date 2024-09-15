package com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManageMerchantConfigCommandHandler implements ICommandHandler<UpdateManageMerchantConfigCommand> {

    private final IManageMerchantService service;
    private final IManageMerchantConfigService configService;

    public UpdateManageMerchantConfigCommandHandler(IManageMerchantService service, IManageMerchantConfigService configService) {
        this.service = service;
        this.configService = configService;
    }

    @Override
    public void handle(UpdateManageMerchantConfigCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Merchant Config ID cannot be null."));

        ManagerMerchantConfigDto test = this.configService.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setUrl, command.getUrl(), test.getUrl(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setAltUrl, command.getAltUrl(), test.getAltUrl(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setSuccessUrl, command.getSuccessUrl(), test.getSuccessUrl(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setErrorUrl, command.getErrorUrl(), test.getErrorUrl(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDeclinedUrl, command.getDeclinedUrl(), test.getDeclinedUrl(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setMerchantType, command.getMerchantType(), test.getMerchantType(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setInstitutionCode, command.getInstitutionCode(), test.getInstitutionCode(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setMerchantNumber, command.getMerchantNumber(), test.getMerchantNumber(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setMerchantTerminal, command.getMerchantTerminal(), test.getMerchantTerminal(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setMethod, command.getMethod().name(), test.getMethod(), update::setUpdate);

        //this.updateStatus(test::setMethod, command.getMethod(), test.getMethod(), update::setUpdate);
        this.updateManageMerchant(test::setManageMerchantDto, command.getManageMerchant(), test.getManageMerchantDto().getId(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.configService.update(test);
        }

    }

    private boolean updateStatus(Consumer<Method> setter, Method newValue, Method oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManageMerchant(Consumer<ManageMerchantDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageMerchantDto managerMerchant = this.service.findById(newValue);
            setter.accept(managerMerchant);
            update.accept(1);

            return true;
        }
        return false;
    }

}
