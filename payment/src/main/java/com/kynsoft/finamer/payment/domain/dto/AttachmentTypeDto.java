package com.kynsoft.finamer.payment.domain.dto;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AttachmentTypeDto {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Boolean defaults;
    private Status status;
    private boolean antiToIncomeImport;
}
