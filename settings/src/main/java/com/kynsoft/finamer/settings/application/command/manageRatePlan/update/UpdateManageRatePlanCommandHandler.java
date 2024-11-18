package com.kynsoft.finamer.settings.application.command.manageRatePlan.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageRatePlanKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import com.kynsoft.finamer.settings.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRatePlan.ProducerUpdateManageRatePlanService;

import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageRatePlanCommandHandler implements ICommandHandler<UpdateManageRatePlanCommand> {

    private final IManageRatePlanService service;

    private final IManageHotelService hotelService;

    private final ProducerUpdateManageRatePlanService producerUpdateManageRatePlanService;

    public UpdateManageRatePlanCommandHandler(IManageRatePlanService service, IManageHotelService hotelService,
                                              ProducerUpdateManageRatePlanService producerUpdateManageRatePlanService) {
        this.service = service;
        this.hotelService = hotelService;
        this.producerUpdateManageRatePlanService = producerUpdateManageRatePlanService;
    }

    @Override
    public void handle(UpdateManageRatePlanCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Rate Plan ID cannot be null."));

        ManageRatePlanDto test = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setDescription, command.getDescription(), test.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(test::setName, command.getName(), test.getName(), update::setUpdate);
        this.updateStatus(test::setStatus, command.getStatus(), test.getStatus(), update::setUpdate);
        updateHotel(test::setHotel, command.getHotel(), test.getHotel().getId(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(test);
            this.producerUpdateManageRatePlanService.update(new UpdateManageRatePlanKafka(test.getId(), test.getName(), test.getStatus().name()));
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
