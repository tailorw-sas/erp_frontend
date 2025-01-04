package com.kynsoft.finamer.invoicing.infrastructure.excel.event;

import com.kynsoft.finamer.invoicing.domain.dto.BookingImportProcessDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ImportBookingProcessEvent extends ApplicationEvent {

    private final BookingImportProcessDto bookingImportProcessDto;
    public ImportBookingProcessEvent(Object source, BookingImportProcessDto bookingImportProcessDto) {
        super(source);
        this.bookingImportProcessDto = bookingImportProcessDto;
    }
}
