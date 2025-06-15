package com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.services.*;
import java.time.LocalDateTime;

import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
public class CreateAdjustmentCommandHandler implements ICommandHandler<CreateAdjustmentCommand> {

    private final IManageInvoiceTransactionTypeService transactionTypeService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;

    private final IManageRoomRateService roomRateService;
    private final IManageBookingService bookingService;
    private final IManageInvoiceService invoiceService;

    private final IInvoiceCloseOperationService closeOperationService;
    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    public CreateAdjustmentCommandHandler(IManageInvoiceTransactionTypeService transactionTypeService,
            IManageRoomRateService roomRateService,
            IManageBookingService bookingService,
            IManageInvoiceService invoiceService,
            IInvoiceCloseOperationService closeOperationService,
            IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                          ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService) {
        this.transactionTypeService = transactionTypeService;
        this.roomRateService = roomRateService;
        this.bookingService = bookingService;
        this.invoiceService = invoiceService;
        this.closeOperationService = closeOperationService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
    }

    @Override
    public void handle(CreateAdjustmentCommand command) {
        ManageRoomRateDto roomRateDto = roomRateService.findById(command.getRoomRate());
        ManageBookingDto bookingDto = roomRateDto.getBooking();

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
                this.getCloseOperationDate(roomRateDto.getBooking().getInvoice().getHotel().getId()),
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

        this.bookingService.calculateInvoiceAmount(bookingDto);

        ManageInvoiceDto invoiceDto = this.invoiceService.findById(roomRateDto.getBooking().getInvoice().getId());
        invoiceService.calculateInvoiceAmount(invoiceDto);
        this.producerReplicateManageInvoiceService.create(invoiceDto, null, null);
    }

    private LocalDateTime getCloseOperationDate(UUID hotel) {
        return this.closeOperationService.getCloseOperationDate(hotel);
    }

}
