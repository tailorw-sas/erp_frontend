package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EImportType;
import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingRequest;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;

public interface IBookingImportHelperService {

    void groupAndCachingImportBooking(BookingRow bookingRow, EImportType importType);

    void createInvoiceFromGroupedBooking(ImportBookingRequest request);

    void removeAllImportCache(String importProcessId);

    void saveCachingImportBooking(BookingRow bookingRow);

    void createInvoiceGroupingByCoupon(String importProcessId, String employee, boolean insisit);

    void createInvoiceGroupingByBooking(String importProcessId, String employee, boolean insisit);
}
