package com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity;

import java.util.UUID;

public interface ManageInvoiceTypeProjection {
    UUID getId();
    String getName();
    String getCode();
}
