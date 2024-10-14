package com.kynsoft.finamer.creditcard.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardnetJobDto {

    private UUID id;
    private UUID transactionId;
    private String session;
    private String sessionKey;
    private Boolean isProcessed;

}
