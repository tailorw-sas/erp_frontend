package com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.utils.BankerRounding;
import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingCommand;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.manageAttachment.ManageAttachmentFileNameNotNullRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageBooking.ManageBookingCheckInCheckOutRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageBooking.ManageBookingHotelBookingNumberValidationRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.InvoiceManualValidateVirtualHotelRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.InvoiceValidateClienteRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceValidateRatePlanRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceValidateRoomTypeRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
@Transactional
public class CreateBulkInvoiceCommandHandler implements ICommandHandler<CreateBulkInvoiceCommand> {

    private final IManageRatePlanService ratePlanService;
    private final IManageNightTypeService nightTypeService;
    private final IManageRoomTypeService roomTypeService;
    private final IManageRoomCategoryService roomCategoryService;
    private final IManageInvoiceTransactionTypeService transactionTypeService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;

    private final IManageInvoiceService service;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageInvoiceTypeService iManageInvoiceTypeService;
    private final IManageInvoiceStatusService manageInvoiceStatusService;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageBookingService bookingService;

    private final IInvoiceCloseOperationService closeOperationService;

    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final IManageEmployeeService employeeService;
    private final IManageResourceTypeService resourceTypeService;

    public CreateBulkInvoiceCommandHandler(IManageRatePlanService ratePlanService,
                                           IManageNightTypeService nightTypeService, IManageRoomTypeService roomTypeService,
                                           IManageRoomCategoryService roomCategoryService,
                                           IManageInvoiceTransactionTypeService transactionTypeService,
                                           IManageInvoiceService service, IManageAgencyService agencyService,
                                           IManageHotelService hotelService,
                                           IManageInvoiceTypeService iManageInvoiceTypeService,
                                           IManageInvoiceStatusService manageInvoiceStatusService,
                                           IManageAttachmentTypeService attachmentTypeService, IManageBookingService bookingService,
                                           IInvoiceCloseOperationService closeOperationService,
                                           IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                           ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService,
                                           IInvoiceStatusHistoryService invoiceStatusHistoryService,
                                           IAttachmentStatusHistoryService attachmentStatusHistoryService,
                                           IManageEmployeeService employeeService, IManageResourceTypeService resourceTypeService) {

        this.ratePlanService = ratePlanService;
        this.nightTypeService = nightTypeService;
        this.roomTypeService = roomTypeService;
        this.roomCategoryService = roomCategoryService;
        this.transactionTypeService = transactionTypeService;
        this.service = service;
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.iManageInvoiceTypeService = iManageInvoiceTypeService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.attachmentTypeService = attachmentTypeService;
        this.bookingService = bookingService;
        this.closeOperationService = closeOperationService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.employeeService = employeeService;
        this.resourceTypeService = resourceTypeService;
    }

    @Override
    public void handle(CreateBulkInvoiceCommand command) {
        ManageHotelDto hotelDto = findHotel(command);
        ManageEmployeeDto employee = findEmployee(command.getEmployee());
        String employeeFullName = getEmployeeFullName(employee, command.getEmployee());
        validateInvoiceDate(command, hotelDto);
        ManageAgencyDto agencyDto = findAgency(command);
        validateClient(agencyDto);

        List<ManageBookingDto> bookings = processBookings(command, hotelDto, agencyDto);
        List<ManageRoomRateDto> roomRates = processRoomRates(command, bookings);
        List<ManageAdjustmentDto> adjustments = processAdjustments(command, roomRates);
        List<ManageAttachmentDto> attachments = processAttachments(command);

        validateBookingsNightType(bookings, agencyDto);
        calculateBookingsHotelAmount(bookings);

        EInvoiceStatus status = determineInvoiceStatus(command, attachments);
        ManageInvoiceDto invoiceDto = buildInvoice(command, hotelDto, agencyDto, bookings, attachments, status);

        ManageInvoiceDto created = saveInvoice(invoiceDto, attachments, employee, employeeFullName);

        command.setInvoiceId(created.getInvoiceId());
        command.setInvoiceNo(created.getInvoiceNumber());
    }

