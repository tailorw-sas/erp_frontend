package com.kynsoft.finamer.creditcard.application.query.resourceType.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindResourceTypeByIdQuery implements IQuery {

    private final UUID id;

    public FindResourceTypeByIdQuery(UUID id) {
        this.id = id;
    }

}
