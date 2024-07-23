package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.rules.closeOperation.CheckBeginDateAndEndDateRule;
import com.kynsoft.finamer.payment.domain.rules.closeOperation.CloseOperationHotelMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentCloseOperationCommandHandler implements ICommandHandler<CreatePaymentCloseOperationCommand> {

    private final IPaymentCloseOperationService closeOperationService;
    private final IManageHotelService hotelService;

    public CreatePaymentCloseOperationCommandHandler(IPaymentCloseOperationService closeOperationService, IManageHotelService hotelService) {
        this.closeOperationService = closeOperationService;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(CreatePaymentCloseOperationCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "id", "Manage Hotel ID cannot be null."));
        RulesChecker.checkRule(new CheckBeginDateAndEndDateRule(command.getBeginDate(), command.getEndDate()));
        RulesChecker.checkRule(new CloseOperationHotelMustBeUniqueRule(closeOperationService, command.getHotel()));

        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());

        this.closeOperationService.create(new PaymentCloseOperationDto(
                command.getId(), 
                command.getStatus(), 
                hotelDto, 
                command.getBeginDate(), 
                command.getEndDate()
        ));
    }
}
