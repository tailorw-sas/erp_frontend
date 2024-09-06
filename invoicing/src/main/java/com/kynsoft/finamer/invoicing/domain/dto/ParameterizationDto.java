package com.kynsoft.finamer.invoicing.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParameterizationDto {

    private UUID id;
    private Boolean isActive;
    private String sent;
    private String reconciled;
    private String processed;
    private String canceled;
    private String pending;
}
