package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;

@Component
public class CreateAdjustmentCommandHandler implements ICommandHandler<CreateAdjustmentCommand> {

    private final IManageAdjustmentService adjustmentService;
    private final IManageInvoiceTransactionTypeService transactionTypeService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;

    private final IManageRoomRateService roomRateService;
    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;

    private final IInvoiceCloseOperationService closeOperationService;


    public CreateAdjustmentCommandHandler(IManageAdjustmentService adjustmentService,
                                          IManageInvoiceTransactionTypeService transactionTypeService, IManageRoomRateService roomRateService,
                                          IManageBookingService bookingService, IManageInvoiceService invoiceService, IInvoiceCloseOperationService closeOperationService, IManagePaymentTransactionTypeService paymentTransactionTypeService) {
        this.adjustmentService = adjustmentService;
        this.transactionTypeService = transactionTypeService;
        this.roomRateService = roomRateService;
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
        this.closeOperationService = closeOperationService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
    }


    @Override
    public void handle(CreateAdjustmentCommand command) {
        ManageRoomRateDto roomRateDto = roomRateService.findById(command.getRoomRate());

  

        ManageInvoiceTransactionTypeDto transactionTypeDto = command.getTransactionType() != null
                ? transactionTypeService.findById(command.getTransactionType())
                : null;


                  

        ManagePaymentTransactionTypeDto paymnetTransactionTypeDto = command.getPaymentTransactionType() != null
        ? paymentTransactionTypeService.findById(command.getPaymentTransactionType())
        : null;



        adjustmentService.create(new ManageAdjustmentDto(
                command.getId(),
                null,
                command.getAmount(),
                command.getDate(),
                command.getDescription(),
                transactionTypeDto,
                paymnetTransactionTypeDto,
                roomRateDto,
                command.getEmployee()));


        if (command.getAmount() != null) {
            roomRateDto.setInvoiceAmount(roomRateDto.getInvoiceAmount() != null ? roomRateDto.getInvoiceAmount() + command.getAmount(): command.getAmount());
            this.roomRateService.update(roomRateDto);
        }

        bookingService.calculateInvoiceAmount(this.bookingService.findById(roomRateDto.getBooking().getId()));
        invoiceService.calculateInvoiceAmount(this.invoiceService.findById(roomRateDto.getBooking().getInvoice().getId()));
    }
}
