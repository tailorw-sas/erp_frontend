package com.kynsoft.finamer.invoicing.application.command.checkDatesInCloseOperation.checkDates;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CheckDatesInCloseOperationRequest {

    private UUID hotelId;
    private List<LocalDate> dates;
}
