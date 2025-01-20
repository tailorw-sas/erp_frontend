package com.kynsoft.finamer.invoicing.application.query.manageHotel.getByCode;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class FindManageHotelByCodeQuery  implements IQuery {

    private final String code;

    public FindManageHotelByCodeQuery(String code) {
        this.code = code;
    }

}
