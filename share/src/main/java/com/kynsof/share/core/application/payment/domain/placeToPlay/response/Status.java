package com.kynsof.share.core.application.payment.domain.placeToPlay.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public  class Status {

    private String status;

    private String reason;

    private String message;

    private String date;
}
