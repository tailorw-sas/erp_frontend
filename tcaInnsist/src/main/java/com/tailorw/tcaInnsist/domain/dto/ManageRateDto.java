package com.tailorw.tcaInnsist.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
public class ManageRateDto {

    public String id;
    public LocalDate invoiceDate;
    public String hotel;
    public String reservationNumber;
    public String couponNumber;
    public String renewalNumber;
    public String hash;

    public ManageRateDto(LocalDate invoiceDate, String hotel, String reservationNumber, String couponNumber, String renewalNumber, String hash){
        this.id = String.join("|", hotel, reservationNumber, couponNumber, renewalNumber);
        this.invoiceDate = invoiceDate;
        this.hotel = hotel;
        this.reservationNumber = reservationNumber;
        this.couponNumber = couponNumber;
        this.renewalNumber = renewalNumber;
        this.hash = hash;
    }
}
