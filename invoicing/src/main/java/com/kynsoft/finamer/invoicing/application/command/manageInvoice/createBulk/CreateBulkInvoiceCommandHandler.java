package com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTransactionTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageNightTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomCategoryService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class CreateBulkInvoiceCommandHandler implements ICommandHandler<CreateBulkInvoiceCommand> {

    private final IManageRatePlanService ratePlanService;
    private final IManageNightTypeService nightTypeService;
    private final IManageRoomTypeService roomTypeService;
    private final IManageRoomCategoryService roomCategoryService;
    private final IManageInvoiceTransactionTypeService transactionTypeService;

    private final IManageInvoiceService service;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageInvoiceTypeService iManageInvoiceTypeService;
    private final IManageInvoiceStatusService manageInvoiceStatusService;
    private final IManageAttachmentTypeService attachmentTypeService;

    public CreateBulkInvoiceCommandHandler(IManageRatePlanService ratePlanService,
            IManageNightTypeService nightTypeService, IManageRoomTypeService roomTypeService,
            IManageRoomCategoryService roomCategoryService, IManageInvoiceTransactionTypeService transactionTypeService,
            IManageInvoiceService service, IManageAgencyService agencyService, IManageHotelService hotelService,
            IManageInvoiceTypeService iManageInvoiceTypeService,
            IManageInvoiceStatusService manageInvoiceStatusService,
            IManageAttachmentTypeService attachmentTypeService) {

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
    }

    @Override
    @Transactional
    public void handle(CreateBulkInvoiceCommand command) {

        List<ManageAdjustmentDto> adjustments = new LinkedList<>();
        List<ManageBookingDto> bookings = new LinkedList<>();
        List<ManageRoomRateDto> roomRates = new LinkedList<>();
        List<ManageAttachmentDto> attachmentDtos = new LinkedList<>();

        for (int i = 0; i < command.getBookingCommands().size(); i++) {

            ManageNightTypeDto nightTypeDto = command.getBookingCommands().get(i).getNightType() != null
                    ? this.nightTypeService.findById(command.getBookingCommands().get(i).getNightType())
                    : null;
            ManageRoomTypeDto roomTypeDto = command.getBookingCommands().get(i).getRoomType() != null
                    ? this.roomTypeService.findById(command.getBookingCommands().get(i).getRoomType())
                    : null;
            ManageRoomCategoryDto roomCategoryDto = command.getBookingCommands().get(i).getRoomCategory() != null
                    ? this.roomCategoryService.findById(command.getBookingCommands().get(i).getRoomCategory())
                    : null;
            ManageRatePlanDto ratePlanDto = command.getBookingCommands().get(i).getRatePlan() != null
                    ? this.ratePlanService.findById(command.getBookingCommands().get(i).getRatePlan())
                    : null;

            bookings.add(new ManageBookingDto(command.getBookingCommands().get(i).getId(),
                    null,
                    command.getBookingCommands().get(i).getHotelCreationDate(),
                    command.getBookingCommands().get(i).getBookingDate(),
                    command.getBookingCommands().get(i).getCheckIn(),
                    command.getBookingCommands().get(i).getCheckOut(),
                    command.getBookingCommands().get(i).getHotelBookingNumber(),
                    command.getBookingCommands().get(i).getFirstName(),
                    command.getBookingCommands().get(i).getLastName(),
                    command.getBookingCommands().get(i).getInvoiceAmount(),
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
                    roomCategoryDto, new LinkedList<>()));

        }

        for (int i = 0; i < command.getRoomRateCommands().size(); i++) {
            ManageRoomRateDto roomRateDto = new ManageRoomRateDto(
                    command.getRoomRateCommands().get(i).getId(),
                    null,
                    command.getRoomRateCommands().get(i).getCheckIn(),
                    command.getRoomRateCommands().get(i).getCheckOut(),
                    command.getRoomRateCommands().get(i).getInvoiceAmount(),
                    command.getRoomRateCommands().get(i).getRoomNumber(),
                    command.getRoomRateCommands().get(i).getAdults(),
                    command.getRoomRateCommands().get(i).getChildren(),
                    command.getRoomRateCommands().get(i).getRateAdult(),
                    command.getRoomRateCommands().get(i).getRateChild(),
                    command.getRoomRateCommands().get(i).getHotelAmount(),
                    command.getRoomRateCommands().get(i).getRemark(),
                    null,
                    new LinkedList<>());

            if (command.getRoomRateCommands().get(i).getBooking() != null) {
                for (ManageBookingDto bookingDto : bookings) {
                    if (bookingDto.getId().equals(command.getRoomRateCommands().get(i).getBooking())) {

                        List<ManageRoomRateDto> rates = bookingDto.getRoomRates();
                        rates.add(roomRateDto);

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
                                            command.getAdjustmentCommands().get(i).getTransactionType())
                                    : null;

            ManageAdjustmentDto adjustmentDto = new ManageAdjustmentDto(
                    command.getAdjustmentCommands().get(i).getId(),
                    null,
                    command.getAdjustmentCommands().get(i).getAmount(),
                    command.getAdjustmentCommands().get(i).getDate(),
                    command.getAdjustmentCommands().get(i).getDescription(),
                    transactionTypeDto,
                    null);

            if (command.getAdjustmentCommands().get(i).getRoomRate() != null) {
                for (ManageRoomRateDto rateDto : roomRates) {
                    if (rateDto.getId().equals(command.getAdjustmentCommands().get(i).getRoomRate())) {

                        List<ManageAdjustmentDto> adjustmentDtos = rateDto.getAdjustments();
                        adjustmentDtos.add(adjustmentDto);

                        rateDto.setAdjustments(adjustmentDtos);

                    }
                }
            }

            adjustments.add(adjustmentDto);

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
                    null);

            attachmentDtos.add(attachmentDto);
        }

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getInvoiceCommand().getAgency());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getInvoiceCommand().getHotel());

        String invoiceNumber = "";

        if (hotelDto.getManageTradingCompanies() != null
                && hotelDto.getManageTradingCompanies().getIsApplyInvoice()) {
            invoiceNumber += hotelDto.getManageTradingCompanies().getCode()
                    + hotelDto.getManageTradingCompanies().getAutogen_code();
        } else {
            invoiceNumber += hotelDto.getCode() + hotelDto.getAutogen_code();
        }

        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(command.getInvoiceCommand().getId(), 0L,
                invoiceNumber,
                command.getInvoiceCommand().getInvoiceDate(), command.getInvoiceCommand().getDueDate(),
                command.getInvoiceCommand().getIsManual(),
                command.getInvoiceCommand().getInvoiceAmount(), hotelDto, agencyDto,
                command.getInvoiceCommand().getInvoiceType(), EInvoiceStatus.PROCECSED,
                false, bookings, attachmentDtos);

        service.create(invoiceDto);

    }
}
