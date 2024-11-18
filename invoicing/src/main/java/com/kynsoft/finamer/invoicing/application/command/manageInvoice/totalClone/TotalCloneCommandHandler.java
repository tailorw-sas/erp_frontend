package com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAdults.UpdateBookingCalculateBookingAdultsCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingAmount.UpdateBookingCalculateBookingAmountCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateBookingChildren.UpdateBookingCalculateBookingChildrenCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateChickInAndCheckOut.UpdateBookingCalculateCheckIntAndCheckOutCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateHotelAmount.UpdateBookingCalculateHotelAmountCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateRateAdult.UpdateBookingCalculateRateAdultCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.calculateRateChild.UpdateBookingCalculateRateChildCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.calculateInvoiceAmount.UpdateInvoiceCalculateInvoiceAmountCommand;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.manageAttachment.ManageAttachmentFileNameNotNullRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TotalCloneCommandHandler implements ICommandHandler<TotalCloneCommand> {

    private final IManageInvoiceService invoiceService;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageBookingService bookingService;
    private final IManageInvoiceStatusService invoiceStatusService;
    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;
    private final IManageRatePlanService ratePlanService;
    private final IManageNightTypeService nightTypeService;
    private final IManageRoomTypeService roomTypeService;
    private final IManageRoomCategoryService roomCategoryService;
    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final IManageInvoiceTransactionTypeService invoiceTransactionTypeService;
    private final IInvoiceCloseOperationService closeOperationService;

    public TotalCloneCommandHandler(IManageInvoiceService invoiceService,
            IManageAgencyService agencyService,
            IManageHotelService hotelService,
            IManageAttachmentTypeService attachmentTypeService,
            IManageBookingService bookingService,
            IManageInvoiceStatusService invoiceStatusService,
            ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService,
            IManageRatePlanService ratePlanService, IManageNightTypeService nightTypeService,
            IManageRoomTypeService roomTypeService, IManageRoomCategoryService roomCategoryService,
            IInvoiceStatusHistoryService invoiceStatusHistoryService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService,
            IManageInvoiceTransactionTypeService invoiceTransactionTypeService,
            IInvoiceCloseOperationService closeOperationService) {
        this.invoiceService = invoiceService;
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.attachmentTypeService = attachmentTypeService;
        this.bookingService = bookingService;
        this.invoiceStatusService = invoiceStatusService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
        this.ratePlanService = ratePlanService;
        this.nightTypeService = nightTypeService;
        this.roomTypeService = roomTypeService;
        this.roomCategoryService = roomCategoryService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.invoiceTransactionTypeService = invoiceTransactionTypeService;
        this.closeOperationService = closeOperationService;
    }

//    @Override
//    public void handle(TotalCloneCommand command){
//        ManageInvoiceDto invoiceToCloneDto = this.invoiceService.findById(command.getInvoiceToClone());
//        ManageHotelDto hotelDto = this.hotelService.findById(invoiceToCloneDto.getHotel().getId());
//        ManageAgencyDto agencyDto = this.agencyService.findById(invoiceToCloneDto.getAgency().getId());
//
//        //todo: es necesario actualizar el invoice number???
//        String invoiceNumber = InvoiceType.getInvoiceTypeCode(invoiceToCloneDto.getInvoiceType());
//        if (hotelDto.getManageTradingCompanies() != null
//                && hotelDto.getManageTradingCompanies().getIsApplyInvoice()) {
//            invoiceNumber += "-" + hotelDto.getManageTradingCompanies().getCode();
//        } else {
//            invoiceNumber += "-" + hotelDto.getCode();
//        }
//
//        LocalDate dueDate = LocalDateTime.now().toLocalDate().plusDays(agencyDto.getCreditDay() != null ? agencyDto.getCreditDay() : 0);
//
//        List<ManageBookingDto> clonedBookings = new ArrayList<>();
//        if(invoiceToCloneDto.getBookings() != null) {
//            for (ManageBookingDto bookingToClone : invoiceToCloneDto.getBookings()) {
//                ManageBookingDto newBooking = new ManageBookingDto(bookingToClone);
//                newBooking.setBookingDate(LocalDateTime.now());
//                List<ManageRoomRateDto> clonedRoomRates = new ArrayList<>();
//                if(bookingToClone.getRoomRates() != null){
//                    for(ManageRoomRateDto roomRateToClone : bookingToClone.getRoomRates()){
//                        ManageRoomRateDto newRoomRate = new ManageRoomRateDto(roomRateToClone);
//                        List<ManageAdjustmentDto> newAdjustments = new ArrayList<>();
//                        if(roomRateToClone.getAdjustments() != null){
//                            for(ManageAdjustmentDto adjustmentToClone : roomRateToClone.getAdjustments()){
//                                ManageAdjustmentDto newAdjustment = new ManageAdjustmentDto(adjustmentToClone);
//                                newAdjustments.add(newAdjustment);
//                            }
//                            newRoomRate.setAdjustments(newAdjustments);
//                        }
//                        clonedRoomRates.add(newRoomRate);
//                    }
//                    newBooking.setRoomRates(clonedRoomRates);
//                }
//                clonedBookings.add(newBooking);
//            }
//        }
//        List<ManageAttachmentDto> clonedAttachments = new ArrayList<>();
//        if(invoiceToCloneDto.getAttachments() != null){
//            for(ManageAttachmentDto attachmentToClone : invoiceToCloneDto.getAttachments()){
//                ManageAttachmentDto newAttachment = new ManageAttachmentDto(attachmentToClone);
//                clonedAttachments.add(newAttachment);
//            }
//        }
//        ManageInvoiceStatusDto invoiceStatus = this.invoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.RECONCILED);
//
//        ManageInvoiceDto newInvoice = new ManageInvoiceDto(
//                command.getClonedInvoice(),
//                0L,
//                0L,
//                invoiceNumber,
//                LocalDateTime.now(),
//                dueDate,
//                true,
//                invoiceToCloneDto.getInvoiceAmount(),
//                invoiceToCloneDto.getDueAmount(),
//                hotelDto,
//                agencyDto,
//                invoiceToCloneDto.getInvoiceType(),
//                EInvoiceStatus.RECONCILED,
//                invoiceToCloneDto.getAutoRec(),
//                clonedBookings,
//                clonedAttachments,
//                invoiceToCloneDto.getReSend(),
//                invoiceToCloneDto.getReSendDate(),
//                invoiceToCloneDto.getManageInvoiceType(),
//                invoiceStatus,
//                null,
//                true,
//                invoiceToCloneDto,
//                invoiceToCloneDto.getCredits()
//        );
//        ManageInvoiceDto created = this.invoiceService.create(newInvoice);
//
//        command.setClonedInvoiceId(created.getInvoiceId());
//        command.setClonedInvoiceNo(this.deleteHotelInfo(created.getInvoiceNumber()));
//
//        this.setInvoiceToCloneAmounts(invoiceToCloneDto, command.getEmployeeName());
//
//        try {
//            this.producerReplicateManageInvoiceService.create(created);
//        } catch (Exception e) {
//        }
//
//        //invoice status history
//        this.invoiceStatusHistoryService.create(
//                new InvoiceStatusHistoryDto(
//                        UUID.randomUUID(),
//                        created,
//                        "The invoice data was inserted.",
//                        null,
//                        command.getEmployeeName(),
//                        EInvoiceStatus.RECONCILED
//                )
//        );
//
//        //attachment status history
//        for(ManageAttachmentDto attachment : created.getAttachments()) {
//            this.attachmentStatusHistoryService.create(
//                    new AttachmentStatusHistoryDto(
//                            UUID.randomUUID(),
//                            "An attachment to the invoice was inserted. The file name: " + attachment.getFilename(),
//                            attachment.getAttachmentId(),
//                            created,
//                            command.getEmployeeName(),
//                            attachment.getEmployeeId(),
//                            null,
//                            null
//                    )
//            );
//        }
//    }
    @Override
    public void handle(TotalCloneCommand command) {

        List<ManageBookingDto> bookings = new ArrayList<>();
        List<ManageAttachmentDto> attachmentDtos = new ArrayList<>();

        ManageInvoiceDto invoiceToClone = this.invoiceService.findById(command.getInvoiceToClone());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());

        //vienen todos los attachments juntos, lo del padre y los nuevos
        for (TotalCloneAttachmentRequest attachmentRequest : command.getAttachments()) {
            RulesChecker.checkRule(new ManageAttachmentFileNameNotNullRule(
                    attachmentRequest.getFile()
            ));
            ManageAttachmentTypeDto attachmentType = this.attachmentTypeService.findById(
                    attachmentRequest.getType());

            ManageAttachmentDto attachmentDto = new ManageAttachmentDto(
                    UUID.randomUUID(),
                    null,
                    attachmentRequest.getFilename(),
                    attachmentRequest.getFile(),
                    attachmentRequest.getRemark(),
                    attachmentType,
                    null,
                    attachmentRequest.getEmployeeName(),
                    attachmentRequest.getEmployeeId(),
                    null,
                    null,
                    false
            );

            attachmentDtos.add(attachmentDto);
        }

        for (TotalCloneBookingRequest bookingRequest : command.getBookings()) {
            //no se agregan nuevos booking solo se pueden editar, el id siempre debe venir
            ManageBookingDto bookingToClone = this.bookingService.findById(bookingRequest.getId());
            List<ManageRoomRateDto> roomRateDtoList = new ArrayList<>();

            //cambiando los ids de los room rates para poder guardarlos
            //no me llevo los adjustments
            for (TotalCloneRoomRateRequest roomRate : bookingRequest.getRoomRates()) {
                long nights = this.calculateNights(roomRate.getCheckIn(), roomRate.getCheckOut());
                double rateAdult = roomRate.getAdults() != null ? this.calculateRateAdult(roomRate.getInvoiceAmount(), nights, roomRate.getAdults()) : 0.00;
                double rateChild = roomRate.getChildren() != null ? this.calculateRateChild(roomRate.getInvoiceAmount(), nights, roomRate.getChildren()) : 0.00;

                ManageRoomRateDto roomRateDto = new ManageRoomRateDto(
                        UUID.randomUUID(),
                        null,
                        roomRate.getCheckIn(),
                        roomRate.getCheckOut(),
                        roomRate.getInvoiceAmount(),
                        roomRate.getRoomNumber(),
                        roomRate.getAdults() != null ? roomRate.getAdults() : 0,
                        roomRate.getChildren() != null ? roomRate.getChildren() : 0,
                        rateAdult,
                        rateChild,
                        roomRate.getHotelAmount(),
                        roomRate.getRemark(),
                        null,
                        new ArrayList<>(),
                        nights,
                        false
                );
                roomRateDtoList.add(roomRateDto);
            }

            //obtener los nomencladores si vienen en el request
            ManageNightTypeDto nightTypeDto = bookingRequest.getNightType() != null
                    ? this.nightTypeService.findById(bookingRequest.getNightType())
                    : null;
            ManageRoomTypeDto roomTypeDto = bookingRequest.getRoomType() != null
                    ? this.roomTypeService.findById(bookingRequest.getRoomType())
                    : null;
            ManageRoomCategoryDto roomCategoryDto = bookingRequest.getRoomCategory() != null
                    ? this.roomCategoryService.findById(bookingRequest.getRoomCategory())
                    : null;
            ManageRatePlanDto ratePlanDto = bookingRequest.getRatePlan() != null
                    ? this.ratePlanService.findById(bookingRequest.getRatePlan())
                    : null;

            //creando el nuevo booking con los valores que vienen, el amount se agarra directo del padre
            ManageBookingDto newBooking = new ManageBookingDto(
                    UUID.randomUUID(),
                    null,
                    null,
                    bookingRequest.getHotelCreationDate(),
                    bookingRequest.getBookingDate(),
                    bookingRequest.getCheckIn(),
                    bookingRequest.getCheckOut(),
                    bookingRequest.getHotelBookingNumber(),
                    bookingRequest.getFullName(),
                    bookingRequest.getFirstName(),
                    bookingRequest.getLastName(),
                    bookingToClone.getInvoiceAmount(),
                    bookingToClone.getDueAmount(),
                    bookingRequest.getRoomNumber(),
                    bookingRequest.getCouponNumber(),
                    bookingRequest.getAdults(),
                    bookingRequest.getChildren(),
                    bookingRequest.getRateAdult(),
                    bookingRequest.getRateChild(),
                    bookingRequest.getHotelInvoiceNumber(),
                    bookingRequest.getFolioNumber(),
                    bookingRequest.getHotelAmount(),
                    bookingRequest.getDescription(),
                    null,
                    ratePlanDto,
                    nightTypeDto,
                    roomTypeDto,
                    roomCategoryDto,
                    roomRateDtoList,
                    null,
                    bookingToClone,
                    bookingRequest.getContract(),
                    false
            );
            bookings.add(newBooking);
        }

        //actualizando los bookings con la info de los room rate
        for (ManageBookingDto booking : bookings) {
            command.getMediator().send(new UpdateBookingCalculateCheckIntAndCheckOutCommand(booking));
            command.getMediator().send(new UpdateBookingCalculateBookingAmountCommand(booking));
            command.getMediator().send(new UpdateBookingCalculateHotelAmountCommand(booking));
            command.getMediator().send(new UpdateBookingCalculateBookingAdultsCommand(booking));
            command.getMediator().send(new UpdateBookingCalculateBookingChildrenCommand(booking));
            command.getMediator().send(new UpdateBookingCalculateRateChildCommand(booking));
            command.getMediator().send(new UpdateBookingCalculateRateAdultCommand(booking));
            booking.setDueAmount(booking.getInvoiceAmount());
        }

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());
        String invoiceNumber = InvoiceType.getInvoiceTypeCode(invoiceToClone.getInvoiceType());
        if (hotelDto.getManageTradingCompanies() != null
                && hotelDto.getManageTradingCompanies().getIsApplyInvoice()) {
            invoiceNumber += "-" + hotelDto.getManageTradingCompanies().getCode();
        } else {
            invoiceNumber += "-" + hotelDto.getCode();
        }

        LocalDate dueDate = command.getInvoiceDate().toLocalDate().plusDays(agencyDto.getCreditDay() != null ? agencyDto.getCreditDay() : 0);
