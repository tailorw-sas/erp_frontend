package com.kynsoft.finamer.insis.application.services.helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDateRange {

    private LocalDate from;
    private LocalDate to;
    private String hotel;
}
