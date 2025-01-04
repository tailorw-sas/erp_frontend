package com.kynsoft.finamer.creditcard.application.query.hotelPaymentStatusHistory.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FindHotelPaymentStatusHistoryByIdQuery implements IQuery {
    private final UUID id;
}
