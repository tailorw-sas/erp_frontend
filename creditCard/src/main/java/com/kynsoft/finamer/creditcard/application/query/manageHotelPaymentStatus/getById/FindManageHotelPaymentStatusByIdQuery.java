package com.kynsoft.finamer.creditcard.application.query.manageHotelPaymentStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindManageHotelPaymentStatusByIdQuery implements IQuery {
    private final UUID id;
}
