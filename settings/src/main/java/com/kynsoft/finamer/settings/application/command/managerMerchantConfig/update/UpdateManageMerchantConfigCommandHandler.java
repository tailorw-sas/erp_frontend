package com.kynsoft.finamer.settings.application.command.managerMerchantConfig.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageMerchantConfigKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Method;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantConfig.ProducerUpdateManagerMerchantConfigService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManageMerchantConfigCommandHandler implements ICommandHandler<UpdateManageMerchantConfigCommand> {

    private final IManagerMerchantService service;
    private final IManageMerchantConfigService configService;
    private final ProducerUpdateManagerMerchantConfigService producerService;

    public UpdateManageMerchantConfigCommandHandler(IManagerMerchantService service, IManageMerchantConfigService configService, ProducerUpdateManagerMerchantConfigService producerService) {
        this.service = service;
        this.configService = configService;
        this.producerService = producerService;
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

        this.updateStatus(test::setMethod, command.getMethod(), test.getMethod(), update::setUpdate);
        this.updateManagerMerchant(test::setManagerMerchantDto, command.getManageMerchant(), test.getManagerMerchantDto().getId(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.configService.update(test);
            this.producerService.update(new UpdateManageMerchantConfigKafka(test.getId()));
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

    private boolean updateManagerMerchant(Consumer<ManagerMerchantDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerMerchantDto managerMerchant = this.service.findById(newValue);
            setter.accept(managerMerchant);
            update.accept(1);

            return true;
        }
        return false;
    }

}
