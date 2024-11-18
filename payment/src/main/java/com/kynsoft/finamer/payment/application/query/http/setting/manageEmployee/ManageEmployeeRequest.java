package com.kynsoft.finamer.payment.application.query.http.setting.manageEmployee;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ManageEmployeeRequest  implements IQuery {

    private final UUID id;

    public ManageEmployeeRequest(UUID id) {
        this.id = id;
    }

}
