package com.kynsoft.notification.application.query.templateEntity.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindTemplateEntityByIdQuery implements IQuery {

    private final UUID id;

    public FindTemplateEntityByIdQuery(UUID id) {
        this.id = id;
    }

}
