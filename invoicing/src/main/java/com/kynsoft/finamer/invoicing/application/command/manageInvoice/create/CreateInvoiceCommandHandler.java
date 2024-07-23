package com.kynsoft.finamer.invoicing.application.command.manageInvoice.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.services.*;
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

        String invoiceNumber = InvoiceType.getInvoiceTypeCode(command.getInvoiceType());

        ManageInvoiceDto invoiceDto = service.create(new ManageInvoiceDto(command.getId(), 0L, 0L,
                invoiceNumber, command.getInvoiceDate(), command.getDueDate(), command.getIsManual(),
                command.getInvoiceAmount(), hotelDto, agencyDto, command.getInvoiceType(), EInvoiceStatus.PROCECSED,
                false,
                null, null, null, null, null, null));
        command.setInvoiceId(invoiceDto.getInvoiceId());
    }
}
