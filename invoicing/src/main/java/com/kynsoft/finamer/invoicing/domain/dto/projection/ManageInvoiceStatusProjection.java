package com.kynsoft.finamer.invoicing.domain.dto.projection;

import java.util.UUID;

public interface ManageInvoiceStatusProjection {
    UUID getId();
    String getCode();
    String getName();
    Boolean getShowClone();
}
