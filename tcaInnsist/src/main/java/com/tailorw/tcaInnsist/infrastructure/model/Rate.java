package com.tailorw.tcaInnsist.infrastructure.model;

import com.tailorw.tcaInnsist.domain.dto.RateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.kynsof.share.core.infrastructure.util.DateUtil.parseDateToLocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Rate {
    private String agencyCode;
    private String checkInDate;
    private String checkOutDate;
    private int stayDays;
    private String reservationCode;
    private String guestName;
    private Double amount;
    private String roomType;
    private String couponNumber;
    private int totalNumberOfGuest;
    private int adults;
    private int childrens;
    private String ratePlan;
    private String invoicingDate;
    private String hotelCreationDate;
    private Double originalAmount;
    private Double amountPaymentApplied;
    private Double rateByAdult;
    private Double rateByChild;
    private String remarks;
    private String roomNumber;
    private Double hotelInvoiceAmount;
    private String hotelInvoiceNumber;
    private String invoiceFolioNumber;
    private Double quote;
    private String renewalNumber;
    private String roomCategory;
    private String hash;

    public RateDto toAggregate(){
        return new RateDto(
                this.reservationCode,
                this.couponNumber,
                LocalDate.parse(this.checkInDate, DateTimeFormatter.ofPattern("yyyyMMdd")),
                LocalDate.parse(this.checkOutDate, DateTimeFormatter.ofPattern("yyyyMMdd")),
                this.stayDays,
                "",
                this.agencyCode,
                this.guestName,
                (this.guestName == null
                        || !this.guestName.contains(",")
                        || this.guestName.split(",").length < 2
                        || this.guestName.split(",")[1].isEmpty())
                        ? ""
                        : this.guestName.split(",")[1].trim(),
                (this.guestName == null || this.guestName.split(",")[0].isEmpty())
                        ? ""
                        : this.guestName.split(",")[0].trim(),
                this.totalNumberOfGuest,
                this.adults,
                this.childrens,
                this.amount,
                this.roomType,
                this.ratePlan,
                LocalDate.parse(this.invoicingDate, DateTimeFormatter.ofPattern("yyyyMMdd")),
                LocalDate.parse(this.hotelCreationDate,  DateTimeFormatter.ofPattern("yyyyMMdd")),
                this.originalAmount,
                this.amountPaymentApplied,
                this.rateByAdult,
                this.rateByChild,
                this.remarks,
                this.roomNumber,
                this.hotelInvoiceAmount,
                this.hotelInvoiceNumber,
                this.invoiceFolioNumber,
                this.quote,
                this.renewalNumber,
                this.roomCategory,
                this.hash
        );
    }
}