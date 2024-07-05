package com.kynsoft.finamer.settings.application.command.manageContact.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageContactDto;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.manageContact.ManageContactEmailMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageContactService;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManageContactCommandHandler implements ICommandHandler<UpdateManageContactCommand> {

    private final IManageContactService service;

    private final IManageHotelService hotelService;

    public UpdateManageContactCommandHandler(IManageContactService service, IManageHotelService hotelService) {
        this.service = service;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(UpdateManageContactCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Contact ID cannot be null."));
        RulesChecker.checkRule(new ManageContactEmailMustBeUniqueRule(this.service, command.getEmail(), command.getManageHotel(), command.getId()));

        ManageContactDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setEmail, command.getEmail(), dto.getEmail(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setPhone, command.getPhone(), dto.getPhone(), update::setUpdate);
        UpdateIfNotNull.updateInteger(dto::setPosition, command.getPosition(), dto.getPosition(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateHotel(dto::setManageHotel, command.getManageHotel(), dto.getManageHotel().getId(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
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

    private boolean updateHotel(Consumer<ManageHotelDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageHotelDto hotelDto = hotelService.findById(newValue);
            setter.accept(hotelDto);
            update.accept(1);

            return true;
        }
        return false;
    }
}
