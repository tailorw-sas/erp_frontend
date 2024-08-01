package com.kynsoft.finamer.invoicing.application.command.income.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.rules.income.CheckIfIncomeDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateIncomeCommandHandler implements ICommandHandler<CreateIncomeCommand> {

    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IManageInvoiceTypeService invoiceTypeService;
    private final IManageInvoiceStatusService invoiceStatusService;
    private final IManageInvoiceService manageInvoiceService;

    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;

    public CreateIncomeCommandHandler(IManageAgencyService agencyService,
                                      IManageHotelService hotelService,
                                      IManageInvoiceTypeService invoiceTypeService,
                                      IManageInvoiceStatusService invoiceStatusService,
                                      IManageInvoiceService manageInvoiceService, IInvoiceStatusHistoryService invoiceStatusHistoryService) {
        this.agencyService = agencyService;
        this.hotelService = hotelService;
        this.invoiceTypeService = invoiceTypeService;
        this.invoiceStatusService = invoiceStatusService;
        this.manageInvoiceService = manageInvoiceService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
    }

    @Override
    public void handle(CreateIncomeCommand command) {

        RulesChecker.checkRule(new CheckIfIncomeDateIsBeforeCurrentDateRule(command.getInvoiceDate()));

        ManageAgencyDto agencyDto = this.agencyService.findById(command.getAgency());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getHotel());

        ManageInvoiceTypeDto invoiceTypeDto = null;
        try {
            invoiceTypeDto = this.invoiceTypeService.findById(command.getInvoiceType());
        } catch (Exception e) {
        }

        ManageInvoiceStatusDto invoiceStatusDto = null;
        try {
            invoiceStatusDto = this.invoiceStatusService.findById(command.getInvoiceStatus());
        } catch (Exception e) {
        }

        ManageInvoiceDto invoiceDto = this.manageInvoiceService.create(new ManageInvoiceDto(
                command.getId(), 
                0L, 
                0L, 
                InvoiceType.getInvoiceTypeCode(EInvoiceType.INCOME), 
                command.getInvoiceDate(), 
                command.getDueDate(), 
                command.getManual(), 
                0.0, 
                0.0, 
                hotelDto, 
                agencyDto, 
                EInvoiceType.INCOME, 
                EInvoiceStatus.SENT, 
                Boolean.FALSE, 
                null, 
                null, 
                command.getReSend(), 
                command.getReSendDate(), 
                invoiceTypeDto, 
                invoiceStatusDto,
                null
        ));
        command.setInvoiceId(invoiceDto.getInvoiceId());

        this.updateInvoiceStatusHistory(invoiceDto, command.getEmployee());

    }

    private void updateInvoiceStatusHistory(ManageInvoiceDto invoiceDto, String employee){

        InvoiceStatusHistoryDto dto = new InvoiceStatusHistoryDto();
        dto.setId(UUID.randomUUID());
        dto.setInvoice(invoiceDto);
        dto.setDescription("The income data was inserted.");
        dto.setEmployee(employee);

        this.invoiceStatusHistoryService.create(dto);

    }
}
