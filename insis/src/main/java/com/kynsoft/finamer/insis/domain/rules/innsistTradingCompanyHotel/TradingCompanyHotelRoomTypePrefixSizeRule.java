package com.kynsoft.finamer.insis.domain.rules.innsistTradingCompanyHotel;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;

public class TradingCompanyHotelRoomTypePrefixSizeRule extends BusinessRule {

    private final String roomTypePrefix;

    public TradingCompanyHotelRoomTypePrefixSizeRule(String roomTypePrefix){
        super(
                DomainErrorMessage.INNSIST_TRADING_COMPANY_HOTEL_ROOM_TYPE_PREFIX_CHECK_SIZE,
                new ErrorField("hostname", "The size of room type must be 1 character.")
        );
        this.roomTypePrefix = roomTypePrefix;
    }

    @Override
    public boolean isBroken() {
        return roomTypePrefix.length() != 1 ? true:false;
    }
}
