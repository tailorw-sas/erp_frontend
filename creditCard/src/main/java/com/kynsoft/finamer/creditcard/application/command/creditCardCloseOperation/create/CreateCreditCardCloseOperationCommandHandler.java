package com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.creditcard.domain.dto.CreditCardCloseOperationDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.services.ICreditCardCloseOperationService;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class CreateCreditCardCloseOperationCommandHandler implements ICommandHandler<CreateCreditCardCloseOperationCommand> {

    private final ICreditCardCloseOperationService closeOperationService;
    private final IManageHotelService hotelService;

    public CreateCreditCardCloseOperationCommandHandler(ICreditCardCloseOperationService closeOperationService, IManageHotelService hotelService) {
        this.closeOperationService = closeOperationService;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(CreateCreditCardCloseOperationCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "id", "Manage Hotel ID cannot be null."));
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());

        this.closeOperationService.create(new CreditCardCloseOperationDto(
                command.getId(), 
                command.getStatus(), 
                hotelDto, 
                command.getBeginDate(), 
                command.getEndDate()
        ));
    }
}
