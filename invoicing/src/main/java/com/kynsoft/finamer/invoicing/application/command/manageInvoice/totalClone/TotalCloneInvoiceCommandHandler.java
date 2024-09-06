package com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.manageBooking.ManageBookingHotelBookingNumberValidationRule;
import com.kynsoft.finamer.invoicing.domain.rules.manageInvoice.ManageInvoiceInvoiceDateInCloseOperationRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
@Transactional
public class TotalCloneInvoiceCommandHandler implements ICommandHandler<TotalCloneInvoiceCommand> {

    private final IManageRatePlanService ratePlanService;
    private final IManageNightTypeService nightTypeService;
    private final IManageRoomTypeService roomTypeService;
    private final IManageRoomCategoryService roomCategoryService;

    private final IManageInvoiceService service;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageBookingService bookingService;
    private final IParameterizationService parameterizationService;
    private final IManageInvoiceStatusService invoiceStatusService;

    private final IInvoiceCloseOperationService closeOperationService;

    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    public TotalCloneInvoiceCommandHandler(IManageRatePlanService ratePlanService,
                                           IManageNightTypeService nightTypeService, IManageRoomTypeService roomTypeService,
                                           IManageRoomCategoryService roomCategoryService, IManageInvoiceService service,
                                           IManageAgencyService agencyService, IManageHotelService hotelService,
                                           IManageAttachmentTypeService attachmentTypeService, IManageBookingService bookingService, IParameterizationService parameterizationService, IManageInvoiceStatusService invoiceStatusService,
                                           IInvoiceCloseOperationService closeOperationService,
                                           ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService) {
        this.ratePlanService = ratePlanService;
        this.nightTypeService = nightTypeService;
        this.roomTypeService = roomTypeService;
        this.roomCategoryService = roomCategoryService;
        this.service = service;
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.attachmentTypeService = attachmentTypeService;
        this.bookingService = bookingService;
        this.parameterizationService = parameterizationService;
        this.invoiceStatusService = invoiceStatusService;
        this.closeOperationService = closeOperationService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
    }

    @Override
    @Transactional
    public void handle(TotalCloneInvoiceCommand command) {
        ManageHotelDto hotelDto = this.hotelService.findById(command.getInvoiceCommand().getHotel());
        RulesChecker.checkRule(new ManageInvoiceInvoiceDateInCloseOperationRule(
                this.closeOperationService,
                command.getInvoiceCommand().getInvoiceDate().toLocalDate(),
                hotelDto.getId()));

        List<ManageBookingDto> bookings = new LinkedList<>();
        List<ManageAttachmentDto> attachmentDtos = new LinkedList<>();

        ManageInvoiceDto invoiceToClone = this.service.findById(command.getInvoiceToClone());

        if (invoiceToClone.getAttachments() != null && invoiceToClone.getAttachments().size() > 0) {

            for (int i = 0; i < invoiceToClone.getAttachments().size(); i++) {

                attachmentDtos.add(new ManageAttachmentDto(UUID.randomUUID(), 0L,
                        invoiceToClone.getAttachments().get(i).getFilename(),
                        invoiceToClone.getAttachments().get(i).getFile(),
                        invoiceToClone.getAttachments().get(i).getRemark(),
                        invoiceToClone.getAttachments().get(i).getType(), null,
                        invoiceToClone.getAttachments().get(i).getEmployee(),
                        invoiceToClone.getAttachments().get(i).getEmployeeId(), null, null));

            }

        }

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

            ManageBookingDto newBookingDto = new ManageBookingDto(command.getBookingCommands().get(i).getId(),
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
                    roomCategoryDto, new LinkedList<>(), null, null);

            ManageBookingDto oldBookingDto = this.bookingService.findById(command.getBookingCommands().get(i).getId());

            newBookingDto.setId(UUID.randomUUID());

            List<ManageRoomRateDto> tempRoomRates = new ArrayList<>();

            for (int index = 0; index < oldBookingDto.getRoomRates().size(); index++) {
                ManageRoomRateDto roomRate = new ManageRoomRateDto(UUID.randomUUID(), 0L,
                        oldBookingDto.getRoomRates().get(index).getCheckIn(),
                        oldBookingDto.getRoomRates().get(index).getCheckOut(),
                        oldBookingDto.getRoomRates().get(index).getInvoiceAmount(),
                        oldBookingDto.getRoomRates().get(index).getRoomNumber(),
                        oldBookingDto.getRoomRates().get(index).getAdults(),
                        oldBookingDto.getRoomRates().get(index).getChildren(),
                        oldBookingDto.getRoomRates().get(index).getRateAdult(),
                        oldBookingDto.getRoomRates().get(index).getRateChild(),
                        oldBookingDto.getRoomRates().get(index).getHotelAmount(),
                        oldBookingDto.getRoomRates().get(index).getRemark(),
                        null,
                        new ArrayList<>(), null);

                ManageAdjustmentDto adjustmentDto = new ManageAdjustmentDto(UUID.randomUUID(), 0L,
                        -oldBookingDto.getRoomRates().get(index).getInvoiceAmount(),
                        LocalDateTime.now(),
                        "Automatic adjustment generated to closed the invoice, because it was cloned", null, null,
                        null, command.getEmployee());

                List<ManageAdjustmentDto> tempAdjustment = new ArrayList<>();

                tempAdjustment.add(adjustmentDto);
                roomRate.setAdjustments(tempAdjustment);

                tempRoomRates.add(roomRate);

            }

            newBookingDto.setRoomRates(tempRoomRates);

            bookings.add(newBookingDto);
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

            attachmentDtos.add(attachmentDto);
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

        EInvoiceStatus status = EInvoiceStatus.RECONCILED;
        ParameterizationDto parameterization = this.parameterizationService.findActiveParameterization();
        ManageInvoiceStatusDto invoiceStatus = parameterization != null ? this.invoiceStatusService.findByCode(parameterization.getProcessed()) : null;
        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(command.getInvoiceCommand().getId(), 0L, 0L,
                invoiceNumber,
                command.getInvoiceCommand().getInvoiceDate(), command.getInvoiceCommand().getDueDate(),
                true,
                0.00,
                0.00, hotelDto, agencyDto,
                command.getInvoiceCommand().getInvoiceType(), status,
                false, bookings, attachmentDtos, null, null, null, invoiceStatus, null, true,
                invoiceToClone, invoiceToClone.getCredits());

        ManageInvoiceDto created = service.create(invoiceDto);

        invoiceToClone.setInvoiceAmount(0.00);
        invoiceToClone.setDueAmount(0.00);

        this.service.update(invoiceToClone);

        command.setInvoiceId(created.getInvoiceId());
        command.setInvoiceNo(created.getInvoiceNumber());

        try {
            this.producerReplicateManageInvoiceService.create(created);
        } catch (Exception e) {
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
