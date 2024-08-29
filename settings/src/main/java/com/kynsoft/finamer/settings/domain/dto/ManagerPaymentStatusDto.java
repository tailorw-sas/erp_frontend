package com.kynsoft.finamer.settings.domain.dto;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagerPaymentStatusDto {
    private UUID id;
    private String code;
    private String name;
    private Status status;
    private Boolean collected;
    private String description;
    private Boolean defaults;
    private Boolean applied;
}
