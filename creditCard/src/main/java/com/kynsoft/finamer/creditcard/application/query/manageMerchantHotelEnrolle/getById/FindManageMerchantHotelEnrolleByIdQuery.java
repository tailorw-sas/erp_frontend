package com.kynsoft.finamer.creditcard.application.query.manageMerchantHotelEnrolle.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindManageMerchantHotelEnrolleByIdQuery implements IQuery {

    private final UUID id;
}
