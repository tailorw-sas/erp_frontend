package com.kynsoft.finamer.audit.application.command.configuration.update;

import lombok.Data;

@Data
public class  UpdateConfigurationRequest {

    private boolean auditCreate;
    private boolean auditDelete;
    private boolean auditUpdate;
    private String serviceName;
    private String entityName;
}
