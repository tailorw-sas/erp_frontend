package com.kynsoft.finamer.invoicing.infrastructure.utils;

import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class InvoiceUtils {

    public static double calculateInvoiceAmount(ManageInvoiceDto manageInvoice) {
        return manageInvoice.getBookings().stream().filter(manageBookingDto -> Objects.nonNull(manageBookingDto.getInvoiceAmount()))
                .mapToDouble(ManageBookingDto::getInvoiceAmount).sum();
    }

    public static String getInvoiceNumberPrefix(String invoiceNumber) {
        if (invoiceNumber != null) {
            int firstIndex = invoiceNumber.indexOf("-");
            int lastIndex = invoiceNumber.lastIndexOf("-");

            if (firstIndex != -1 && lastIndex != -1 && firstIndex != lastIndex) {
                String beginInvoiceNumber = invoiceNumber.substring(0, firstIndex);
                String lastInvoiceNumber = invoiceNumber.substring(lastIndex + 1);

                return beginInvoiceNumber + "-" + lastInvoiceNumber;

            } else {
                return invoiceNumber;
            }

        }
        return "";
    }

    public static String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }


    public static ManageInvoiceDto calculateInvoiceAging(ManageInvoiceDto manageInvoiceDto) {
        LocalDate dueDate = manageInvoiceDto.getDueDate();
        LocalDate serverDate = LocalDate.now();
        if (Objects.isNull(dueDate) || serverDate.isEqual(dueDate) || dueDate.isAfter(serverDate)) {
            manageInvoiceDto.setAging(0);
        } else {
            long dayBetween = ChronoUnit.DAYS.between(dueDate, serverDate);

            if (dayBetween <= 30) {
                manageInvoiceDto.setAging(30);
            } else if (dayBetween > 31 && dayBetween <= 60) {
                manageInvoiceDto.setAging(60);
            } else if (dayBetween > 61 && dayBetween <= 90) {
                manageInvoiceDto.setAging(90);
            } else {
                manageInvoiceDto.setAging(120);
            }
        }
        return manageInvoiceDto;
    }

    public static ManageInvoiceDto establishDueDate(ManageInvoiceDto manageInvoiceDto) {
        LocalDateTime transactionDate = manageInvoiceDto.getInvoiceDate();
        if (!manageInvoiceDto.getManageInvoiceStatus().isSentStatus()) {
            int creditDay = manageInvoiceDto.getAgency().getCreditDay();
            transactionDate = transactionDate.plusDays(creditDay);
            manageInvoiceDto.setDueDate(transactionDate.toLocalDate());
            return manageInvoiceDto;
        }
        return manageInvoiceDto;
    }
}
