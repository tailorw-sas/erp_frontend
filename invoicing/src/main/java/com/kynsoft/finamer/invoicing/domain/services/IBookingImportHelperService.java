package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EImportType;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;

public interface IBookingImportHelperService {


    void groupAndCachingImportBooking(BookingRow bookingRow, EImportType importType);

    void createInvoiceFromGroupedBooking(String importProcessId);

    void removeAllImportCache(String importProcessId);

    boolean canImportRow(BookingRow bookingRow,EImportType importType);

}
