package com.kynsoft.finamer.invoicing.domain.dto.roomrate;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EGenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Unified DTO that represents Room Rates from any source (Excel, External Systems, etc.)
 * Centralizes the data structure for uniform processing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedRoomRateDto {

    // === Source Metadata ===
    private String importProcessId;
    private String sourceType;        // "EXCEL", "INNSIST", "EXTERNAL_SYSTEM_B"
    private String sourceIdentifier;  // "Row 15", "Object ID: 12345", etc.
    private String rowReference;        // Para ordenamiento y referencia

    // === Room Rate Data ===
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

    // === Calculated/Derived Data ===
    private EGenerationType generationType;

    /**
     * Construct the full name of the guest
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
     * Calculate the grouping key for booking based on business rules
     * @return
     */
    public String calculateBookingGroupKey() {
        switch (generationType) {
            case ByHotelInvoiceNumber:
                return String.format("%s|%s|%s|%s",
                        Objects.toString(hotelCode, ""),
                        Objects.toString(agencyCode, ""),
                        normalizeDate(transactionDate),
                        Objects.toString(hotelInvoiceNumber, ""));
            case ByCheckIn:
                return String.format("%s|%s|%s|%s",
                        Objects.toString(hotelCode, ""),
                        Objects.toString(agencyCode, ""),
                        normalizeDate(transactionDate),
                        normalizeDate(checkIn));
            case ByCheckInCheckOut:
                return String.format("%s|%s|%s|%s|%s",
                        Objects.toString(hotelCode, ""),
                        Objects.toString(agencyCode, ""),
                        normalizeDate(transactionDate),
                        normalizeDate(checkIn),
                        normalizeDate(checkOut));
            case ByCoupon:
                return String.format("%s|%s|%s|%s",
                        Objects.toString(hotelCode, ""),
                        Objects.toString(agencyCode, ""),
                        normalizeDate(transactionDate),
                        Objects.toString(coupon, ""));
            case ByBooking:
            default:
                return String.format("%s|%s|%s|%s",
                        Objects.toString(hotelCode, ""),
                        Objects.toString(agencyCode, ""),
                        normalizeDate(transactionDate),
                        Objects.toString(hotelBookingNumber, ""));
        }
    }

    /**
     * Calculate the grouping key for booking based on business rules
     * @return
     */
    public String forceGroupByBooking() {
            return String.format("%s|%s|%s|%s",
                    Objects.toString(hotelCode, ""),
                    Objects.toString(agencyCode, ""),
                    normalizeDate(transactionDate),
                    Objects.toString(hotelBookingNumber, ""));
    }

    /**
     * Validates that the essential fields are present
     */
    public boolean hasEssentialFields() {
        return Objects.nonNull(hotelCode) &&
                Objects.nonNull(agencyCode) &&
                Objects.nonNull(transactionDate) &&
                Objects.nonNull(invoiceAmount);
    }

    /**
     * Returns a unique identifier for this room rate within the import process
     */
    public String getUniqueIdentifier() {
        return String.format("%s-%s-%s",
                importProcessId,
                sourceType,
                sourceIdentifier);
    }

    private static String normalizeDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return "";
        try {
            return java.time.LocalDate.parse(dateStr)
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (Exception e) {
            return dateStr; // fallback to original if parsing fails
        }
    }
}
