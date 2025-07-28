package com.kynsoft.finamer.invoicing.domain.dto.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * DTO para representar combinación Hotel+BookingNumber para búsqueda de duplicados
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelBookingCombinationDto {
    private String hotelCode;
    private String bookingNumber;

    /**
     * Genera clave única para usar en cache/map
     */
    public String getKey() {
        return hotelCode + "|" + bookingNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelBookingCombinationDto that = (HotelBookingCombinationDto) o;
        return Objects.equals(hotelCode, that.hotelCode) &&
                Objects.equals(bookingNumber, that.bookingNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelCode, bookingNumber);
    }
}
