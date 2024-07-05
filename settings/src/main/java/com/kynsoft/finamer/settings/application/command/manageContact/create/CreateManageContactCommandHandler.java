package com.kynsoft.finamer.settings.application.command.manageContact.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageContactDto;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.rules.manageContact.*;
import com.kynsoft.finamer.settings.domain.services.IManageContactService;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageContactCommandHandler implements ICommandHandler<CreateManageContactCommand> {

    private final IManageContactService service;

    private final IManageHotelService hotelService;

    public CreateManageContactCommandHandler(IManageContactService service, IManageHotelService hotelService) {
        this.service = service;
        this.hotelService = hotelService;
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

        service.create(new ManageContactDto(
                command.getId(), command.getCode(), command.getDescription(),
                command.getStatus(), command.getName(), hotelDto,
                command.getEmail(), command.getPhone(), command.getPosition()
        ));
    }
}
