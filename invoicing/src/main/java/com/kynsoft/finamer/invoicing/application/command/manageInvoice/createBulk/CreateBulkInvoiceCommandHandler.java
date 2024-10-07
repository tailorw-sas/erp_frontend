package com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.manageBooking.ManageBookingHotelBookingNumberValidationRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageRoomRate.ManageRoomRateCheckAdultsAndChildrenRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
                                           ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService, IInvoiceStatusHistoryService invoiceStatusHistoryService, IAttachmentStatusHistoryService attachmentStatusHistoryService) {

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
    }

    @Override
    @Transactional
    public void handle(CreateBulkInvoiceCommand command) {
        ManageHotelDto hotelDto = this.hotelService.findById(command.getInvoiceCommand().getHotel());
        RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(
                this.closeOperationService,
                command.getInvoiceCommand().getInvoiceDate().toLocalDate(),
                hotelDto.getId()));

        List<ManageAdjustmentDto> adjustments = new LinkedList<>();
        List<ManageBookingDto> bookings = new LinkedList<>();
        List<ManageRoomRateDto> roomRates = new LinkedList<>();
        List<ManageAttachmentDto> attachmentDtos = new LinkedList<>();

        for (int i = 0; i < command.getBookingCommands().size(); i++) {

            if (command.getBookingCommands().get(i).getHotelBookingNumber().length() > 2
                    && command.getInvoiceCommand().getInvoiceType() != null
                    && !command.getInvoiceCommand().getInvoiceType().equals(EInvoiceType.CREDIT)) {

                RulesChecker.checkRule(new ManageBookingHotelBookingNumberValidationRule(bookingService,
                        command.getBookingCommands().get(i).getHotelBookingNumber()
                                .split("\\s+")[command.getBookingCommands().get(i).getHotelBookingNumber()
                                        .split("\\s+").length - 1],
                        command.getInvoiceCommand().getHotel()));
            }

            ManageNightTypeDto nightTypeDto = command.getBookingCommands().get(i).getNightType() != null
                    ? this.nightTypeService
                            .findById(command.getBookingCommands().get(i).getNightType())
                    : null;
            ManageRoomTypeDto roomTypeDto = command.getBookingCommands().get(i).getRoomType() != null
                    ? this.roomTypeService
                            .findById(command.getBookingCommands().get(i).getRoomType())
                    : null;
            ManageRoomCategoryDto roomCategoryDto = command.getBookingCommands().get(i)
                    .getRoomCategory() != null
                            ? this.roomCategoryService.findById(command.getBookingCommands()
                                    .get(i).getRoomCategory())
                            : null;
            ManageRatePlanDto ratePlanDto = command.getBookingCommands().get(i).getRatePlan() != null
                    ? this.ratePlanService
                            .findById(command.getBookingCommands().get(i).getRatePlan())
                    : null;

            Double invoiceAmount = 0.00;
            if (command.getInvoiceCommand().getInvoiceType() != null
                    && command.getInvoiceCommand().getInvoiceType().equals(EInvoiceType.CREDIT)) {

                if (command.getBookingCommands().get(i).getInvoiceAmount() > 0) {
                    invoiceAmount -= command.getBookingCommands().get(i).getInvoiceAmount();

                } else {
                    invoiceAmount = command.getBookingCommands().get(i).getInvoiceAmount();
                }

            } else {
                invoiceAmount = command.getBookingCommands().get(i).getInvoiceAmount();
            }

            bookings.add(new ManageBookingDto(command.getBookingCommands().get(i).getId(),
                    null,
                    null,
                    command.getBookingCommands().get(i).getHotelCreationDate(),
                    command.getBookingCommands().get(i).getBookingDate(),
                    command.getBookingCommands().get(i).getCheckIn(),
                    command.getBookingCommands().get(i).getCheckOut(),
                    command.getBookingCommands().get(i).getHotelBookingNumber(),
                    command.getBookingCommands().get(i).getFullName(),
                    command.getBookingCommands().get(i).getFirstName(),
                    command.getBookingCommands().get(i).getLastName(),
                    invoiceAmount,
                    invoiceAmount,
                    command.getBookingCommands().get(i).getRoomNumber(),
                    command.getBookingCommands().get(i).getCouponNumber(),
                    command.getBookingCommands().get(i).getAdults(),
                    command.getBookingCommands().get(i).getChildren(),
                    command.getBookingCommands().get(i).getRateAdult(),
                    command.getBookingCommands().get(i).getRateChild(),
                    command.getBookingCommands().get(i).getHotelInvoiceNumber(),
                    command.getBookingCommands().get(i).getFolioNumber(),
                    command.getBookingCommands().get(i).getHotelAmount(),
                    command.getBookingCommands().get(i).getDescription(),
                    null,
                    ratePlanDto,
                    nightTypeDto,
                    roomTypeDto,
                    roomCategoryDto, new LinkedList<>(), null, null,
                    command.getBookingCommands().get(i).getContract()));

        }

        for (int i = 0; i < command.getRoomRateCommands().size(); i++) {
            RulesChecker.checkRule(new ManageRoomRateCheckAdultsAndChildrenRule(command.getRoomRateCommands().get(i).getAdults(), command.getRoomRateCommands().get(i).getChildren()));
            Double invoiceAmount = command.getRoomRateCommands().get(i).getInvoiceAmount();
            if (command.getInvoiceCommand().getInvoiceType().compareTo(EInvoiceType.CREDIT) == 0
                    && invoiceAmount > 0) {
                invoiceAmount = -invoiceAmount;
            }
            ManageRoomRateDto roomRateDto = new ManageRoomRateDto(
                    command.getRoomRateCommands().get(i).getId(),
                    null,
                    command.getRoomRateCommands().get(i).getCheckIn(),
                    command.getRoomRateCommands().get(i).getCheckOut(),
                    invoiceAmount,
                    command.getRoomRateCommands().get(i).getRoomNumber(),
                    command.getRoomRateCommands().get(i).getAdults(),
                    command.getRoomRateCommands().get(i).getChildren(),
                    command.getRoomRateCommands().get(i).getRateAdult(),
                    command.getRoomRateCommands().get(i).getRateChild(),
                    command.getRoomRateCommands().get(i).getHotelAmount(),
                    command.getRoomRateCommands().get(i).getRemark(),
                    null,
                    new LinkedList<>(), null);

            if (command.getRoomRateCommands().get(i).getBooking() != null) {
                for (ManageBookingDto bookingDto : bookings) {
                    if (bookingDto.getId()
                            .equals(command.getRoomRateCommands().get(i).getBooking())) {

                        List<ManageRoomRateDto> rates = bookingDto.getRoomRates();
                        roomRateDto.setRoomRateId(rates != null ? rates.size() + 1L : 1L);
                        if (rates != null) {
                            rates.add(roomRateDto);
                        } else {
                            rates = new LinkedList<>();
                            rates.add(roomRateDto);
                        }

                        bookingDto.setRoomRates(rates);

                    }
                }
            }
            roomRates.add(roomRateDto);

        }

        for (int i = 0; i < command.getAdjustmentCommands().size(); i++) {

            ManageInvoiceTransactionTypeDto transactionTypeDto = command.getAdjustmentCommands().get(i)
                    .getTransactionType() != null
                    && !command
                            .getAdjustmentCommands().get(i).getTransactionType().equals("")
                                    ? transactionTypeService.findById(
                                            command.getAdjustmentCommands()
                                                    .get(i)
                                                    .getTransactionType())
                                    : null;

            ManagePaymentTransactionTypeDto paymnetTransactionTypeDto = command.getAdjustmentCommands()
                    .get(i).getPaymentTransactionType() != null
                            ? paymentTransactionTypeService
                                    .findById(command.getAdjustmentCommands().get(i)
                                            .getPaymentTransactionType())
                            : null;

            ManageAdjustmentDto adjustmentDto = new ManageAdjustmentDto(
                    command.getAdjustmentCommands().get(i).getId(),
                    null,
                    command.getAdjustmentCommands().get(i).getAmount(),
                    command.getAdjustmentCommands().get(i).getDate(),
                    command.getAdjustmentCommands().get(i).getDescription(),
                    transactionTypeDto,
                    paymnetTransactionTypeDto,
                    null, command.getAdjustmentCommands().get(i).getEmployee());

            if (command.getAdjustmentCommands().get(i).getRoomRate() != null) {
                for (ManageRoomRateDto rateDto : roomRates) {
                    if (rateDto.getId()
                            .equals(command.getAdjustmentCommands().get(i).getRoomRate())) {

                        List<ManageAdjustmentDto> adjustmentDtos = rateDto.getAdjustments();
                        adjustmentDtos.add(adjustmentDto);

                        if (adjustmentDto.getAmount() != null) {

                            rateDto.setInvoiceAmount(rateDto.getInvoiceAmount() != null
                                    ? rateDto.getInvoiceAmount()
                                    : 0.00 + adjustmentDto.getAmount());
                        }

                        rateDto.setAdjustments(adjustmentDtos);

                    }
                }
            }

            adjustments.add(adjustmentDto);

        }

        int cont = 0;
        for (int i = 0; i < command.getAttachmentCommands().size(); i++) {
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
        for (ManageBookingDto booking : bookings) {
            this.calculateBookingHotelAmount(booking);

        }

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getInvoiceCommand().getAgency());

        String invoiceNumber = InvoiceType.getInvoiceTypeCode(command.getInvoiceCommand().getInvoiceType());

        if (hotelDto.getManageTradingCompanies() != null
                && hotelDto.getManageTradingCompanies().getIsApplyInvoice()) {
            invoiceNumber += "-" + hotelDto.getManageTradingCompanies().getCode();
        } else {
            invoiceNumber += "-" + hotelDto.getCode();
        }

        EInvoiceStatus status = EInvoiceStatus.PROCECSED;
        ManageInvoiceStatusDto invoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.PROCECSED);
        if (command.getInvoiceCommand().getInvoiceType().equals(EInvoiceType.CREDIT)
                || command.getInvoiceCommand().getInvoiceType().equals(EInvoiceType.OLD_CREDIT)) {
            status = EInvoiceStatus.SENT;
            //TODO setear el objeto ManageInvoiceStatus segun la parametrización a partir de el codigo EInvoiceStatus.SENT
            invoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.SENT);
        }

        if (status.equals(EInvoiceStatus.PROCECSED) && !attachmentDtos.isEmpty()) {
            status = EInvoiceStatus.RECONCILED;
            //TODO setear el objeto ManageInvoiceStatus segun la parametrización a partir de el codigo EInvoiceStatus.RECONCILED
            invoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.RECONCILED);
        }
        LocalDate dueDate = command.getInvoiceCommand().getInvoiceDate().toLocalDate().plusDays(agencyDto.getCreditDay() != null ? agencyDto.getCreditDay() : 0);
        ManageInvoiceTypeDto invoiceTypeDto = this.iManageInvoiceTypeService.findByEInvoiceType(command.getInvoiceCommand().getInvoiceType());

        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(command.getInvoiceCommand().getId(), 0L, 0L,
                invoiceNumber,
                command.getInvoiceCommand().getInvoiceDate(), dueDate,
                true,
                command.getInvoiceCommand().getInvoiceAmount(),
                command.getInvoiceCommand().getInvoiceAmount(), hotelDto, agencyDto,
                command.getInvoiceCommand().getInvoiceType(), status,
                false, bookings, attachmentDtos, null, null, invoiceTypeDto, invoiceStatus, null,  false,
                null, 0.0);

        ManageInvoiceDto created = service.create(invoiceDto);

        command.setInvoiceId(created.getInvoiceId());
        command.setInvoiceNo(created.getInvoiceNumber());

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
                            command.getEmployee(),
                            attachment.getEmployeeId(),
                            null,
                            null
                    )
            );
        }

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

}
