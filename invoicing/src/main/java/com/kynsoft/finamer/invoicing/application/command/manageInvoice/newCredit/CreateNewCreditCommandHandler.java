package com.kynsoft.finamer.invoicing.application.command.manageInvoice.newCredit;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;
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
import java.util.UUID;

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
        ManageHotelDto hotelDto = this.hotelService.findById(parentInvoice.getHotel().getId());
        RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(
                this.closeOperationService,
                command.getInvoiceDate().toLocalDate(),
                hotelDto.getId()));

        EmployeeData employeeData = retrieveEmployeeData(command);
        List<ManageBookingDto> parentBookings = parentInvoice.getBookings();
        List<ManageBookingDto> newBookings = new LinkedList<>();
        Double credits = this.invoiceService.findSumOfAmountByParentId(command.getInvoice());
        Double invoiceAmount = 0.0;

        for (CreateNewCreditBookingRequest bookingRequest : command.getBookings()) {
            ManageBookingDto parentBooking = this.bookingService.findById(bookingRequest.getId());

            Double newBookingAmount = bookingRequest.getAmount();

            if (newBookingAmount != 0) {
                //en caso de que venga positivo
                if (newBookingAmount > 0) {
                    newBookingAmount = -newBookingAmount;
                }
                //sumando credits, el nuevo amount del booking y el amount del invoice
                //para saber si excede el amount del invoice
                if (credits + newBookingAmount + parentInvoice.getInvoiceAmount() < 0) {
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
            //actualizando el invoiceAmount a guardar
            invoiceAmount += newBookingAmount;
        }

        List<ManageAttachmentDto> attachments = createAttachments(command.getAttachmentCommands(), command.getEmployee());

        //creando los type
        //TODO: crear parametrizacion para guardar el ManageInvoiceType
        EInvoiceType invoiceType = EInvoiceType.CREDIT;
        ManageInvoiceTypeDto invoiceTypeDto = this.iManageInvoiceTypeService.findByEInvoiceType(EInvoiceType.CREDIT);

        //creando los status
        EInvoiceStatus invoiceStatus = EInvoiceStatus.SENT;
        ManageInvoiceStatusDto manageInvoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.SENT);

        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(
                UUID.randomUUID(),
                0L,
                0L,
                null,
                null,
                command.getInvoiceDate(),
                null,
                true,
                invoiceAmount,
                invoiceAmount,
                parentInvoice.getHotel(),
                parentInvoice.getAgency(),
                invoiceType,
                invoiceStatus,
                false,
                newBookings,
                attachments,
                false,
                null,
                invoiceTypeDto,
                manageInvoiceStatus,
                null,
                false,
                parentInvoice,
                0.0,
                0
        );
        invoiceDto.setOriginalAmount(invoiceAmount);
        ManageInvoiceDto created = this.invoiceService.create(invoiceDto);
        UUID uuidEmployee = employeeData.getId();

        UUID attachmentDefault = null;
        for (ManageAttachmentDto attachmentDto : attachments) {
            ManageAttachmentTypeDto attachmentType = attachmentDto.getAttachmentType();
            if (attachmentType.isAttachInvDefault()) {
                attachmentDefault = attachmentDto.getId();
                break;
            }
        }

        this.producerReplicateManageInvoiceService.create(created, attachmentDefault, uuidEmployee);

        command.setCredit(created.getId());
        command.setInvoiceId(created.getInvoiceId());
        command.setInvoiceNumber(created.getInvoiceNumber());
        parentInvoice.setCredits(Math.abs(credits) + Math.abs(invoiceAmount));
        this.invoiceService.update(parentInvoice);
        this.invoiceStatusHistoryService.create(
                new InvoiceStatusHistoryDto(
                        UUID.randomUUID(),
                        created,
                        "The invoice data was inserted.",
                        null,
                        employeeData.getFullName(),
                        invoiceStatus,
                        0L
                )
        );
        for (ManageAttachmentDto attachment : created.getAttachments()) {
            this.attachmentStatusHistoryService.create(
                    new AttachmentStatusHistoryDto(
                            UUID.randomUUID(),
                            "An attachment to the invoice was inserted. The file name: " + attachment.getFilename(),
                            attachment.getAttachmentId(),
                            created,
                            employeeData.getFullName(),
                            UUID.fromString(command.getEmployee()),
                            null,
                            null
                    )
            );
        }
    }

    private List<ManageAttachmentDto> createAttachments(List<CreateAttachmentCommand> attachmentCommands, String employeeId) {
        List<ManageAttachmentDto> attachments = new LinkedList<>();
        int cont = 0;
        for (CreateAttachmentCommand attachmentCommand : attachmentCommands) {
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
        //debe venir al menos un attachment de tipo attinvdefault
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