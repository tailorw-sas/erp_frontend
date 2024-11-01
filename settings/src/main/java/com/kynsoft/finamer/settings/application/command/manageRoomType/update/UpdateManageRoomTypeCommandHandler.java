package com.kynsoft.finamer.settings.application.command.manageRoomType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageRoomTypeKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.rules.manageRoomType.ManageRoomTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import com.kynsoft.finamer.settings.domain.services.IManageRoomTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRoomType.ProducerUpdateManageRoomTypeService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManageRoomTypeCommandHandler implements ICommandHandler<UpdateManageRoomTypeCommand> {

    private final IManageRoomTypeService service;

    private final IManageHotelService hotelService;

    private final ProducerUpdateManageRoomTypeService producerUpdateManageRoomTypeService;

    public UpdateManageRoomTypeCommandHandler(IManageRoomTypeService service, IManageHotelService hotelService,
                                              ProducerUpdateManageRoomTypeService producerUpdateManageRoomTypeService) {
        this.service = service;
        this.hotelService = hotelService;
        this.producerUpdateManageRoomTypeService = producerUpdateManageRoomTypeService;
    }

    @Override
    public void handle(UpdateManageRoomTypeCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Room Type ID cannot be null."));

        ManageRoomTypeDto dto = service.findById(command.getId());
        RulesChecker.checkRule(new ManageRoomTypeCodeMustBeUniqueRule(service, dto.getCode(), command.getManageHotel(), dto.getId()));

        ManageHotelDto hotelDto = hotelService.findById(command.getManageHotel());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        updateHotel(dto::setManageHotel, command.getManageHotel(), dto.getManageHotel().getId(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
            this.producerUpdateManageRoomTypeService.update(new UpdateManageRoomTypeKafka(dto.getId(), dto.getName(), dto.getStatus().name()));
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
