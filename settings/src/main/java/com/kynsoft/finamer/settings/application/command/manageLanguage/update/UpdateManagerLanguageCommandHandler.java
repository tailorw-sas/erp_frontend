package com.kynsoft.finamer.settings.application.command.manageLanguage.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageLanguageKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagerLanguageDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.managerLanguage.ManageLanguageDefaultMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageLanguage.ProducerUpdateManageLanguageService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateManagerLanguageCommandHandler implements ICommandHandler<UpdateManagerLanguageCommand> {

    private final IManagerLanguageService service;

    private final ProducerUpdateManageLanguageService producerUpdateManageLanguageService;

    public UpdateManagerLanguageCommandHandler(IManagerLanguageService service,
                                               ProducerUpdateManageLanguageService producerUpdateManageLanguageService) {
        this.service = service;
        this.producerUpdateManageLanguageService = producerUpdateManageLanguageService;
    }

    @Override
    public void handle(UpdateManagerLanguageCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Language ID cannot be null."));
        if(command.getDefaults() != null) {
            if(command.getDefaults()) {
                RulesChecker.checkRule(new ManageLanguageDefaultMustBeUniqueRule(this.service, command.getId()));
            }
        }
        ManagerLanguageDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsEnabled, command.getIsEnabled(), dto.getIsEnabled(), update::setUpdate);
        this.updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setDefaults, command.getDefaults(), dto.getDefaults(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
            this.producerUpdateManageLanguageService.update(new UpdateManageLanguageKafka(dto.getId(), dto.getName()));
        }

    }

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }
}