    // Métodos auxiliares privados
    private ManageHotelDto findHotel(CreateBulkInvoiceCommand command) {
        ManageHotelDto hotelDto = this.hotelService.findById(command.getInvoiceCommand().getHotel());
        RulesChecker.checkRule(new InvoiceManualValidateVirtualHotelRule(hotelDto));
        return hotelDto;
    }

    private ManageEmployeeDto findEmployee(String employeeId) {
        try {
            return this.employeeService.findById(UUID.fromString(employeeId));
        } catch (Exception e) {
            return null;
        }
    }

    private String getEmployeeFullName(ManageEmployeeDto employee, String employeeId) {
        if (employee != null) {
            return employee.getFirstName() + " " + employee.getLastName();
        }
        return employeeId;
    }

    private void validateInvoiceDate(CreateBulkInvoiceCommand command, ManageHotelDto hotelDto) {
        RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(
                this.closeOperationService,
                command.getInvoiceCommand().getInvoiceDate().toLocalDate(),
                hotelDto.getId()
        ));
    }

    private ManageAgencyDto findAgency(CreateBulkInvoiceCommand command) {
        return this.agencyService.findById(command.getInvoiceCommand().getAgency());
    }

    private void validateClient(ManageAgencyDto agencyDto) {
        RulesChecker.checkRule(new InvoiceValidateClienteRule(agencyDto.getClient()));
    }

    private List<ManageBookingDto> processBookings(CreateBulkInvoiceCommand command, ManageHotelDto hotelDto, ManageAgencyDto agencyDto) {
        List<ManageBookingDto> bookings = new LinkedList<>();
        StringBuilder hotelBookingNumber = new StringBuilder();
        EInvoiceType invoiceType = command.getInvoiceCommand().getInvoiceType();
        for (int i = 0; i < command.getBookingCommands().size(); i++) {
            CreateBookingCommand bookingDto = command.getBookingCommands().get(i);
            RulesChecker.checkRule(new ManageBookingCheckInCheckOutRule(bookingDto.getCheckIn(), bookingDto.getCheckOut()));

            if (bookingDto.getHotelBookingNumber().length() > 2 && invoiceType != null && !invoiceType.equals(EInvoiceType.CREDIT)) {
                RulesChecker.checkRule(new ManageBookingHotelBookingNumberValidationRule(
                        bookingService,
                        removeBlankSpaces(bookingDto.getHotelBookingNumber()),
                        command.getInvoiceCommand().getHotel(),
                        bookingDto.getHotelBookingNumber()));
            }

            ManageNightTypeDto nightTypeDto = bookingDto.getNightType() != null ? this.nightTypeService.findById(bookingDto.getNightType()) : null;
            ManageRoomCategoryDto roomCategoryDto = bookingDto.getRoomCategory() != null ? this.roomCategoryService.findById(bookingDto.getRoomCategory()) : null;
            ManageRatePlanDto ratePlanDto = bookingDto.getRatePlan() != null ? this.ratePlanService.findById(bookingDto.getRatePlan()) : null;
            if (ratePlanDto != null)
                RulesChecker.checkRule(new ManageInvoiceValidateRatePlanRule(hotelDto, ratePlanDto, bookingDto.getHotelBookingNumber()));
            ManageRoomTypeDto roomTypeDto = bookingDto.getRoomType() != null ? this.roomTypeService.findById(bookingDto.getRoomType()) : null;
            if (roomTypeDto != null)
                RulesChecker.checkRule(new ManageInvoiceValidateRoomTypeRule(hotelDto, roomTypeDto, bookingDto.getHotelBookingNumber()));

            Double invoiceAmount = 0.00;
            if (invoiceType != null && invoiceType.equals(EInvoiceType.CREDIT)) {
                if (bookingDto.getInvoiceAmount() > 0) {
                    invoiceAmount -= bookingDto.getInvoiceAmount();
                } else {
                    invoiceAmount = bookingDto.getInvoiceAmount();
                }
            } else {
                invoiceAmount = bookingDto.getInvoiceAmount();
            }

            bookings.add(new ManageBookingDto(
                    bookingDto.getId(),
                    null,
                    null,
                    bookingDto.getHotelCreationDate(),
                    bookingDto.getBookingDate(),
                    bookingDto.getCheckIn(),
                    bookingDto.getCheckOut(),
                    bookingDto.getHotelBookingNumber(),
                    bookingDto.getFullName(),
                    bookingDto.getFirstName(),
                    bookingDto.getLastName(),
                    invoiceAmount,
                    invoiceAmount,
                    bookingDto.getRoomNumber(),
                    bookingDto.getCouponNumber(),
                    bookingDto.getAdults(),
                    bookingDto.getChildren(),
                    bookingDto.getRateAdult(),
                    bookingDto.getRateChild(),
                    bookingDto.getHotelInvoiceNumber(),
                    bookingDto.getFolioNumber(),
                    bookingDto.getHotelAmount(),
                    bookingDto.getDescription(),
                    null,
                    ratePlanDto,
                    nightTypeDto,
                    roomTypeDto,
                    roomCategoryDto,
                    new LinkedList<>(),
                    null,
                    null,
                    bookingDto.getContract(),
                    false,
                    null
            ));
            if (agencyDto.getClient().getIsNightType() && nightTypeDto == null) {
                hotelBookingNumber.append(bookingDto.getHotelBookingNumber()).append(", ");
            }
        }
        if (!hotelBookingNumber.isEmpty()) {
            throw new BusinessException(
                    DomainErrorMessage.NIGHT_TYPE_REQUIRED,
                    "Bookings with Hotel Booking Number: " + hotelBookingNumber + "require the Night Type field."
            );
        }
        return bookings;
    }

    private List<ManageRoomRateDto> processRoomRates(CreateBulkInvoiceCommand command, List<ManageBookingDto> bookings) {
        List<ManageRoomRateDto> roomRates = new LinkedList<>();
        EInvoiceType invoiceType = command.getInvoiceCommand().getInvoiceType();

        for (var rateCommand : command.getRoomRateCommands()) {
            Double invoiceAmount = rateCommand.getInvoiceAmount();
            if (invoiceType == EInvoiceType.CREDIT && invoiceAmount > 0) {
                invoiceAmount = -invoiceAmount;
            }

            ManageRoomRateDto roomRateDto = new ManageRoomRateDto(
                    rateCommand.getId(),
                    null,
                    rateCommand.getCheckIn(),
                    rateCommand.getCheckOut(),
                    invoiceAmount,
                    rateCommand.getRoomNumber(),
                    rateCommand.getAdults(),
                    rateCommand.getChildren(),
                    rateCommand.getRateAdult(),
                    rateCommand.getRateChild(),
                    rateCommand.getHotelAmount(),
                    rateCommand.getRemark(),
                    null,
                    new LinkedList<>(),
                    null,
                    false,
                    null
            );

            if (rateCommand.getBooking() != null) {
                for (ManageBookingDto bookingDto : bookings) {
                    if (bookingDto.getId().equals(rateCommand.getBooking())) {
                        List<ManageRoomRateDto> rates = bookingDto.getRoomRates();
                        if (rates == null) {
                            rates = new LinkedList<>();
                            bookingDto.setRoomRates(rates);
                        }
                        roomRateDto.setRoomRateId((long) (rates.size() + 1));
                        rates.add(roomRateDto);
                        break;
                    }
                }
            }

            roomRates.add(roomRateDto);
        }

        return roomRates;
    }

    private List<ManageAdjustmentDto> processAdjustments(CreateBulkInvoiceCommand command, List<ManageRoomRateDto> roomRates) {
        List<ManageAdjustmentDto> adjustments = new LinkedList<>();

        for (var adjCmd : command.getAdjustmentCommands()) {
            ManageInvoiceTransactionTypeDto transactionTypeDto = (adjCmd.getTransactionType() != null && !adjCmd.getTransactionType().equals(""))
                    ? transactionTypeService.findById(adjCmd.getTransactionType())
                    : null;

            ManagePaymentTransactionTypeDto paymentTransactionTypeDto = (adjCmd.getPaymentTransactionType() != null)
                    ? paymentTransactionTypeService.findById(adjCmd.getPaymentTransactionType())
                    : null;

            ManageAdjustmentDto adjustmentDto = new ManageAdjustmentDto(
                    adjCmd.getId(),
                    null,
                    adjCmd.getAmount(),
                    adjCmd.getDate(),
                    adjCmd.getDescription(),
                    transactionTypeDto,
                    paymentTransactionTypeDto,
                    null,
                    adjCmd.getEmployee(),
                    false
            );

            if (adjCmd.getRoomRate() != null) {
                for (ManageRoomRateDto rateDto : roomRates) {
                    if (rateDto.getId().equals(adjCmd.getRoomRate())) {
                        if (rateDto.getAdjustments() == null) {
                            rateDto.setAdjustments(new LinkedList<>());
                        }
                        rateDto.getAdjustments().add(adjustmentDto);

                        Double invoiceAmount = rateDto.getInvoiceAmount() != null ? rateDto.getInvoiceAmount() : 0.0;
                        Double adjustmentAmount = adjustmentDto.getAmount() != null ? adjustmentDto.getAmount() : 0.0;
                        rateDto.setInvoiceAmount(invoiceAmount + adjustmentAmount);

                        break;
                    }
                }
            }

            adjustments.add(adjustmentDto);
        }

        return adjustments;
    }

    private List<ManageAttachmentDto> processAttachments(CreateBulkInvoiceCommand command) {
        List<ManageAttachmentDto> attachmentDtos = new LinkedList<>();

        UUID attachmentDefault = null;
        int defaultCount = 0;

        for (var attachmentCmd : command.getAttachmentCommands()) {
            RulesChecker.checkRule(new ManageAttachmentFileNameNotNullRule(attachmentCmd.getFile()));

            var attachmentType = attachmentTypeService.findById(attachmentCmd.getType());
            var resourceTypeDto = resourceTypeService.findById(attachmentCmd.getPaymentResourceType());

            ManageAttachmentDto attachmentDto = new ManageAttachmentDto(
                attachmentCmd.getId(),
                null,
                attachmentCmd.getFilename(),
                attachmentCmd.getFile(),
                attachmentCmd.getRemark(),
                attachmentType,
                null,
                attachmentCmd.getEmployee(),
                attachmentCmd.getEmployeeId(),
                null,
                resourceTypeDto,
                false
            );

            if (attachmentType.isAttachInvDefault()) {
                defaultCount++;
                if (defaultCount == 1) {
                    attachmentDefault = attachmentDto.getId();
                }
            }

            attachmentDtos.add(attachmentDto);
        }

        if (defaultCount == 0) {
            throw new BusinessException(
                DomainErrorMessage.INVOICE_MUST_HAVE_ATTACHMENT_TYPE,
                DomainErrorMessage.INVOICE_MUST_HAVE_ATTACHMENT_TYPE.getReasonPhrase()
            );
        }

        return attachmentDtos;
    }

    private void validateBookingsNightType(List<ManageBookingDto> bookings, ManageAgencyDto agencyDto) {
        // Ya validado en processBookings, método incluido para mantener la estructura.
    }

    private void calculateBookingsHotelAmount(List<ManageBookingDto> bookings) {
        for (ManageBookingDto booking : bookings) {
            this.calculateBookingHotelAmount(booking);
        }
    }

    private EInvoiceStatus determineInvoiceStatus(CreateBulkInvoiceCommand command, List<ManageAttachmentDto> attachments) {
        EInvoiceStatus status = EInvoiceStatus.PROCESSED;
        if (command.getInvoiceCommand().getInvoiceType().equals(EInvoiceType.CREDIT)
                || command.getInvoiceCommand().getInvoiceType().equals(EInvoiceType.OLD_CREDIT)) {
            status = EInvoiceStatus.SENT;
        }
        if (status.equals(EInvoiceStatus.PROCESSED) && !attachments.isEmpty()) {
            status = EInvoiceStatus.RECONCILED;
        }
        return status;
    }

    private ManageInvoiceDto buildInvoice(CreateBulkInvoiceCommand command, ManageHotelDto hotelDto, ManageAgencyDto agencyDto,
                                          List<ManageBookingDto> bookings, List<ManageAttachmentDto> attachments, EInvoiceStatus status) {
        ManageInvoiceStatusDto invoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(status);
        // Convertir OLD_CREDIT a CREDIT para el tipo de invoice
        EInvoiceType invoiceType = command.getInvoiceCommand().getInvoiceType().equals(EInvoiceType.OLD_CREDIT)
                ? EInvoiceType.CREDIT
                : command.getInvoiceCommand().getInvoiceType();
        ManageInvoiceTypeDto invoiceTypeDto = this.iManageInvoiceTypeService.findByEInvoiceType(invoiceType);

        Double amount = BankerRounding.round(command.getInvoiceCommand().getInvoiceAmount());//TODO APF validar si esto es igual a la suma de los bookings
        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(
                command.getInvoiceCommand().getId(),
                hotelDto,
                agencyDto,
                command.getInvoiceCommand().getInvoiceType(),
                invoiceTypeDto,
                status,
                invoiceStatus,
                command.getInvoiceCommand().getInvoiceDate(),
                true,
                amount,
                amount,
                amount,
                bookings,
                attachments,
                false,
                null
        );

        if (status.equals(EInvoiceStatus.RECONCILED)) {
            invoiceDto = this.service.changeInvoiceStatus(invoiceDto, invoiceStatus);
        }
        return invoiceDto;
    }

    private ManageInvoiceDto saveInvoice(ManageInvoiceDto invoiceDto, List<ManageAttachmentDto> attachments, ManageEmployeeDto employee,
                                         String employeeFullName) {
        // Calcular los montos de los bookings antes
        for (ManageBookingDto booking : invoiceDto.getBookings()) {
            this.bookingService.calculateInvoiceAmount(booking);
        }
        // Crear el invoice ya con los montos de bookings
        ManageInvoiceDto created = service.create(invoiceDto);
        // Buscar el attachment default (si existe)
        UUID attachmentDefault = findDefaultAttachmentId(attachments);

        // Replicación en Kafka (con manejo de error)
        try {
            UUID employeeId = (employee != null) ? employee.getId() : null;
            this.producerReplicateManageInvoiceService.create(created, attachmentDefault, employeeId);
        } catch (Exception ex) {
        }

        // Historial de estado del invoice
        this.invoiceStatusHistoryService.create(
                new InvoiceStatusHistoryDto(
                        UUID.randomUUID(),
                        created,
                        "The invoice data was inserted.",
                        null,
                        employeeFullName,
                        created.getStatus(),
                        0L
                )
        );

        // Historial de status de attachments
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

        return created;
    }

    private UUID findDefaultAttachmentId(List<ManageAttachmentDto> attachments) {
        return attachments.stream()
                .filter(a -> a.getType() != null && a.getType().isAttachInvDefault())
                .map(ManageAttachmentDto::getId)
                .findFirst()
                .orElse(null);
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

    private String removeBlankSpaces(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }
}
