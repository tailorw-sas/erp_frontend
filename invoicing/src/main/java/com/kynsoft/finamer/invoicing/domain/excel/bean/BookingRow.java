package com.kynsoft.finamer.invoicing.domain.excel.bean;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class BookingRow {
    private String importProcessId;

    @Cell(position = -1)
    private int rowNumber;
    @Cell(position = 0)
    private String transactionDate;
    @Cell(position = 1)
    private String manageHotelCode;
    @Cell(position = 2)
    private String manageAgencyCode;
    @Cell(position = 3)
    private String firstName;
    @Cell(position = 4)
    private String lastName;
    @Cell(position = 5)
    private String checkIn;
    @Cell(position = 6)
    private String checkOut;
    @Cell(position = 7,cellType = CustomCellType.NUMERIC)
    private double nights;
    @Cell(position = 8,cellType = CustomCellType.NUMERIC)
    private double adults;
    @Cell(position = 9,cellType = CustomCellType.NUMERIC)
    private double children;
    @Cell(position = 10,cellType = CustomCellType.NUMERIC)
    private double invoiceAmount;
    @Cell(position = 11)
    private String coupon;
    @Cell(position = 12)
    private String hotelBookingNumber;
    @Cell(position = 13)
    private String roomType;
    @Cell(position = 14)
    private String ratePlan;
    @Cell(position = 15)
    private String hotelInvoiceNumber;
    @Cell(position = 16)
    private String remarks;
    @Cell(position = 17,cellType = CustomCellType.FORMULA)
    private double amountPAX;
    @Cell(position = 18,cellType = CustomCellType.NUMERIC)
    private double roomNumber;
    @Cell(position = 19,cellType = CustomCellType.NUMERIC)
    private double hotelInvoiceAmount;
    @Cell(position = 20)
    private String bookingDate;
    @Cell(position =21)
    private String hotelType;

    public ManageBookingDto toAgregate() {
        ManageBookingDto manageBookingDto = new ManageBookingDto();
        manageBookingDto.setAdults((int) this.adults);
        manageBookingDto.setChildren((int) this.children);
        LocalDate checkInDate= LocalDate.parse(this.checkIn,DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate checkOutDate=LocalDate.parse(this.checkOut,DateTimeFormatter.ofPattern("yyyyMMdd"));
        manageBookingDto.setCheckIn(checkInDate.atTime(0,0));
        manageBookingDto.setCheckOut(checkOutDate.atTime(0,0));
        manageBookingDto.setCouponNumber(this.coupon);
        manageBookingDto.setHotelAmount(this.hotelInvoiceAmount);
        manageBookingDto.setFirstName(this.firstName);
        manageBookingDto.setLastName(this.lastName);
        manageBookingDto.setHotelBookingNumber(this.hotelBookingNumber);
        manageBookingDto.setHotelInvoiceNumber(this.hotelInvoiceNumber);
        manageBookingDto.setDescription(this.remarks);
        manageBookingDto.setRoomNumber(String.valueOf(this.roomNumber));
        // manageBookingDto.setAmountPax();
        LocalDate bookingDate=LocalDate.parse(this.bookingDate,DateTimeFormatter.ofPattern("yyyyMMdd"));
        manageBookingDto.setBookingDate(bookingDate.atTime(0,0));
        return manageBookingDto;
    }

}
