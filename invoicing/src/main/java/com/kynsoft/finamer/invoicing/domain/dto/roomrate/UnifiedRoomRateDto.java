package com.kynsoft.finamer.invoicing.domain.dto.roomrate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * DTO unificado que representa Room Rates de cualquier fuente (Excel, Sistemas Externos, etc.)
 * Centraliza la estructura de datos para procesamiento uniforme.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedRoomRateDto {

    // === Metadatos de Origen ===
    private String importProcessId;
    private String sourceType;        // "EXCEL", "EXTERNAL_SYSTEM_A", "EXTERNAL_SYSTEM_B"
    private String sourceIdentifier;  // "Row 15", "Object ID: 12345", etc.
    private String rowReference;        // Para ordenamiento y referencia

    // === Datos del Room Rate ===
    private String transactionDate;
    private String hotelCode;
    private String agencyCode;
    private String firstName;
    private String lastName;
    private String checkIn;
    private String checkOut;
    private Double nights;
    private Double adults;
    private Double children;
    private Double invoiceAmount;
    private String coupon;
    private String hotelBookingNumber;
    private String roomType;
    private String ratePlan;
    private String hotelInvoiceNumber;
    private String remarks;
    private Double amountPAX;
    private String roomNumber;
    private Double hotelInvoiceAmount;
    private String bookingDate;
    private String hotelType;
    private String nightType;
    private String folioNumber;

    // === Datos Calculados/Derivados ===
    private String bookingGroupKey;   // Se calcula según reglas de agrupación
    private String invoiceGroupKey;   // Se calcula según reglas de agrupación

    /**
     * Construye el nombre completo del huésped
     */
    public String getFullName() {
        if (Objects.nonNull(firstName) && Objects.nonNull(lastName)) {
            return firstName.trim() + " " + lastName.trim();
        }
        if (Objects.nonNull(firstName)) {
            return firstName.trim();
        }
        if (Objects.nonNull(lastName)) {
            return lastName.trim();
        }
        return "";
    }

    /**
     * Calcula la clave de agrupación para bookings según reglas de negocio
     */
    public String calculateBookingGroupKey() {
        // La lógica puede variar según el tipo de import y reglas de negocio
        return String.format("%s|%s|%s|%s",
                Objects.toString(hotelCode, ""),
                Objects.toString(agencyCode, ""),
                Objects.toString(hotelBookingNumber, ""),
                Objects.toString(transactionDate, ""));
    }

    /**
     * Calcula la clave de agrupación para invoices según reglas de negocio
     */
    public String calculateInvoiceGroupKey() {
        return String.format("%s|%s|%s",
                Objects.toString(hotelCode, ""),
                Objects.toString(agencyCode, ""),
                Objects.toString(transactionDate, ""));
    }

    /**
     * Valida que los campos esenciales estén presentes
     */
    public boolean hasEssentialFields() {
        return Objects.nonNull(hotelCode) &&
                Objects.nonNull(agencyCode) &&
                Objects.nonNull(transactionDate) &&
                Objects.nonNull(invoiceAmount);
    }

    /**
     * Retorna un identificador único para este room rate dentro del proceso de importación
     */
    public String getUniqueIdentifier() {
        return String.format("%s-%s-%s",
                importProcessId,
                sourceType,
                sourceIdentifier);
    }
}