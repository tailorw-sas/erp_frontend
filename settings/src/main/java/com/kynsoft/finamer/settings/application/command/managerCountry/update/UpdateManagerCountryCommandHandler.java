package com.kynsoft.finamer.settings.application.command.managerCountry.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerLanguageDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.managerCountry.ManagerCountryDialCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.managerCountry.ManagerCountryNameMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManagerCountryService;
import com.kynsoft.finamer.settings.domain.services.IManagerLanguageService;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagerCountryCommandHandler implements ICommandHandler<UpdateManagerCountryCommand> {

    private final IManagerCountryService service;
    private final IManagerLanguageService serviceLanguage;

    public UpdateManagerCountryCommandHandler(IManagerCountryService service,
                                              IManagerLanguageService serviceLanguage) {
        this.service = service;
        this.serviceLanguage = serviceLanguage;
    }

    @Override
    public void handle(UpdateManagerCountryCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manager Country ID cannot be null."));

        ManagerCountryDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setIso3, command.getIso3(), test.getIso3(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(test::setIsDefault, command.getIsDefault(), test.getIsDefault(), update::setUpdate);
        this.updateManagerLanguage(test::setManagerLanguage, command.getManagerLanguage(), test.getManagerLanguage().getId(), update::setUpdate);

        if (UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate)) {
            RulesChecker.checkRule(new ManagerCountryNameMustBeUniqueRule(this.service, command.getName(), command.getId()));
        }
        //Si el Dial Code cambio, hay que verificar que este correcto
        if (UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDialCode, command.getDialCode(), test.getDialCode(), update::setUpdate)) {
            RulesChecker.checkRule(new ManagerCountryDialCodeSizeRule(command.getDialCode()));
        }

        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
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

    private boolean updateManagerLanguage(Consumer<ManagerLanguageDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerLanguageDto languageDto = this.serviceLanguage.findById(newValue);
            setter.accept(languageDto);
            update.accept(1);

            return true;
        }
        return false;
    }

}
