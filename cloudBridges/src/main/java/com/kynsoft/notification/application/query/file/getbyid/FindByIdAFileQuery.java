package com.kynsoft.notification.application.query.file.getbyid;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindByIdAFileQuery implements IQuery {

    private final UUID id;

    public FindByIdAFileQuery(UUID id) {
        this.id = id;
    }

}
