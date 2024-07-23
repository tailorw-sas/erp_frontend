package com.kynsoft.finamer.invoicing.infrastructure.utils;

import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageBooking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoice;

import java.util.Objects;
import java.util.stream.Collectors;

public class InvoiceUtils {

    public static double calculateInvoiceAmount(ManageInvoiceDto manageInvoice){
        return manageInvoice.getBookings().stream().filter(manageBookingDto -> Objects.nonNull(manageBookingDto.getInvoiceAmount()))
                .mapToDouble(ManageBookingDto::getInvoiceAmount).sum();
    }
}
