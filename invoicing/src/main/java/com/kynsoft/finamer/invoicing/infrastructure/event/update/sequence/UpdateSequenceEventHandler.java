package com.kynsoft.finamer.invoicing.infrastructure.event.update.sequence;

import com.kynsoft.finamer.invoicing.domain.dto.HotelInvoiceNumberSequenceDto;
import com.kynsoft.finamer.invoicing.domain.services.IHotelInvoiceNumberSequenceService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UpdateSequenceEventHandler implements ApplicationListener<UpdateSequenceEvent> {

    private final IHotelInvoiceNumberSequenceService service;

    public UpdateSequenceEventHandler(IHotelInvoiceNumberSequenceService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(UpdateSequenceEvent event) {
        HotelInvoiceNumberSequenceDto sequenceDto = event.getSequenceDto();
        sequenceDto.setInvoiceNo(sequenceDto.getInvoiceNo() + 1);
        this.service.update(sequenceDto);
    }
}
