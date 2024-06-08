package com.kynsof.share.core.application.payment.domain.placeToPlay.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class Payment {
    private String ServiceType;
    private String Description;
    private Amount Amount;
    private boolean AllowPartial;
    private Shipping Shipping;
    private String Items;
    private List<?> ProcessorFields;
    private boolean Subscribe;
}
