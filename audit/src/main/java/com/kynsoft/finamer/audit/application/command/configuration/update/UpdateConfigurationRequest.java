package com.kynsoft.finamer.audit.application.command.configuration.update;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Data;
import lombok.Getter;

@Data
public class UpdateConfigurationRequest {

    private boolean auditCreate;
    private boolean auditDelete;
    private  boolean auditUpdate;
}
