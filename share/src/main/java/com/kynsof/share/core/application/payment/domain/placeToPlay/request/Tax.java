package com.kynsof.share.core.application.payment.domain.placeToPlay.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tax {
    private String Kind;
    private Double Amount;
    private Double Base;
}
