package com.kynsoft.finamer.invoicing.application.command.manageInvoice.partialClone;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;

import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageBooking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomRate;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Transactional
public class PartialCloneInvoiceCommandHandler implements ICommandHandler<PartialCloneInvoiceCommand> {

    private final IManageInvoiceService service;

    private final IManageAttachmentTypeService attachmentTypeService;

    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;
    private final IManageRoomRateService rateService;
    private final IParameterizationService parameterizationService;
    private final IManageInvoiceStatusService manageInvoiceStatusService;
    private final IInvoiceCloseOperationService closeOperationService;

    public PartialCloneInvoiceCommandHandler(

            IManageInvoiceService service,
            IManageAttachmentTypeService attachmentTypeService,
            ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService, IManageRoomRateService rateService, IParameterizationService parameterizationService, IManageInvoiceStatusService manageInvoiceStatusService, IInvoiceCloseOperationService closeOperationService) {

        this.service = service;

        this.attachmentTypeService = attachmentTypeService;

        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
        this.rateService = rateService;
        this.parameterizationService = parameterizationService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.closeOperationService = closeOperationService;
    }

    @Override
    @Transactional
    public void handle(PartialCloneInvoiceCommand command) {

        ManageInvoiceDto invoiceToClone = this.service.findById(command.getInvoice());
        List<ManageBookingDto> bookingDtos = new ArrayList<>();

        List<ManageRoomRateDto> roomRateDtos = new ArrayList<>();

        for (int i = 0; i < invoiceToClone.getBookings().size(); i++) {

            ManageBookingDto newBooking = new ManageBookingDto(invoiceToClone.getBookings().get(i));

            List<ManageRoomRate> roomRates = this.rateService.findByBooking(new ManageBooking(invoiceToClone.getBookings().get(i)));

            for (ManageRoomRate roomRate : roomRates) {

                ManageRoomRateDto newRoomRate = roomRate.toAggregate();

                newRoomRate.setBooking(newBooking);
                newRoomRate.setAdjustments(null);
                newRoomRate.setHotelAmount(0.00);
                roomRateDtos.add(newRoomRate);

            }

            bookingDtos.add(newBooking);

        }

        for (int i = 0; i < command.getRoomRateAdjustments().size(); i++) {

            for (ManageRoomRateDto roomRate : roomRateDtos) {
                if (command.getRoomRateAdjustments().get(i).getRoomRate().equals(roomRate.getId())) {
                    roomRate.setInvoiceAmount(command.getRoomRateAdjustments().get(i).getAdjustment().getAmount());
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

        for (ManageBookingDto booking : bookingDtos) {
            this.calculateBookingHotelAmount(booking);

        }

        String invoiceNumber = InvoiceType.getInvoiceTypeCode(invoiceToClone.getInvoiceType());

        if (invoiceToClone.getHotel().getManageTradingCompanies() != null
                && invoiceToClone.getHotel().getManageTradingCompanies().getIsApplyInvoice()) {
            invoiceNumber += "-" + invoiceToClone.getHotel().getManageTradingCompanies().getCode();
        } else {
            invoiceNumber += "-" + invoiceToClone.getHotel().getCode();
        }

        EInvoiceStatus status = EInvoiceStatus.RECONCILED;
        ParameterizationDto parameterization = this.parameterizationService.findActiveParameterization();
        ManageInvoiceStatusDto invoiceStatus = parameterization != null ? this.manageInvoiceStatusService.findByCode(parameterization.getProcessed()) : null;
        ManageInvoiceDto invoiceDto = new ManageInvoiceDto(UUID.randomUUID(), 0L, 0L,
                invoiceNumber,
                invoiceToClone.getInvoiceDate(), invoiceToClone.getDueDate(),
                true,
                invoiceToClone.getInvoiceAmount(),
                invoiceToClone.getInvoiceAmount(), invoiceToClone.getHotel(), invoiceToClone.getAgency(),
                invoiceToClone.getInvoiceType(), status,
                false, bookingDtos, attachmentDtos, null, null, null, invoiceStatus, null, true, invoiceToClone, invoiceToClone.getCredits());

        ManageInvoiceDto created = service.create(invoiceDto);

        try {
            this.producerReplicateManageInvoiceService.create(created);
        } catch (Exception e) {
        }

        command.setBookings(bookingDtos.stream().map(e -> e.getId()).collect(Collectors.toList()));
        command.setRoomRates(roomRateDtos.stream().map(e -> e.getId()).collect(Collectors.toList()));
        command.setAttachments(attachmentDtos.stream().map(e -> e.getId()).collect(Collectors.toList()));
        command.setCloned(created.getId());
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
