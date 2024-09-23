package com.kynsoft.finamer.invoicing.domain.dto.projection;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface InvoiceCloseOperationProjection {
    UUID getId();
    Status getStatus();
    LocalDate getBeginDate();
    LocalDate getEndDate();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
