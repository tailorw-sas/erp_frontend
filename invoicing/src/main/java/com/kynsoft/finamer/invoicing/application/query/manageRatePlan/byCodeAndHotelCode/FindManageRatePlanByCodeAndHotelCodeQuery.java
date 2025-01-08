package com.kynsoft.finamer.invoicing.application.query.manageRatePlan.byCodeAndHotelCode;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindManageRatePlanByCodeAndHotelCodeQuery implements IQuery {

    private final String code;
    private final String hotelCode;
}
