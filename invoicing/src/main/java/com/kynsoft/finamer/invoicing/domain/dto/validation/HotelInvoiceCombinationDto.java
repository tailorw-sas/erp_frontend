package com.kynsoft.finamer.invoicing.domain.dto.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
/**
 * DTO para representar combinación Hotel+InvoiceNumber para búsqueda de duplicados
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelInvoiceCombinationDto {
    private String hotelCode;
    private String invoiceNumber;

    /**
     * Genera clave única para usar en cache/map
     */
    public String getKey() {
        return hotelCode + "|" + invoiceNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelInvoiceCombinationDto that = (HotelInvoiceCombinationDto) o;
        return Objects.equals(hotelCode, that.hotelCode) &&
                Objects.equals(invoiceNumber, that.invoiceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelCode, invoiceNumber);
    }
}
