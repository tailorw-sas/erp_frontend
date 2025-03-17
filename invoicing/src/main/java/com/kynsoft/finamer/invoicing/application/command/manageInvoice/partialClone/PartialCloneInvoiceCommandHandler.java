package com.kynsoft.finamer.invoicing.application.command.manageInvoice.partialClone;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;

import com.kynsoft.finamer.invoicing.domain.rules.manageAttachment.ManageAttachmentFileNameNotNullRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    private final IManageEmployeeService employeeService;
    private final IManageResourceTypeService resourceTypeService;

    public PartialCloneInvoiceCommandHandler(
            IManageInvoiceService service,
            IManageAttachmentTypeService attachmentTypeService,
            ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService,
            IManageRoomRateService rateService, IManageInvoiceStatusService manageInvoiceStatusService,
            IManageBookingService bookingService,
            IInvoiceStatusHistoryService invoiceStatusHistoryService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService,
            IManageInvoiceTransactionTypeService transactionTypeService,
            IManagePaymentTransactionTypeService paymentTransactionTypeService,
            IInvoiceCloseOperationService closeOperationService,
            IManageEmployeeService employeeService, IManageResourceTypeService resourceTypeService) {

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
        this.employeeService = employeeService;
        this.resourceTypeService = resourceTypeService;
    }

    @Override
    @Transactional
    public void handle(PartialCloneInvoiceCommand command) {
        ManageInvoiceDto invoiceToClone = this.service.findById(command.getInvoice());
        ManageEmployeeDto employee = null;
        String employeeFullName = "";
        try {
            employee = this.employeeService.findById(UUID.fromString(command.getEmployee()));
            employeeFullName = employee.getFirstName() + " " + employee.getLastName();
        } catch (Exception e) {
            employeeFullName = command.getEmployee();
        }
        List<ManageBookingDto> bookingDtos = new ArrayList<>();

        List<ManageRoomRateDto> roomRateDtos = new ArrayList<>();

        for (int i = 0; i < invoiceToClone.getBookings().size(); i++) {

            ManageBookingDto newBooking = new ManageBookingDto(invoiceToClone.getBookings().get(i));

            List<ManageRoomRateDto> roomRates = this.rateService.findByBooking(invoiceToClone.getBookings().get(i).getId());

            for (ManageRoomRateDto roomRate : roomRates) {
                roomRate.setBooking(newBooking);
                roomRate.setAdjustments(new LinkedList<>());
                roomRate.setHotelAmount(0.00);
                roomRate.setInvoiceAmount(0.00);
                roomRateDtos.add(roomRate);
            }

            bookingDtos.add(newBooking);

        }

        for (PartialCloneInvoiceAdjustmentRelation adjustmentRequest : command.getRoomRateAdjustments()) {
            for (ManageRoomRateDto roomRate : roomRateDtos) {
                if (adjustmentRequest.getRoomRate().equals(roomRate.getId())) {
                    Double adjustmentAmount = adjustmentRequest.getAdjustment().getAmount();
                    roomRate.setInvoiceAmount(roomRate.getInvoiceAmount() + adjustmentAmount);
                    List<ManageAdjustmentDto> adjustmentDtoList = roomRate.getAdjustments() != null ? roomRate.getAdjustments() : new LinkedList<>();
                    adjustmentDtoList.add(new ManageAdjustmentDto(
                            adjustmentRequest.getAdjustment().getId(),
                            null,
                            adjustmentAmount,
                            invoiceDate(invoiceToClone.getHotel().getId()),
                            adjustmentRequest.getAdjustment().getDescription(),
                            adjustmentRequest.getAdjustment().getTransactionType() != null
                                ? this.transactionTypeService.findById(adjustmentRequest.getAdjustment().getTransactionType())
                                : null,
                            adjustmentRequest.getAdjustment().getPaymentTransactionType() != null
                                ? this.paymentTransactionTypeService.findById(adjustmentRequest.getAdjustment().getPaymentTransactionType())
                                : null,
                            null,
                            adjustmentRequest.getAdjustment().getEmployee(),
                            false
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
            ResourceTypeDto resourceTypeDto = this.resourceTypeService.findById(command.getAttachmentCommands().get(i).getPaymentResourceType());
            if (attachmentType.isAttachInvDefault()) {
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
                    command.getAttachmentCommands().get(i).getEmployeeId(),
                    null,
                    resourceTypeDto,
                    false
            );

            attachmentDtos.add(attachmentDto);
        }
        if (cont == 0) {
            throw new BusinessException(
                    DomainErrorMessage.INVOICE_MUST_HAVE_ATTACHMENT_TYPE,
                    DomainErrorMessage.INVOICE_MUST_HAVE_ATTACHMENT_TYPE.getReasonPhrase()
            );
        }

        for (ManageBookingDto booking : bookingDtos) {
            this.calculateBookingAmounts(booking);
        }
        if (!validateManageAdjustments(bookingDtos)) {
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
        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(
                UUID.randomUUID(),
                0L,
                0L,
                invoiceNumber,
                InvoiceType.getInvoiceTypeCode(invoiceToClone.getInvoiceType()) + "-" + 0L,
                //invoiceToClone.getInvoiceDate(), 
                this.invoiceDate(invoiceToClone.getHotel().getId(), command.getInvoiceDate()),
                invoiceToClone.getDueDate(),
                true,
                invoiceToClone.getInvoiceAmount(),
                invoiceToClone.getInvoiceAmount(),
                invoiceToClone.getHotel(),
                invoiceToClone.getAgency(),
                invoiceToClone.getInvoiceType(),
                status,
                false,
                bookingDtos,
                attachmentDtos,
                null,
                null,
                invoiceToClone.getManageInvoiceType(),
                invoiceStatus,
                null,
                true,
                invoiceToClone,
                0.00,
                0
        );

        invoiceDto.setOriginalAmount(invoiceToClone.getInvoiceAmount());
        ManageInvoiceDto created = service.create(invoiceDto);

        //calcular el amount de los bookings
//        for (ManageBookingDto booking : created.getBookings()) {
//            this.bookingService.calculateInvoiceAmount(booking);
//        }

        //calcular el amount del invoice
        this.service.calculateInvoiceAmount(created);

        //establecer el original amount
        created.setOriginalAmount(created.getInvoiceAmount());
        this.service.update(created);

        try {
            this.producerReplicateManageInvoiceService.create(created, null, null);
        } catch (Exception e) {
        }

        //invoice status history
        this.invoiceStatusHistoryService.create(
                new InvoiceStatusHistoryDto(
                        UUID.randomUUID(),
                        created,
                        "The invoice data was inserted.",
                        null,
                        //command.getEmployee(),
                        employeeFullName,
                        status,
                        0L
                )
        );

        //attachment status history
        for (ManageAttachmentDto attachment : created.getAttachments()) {
            this.attachmentStatusHistoryService.create(
                    new AttachmentStatusHistoryDto(
                            UUID.randomUUID(),
                            "An attachment to the invoice was inserted. The file name: " + attachment.getFilename(),
                            attachment.getAttachmentId(),
                            created,
                            employeeFullName,
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

    private LocalDateTime invoiceDate(UUID hotel) {
        InvoiceCloseOperationDto closeOperationDto = this.closeOperationService.findActiveByHotelId(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return LocalDateTime.now(ZoneId.of("UTC"));
        }
        return LocalDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")));
    }

    private LocalDateTime invoiceDate(UUID hotel, LocalDateTime invoiceDate) {
        InvoiceCloseOperationDto closeOperationDto = this.closeOperationService.findActiveByHotelId(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate(), invoiceDate.toLocalDate())) {
            return invoiceDate;
        }

        if (closeOperationDto.getEndDate().isAfter(LocalDate.now())){
            return LocalDateTime.now(ZoneId.of("UTC"));
        }

        return LocalDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")));
    }

    public void calculateBookingAmounts(ManageBookingDto dto) {
        Double hotelAmount = 0.00;
        Double bookingAmount = 0.00;
        if (dto.getRoomRates() != null) {
            for (int i = 0; i < dto.getRoomRates().size(); i++) {
                hotelAmount += dto.getRoomRates().get(i).getHotelAmount();
                bookingAmount += dto.getRoomRates().get(i).getInvoiceAmount();
            }
            dto.setHotelAmount(hotelAmount);
            dto.setInvoiceAmount(bookingAmount);
            dto.setDueAmount(bookingAmount);
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
