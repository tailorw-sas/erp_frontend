package com.kynsoft.finamer.invoicing.application.command.hotelInvoiceNumberSequence.updateByCode;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.HotelInvoiceNumberSequenceDto;
import com.kynsoft.finamer.invoicing.domain.services.IHotelInvoiceNumberSequenceService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UpdateCodeAndValueCommandHandler implements ICommandHandler<UpdateCodeAndValueCommand> {

    private final IHotelInvoiceNumberSequenceService service;
    public UpdateCodeAndValueCommandHandler(IHotelInvoiceNumberSequenceService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateCodeAndValueCommand command) {
        this.createHotels(command.getHotels());
        this.createTradings(command.getTradings());
    }

    private void createTradings(List<UpdateCodeAndValue> listTradingCompaniesDtos) {
        for (UpdateCodeAndValue dto : listTradingCompaniesDtos) {
            HotelInvoiceNumberSequenceDto sequenceDto = this.service.getByTradingCompanyCodeAndInvoiceType(dto.getCode(), dto.getInvoiceType());
            sequenceDto.setInvoiceNo(dto.getValue());
            this.create(sequenceDto);
        }
    }

    private void createHotels(List<UpdateCodeAndValue> listManageHotelDtos) {
        for (UpdateCodeAndValue dto : listManageHotelDtos) {
            HotelInvoiceNumberSequenceDto sequenceDto = this.service.getByHotelCodeAndInvoiceType(dto.getCode(), dto.getInvoiceType());
            sequenceDto.setInvoiceNo(dto.getValue());
            this.create(sequenceDto);
        }
    }

    private void create(HotelInvoiceNumberSequenceDto sequenceDto) {
        service.create(sequenceDto);
    }
}
