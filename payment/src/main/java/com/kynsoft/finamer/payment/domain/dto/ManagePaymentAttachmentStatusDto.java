package com.kynsoft.finamer.payment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ManagePaymentAttachmentStatusDto {

    private UUID id;
    private String code;
    private String name;
    private String status;
    private Boolean defaults;

}
