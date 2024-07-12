package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceCloseOperationDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class CreateInvoiceCloseOperationCommandHandler implements ICommandHandler<CreateInvoiceCloseOperationCommand> {

    private final IInvoiceCloseOperationService closeOperationService;
    private final IManageHotelService hotelService;

    public CreateInvoiceCloseOperationCommandHandler(IInvoiceCloseOperationService closeOperationService, IManageHotelService hotelService) {
        this.closeOperationService = closeOperationService;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(CreateInvoiceCloseOperationCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "id", "Manage Hotel ID cannot be null."));
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());

        this.closeOperationService.create(new InvoiceCloseOperationDto(
                command.getId(), 
                command.getStatus(), 
                hotelDto, 
                command.getBeginDate(), 
                command.getEndDate()
        ));
    }
}
