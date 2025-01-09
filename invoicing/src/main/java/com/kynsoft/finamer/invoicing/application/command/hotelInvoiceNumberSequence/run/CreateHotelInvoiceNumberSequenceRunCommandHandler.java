package com.kynsoft.finamer.invoicing.application.command.hotelInvoiceNumberSequence.run;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.HotelInvoiceNumberSequenceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.services.IHotelInvoiceNumberSequenceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageTradingCompaniesService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CreateHotelInvoiceNumberSequenceRunCommandHandler implements ICommandHandler<CreateHotelInvoiceNumberSequenceRunCommand> {

    private final IHotelInvoiceNumberSequenceService service;
    private final IManageHotelService hotelService;
    private final IManageTradingCompaniesService tradingCompaniesService;

    public CreateHotelInvoiceNumberSequenceRunCommandHandler(IHotelInvoiceNumberSequenceService service,
                                                          IManageHotelService hotelService,
                                                          IManageTradingCompaniesService tradingCompaniesService) {
        this.service = service;
        this.hotelService = hotelService;
        this.tradingCompaniesService = tradingCompaniesService;
    }

    @Override
    public void handle(CreateHotelInvoiceNumberSequenceRunCommand command) {

        List<ManageTradingCompaniesDto> listTradingCompaniesDtos = this.tradingCompaniesService.findAll();
        this.createTradings(listTradingCompaniesDtos);
        List<ManageHotelDto> listManageHotelDtos = this.hotelService.findAll();
        this.createHotels(listManageHotelDtos);        
    }

    private void createTradings(List<ManageTradingCompaniesDto> listTradingCompaniesDtos) {
        for (EInvoiceType value : EInvoiceType.values()) {
            if (!value.name().equals(EInvoiceType.OLD_CREDIT.name())) {
                for (ManageTradingCompaniesDto dto : listTradingCompaniesDtos) {
                    this.create(new HotelInvoiceNumberSequenceDto(
                            UUID.randomUUID(), 
                            0L, 
                            null, 
                            dto, 
                            value
                    ));
                }
            }
        }
    }

    private void createHotels(List<ManageHotelDto> listManageHotelDtos) {
        for (EInvoiceType value : EInvoiceType.values()) {
            if (!value.name().equals(EInvoiceType.OLD_CREDIT.name())) {
                for (ManageHotelDto dto : listManageHotelDtos) {
                    this.create(new HotelInvoiceNumberSequenceDto(
                            UUID.randomUUID(), 
                            0L, 
                            dto, 
                            null, 
                            value
                    ));
                }
            }
        }
    }

    private void create(HotelInvoiceNumberSequenceDto sequenceDto) {
        service.create(sequenceDto);
    }
}
