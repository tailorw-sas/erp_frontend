package com.kynsoft.finamer.invoicing.infrastructure.event.update.sequence;

import com.kynsoft.finamer.invoicing.domain.dto.HotelInvoiceNumberSequenceDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UpdateSequenceEvent extends ApplicationEvent {

    private HotelInvoiceNumberSequenceDto sequenceDto;

    public UpdateSequenceEvent(Object source, HotelInvoiceNumberSequenceDto sequenceDto) {
        super(source);
        this.sequenceDto = sequenceDto;
    }
}
