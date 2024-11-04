package com.kynsoft.finamer.creditcard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardNetStatusResponseDto {
    private String code;
    private String status;
    private String description;
}
