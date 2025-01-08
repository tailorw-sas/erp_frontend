package com.kynsoft.finamer.invoicing.application.query.manageRoomType.getByCodeAndHotelCode;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindManageRoomTypeByCodeAndHotelCodeQuery implements IQuery {

    private final String code;
    private final String hotelCode;
}
