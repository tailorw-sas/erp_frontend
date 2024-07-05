package com.kynsoft.finamer.creditcard.application.query.manageMerchantHotelEnrolle.findHotelsByMerchant;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FindHotelsByMerchantQuery implements IQuery {
    private final UUID manageMerchant;
}
