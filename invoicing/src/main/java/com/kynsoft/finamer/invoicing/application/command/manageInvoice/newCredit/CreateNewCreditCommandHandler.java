package com.kynsoft.finamer.invoicing.application.command.manageInvoice.newCredit;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class CreateNewCreditCommandHandler implements ICommandHandler<CreateNewCreditCommand> {

    private final IManageRatePlanService ratePlanService;
    private final IManageNightTypeService nightTypeService;
    private final IManageRoomTypeService roomTypeService;
    private final IManageRoomCategoryService roomCategoryService;
    private final IManageInvoiceTransactionTypeService transactionTypeService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;

    private final IManageInvoiceService invoiceService;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageInvoiceTypeService iManageInvoiceTypeService;
    private final IManageInvoiceStatusService manageInvoiceStatusService;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageBookingService bookingService;
    private final IManageRoomRateService rateService;

    private final IInvoiceCloseOperationService closeOperationService;

    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    private final IParameterizationService parameterizationService;

    public CreateNewCreditCommandHandler(IManageRatePlanService ratePlanService, IManageNightTypeService nightTypeService, IManageRoomTypeService roomTypeService, IManageRoomCategoryService roomCategoryService, IManageInvoiceTransactionTypeService transactionTypeService, IManagePaymentTransactionTypeService paymentTransactionTypeService, IManageInvoiceService invoiceService, IManageAgencyService agencyService, IManageHotelService hotelService, IManageInvoiceTypeService iManageInvoiceTypeService, IManageInvoiceStatusService manageInvoiceStatusService, IManageAttachmentTypeService attachmentTypeService, IManageBookingService bookingService, IManageRoomRateService rateService, IInvoiceCloseOperationService closeOperationService, ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService, IParameterizationService parameterizationService) {
        this.ratePlanService = ratePlanService;
        this.nightTypeService = nightTypeService;
        this.roomTypeService = roomTypeService;
        this.roomCategoryService = roomCategoryService;
        this.transactionTypeService = transactionTypeService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.invoiceService = invoiceService;
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.iManageInvoiceTypeService = iManageInvoiceTypeService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.attachmentTypeService = attachmentTypeService;
        this.bookingService = bookingService;
        this.rateService = rateService;
        this.closeOperationService = closeOperationService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
        this.parameterizationService = parameterizationService;
    }

    @Override
    public void handle(CreateNewCreditCommand command) {
        ManageInvoiceDto parentInvoice = this.invoiceService.findById(command.getInvoice());
        ManageHotelDto hotelDto = this.hotelService.findById(parentInvoice.getHotel().getId());
        RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(
                this.closeOperationService,
                command.getInvoiceDate().toLocalDate(),
                hotelDto.getId()));

        //preparando lo necesario
        List<ManageBookingDto> parentBookings = parentInvoice.getBookings();
        List<ManageBookingDto> newBookings = new LinkedList<>();
        List<ManageAttachmentDto> attachments = new LinkedList<>();
        Double credits = this.invoiceService.findSumOfAmountByParentId(command.getInvoice());
        Double invoiceAmount = 0.0;

        for (CreateNewCreditBookingRequest bookingRequest : command.getBookings()) {
            ManageBookingDto parentBooking = this.bookingService.findById(bookingRequest.getId());

            Double bookingAmount = parentBooking.getInvoiceAmount();
            Double newBookingAmount = bookingRequest.getAmount();

            if(!Objects.equals(bookingAmount, newBookingAmount)){
                //en caso de que venga positivo
                if (newBookingAmount > 0) {
                    newBookingAmount = -newBookingAmount;
                }
                //sumando credits, el nuevo amount del booking y el amount del invoice
                //para saber si excede el amount del invoice
                if(credits + newBookingAmount + parentInvoice.getInvoiceAmount() < 0){
                    throw new BusinessException(
                            DomainErrorMessage.CREDITS_CANNOT_EXCEED_INVOICE_AMOUNT,
                            DomainErrorMessage.CREDITS_CANNOT_EXCEED_INVOICE_AMOUNT.getReasonPhrase());
                }
                //creando el nuevo booking
                ManageBookingDto newBooking = new ManageBookingDto(parentBooking);
                newBooking.setInvoiceAmount(newBookingAmount);
                newBooking.setDueAmount(newBookingAmount);
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
                        newBooking.getNights()
                );
                List<ManageRoomRateDto> rates = new LinkedList<>();
                rates.add(roomRateDto);

                //agregando roomRates a nuevo booking
                newBooking.setRoomRates(rates);

                //llenando la lista de bookings a guardar en el nuevo invoice
                newBookings.add(newBooking);
                System.out.println(credits + newBookingAmount + parentInvoice.getInvoiceAmount());
            }
            //actualizando el invoiceAmount a guardar
            invoiceAmount+=newBookingAmount;
        }

        for (int i = 0; i < command.getAttachmentCommands().size(); i++) {
            ManageAttachmentTypeDto attachmentType = this.attachmentTypeService.findById(
                    command.getAttachmentCommands().get(i).getType());

            ManageAttachmentDto attachmentDto = new ManageAttachmentDto(
                    command.getAttachmentCommands().get(i).getId(),
                    null,
                    command.getAttachmentCommands().get(i).getFilename(),
                    command.getAttachmentCommands().get(i).getFile(),
                    command.getAttachmentCommands().get(i).getRemark(),
                    attachmentType,
                    null, command.getAttachmentCommands().get(i).getEmployee(),
                    command.getAttachmentCommands().get(i).getEmployeeId(), null, null);

            attachments.add(attachmentDto);
        }

        //preparando los datos del nuevo invoice
        //creando el invoiceNumber
        String invoiceNumber = InvoiceType.getInvoiceTypeCode(EInvoiceType.CREDIT);
        if (hotelDto.getManageTradingCompanies() != null
                && hotelDto.getManageTradingCompanies().getIsApplyInvoice()) {
            invoiceNumber += "-" + hotelDto.getManageTradingCompanies().getCode();
        } else {
            invoiceNumber += "-" + hotelDto.getCode();
        }

        //creando los type
        //TODO: crear parametrizacion para guardar el ManageInvoiceType
        EInvoiceType invoiceType = EInvoiceType.CREDIT;

        //creando los status
        EInvoiceStatus invoiceStatus = EInvoiceStatus.SENT;
        ParameterizationDto parameterization = this.parameterizationService.findActiveParameterization();
        ManageInvoiceStatusDto manageInvoiceStatus = parameterization != null ? this.manageInvoiceStatusService.findByCode(parameterization.getSent()) : null;

        //calculando dueDate
        ManageAgencyDto agencyDto = this.agencyService.findById(parentInvoice.getAgency().getId());
        //TODO: replicar el campo creditDay de Agency para calcular dueDate
        //LocalDate dueDate = command.getInvoiceDate().toLocalDate().plusDays(5L);
        LocalDate dueDate = command.getInvoiceDate().toLocalDate();

        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(
                UUID.randomUUID(),
                0L,
                0L,
                invoiceNumber,
                command.getInvoiceDate(),
                dueDate,
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
                null,
                manageInvoiceStatus,
                null,
                false,
                parentInvoice,
                0.0
        );
        ManageInvoiceDto created = this.invoiceService.create(invoiceDto);
        command.setCredit(created.getId());
        command.setInvoiceId(created.getInvoiceId());
        parentInvoice.setCredits(Math.abs(credits) + Math.abs(invoiceAmount));
        this.invoiceService.update(parentInvoice);
    }
}
