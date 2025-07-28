package com.kynsoft.finamer.invoicing.domain.dto.validation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Resultado de validación de duplicados con información detallada
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateValidationResult {
    private boolean hasDuplicates;
    private Set<String> existingCombinationKeys;
    private int totalChecked;
    private int duplicatesFound;
    private String importType;

    /**
     * Verifica si una combinación específica es duplicada
     */
    public boolean isDuplicate(String hotelCode, String bookingNumber) {
        String key = hotelCode + "|" + bookingNumber;
        return existingCombinationKeys.contains(key);
    }

    /**
     * Verifica si una combinación específica es duplicada (usando DTO)
     */
    public boolean isDuplicate(HotelBookingCombinationDto combination) {
        return existingCombinationKeys.contains(combination.getKey());
    }

    /**
     * Verifica si una combinación específica es duplicada (using invoice DTO)
     */
    public boolean isDuplicate(HotelInvoiceCombinationDto combination) {
        return existingCombinationKeys.contains(combination.getKey());
    }

    public static DuplicateValidationResult noDuplicates(int totalChecked, String importType) {
        return new DuplicateValidationResult(false, new HashSet<>(), totalChecked, 0, importType);
    }

    public static DuplicateValidationResult withDuplicates(Set<String> existingKeys, int totalChecked, String importType) {
        return new DuplicateValidationResult(true, existingKeys, totalChecked, existingKeys.size(), importType);
    }
}