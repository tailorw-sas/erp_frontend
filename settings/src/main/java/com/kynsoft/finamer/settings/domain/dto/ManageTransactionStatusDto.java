package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageTransactionStatusDto {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private List<ManageTransactionStatusDto> navigate;
    private Boolean enablePayment;
    private Boolean visible;
    private Status status;

}