package com.kynsof.share.core.domain.kafka.entity.importInnsist;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
public class ImportInnsistBookingKafka implements Serializable {
    private UUID id;
    //private Long reservationNumber; //Auto incremental
    private LocalDateTime hotelCreationDate;//
    private LocalDateTime bookingDate;//
    private LocalDateTime checkIn;//
    private LocalDateTime checkOut;//

    private String hotelBookingNumber;//
    private String firstName;//
    private String lastName;//

    private String roomNumber;//
    private Integer adults;//
    private Integer children;//
    private Integer nights;//
    private Double rateAdult;//Se calcula
    private Double rateChild;//Se calcula
    private Long hotelInvoiceNumber;//
    private String folioNumber;
    private Double hotelAmount;//
    private String description;//

    private String ratePlanCode;//
    private String nightTypeCode;//
    private String roomTypeCode;//
    private String roomCategoryCode;//Evaluar si se puede enviar el codigo de la categoria

    //Los booking deben de tener estos tres campos, para poder garantizar agrupacion y definir en una sola factura. 
    private String manageHotelCode;
    private String manageAgencyCode;
    private String couponNumber;

    private List<ImportInnsistRoomRateKafka> roomRates;

    public ImportInnsistBookingKafka(UUID id, LocalDateTime hotelCreationDate, LocalDateTime bookingDate, LocalDateTime checkIn, LocalDateTime checkOut, String hotelBookingNumber, String firstName, String lastName, String roomNumber, Integer adults, Integer children, Integer nights, Double rateAdult, Double rateChild, Long hotelInvoiceNumber, String folioNumber, Double hotelAmount, String description, String ratePlanCode, String nightTypeCode, String roomTypeCode, String roomCategoryCode, String manageHotelCode, String manageAgencyCode, String couponNumber, List<ImportInnsistRoomRateKafka> roomRates) {
        this.id = id;
        this.hotelCreationDate = hotelCreationDate;
        this.bookingDate = bookingDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.hotelBookingNumber = hotelBookingNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roomNumber = roomNumber;
        this.adults = adults;
        this.children = children;
        this.nights = nights;
        this.rateAdult = rateAdult;
        this.rateChild = rateChild;
        this.hotelInvoiceNumber = hotelInvoiceNumber;
        this.folioNumber = folioNumber;
        this.hotelAmount = hotelAmount;
        this.description = description;
        this.ratePlanCode = ratePlanCode;
        this.nightTypeCode = nightTypeCode;
        this.roomTypeCode = roomTypeCode;
        this.roomCategoryCode = roomCategoryCode;
        this.manageHotelCode = manageHotelCode;
        this.manageAgencyCode = manageAgencyCode;
        this.couponNumber = couponNumber;
        this.roomRates = roomRates;
    }

    private String generationType;
    private boolean virtualHotel;
}