//        LocalDate dueDate = command.getInvoiceDate().toLocalDate();

        EInvoiceStatus status = EInvoiceStatus.RECONCILED;
        ManageInvoiceStatusDto invoiceStatus = this.invoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.RECONCILED);
        ManageInvoiceDto clonedInvoice = new ManageInvoiceDto(
                command.getClonedInvoice(),
                0L,
                0L,
                invoiceNumber,
                //command.getInvoiceDate(),
                this.invoiceDate(invoiceToClone.getHotel().getId()),
                dueDate,
                true,
                invoiceToClone.getInvoiceAmount(), //TODO: revisar si es mejor asi o calcularlo nuevamente
                invoiceToClone.getDueAmount(),
                hotelDto,
                agencyDto,
                invoiceToClone.getInvoiceType(), //TODO: se queda con el invoiceType del padre?
                status,
                false, //TODO: de donde sale esto?
                bookings,
                attachmentDtos,
                false,
                null,
                invoiceToClone.getManageInvoiceType(),
                invoiceStatus,
                null,
                true,
                invoiceToClone,
                invoiceToClone.getCredits(),0
        );
        //actualizando el invoice con la info de los bookings
        command.getMediator().send(new UpdateInvoiceCalculateInvoiceAmountCommand(clonedInvoice));
        clonedInvoice.setDueAmount(clonedInvoice.getInvoiceAmount());
        clonedInvoice.setOriginalAmount(clonedInvoice.getInvoiceAmount());

        ManageInvoiceDto created = this.invoiceService.create(clonedInvoice);
        command.setClonedInvoiceId(created.getInvoiceId());
        command.setClonedInvoiceNo(this.deleteHotelInfo(created.getInvoiceNumber()));

        this.setInvoiceToCloneAmounts(invoiceToClone, command.getEmployeeName());

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
                        command.getEmployeeName(),
                        status
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
                            command.getEmployeeName(),
                            attachment.getEmployeeId(),
                            null,
                            null
                    )
            );
        }
    }

    private LocalDateTime invoiceDate(UUID hotel) {
        InvoiceCloseOperationDto closeOperationDto = this.closeOperationService.findActiveByHotelId(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return LocalDateTime.now(ZoneId.of("UTC"));
        }
        return LocalDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")));
    }

    private void setInvoiceToCloneAmounts(ManageInvoiceDto invoiceDto, String employee) {

        for (ManageBookingDto bookingDto : invoiceDto.getBookings()) {
            for (ManageRoomRateDto roomRateDto : bookingDto.getRoomRates()) {
                List<ManageAdjustmentDto> adjustmentDtoList = new ArrayList<>();
                ManageAdjustmentDto adjustmentDto = new ManageAdjustmentDto(
                        UUID.randomUUID(),
                        null,
                        -roomRateDto.getInvoiceAmount(),
                        LocalDateTime.now(),
                        "Automatic adjustment generated to closed the invoice, because it was cloned",
                        this.invoiceTransactionTypeService.findByDefaults(),
                        null,
                        null,
                        employee,
                        false
                );
                adjustmentDtoList.add(adjustmentDto);
                roomRateDto.setAdjustments(adjustmentDtoList);
                roomRateDto.setInvoiceAmount(0.00);
            }
            this.bookingService.calculateInvoiceAmount(bookingDto);
        }
        this.invoiceService.calculateInvoiceAmount(invoiceDto);
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }

    private Double calculateRateAdult(Double rateAmount, Long nights, Integer adults) {
        return adults == 0
                ? 0.0
                : rateAmount / ((nights == 0 ? 1 : nights) * adults);
    }

    private Double calculateRateChild(Double rateAmount, Long nights, Integer children) {
        return children == 0
                ? 0.0
                : rateAmount / ((nights == 0 ? 1 : nights) * children);
    }

    private Long calculateNights(LocalDateTime checkIn, LocalDateTime checkOut) {
        return ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
    }
}
