package com.kynsoft.finamer.invoicing.application.command.manageInvoice.newCredit;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.manageAttachment.ManageAttachmentFileNameNotNullRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CreateNewCreditCommandHandler implements ICommandHandler<CreateNewCreditCommand> {

    private final IManageInvoiceService invoiceService;
    private final IManageHotelService hotelService;
    private final IManageInvoiceTypeService iManageInvoiceTypeService;
    private final IManageInvoiceStatusService manageInvoiceStatusService;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageBookingService bookingService;

    private final IInvoiceCloseOperationService closeOperationService;

    private final IManageResourceTypeService resourceTypeService;
    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;
    private final IManageEmployeeService employeeService;

    public CreateNewCreditCommandHandler(IManageInvoiceService invoiceService, IManageHotelService hotelService,
                                         IManageInvoiceTypeService iManageInvoiceTypeService, IManageInvoiceStatusService manageInvoiceStatusService,
                                         IManageAttachmentTypeService attachmentTypeService, IManageBookingService bookingService,
                                         IInvoiceCloseOperationService closeOperationService, IManageResourceTypeService resourceTypeService,
                                         IInvoiceStatusHistoryService invoiceStatusHistoryService,
                                         IAttachmentStatusHistoryService attachmentStatusHistoryService,
            ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService, IManageEmployeeService employeeService) {
        this.invoiceService = invoiceService;
        this.hotelService = hotelService;
        this.iManageInvoiceTypeService = iManageInvoiceTypeService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.attachmentTypeService = attachmentTypeService;
        this.bookingService = bookingService;
        this.closeOperationService = closeOperationService;
        this.resourceTypeService = resourceTypeService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
        this.employeeService = employeeService;
    }

    @Override
    public void handle(CreateNewCreditCommand command) {
        ManageInvoiceDto parentInvoice = this.invoiceService.findById(command.getInvoice());
        ManageHotelDto hotelDto = parentInvoice.getHotel();
        RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(
                this.closeOperationService,
                command.getInvoiceDate().toLocalDate(),
                hotelDto.getId()));

        EmployeeData employeeData = retrieveEmployeeData(command);
        Map<UUID, ManageBookingDto> parentBookingsMap = parentInvoice.getBookings().stream().collect(Collectors.toMap(ManageBookingDto::getId, booking -> booking));
        List<ManageBookingDto> newBookings = new LinkedList<>();
        Double credits = this.invoiceService.findSumOfAmountByParentId(command.getInvoice());
        Double invoiceAmount = 0.0;

        for (CreateNewCreditBookingRequest bookingRequest : command.getBookings()) {
            ManageBookingDto parentBooking = parentBookingsMap.get(bookingRequest.getId());

            Double newBookingAmount = bookingRequest.getAmount();

            if (newBookingAmount != 0) {
                //en caso de que venga positivo
                if (newBookingAmount > 0) {
                    newBookingAmount = -newBookingAmount;
                }
                //actualizando el invoiceAmount a guardar
                invoiceAmount += newBookingAmount;

                //sumando credits, el nuevo amount del booking y el amount del invoice
                //para saber si excede el amount del invoice
                if (credits + invoiceAmount + parentInvoice.getInvoiceAmount() < 0) {
                    throw new BusinessException(
                            DomainErrorMessage.CREDITS_CANNOT_EXCEED_INVOICE_AMOUNT,
                            DomainErrorMessage.CREDITS_CANNOT_EXCEED_INVOICE_AMOUNT.getReasonPhrase());
                }
                //creando el nuevo booking usando el método extraído
                ManageBookingDto newBooking = createNewBooking(parentBooking, newBookingAmount);

                //llenando la lista de bookings a guardar en el nuevo invoice
                newBookings.add(newBooking);
                System.out.println(credits + newBookingAmount + parentInvoice.getInvoiceAmount());
            }
        }

        List<ManageAttachmentDto> attachments = createAttachments(command.getAttachmentCommands(), command.getEmployee());

        //creando los type
        //TODO: crear parametrizacion para guardar el ManageInvoiceType
        EInvoiceType invoiceType = EInvoiceType.CREDIT;
        ManageInvoiceTypeDto invoiceTypeDto = this.iManageInvoiceTypeService.findByEInvoiceType(EInvoiceType.CREDIT);

        //creando los status
        EInvoiceStatus invoiceStatus = EInvoiceStatus.SENT;
        ManageInvoiceStatusDto manageInvoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.SENT);

        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(UUID.randomUUID(), parentInvoice.getHotel(), parentInvoice.getAgency(),
                invoiceType, invoiceTypeDto, invoiceStatus, manageInvoiceStatus, command.getInvoiceDate(), true,
                invoiceAmount, invoiceAmount, invoiceAmount, newBookings, attachments,
                false, parentInvoice);

        //ManageInvoiceDto created = this.invoiceService.create(invoiceDto);
        this.invoiceService.insert(invoiceDto);
        UUID uuidEmployee = employeeData.getId();

        UUID attachmentDefault = null;
        for (ManageAttachmentDto attachmentDto : attachments) {
            ManageAttachmentTypeDto attachmentType = attachmentDto.getType();
            if (attachmentType.isAttachInvDefault()) {
                attachmentDefault = attachmentDto.getId();
                break;
            }
        }

        this.producerReplicateManageInvoiceService.create(invoiceDto, attachmentDefault, uuidEmployee);

        command.setCredit(invoiceDto.getId());
        command.setInvoiceId(invoiceDto.getInvoiceId());
        command.setInvoiceNumber(invoiceDto.getInvoiceNumber());
        parentInvoice.setCredits(Math.abs(credits) + Math.abs(invoiceAmount));
        this.invoiceService.update(parentInvoice);
        this.invoiceStatusHistoryService.create(
                new InvoiceStatusHistoryDto(
                        UUID.randomUUID(),
                        invoiceDto,
                        "The invoice data was inserted.",
                        null,
                        employeeData.getFullName(),
                        invoiceStatus,
                        0L
                )
        );
        for (ManageAttachmentDto attachment : invoiceDto.getAttachments()) {
            this.attachmentStatusHistoryService.create(
                    new AttachmentStatusHistoryDto(
                            UUID.randomUUID(),
                            "An attachment to the invoice was inserted. The file name: " + attachment.getFilename(),
                            attachment.getAttachmentId(),
                            invoiceDto,
                            employeeData.getFullName(),
                            UUID.fromString(command.getEmployee()),
                            null,
                            null
                    )
            );
        }
    }

    private List<ManageAttachmentDto> createAttachments(List<CreateNewCreditAttachmentRequest> attachmentCommands, String employeeId) {
        List<ManageAttachmentDto> attachments = new LinkedList<>();
        int cont = 0;
        for (CreateNewCreditAttachmentRequest attachmentCommand : attachmentCommands) {
            RulesChecker.checkRule(new ManageAttachmentFileNameNotNullRule(
                    attachmentCommand.getFile()
            ));
            ManageAttachmentTypeDto attachmentType = this.attachmentTypeService.findById(
                    attachmentCommand.getType());
            if (attachmentType.isAttachInvDefault()) {
                cont++;
            }

            ResourceTypeDto resourceTypeDto = resourceTypeService.findById(attachmentCommand.getPaymentResourceType());

            ManageAttachmentDto attachmentDto = new ManageAttachmentDto(
                    UUID.randomUUID(),
                    null,
                    attachmentCommand.getFilename(),
                    attachmentCommand.getFile(),
                    attachmentCommand.getRemark(),
                    attachmentType,
                    null,
                    employeeId, //TODO hay que colocar el nombre del empleado
                    UUID.fromString(employeeId),
                    null,
                    resourceTypeDto,
                    false
            );

            attachments.add(attachmentDto);
        }

        if (cont == 0) {
            throw new BusinessException(
                    DomainErrorMessage.INVOICE_MUST_HAVE_ATTACHMENT_TYPE,
                    DomainErrorMessage.INVOICE_MUST_HAVE_ATTACHMENT_TYPE.getReasonPhrase()
            );
        }
        return attachments;
    }

    private ManageBookingDto createNewBooking(ManageBookingDto parentBooking, Double newAmount) {
        ManageBookingDto newBooking = new ManageBookingDto(parentBooking);
        newBooking.setInvoiceAmount(newAmount);
        newBooking.setDueAmount(newAmount);
        newBooking.setParent(parentBooking);
        newBooking.setBookingId(null);
        newBooking.setInvoice(null);
        newBooking.setBookingDate(LocalDateTime.now());

        //creando el roomRate
        ManageRoomRateDto roomRateDto = new ManageRoomRateDto(
                UUID.randomUUID(),
                null,
                newBooking.getCheckIn(),
                newBooking.getCheckOut(),
                newBooking.getInvoiceAmount(),
                newBooking.getRoomNumber(),
                newBooking.getAdults(),
                newBooking.getChildren(),
                newBooking.getRateAdult(),
                newBooking.getRateChild(),
                newBooking.getHotelAmount(),
                null,
                newBooking,
                new LinkedList<>(),
                newBooking.getNights(),
                false,
                null
        );
        List<ManageRoomRateDto> rates = new LinkedList<>();
        rates.add(roomRateDto);

        //agregando roomRates a nuevo booking
        newBooking.setRoomRates(rates);

        return newBooking;
    }

    private EmployeeData retrieveEmployeeData(CreateNewCreditCommand command) {
        try {
            ManageEmployeeDto employee = this.employeeService.findById(UUID.fromString(command.getEmployee()));
            String fullName = employee.getFirstName() + " " + employee.getLastName();
            return new EmployeeData(employee.getId(), fullName);
        } catch (Exception e) {
            return new EmployeeData(null, command.getEmployeeName());
        }
    }

    private static class EmployeeData {
        private final UUID id;
        private final String fullName;

        public EmployeeData(UUID id, String fullName) {
            this.id = id;
            this.fullName = fullName;
        }

        public UUID getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }
    }
}