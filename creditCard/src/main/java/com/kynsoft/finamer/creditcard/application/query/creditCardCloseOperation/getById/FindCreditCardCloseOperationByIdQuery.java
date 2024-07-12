package com.kynsoft.finamer.creditcard.application.query.creditCardCloseOperation.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindCreditCardCloseOperationByIdQuery  implements IQuery {

    private final UUID id;

    public FindCreditCardCloseOperationByIdQuery(UUID id) {
        this.id = id;
    }

}
