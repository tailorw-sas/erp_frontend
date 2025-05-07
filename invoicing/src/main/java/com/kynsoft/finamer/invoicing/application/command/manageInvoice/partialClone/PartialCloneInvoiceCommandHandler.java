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

import java.util.*;

@Component
@Transactional
public class PartialCloneInvoiceCommandHandler implements ICommandHandler<PartialCloneInvoiceCommand> {

    private final IManageInvoiceService service;

    private final IManageAttachmentTypeService attachmentTypeService;

    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;
    private final IManageRoomRateService rateService;
    private final IManageInvoiceStatusService manageInvoiceStatusService;
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
        String employeeFullName;
        try {
            ManageEmployeeDto employee = this.employeeService.findById(UUID.fromString(command.getEmployee()));
            employeeFullName = String.format("%s %s", employee.getFirstName(), employee.getLastName());
        } catch (Exception e) {
            employeeFullName = command.getEmployee();
        }

        Map<UUID, ManageBookingDto> bookingMap = new LinkedHashMap<>();
        Map<UUID, List<ManageRoomRateDto>> bookingRoomRateMap = new HashMap<>();
        Map<UUID, ManageRoomRateDto> roomRateMap = new HashMap<>();

        // Agrupamos las RoomRates por bookingId y preprocesamos los ajustes
        Map<UUID, List<PartialCloneInvoiceAdjustmentRelation>> adjustmentsByRoomRate = new HashMap<>();
        for (PartialCloneInvoiceAdjustmentRelation adj : command.getRoomRateAdjustments()) {
            adjustmentsByRoomRate.computeIfAbsent(adj.getRoomRate(), k -> new ArrayList<>()).add(adj);
        }

        for (ManageBookingDto originalBooking : invoiceToClone.getBookings()) {
            ManageBookingDto newBooking = new ManageBookingDto(originalBooking);
            bookingMap.put(newBooking.getId(), newBooking);
            List<ManageRoomRateDto> roomRates = this.rateService.findByBooking(originalBooking.getId());

            for (ManageRoomRateDto roomRate : roomRates) {
                roomRate.setBooking(newBooking);
                roomRate.setAdjustments(new ArrayList<>());
                roomRate.setHotelAmount(0.00);
                roomRate.setInvoiceAmount(0.00);

                List<PartialCloneInvoiceAdjustmentRelation> adjustments = adjustmentsByRoomRate.getOrDefault(roomRate.getId(), Collections.emptyList());
                for (PartialCloneInvoiceAdjustmentRelation adj : adjustments) {
                    var _adj = adj.getAdjustment();
                    double amount = _adj.getAmount();
                    roomRate.setInvoiceAmount(roomRate.getInvoiceAmount() + amount);
                    roomRate.getAdjustments().add(new ManageAdjustmentDto(
                            _adj.getId(), null, amount,
                            invoiceDate(invoiceToClone.getHotel().getId()),
                            _adj.getDescription(),
                            _adj.getTransactionType() != null ? this.transactionTypeService.findById(_adj.getTransactionType()) : null,
                            _adj.getPaymentTransactionType() != null ? this.paymentTransactionTypeService.findById(_adj.getPaymentTransactionType()) : null,
                            null, _adj.getEmployee(), false
                    ));
                }
                roomRate.setId(UUID.randomUUID());
                roomRateMap.put(roomRate.getId(), roomRate);
                bookingRoomRateMap.computeIfAbsent(newBooking.getId(), k -> new ArrayList<>()).add(roomRate);
            }
        }

        for (Map.Entry<UUID, List<ManageRoomRateDto>> entry : bookingRoomRateMap.entrySet()) {
            ManageBookingDto booking = bookingMap.get(entry.getKey());
            if (booking != null) {
                booking.setRoomRates(entry.getValue());
            }
        }

        List<ManageBookingDto> bookingDtos = new ArrayList<>(bookingMap.values());

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
            throw new BusinessException(DomainErrorMessage.INVOICE_MUST_HAVE_ATTACHMENT_TYPE,
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

        EInvoiceStatus status = EInvoiceStatus.RECONCILED;
        ManageInvoiceStatusDto invoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.RECONCILED);
        var _invoiceDate = this.invoiceDate(invoiceToClone.getHotel().getId(), command.getInvoiceDate());
        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(UUID.randomUUID(), invoiceToClone.getHotel(), invoiceToClone.getAgency(),
                invoiceToClone.getInvoiceType(), invoiceToClone.getManageInvoiceType(), status, invoiceStatus, _invoiceDate, true,
                0d, 0d, 0d, bookingDtos, attachmentDtos,
                true, invoiceToClone);

        this.service.calculateInvoiceAmount(invoiceDto);
        invoiceDto.setOriginalAmount(invoiceDto.getInvoiceAmount());
        ManageInvoiceDto created = service.create(invoiceDto);

        //calcular el amount del invoice


        //establecer el original amount
        //created.setOriginalAmount(created.getInvoiceAmount());
        //this.service.update(created);

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
