package com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity;

import java.util.UUID;

public interface ManageInvoiceAgencyProjection {
    UUID getId();
    String getCode();
    String getName();
}
