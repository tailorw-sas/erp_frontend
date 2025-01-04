package com.kynsoft.finamer.settings.application.command.manageContact.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ManageContactKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageContactDto;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.rules.manageContact.*;
import com.kynsoft.finamer.settings.domain.services.IManageContactService;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageContact.ProducerReplicateManageContactService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageContactCommandHandler implements ICommandHandler<CreateManageContactCommand> {

    private final IManageContactService service;

    private final IManageHotelService hotelService;

    private final ProducerReplicateManageContactService producerReplicateManageContactService;

    public CreateManageContactCommandHandler(IManageContactService service, IManageHotelService hotelService,
                                             ProducerReplicateManageContactService producerReplicateManageContactService) {
        this.service = service;
        this.hotelService = hotelService;
        this.producerReplicateManageContactService = producerReplicateManageContactService;
    }

    @Override
    public void handle(CreateManageContactCommand command) {
        RulesChecker.checkRule(new ManageContactCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageContactNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageContactCodeMustBeUniqueRule(this.service, command.getCode(), command.getManageHotel(), command.getId()));
        RulesChecker.checkRule(new ManageContactEmailRule(command.getEmail()));
        //RulesChecker.checkRule(new ManageContactPhoneRule(command.getPhone()));
        RulesChecker.checkRule(new ManageContactEmailMustBeUniqueRule(this.service, command.getEmail(), command.getManageHotel(), command.getId()));

        ManageHotelDto hotelDto = hotelService.findById(command.getManageHotel());

        ManageContactDto manageContactDto = new ManageContactDto(
                command.getId(), command.getCode(), command.getDescription(),
                command.getStatus(), command.getName(), hotelDto,
                command.getEmail(), command.getPhone(), command.getPosition()
        );
        service.create(manageContactDto);
        this.producerReplicateManageContactService.create(new ManageContactKafka(
                manageContactDto.getId(), 
                manageContactDto.getCode(), 
                manageContactDto.getDescription(), 
                manageContactDto.getName(), 
                manageContactDto.getManageHotel().getId(), 
                manageContactDto.getEmail(), 
                manageContactDto.getPhone(), 
                manageContactDto.getPosition()
        ));
    }
}
