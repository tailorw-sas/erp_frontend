package com.kynsoft.finamer.invoicing.application.command.manageInvoice.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Component;

@Component
public class CreateInvoiceCommandHandler implements ICommandHandler<CreateInvoiceCommand> {

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

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());

        String invoiceNumber = InvoiceType.getInvoiceTypeCode(command.getInvoiceType());

        if (hotelDto.getManageTradingCompanies() != null && hotelDto.getManageTradingCompanies().getIsApplyInvoice()) {
            invoiceNumber += "-" + hotelDto.getManageTradingCompanies().getCode();
        } else {
            invoiceNumber += "-" + hotelDto.getCode();
        }

        ManageInvoiceDto invoiceDto = service.create(new ManageInvoiceDto(command.getId(), 0L, 0L,
                invoiceNumber, command.getInvoiceDate(), command.getDueDate(), command.getIsManual(),
                command.getInvoiceAmount(), command.getInvoiceAmount(), hotelDto, agencyDto, command.getInvoiceType(), EInvoiceStatus.PROCECSED,
                false,
                null, null, null, null, null, null, null,  false,
                null, 0.0));
        command.setInvoiceId(invoiceDto.getInvoiceId());
        try {
            this.producerReplicateManageInvoiceService.create(invoiceDto);
        } catch (Exception e) {
        }
    }
}
