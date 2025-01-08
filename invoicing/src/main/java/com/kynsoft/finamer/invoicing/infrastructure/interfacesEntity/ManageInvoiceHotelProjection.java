package com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity;

import java.util.UUID;

public interface ManageInvoiceHotelProjection {
    UUID getId();
    String getCode();
    String getName();
    Boolean getIsVirtual();
}
