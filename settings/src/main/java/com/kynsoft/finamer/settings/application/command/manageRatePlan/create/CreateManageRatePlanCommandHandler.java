package com.kynsoft.finamer.settings.application.command.manageRatePlan.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRatePlanKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.settings.domain.rules.manageRatePlan.ManageRatePlanCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageRatePlan.ManageRatePlanCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageRatePlan.ManageRatePlanNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import com.kynsoft.finamer.settings.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRatePlan.ProducerReplicateManageRatePlanService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRatePlanCommandHandler implements ICommandHandler<CreateManageRatePlanCommand> {

    private final IManageRatePlanService service;

    private final ProducerReplicateManageRatePlanService producerReplicateManageRatePlanService;

    private final IManageHotelService hotelService;

    public CreateManageRatePlanCommandHandler(IManageRatePlanService service, ProducerReplicateManageRatePlanService producerReplicateManageRatePlanService, IManageHotelService hotelService) {
        this.service = service;
        this.producerReplicateManageRatePlanService = producerReplicateManageRatePlanService;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(CreateManageRatePlanCommand command) {
        RulesChecker.checkRule(new ManageRatePlanNameMustBeNullRule(command.getName()));

        ManageHotelDto hotelDto = hotelService.findById(command.getHotel());

        service.create(new ManageRatePlanDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                hotelDto,
                command.getDescription(),
                command.getStatus()
        ));
        this.producerReplicateManageRatePlanService.create(new ReplicateManageRatePlanKafka(command.getId(), hotelDto.getId(), command.getCode(), command.getName(), command.getStatus().name()));
    }
}
