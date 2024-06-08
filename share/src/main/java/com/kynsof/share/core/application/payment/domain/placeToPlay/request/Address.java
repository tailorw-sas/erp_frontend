package com.kynsof.share.core.application.payment.domain.placeToPlay.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Address {
    private String Street;
    private String City;
    private String Estate;
    private String PostalCode;
    private String Country;
    private String Phone;
}
