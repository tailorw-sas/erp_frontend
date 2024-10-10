package com.kynsoft.finamer.invoicing.application.command.manageInvoice.partialClone;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;

import com.kynsoft.finamer.invoicing.domain.rules.manageAttachment.ManageAttachmentFileNameNotNullRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageBooking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomRate;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
@Transactional
public class PartialCloneInvoiceCommandHandler implements ICommandHandler<PartialCloneInvoiceCommand> {

    private final IManageInvoiceService service;

    private final IManageAttachmentTypeService attachmentTypeService;

    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;
    private final IManageRoomRateService rateService;
    private final IManageInvoiceStatusService manageInvoiceStatusService;
    private final IManageBookingService bookingService;
    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final IManageInvoiceTransactionTypeService transactionTypeService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IInvoiceCloseOperationService closeOperationService;

    public PartialCloneInvoiceCommandHandler(

            IManageInvoiceService service,
            IManageAttachmentTypeService attachmentTypeService,
            ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService,
            IManageRoomRateService rateService, IManageInvoiceStatusService manageInvoiceStatusService,
            IManageBookingService bookingService,
            IInvoiceStatusHistoryService invoiceStatusHistoryService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService,
            IManageInvoiceTransactionTypeService transactionTypeService,
            IManagePaymentTransactionTypeService paymentTransactionTypeService, IInvoiceCloseOperationService closeOperationService) {

        this.service = service;

        this.attachmentTypeService = attachmentTypeService;

        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
        this.rateService = rateService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.bookingService = bookingService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.transactionTypeService = transactionTypeService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.closeOperationService = closeOperationService;
    }

    @Override
    @Transactional
    public void handle(PartialCloneInvoiceCommand command) {
        ManageInvoiceDto invoiceToClone = this.service.findById(command.getInvoice());

        List<ManageBookingDto> bookingDtos = new ArrayList<>();

        List<ManageRoomRateDto> roomRateDtos = new ArrayList<>();

        for (int i = 0; i < invoiceToClone.getBookings().size(); i++) {

            ManageBookingDto newBooking = new ManageBookingDto(invoiceToClone.getBookings().get(i));

            List<ManageRoomRate> roomRates = this.rateService.findByBooking(new ManageBooking(invoiceToClone.getBookings().get(i)));

            for (ManageRoomRate roomRate : roomRates) {

                ManageRoomRateDto newRoomRate = roomRate.toAggregate();

                newRoomRate.setBooking(newBooking);
                newRoomRate.setAdjustments(new LinkedList<>());
                newRoomRate.setHotelAmount(0.00);
                newRoomRate.setInvoiceAmount(0.00);
                roomRateDtos.add(newRoomRate);

            }

            bookingDtos.add(newBooking);

        }

        for (PartialCloneInvoiceAdjustmentRelation adjustmentRequest : command.getRoomRateAdjustments()) {
            for (ManageRoomRateDto roomRate : roomRateDtos) {
                if (adjustmentRequest.getRoomRate().equals(roomRate.getId())) {
                    RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(
                            this.closeOperationService,
                            adjustmentRequest.getAdjustment().getDate().toLocalDate(),
                            invoiceToClone.getHotel().getId()
                    ));
                    Double adjustmentAmount = adjustmentRequest.getAdjustment().getAmount();
                    roomRate.setInvoiceAmount(roomRate.getInvoiceAmount() + adjustmentAmount);
                    List<ManageAdjustmentDto> adjustmentDtoList = roomRate.getAdjustments() != null ? roomRate.getAdjustments() : new LinkedList<>();
                    adjustmentDtoList.add(new ManageAdjustmentDto(
                            adjustmentRequest.getAdjustment().getId(),
                            null,
                            adjustmentAmount,
                            adjustmentRequest.getAdjustment().getDate(),
                            adjustmentRequest.getAdjustment().getDescription(),
                            adjustmentRequest.getAdjustment().getTransactionType() != null
                                    ? this.transactionTypeService.findById(adjustmentRequest.getAdjustment().getTransactionType())
                                    : null,
                            adjustmentRequest.getAdjustment().getPaymentTransactionType() != null
                                    ? this.paymentTransactionTypeService.findById(adjustmentRequest.getAdjustment().getPaymentTransactionType())
                                    : null,
                            null,
                            adjustmentRequest.getAdjustment().getEmployee()
                    ));
                    roomRate.setAdjustments(adjustmentDtoList);
                }
            }

        }

        for (ManageRoomRateDto roomRate : roomRateDtos) {
            roomRate.setId(UUID.randomUUID());

            for (ManageBookingDto booking : bookingDtos) {
                if (booking.getId().equals(roomRate.getBooking().getId())) {

                    List<ManageRoomRateDto> list = booking.getRoomRates();
                    list.add(roomRate);

                    booking.setRoomRates(list);
                }
            }
        }

        List<ManageAttachmentDto> attachmentDtos = new LinkedList<>();

