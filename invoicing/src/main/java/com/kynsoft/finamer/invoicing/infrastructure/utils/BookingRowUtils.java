package com.kynsoft.finamer.invoicing.infrastructure.utils;

import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;

public class BookingRowUtils {

    public static void removeBlankSpacesInBookingRow(BookingRow bookingRow) {
        bookingRow.setManageHotelCode(bookingRow.getManageHotelCode() != null ? bookingRow.getManageHotelCode().trim() : null);
        bookingRow.setManageAgencyCode(bookingRow.getManageAgencyCode() != null ? bookingRow.getManageAgencyCode().trim() : null);
        bookingRow.setHotelBookingNumber(bookingRow.getHotelBookingNumber() != null ? bookingRow.getHotelBookingNumber().trim() : null);
        bookingRow.setHotelInvoiceNumber(bookingRow.getHotelInvoiceNumber() != null ? bookingRow.getHotelInvoiceNumber().trim() : null);
        bookingRow.setRoomType(bookingRow.getRoomType() != null ? bookingRow.getRoomType().trim() : null);
        bookingRow.setRatePlan(bookingRow.getRatePlan() != null ? bookingRow.getRatePlan().trim() : null);
        bookingRow.setNightType(bookingRow.getNightType() != null ? bookingRow.getNightType().trim() : null);
        bookingRow.setBookingDate(bookingRow.getBookingDate() != null ? bookingRow.getBookingDate().trim() : null);
        bookingRow.setCheckIn(bookingRow.getCheckIn() != null ? bookingRow.getCheckIn().trim() : null);
        bookingRow.setCheckOut(bookingRow.getCheckOut() != null ? bookingRow.getCheckOut().trim() : null);
        bookingRow.setFirstName(bookingRow.getFirstName() != null ? bookingRow.getFirstName().trim() : null);
        bookingRow.setLastName(bookingRow.getLastName() != null ? bookingRow.getLastName().trim() : null);
        bookingRow.setRemarks(bookingRow.getRemarks() != null ? bookingRow.getRemarks().trim() : null);
        bookingRow.setRoomNumber(bookingRow.getRoomNumber() != null ? bookingRow.getRoomNumber().trim() : null);
        bookingRow.setCoupon(bookingRow.getCoupon() != null ? bookingRow.getCoupon().trim() : null);
        bookingRow.setTransactionDate(bookingRow.getTransactionDate() != null ? bookingRow.getTransactionDate().trim() : null);
    }
}
