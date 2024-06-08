package com.kynsoft.finamer.settings.application.command.managerMessage.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagerLanguageDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMessageDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;
import com.kynsoft.finamer.settings.domain.services.IManagerMessageService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManagerMessageCommandHandler implements ICommandHandler<UpdateManagerMessageCommand> {

    private final IManagerMessageService service;

    private final IManagerLanguageService languageService;

    public UpdateManagerMessageCommandHandler(IManagerMessageService service, IManagerLanguageService languageService) {
        this.service = service;
        this.languageService = languageService;
    }

    @Override
    public void handle(UpdateManagerMessageCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Message ID cannot be null."));

        ManagerMessageDto messageDto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(messageDto::setDescription, command.getDescription(), messageDto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(messageDto::setName, command.getName(), messageDto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(messageDto::setType, command.getType(), messageDto.getType(), update::setUpdate);

        updateStatus(messageDto::setStatus, command.getStatus(), messageDto.getStatus(), update::setUpdate);
        updateLanguage(messageDto::setLanguage, command.getLanguage(), messageDto.getLanguage().getId(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(messageDto);
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

    private boolean updateLanguage(Consumer<ManagerLanguageDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerLanguageDto languageDto = this.languageService.findById(newValue);
            setter.accept(languageDto);
            update.accept(1);

            return true;
        }
        return false;
    }
}
