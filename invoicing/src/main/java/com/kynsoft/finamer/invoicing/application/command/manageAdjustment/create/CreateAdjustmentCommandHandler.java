package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAdjustmentService;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import org.springframework.stereotype.Component;

@Component
public class CreateAdjustmentCommandHandler implements ICommandHandler<CreateAdjustmentCommand> {

    private final IManageAdjustmentService adjustmentService;
    private final IManageInvoiceTransactionTypeService transactionTypeService;

    private final IManageRoomRateService roomRateService;
    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;

 

    public CreateAdjustmentCommandHandler(IManageAdjustmentService adjustmentService,
            IManageInvoiceTransactionTypeService transactionTypeService, IManageRoomRateService roomRateService,
            IManageBookingService bookingService, IManageInvoiceService invoiceService) {
        this.adjustmentService = adjustmentService;
        this.transactionTypeService = transactionTypeService;
        this.roomRateService = roomRateService;
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
    }



    @Override
    public void handle(CreateAdjustmentCommand command) {
        ManageInvoiceTransactionTypeDto transactionTypeDto = command.getTransactionType() != null
                && !command.getTransactionType().equals("")
                        ? transactionTypeService.findById(command.getTransactionType())
                        : null;
        ManageRoomRateDto roomRateDto = roomRateService.findById(command.getRoomRate());

        adjustmentService.create(new ManageAdjustmentDto(
                command.getId(),
                null,
                command.getAmount(),
                command.getDate(),
                command.getDescription(),
                transactionTypeDto,
                roomRateDto,
                command.getEmployee()));

               
                if(command.getAmount() != null){
                roomRateDto.setInvoiceAmount(roomRateDto.getInvoiceAmount() + command.getAmount());
                this.roomRateService.update(roomRateDto);
                }

                bookingService.calculateInvoiceAmount(this.bookingService.findById(roomRateDto.getBooking().getId()));
                invoiceService.calculateInvoiceAmount(this.invoiceService.findById(roomRateDto.getBooking().getInvoice().getId()));
    }
}
