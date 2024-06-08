package com.kynsoft.finamer.settings.application.command.manageCityState.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerTimeZoneDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageCityStateService;
import com.kynsoft.finamer.settings.domain.services.IManagerCountryService;
import com.kynsoft.finamer.settings.domain.services.IManagerTimeZoneService;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageCityStateCommandHandler implements ICommandHandler<UpdateManageCityStateCommand> {
    
    private final IManageCityStateService service;
    private final IManagerCountryService serviceCountry;
    private final IManagerTimeZoneService serviceTimeZone;

    public UpdateManageCityStateCommandHandler(IManageCityStateService service,
                                               IManagerCountryService serviceCountry,
                                               IManagerTimeZoneService serviceTimeZone) {
        this.service = service;
        this.serviceCountry = serviceCountry;
        this.serviceTimeZone = serviceTimeZone;
    }

    @Override
    public void handle(UpdateManageCityStateCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage City State ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getCountry(), "country", "Manage Country ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getTimeZone(), "timeZone", "Manage Time Zone ID cannot be null."));

        ManageCityStateDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        this.updateManageCountry(test::setCountry, command.getCountry(), test.getCountry().getId(), update::setUpdate);
        this.updateManageTimeZone(test::setTimeZone, command.getTimeZone(), test.getTimeZone().getId(), update::setUpdate);

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);

        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
        }

    }

    private boolean updateManageCountry(Consumer<ManagerCountryDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerCountryDto countryDto = this.serviceCountry.findById(newValue);
            setter.accept(countryDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManageTimeZone(Consumer<ManagerTimeZoneDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerTimeZoneDto timeZoneDto = this.serviceTimeZone.findById(newValue);
            setter.accept(timeZoneDto);
            update.accept(1);

            return true;
        }
        return false;
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
