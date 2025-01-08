package com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity;

import java.util.UUID;

public interface ManageInvoiceStatusProjection {
    UUID getId();
    String getName();
    String getCode();
    Boolean getShowClone();
    Boolean getEnabledToApply();
    Boolean getProcessStatus();
    boolean getSentStatus();
    boolean getReconciledStatus();
    boolean getCanceledStatus();
}
