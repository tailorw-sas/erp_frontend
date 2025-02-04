package com.kynsoft.finamer.insis.application.query.manageHotel.getByCode;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetManageHotelByCodeQuery implements IQuery {
    private String code;
}
