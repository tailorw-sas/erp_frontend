package com.kynsoft.finamer.invoicing.application.command.manageInvoice.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTypeService;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class CreateInvoiceCommandHandler implements ICommandHandler<CreateInvoiceCommand> {

    private final IManageInvoiceService service;
    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageInvoiceTypeService iManageInvoiceTypeService;
    private final IManageInvoiceStatusService manageInvoiceStatusService;

    public CreateInvoiceCommandHandler(IManageInvoiceService service, IManageAgencyService agencyService,
            IManageHotelService hotelService, IManageInvoiceTypeService iManageInvoiceTypeService,
            IManageInvoiceStatusService manageInvoiceStatusService) {
        this.service = service;
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.iManageInvoiceTypeService = iManageInvoiceTypeService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
    }

    @Override
    public void handle(CreateInvoiceCommand command) {

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());
        ManageInvoiceTypeDto invoiceTypeDto = this.iManageInvoiceTypeService.findById(command.getInvoiceType());

        ManageInvoiceStatusDto invoiceStatusDto = this.manageInvoiceStatusService.findByCode("PRO");

        if (invoiceStatusDto == null) {
            ManageInvoiceStatusDto newInvoiceStatusDto = new ManageInvoiceStatusDto(UUID.randomUUID(), "PRO",
                    "Procesed");
            this.manageInvoiceStatusService.create(newInvoiceStatusDto);
            invoiceStatusDto = newInvoiceStatusDto;
        }

        System.err.println("INVOICE COMMAND ID " + command.getId().toString());

        service.create(new ManageInvoiceDto(command.getId(), 0L, 0L, command.getInvoiceDate(), command.getIsManual(),
                command.getInvoiceAmount(), hotelDto, agencyDto, invoiceTypeDto, invoiceStatusDto, false));
    }
}
