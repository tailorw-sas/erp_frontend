package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EImportType;
import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingRequest;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;

public interface IBookingImportHelperService {


    void groupAndCachingImportBooking(BookingRow bookingRow, EImportType importType);

    void createInvoiceFromGroupedBooking(ImportBookingRequest request);

    void removeAllImportCache(String importProcessId);

    boolean canImportRow(BookingRow bookingRow,EImportType importType);

    BookingImportCache saveCachingImportBooking(BookingRow bookingRow, ManageAgencyDto agencyDto);

    void createInvoiceGroupingByCoupon(String importProcessId, String employee, boolean insisit);

    void createInvoiceGroupingByBooking(String importProcessId, String employee, boolean insisit);
}
