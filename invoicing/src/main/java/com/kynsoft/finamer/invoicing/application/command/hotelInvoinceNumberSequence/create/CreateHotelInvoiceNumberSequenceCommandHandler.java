package com.kynsoft.finamer.invoicing.application.command.hotelInvoinceNumberSequence.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.HotelInvoiceNumberSequenceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.services.IHotelInvoiceNumberSequenceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;
import org.springframework.stereotype.Component;

@Component
public class CreateHotelInvoiceNumberSequenceCommandHandler implements ICommandHandler<CreateHotelInvoiceNumberSequenceCommand> {

    private final IHotelInvoiceNumberSequenceService service;
    private final IManageHotelService hotelService;
    private final IManageTradingCompaniesService tradingCompaniesService;

    public CreateHotelInvoiceNumberSequenceCommandHandler(IHotelInvoiceNumberSequenceService service,
                                                          IManageHotelService hotelService,
                                                          IManageTradingCompaniesService tradingCompaniesService) {
        this.service = service;
        this.hotelService = hotelService;
        this.tradingCompaniesService = tradingCompaniesService;
    }

    @Override
    public void handle(CreateHotelInvoiceNumberSequenceCommand command) {
        ManageHotelDto hotelDto = command.getCodeHotel() != null ? this.hotelService.findByCode(command.getCodeHotel()) : null;
        ManageTradingCompaniesDto tradingCompaniesDto = command.getCodeTradingCompanies() != null ? this.tradingCompaniesService.findManageTradingCompaniesByCode(command.getCodeTradingCompanies()) : null;
        
        if (hotelDto == null && tradingCompaniesDto == null) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.HOTEL_INVOICE_NUMBER_SEQUENCE_NOT_FOUND, new ErrorField("id", "El Hotel y el trading company no pueden ser nulos ambos.")));
        }
        if (command.getInvoiceNo() < 0) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.HOTEL_INVOICE_NUMBER_SEQUENCE_NOT_FOUND, new ErrorField("id", "El numero de factura debe ser mayor que cero.")));
        }
        service.create(new HotelInvoiceNumberSequenceDto(
                command.getId(), 
                command.getInvoiceNo(), 
                hotelDto, 
                tradingCompaniesDto, 
                command.getInvoiceType()
        ));
    }
}
