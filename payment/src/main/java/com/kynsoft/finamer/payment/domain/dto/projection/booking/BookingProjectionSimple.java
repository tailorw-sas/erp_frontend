package com.kynsoft.finamer.payment.domain.dto.projection.booking;

import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingProjectionSimple implements Serializable {
    /**
     * Este Objeto sera cacheable, solo durante el proceso de validacion del expense to booking.
     * Con los elementos que se necesitan en dicho proceso.
     */
    private UUID bookingId;
    private Long bookingGenId;
    private double bookingAmountBalance;
    //Datos de la invoice para ese booking.
    private EInvoiceType invoiceType;
    private UUID invoiceHotel;
    private String clientName;
    private UUID invoiceAgency;
}
