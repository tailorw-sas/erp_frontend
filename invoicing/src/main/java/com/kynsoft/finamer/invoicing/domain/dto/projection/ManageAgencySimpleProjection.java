package com.kynsoft.finamer.invoicing.domain.dto.projection;

import java.util.UUID;

public interface ManageAgencySimpleProjection {
    UUID getId();
    String getCode();
    String getName();
}
