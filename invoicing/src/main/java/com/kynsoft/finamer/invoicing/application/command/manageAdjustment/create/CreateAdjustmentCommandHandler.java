package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceCloseOperationDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.services.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
            IManageInvoiceTransactionTypeService transactionTypeService,
            IManageRoomRateService roomRateService,
            IManageBookingService bookingService,
            IManageInvoiceService invoiceService,
            IInvoiceCloseOperationService closeOperationService,
            IManagePaymentTransactionTypeService paymentTransactionTypeService) {
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

        List<ManageAdjustmentDto> adjustmentDtoList = roomRateDto.getAdjustments() != null ? roomRateDto.getAdjustments() : new LinkedList<>();

        adjustmentDtoList.add(new ManageAdjustmentDto(
                command.getId(),
                null,
                command.getAmount(),
                //command.getDate(),
                this.date(roomRateDto.getBooking().getInvoice().getHotel().getId()),
                command.getDescription(),
                transactionTypeDto,
                paymnetTransactionTypeDto,
                null,
                command.getEmployee(),
                false
        ));

        roomRateDto.setAdjustments(adjustmentDtoList);

        if (command.getAmount() != null) {
            roomRateDto.setInvoiceAmount(roomRateDto.getInvoiceAmount() != null ? roomRateDto.getInvoiceAmount() + command.getAmount() : command.getAmount());
            this.roomRateService.update(roomRateDto);
        }

        bookingService.calculateInvoiceAmount(this.bookingService.findById(roomRateDto.getBooking().getId()));
        invoiceService.calculateInvoiceAmount(this.invoiceService.findById(roomRateDto.getBooking().getInvoice().getId()));
    }

    private LocalDateTime date(UUID hotel) {
        InvoiceCloseOperationDto closeOperationDto = this.closeOperationService.findActiveByHotelId(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return LocalDateTime.now(ZoneId.of("UTC"));
        }
        return LocalDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")));
    }

}
