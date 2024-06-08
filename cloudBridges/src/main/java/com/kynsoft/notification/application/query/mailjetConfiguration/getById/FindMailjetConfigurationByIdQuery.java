package com.kynsoft.notification.application.query.mailjetConfiguration.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindMailjetConfigurationByIdQuery implements IQuery {

    private final UUID id;

    public FindMailjetConfigurationByIdQuery(UUID id) {
        this.id = id;
    }

}
