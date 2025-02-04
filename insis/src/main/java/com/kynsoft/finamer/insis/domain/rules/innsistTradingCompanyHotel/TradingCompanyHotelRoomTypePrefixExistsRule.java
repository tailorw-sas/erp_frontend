package com.kynsoft.finamer.insis.domain.rules.innsistTradingCompanyHotel;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.insis.domain.services.IInnsistHotelRoomTypeService;

import java.util.UUID;

public class TradingCompanyHotelRoomTypePrefixExistsRule extends BusinessRule {

    private final String roomTypePrefix;
    private final UUID tradingCompanyId;
    private final IInnsistHotelRoomTypeService service;

    public TradingCompanyHotelRoomTypePrefixExistsRule(String roomTypePrefix,
                                                       UUID tradingCompanyId,
                                                       IInnsistHotelRoomTypeService service){
        super(
                DomainErrorMessage.INNSIST_TRADING_COMPANY_HOTEL_ROOM_TYPE_PREFIX_EXISTS,
                new ErrorField("hostname", "The size of room type must be 1 character.")
        );
        this.roomTypePrefix = roomTypePrefix;
        this.tradingCompanyId = tradingCompanyId;
        this.service = service;
    }

    @Override
    public boolean isBroken() {
        return service.countByRoomTypePrefixAndTradingCompanyId(tradingCompanyId, roomTypePrefix) > 0;
    }
}