        int cont = 0;
        for (int i = 0; i < command.getAttachmentCommands().size(); i++) {
            RulesChecker.checkRule(new ManageAttachmentFileNameNotNullRule(
                    command.getAttachmentCommands().get(i).getFile()
            ));
            ManageAttachmentTypeDto attachmentType = this.attachmentTypeService.findById(
                    command.getAttachmentCommands().get(i).getType());
            if(attachmentType.isAttachInvDefault()) {
                cont++;
            }
            ManageAttachmentDto attachmentDto = new ManageAttachmentDto(
                    command.getAttachmentCommands().get(i).getId(),
                    null,
                    command.getAttachmentCommands().get(i).getFilename(),
                    command.getAttachmentCommands().get(i).getFile(),
                    command.getAttachmentCommands().get(i).getRemark(),
                    attachmentType,
                    null, command.getAttachmentCommands().get(i).getEmployee(),
                    command.getAttachmentCommands().get(i).getEmployeeId(), null, null);

            attachmentDtos.add(attachmentDto);
        }
        if(cont == 0){
            throw new BusinessException(
                    DomainErrorMessage.INVOICE_MUST_HAVE_ATTACHMENT_TYPE,
                    DomainErrorMessage.INVOICE_MUST_HAVE_ATTACHMENT_TYPE.getReasonPhrase()
            );
        }

        for (ManageBookingDto booking : bookingDtos) {
            this.calculateBookingHotelAmount(booking);
        }
        if(!validateManageAdjustments(bookingDtos)){
            throw new BusinessException(
                    DomainErrorMessage.MANAGE_BOOKING_ADJUSTMENT,
                    DomainErrorMessage.MANAGE_BOOKING_ADJUSTMENT.getReasonPhrase()
            );
        }
        String invoiceNumber = InvoiceType.getInvoiceTypeCode(invoiceToClone.getInvoiceType());

        if (invoiceToClone.getHotel().getManageTradingCompanies() != null
                && invoiceToClone.getHotel().getManageTradingCompanies().getIsApplyInvoice()) {
            invoiceNumber += "-" + invoiceToClone.getHotel().getManageTradingCompanies().getCode();
        } else {
            invoiceNumber += "-" + invoiceToClone.getHotel().getCode();
        }

        EInvoiceStatus status = EInvoiceStatus.RECONCILED;
        ManageInvoiceStatusDto invoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.RECONCILED);
        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(UUID.randomUUID(), 0L, 0L,
                invoiceNumber,
                invoiceToClone.getInvoiceDate(), invoiceToClone.getDueDate(),
                true,
                invoiceToClone.getInvoiceAmount(),
                invoiceToClone.getInvoiceAmount(), invoiceToClone.getHotel(), invoiceToClone.getAgency(),
                invoiceToClone.getInvoiceType(), status,
                false, bookingDtos, attachmentDtos, null, null, invoiceToClone.getManageInvoiceType(), invoiceStatus, null, true, invoiceToClone, 0.00);

        ManageInvoiceDto created = service.create(invoiceDto);

        //calcular el amount de los bookings
        for(ManageBookingDto booking : created.getBookings()){
            this.bookingService.calculateInvoiceAmount(booking);
        }
        //calcular el amount del invoice
        this.service.calculateInvoiceAmount(created);

        try {
            this.producerReplicateManageInvoiceService.create(created);
        } catch (Exception e) {
        }

        //invoice status history
        this.invoiceStatusHistoryService.create(
                new InvoiceStatusHistoryDto(
                        UUID.randomUUID(),
                        created,
                        "The invoice data was inserted.",
                        null,
                        command.getEmployee(),
                        status
                )
        );

        //attachment status history
        for(ManageAttachmentDto attachment : created.getAttachments()){
            this.attachmentStatusHistoryService.create(
                    new AttachmentStatusHistoryDto(
                            UUID.randomUUID(),
                            "An attachment to the invoice was inserted. The file name: " + attachment.getFilename(),
                            attachment.getAttachmentId(),
                            created,
                            attachment.getEmployee(),
                            attachment.getEmployeeId(),
                            null,
                            null
                    )
            );
        }

//        command.setBookings(bookingDtos.stream().map(e -> e.getId()).collect(Collectors.toList()));
//        command.setRoomRates(roomRateDtos.stream().map(e -> e.getId()).collect(Collectors.toList()));
//        command.setAttachments(attachmentDtos.stream().map(e -> e.getId()).collect(Collectors.toList()));
        command.setCloned(created.getId());
    }

    public void calculateBookingHotelAmount(ManageBookingDto dto) {
        Double HotelAmount = 0.00;

        if (dto.getRoomRates() != null) {

            for (int i = 0; i < dto.getRoomRates().size(); i++) {

                HotelAmount += dto.getRoomRates().get(i).getHotelAmount();

            }

            dto.setHotelAmount(HotelAmount);

        }
    }

    public boolean validateManageAdjustments(List<ManageBookingDto> bookings) {
        for (ManageBookingDto booking : bookings) {
            if (booking.getRoomRates() != null) {
                boolean hasAdjustment = false;
                for (ManageRoomRateDto roomRate : booking.getRoomRates()) {
                    if (roomRate.getAdjustments() != null && !roomRate.getAdjustments().isEmpty()) {
                        hasAdjustment = true;
                        break;
                    }
                }
                if (!hasAdjustment) {
                    return false;
                }
            } else {
                return false;
            }
        }

        // Si todos los ManageBooking tienen al menos un ManageAdjustment, retornamos true
        return true;
    }


}
