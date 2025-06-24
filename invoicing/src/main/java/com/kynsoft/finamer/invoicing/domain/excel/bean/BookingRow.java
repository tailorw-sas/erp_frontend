package com.kynsoft.finamer.invoicing.domain.excel.bean;

import com.kynsof.share.core.application.excel.CustomCellType;
import com.kynsof.share.core.application.excel.annotation.Cell;
import com.kynsof.share.utils.BankerRounding;
import com.kynsof.share.utils.ScaleAmount;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
public class BookingRow {

    private String importProcessId;
    @Cell(position = -1, headerName = "")
    private int rowNumber;
    @Cell(position = 0, cellType = CustomCellType.DATAFORMAT, headerName = "Transaction Date")
    private String transactionDate;
    @Cell(position = 1, cellType = CustomCellType.DATAFORMAT, headerName = "Hotel")
    private String manageHotelCode;
    @Cell(position = 2, cellType = CustomCellType.DATAFORMAT, headerName = "Agency")
    private String manageAgencyCode;
    @Cell(position = 3, cellType = CustomCellType.DATAFORMAT, headerName = "First Name")
    private String firstName;
    @Cell(position = 4, cellType = CustomCellType.DATAFORMAT, headerName = "Last Name")
    private String lastName;
    @Cell(position = 5, cellType = CustomCellType.DATAFORMAT, headerName = "Check In")
    private String checkIn;
    @Cell(position = 6, cellType = CustomCellType.DATAFORMAT, headerName = "Check Out")
    private String checkOut;
    @Cell(position = 7, cellType = CustomCellType.NUMERIC, headerName = "Nights")
    private Double nights;
    @Cell(position = 8, cellType = CustomCellType.NUMERIC, headerName = "Adults")
    private Double adults;
    @Cell(position = 9, cellType = CustomCellType.NUMERIC, headerName = "Children")
    private Double children;
    @Cell(position = 10, cellType = CustomCellType.NUMERIC, headerName = "Invoice Amount")
    private Double invoiceAmount;
    @Cell(position = 11, cellType = CustomCellType.DATAFORMAT, headerName = "Coupon")
    private String coupon;
    @Cell(position = 12, cellType = CustomCellType.DATAFORMAT, headerName = "Hotel Booking No")
    private String hotelBookingNumber;
    @Cell(position = 13, cellType = CustomCellType.DATAFORMAT, headerName = "Room Type")
    private String roomType;
    @Cell(position = 14, cellType = CustomCellType.DATAFORMAT, headerName = "Rate Plan")
    private String ratePlan;
    @Cell(position = 15, cellType = CustomCellType.DATAFORMAT, headerName = "Hotel Invoice Number")
    private String hotelInvoiceNumber;
    @Cell(position = 16, cellType = CustomCellType.DATAFORMAT, headerName = "Remark")
    private String remarks;
    @Cell(position = 17, cellType = CustomCellType.FORMULA, headerName = "CANTIDAD DE PAX")
    private Double amountPAX;
    @Cell(position = 18, cellType = CustomCellType.DATAFORMAT, headerName = "Room Number")
    private String roomNumber;
    @Cell(position = 19, cellType = CustomCellType.NUMERIC, headerName = "Hotel Invoice Amount")
    private Double hotelInvoiceAmount;
    @Cell(position = 20, cellType = CustomCellType.DATAFORMAT, headerName = "Booking Date")
    private String bookingDate;
    @Cell(position = 21, cellType = CustomCellType.DATAFORMAT, headerName = "IDENTIFICADOR EN HOTELES DOBLE O TRIPLE")
    private String hotelType;
    @Cell(position = 22, cellType = CustomCellType.DATAFORMAT, headerName = "Night Type")
    private String nightType;

    private String trendingCompany;
    private String insistImportProcessId;
    private String insistImportProcessBookingId;
    
    List<UUID> agencies = new ArrayList<>();
    List<UUID> hotels = new ArrayList<>();

    public BookingRow() {
        this.firstName = "";
        this.lastName = "";
    }

    public ManageBookingDto toAggregate() {
        ManageBookingDto manageBookingDto = new ManageBookingDto();
        manageBookingDto.setAdults(Objects.nonNull(this.adults) ? this.adults.intValue() : 0);
        manageBookingDto.setChildren(Objects.nonNull(this.children) ? this.children.intValue() : 0);
        manageBookingDto.setCheckIn(DateUtil.parseDateToDateTime(this.checkIn));
        manageBookingDto.setCheckOut(DateUtil.parseDateToDateTime(this.checkOut));
        manageBookingDto.setCouponNumber(Objects.nonNull(this.coupon) ? this.coupon : "");
        manageBookingDto.setHotelAmount(Objects.nonNull(this.hotelInvoiceAmount) ? BankerRounding.round(this.hotelInvoiceAmount) : 0.0);
        manageBookingDto.setFirstName(Objects.nonNull(this.firstName) ? this.firstName : "");
        manageBookingDto.setLastName(Objects.nonNull(this.lastName) ? this.lastName : "");
        manageBookingDto.setFullName(buildFullName());
        manageBookingDto.setHotelBookingNumber(Objects.nonNull(this.hotelBookingNumber) ? removeBlankSpaces(this.hotelBookingNumber) : "");
        manageBookingDto.setHotelInvoiceNumber(Objects.nonNull(this.hotelInvoiceNumber) ? this.hotelInvoiceNumber : "");
        manageBookingDto.setDescription(Objects.nonNull(this.remarks) ? this.remarks.trim() : "");
        manageBookingDto.setRoomNumber(this.roomNumber);
        manageBookingDto.setInvoiceAmount(BankerRounding.round(this.invoiceAmount));
        manageBookingDto.setDueAmount(BankerRounding.round(this.invoiceAmount));
        manageBookingDto.setNights(this.nights.longValue());
        // manageBookingDto.setAmountPax();
        manageBookingDto.setBookingDate(this.bookingDate != null ? DateUtil.parseDateToDateTime(this.bookingDate) : LocalDateTime.of(1999, 1, 1, 0, 0));
        return manageBookingDto;
    }

    private String removeBlankSpaces(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

    private String buildFullName() {
        if (Objects.nonNull(this.firstName) && (Objects.nonNull(this.lastName))) {
            return firstName + " " + lastName;
        }
        if (Objects.nonNull(firstName)) {
            return firstName;
        }
        if (Objects.nonNull(lastName)) {
            return lastName;
        }
        return "";
    }

}
