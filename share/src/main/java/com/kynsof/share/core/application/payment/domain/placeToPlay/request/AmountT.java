package com.kynsof.share.core.application.payment.domain.placeToPlay.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmountT {
    private List<TaxT> Taxes;
    private List<?> Details;
    private String Currency;
    private Double Total;

}
