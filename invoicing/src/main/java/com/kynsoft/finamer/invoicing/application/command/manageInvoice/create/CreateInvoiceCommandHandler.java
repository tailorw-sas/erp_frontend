package com.kynsoft.finamer.invoicing.application.command.manageInvoice.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreateInvoiceCommandHandler implements ICommandHandler<CreateInvoiceCommand> {

    private Logger log = LoggerFactory.getLogger(CreateInvoiceCommandHandler.class);
    private final IManageInvoiceService service;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageInvoiceTypeService iManageInvoiceTypeService;
    private final IManageInvoiceStatusService manageInvoiceStatusService;
    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    public CreateInvoiceCommandHandler(IManageInvoiceService service, IManageAgencyService agencyService,
                                       IManageHotelService hotelService, IManageInvoiceTypeService iManageInvoiceTypeService,
                                       IManageInvoiceStatusService manageInvoiceStatusService,
                                       ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService) {
        this.service = service;
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.iManageInvoiceTypeService = iManageInvoiceTypeService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
    }

    @Override
    public void handle(CreateInvoiceCommand command) {

        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());
        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());

        ManageInvoiceStatusDto manageInvoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.PROCESSED);
        ManageInvoiceTypeDto invoiceTypeDto = this.iManageInvoiceTypeService.findByEInvoiceType(command.getInvoiceType());
        ManageInvoiceStatusDto manageInvoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.PROCESSED);

        ManageInvoiceDto creInvoiceDto = new ManageInvoiceDto(command.getId(), hotelDto, agencyDto, command.getInvoiceType(), invoiceTypeDto,
                EInvoiceStatus.PROCESSED, manageInvoiceStatus, command.getInvoiceDate(), command.getIsManual(), command.getInvoiceAmount(),
                command.getInvoiceAmount(), command.getInvoiceAmount(), null, null, Boolean.FALSE, null);

        ManageInvoiceDto creInvoiceDto = new ManageInvoiceDto(command.getId(), 0L, null,
                null, null, command.getInvoiceDate(), command.getDueDate(), command.getIsManual(),
                command.getInvoiceAmount(), command.getInvoiceAmount(), hotelDto, agencyDto, command.getInvoiceType(), EInvoiceStatus.PROCESSED,
                false,
                null, null, null, null, invoiceTypeDto, manageInvoiceStatus, null,  false,
                null, 0.0,0);
        creInvoiceDto.setOriginalAmount(command.getInvoiceAmount());
        creInvoiceDto.setDeleteInvoice(false);
        ManageInvoiceDto invoiceDto = service.create(creInvoiceDto);
        command.setInvoiceId(invoiceDto.getInvoiceId());
        try {
            this.producerReplicateManageInvoiceService.create(invoiceDto, null, null);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
