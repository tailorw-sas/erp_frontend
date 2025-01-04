package com.kynsoft.finamer.creditcard.application.query.hotelPayment.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FindHotelPaymentByIdQuery implements IQuery {
    private final UUID id;
}
