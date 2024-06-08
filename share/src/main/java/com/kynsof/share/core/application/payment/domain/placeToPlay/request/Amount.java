package com.kynsof.share.core.application.payment.domain.placeToPlay.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Amount {
    private List<Tax> Taxes;
    private List<?> Details;
    private String Currency;
    private Double Total;

}
