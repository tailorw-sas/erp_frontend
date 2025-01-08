package com.kynsoft.finamer.settings.application.command.manageRoomType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageInvoiceTypeKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageRoomTypeKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.settings.domain.rules.manageRoomType.ManageRoomTypeCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageRoomType.ManageRoomTypeCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageRoomType.ManageRoomTypeNameMustBeNullRule;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import com.kynsoft.finamer.settings.domain.services.IManageRoomTypeService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageRoomType.ProducerReplicateManageRoomTypeService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageRoomTypeCommandHandler implements ICommandHandler<CreateManageRoomTypeCommand> {

    private final IManageRoomTypeService service;

    private final IManageHotelService hotelService;

    private final ProducerReplicateManageRoomTypeService producerReplicateManageRoomTypeService;

    public CreateManageRoomTypeCommandHandler(IManageRoomTypeService service, IManageHotelService hotelService, ProducerReplicateManageRoomTypeService producerReplicateManageRoomTypeService) {
        this.service = service;
        this.hotelService = hotelService;
        this.producerReplicateManageRoomTypeService = producerReplicateManageRoomTypeService;
    }

    @Override
    public void handle(CreateManageRoomTypeCommand command) {
        RulesChecker.checkRule(new ManageRoomTypeCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageRoomTypeNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageRoomTypeCodeMustBeUniqueRule(service, command.getCode(), command.getManageHotel(), command.getId()));

        ManageHotelDto hotelDto = hotelService.findById(command.getManageHotel());

        service.create(new ManageRoomTypeDto(
                command.getId(), command.getCode(), command.getDescription(),
                command.getStatus(), command.getName(), hotelDto
        ));
        this.producerReplicateManageRoomTypeService.create(new ReplicateManageRoomTypeKafka(command.getId(), command.getCode(), command.getName(), command.getStatus().name()));
    }
}
