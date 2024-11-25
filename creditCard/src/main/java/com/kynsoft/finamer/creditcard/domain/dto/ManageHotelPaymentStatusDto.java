package com.kynsoft.finamer.creditcard.domain.dto;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManageHotelPaymentStatusDto {
    private UUID id;
    private String code;
    private String name;
    private Status status;
    private String description;

    private boolean inProgress;
    private boolean completed;
    private boolean cancelled;
    private boolean applied;
}
