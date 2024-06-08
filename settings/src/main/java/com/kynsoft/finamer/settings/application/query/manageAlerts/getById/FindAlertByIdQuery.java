package com.kynsoft.finamer.settings.application.query.manageAlerts.getById;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FindAlertByIdQuery implements IQuery {

    private final UUID id;
    public FindAlertByIdQuery(final UUID id) {this.id=id;}
}
