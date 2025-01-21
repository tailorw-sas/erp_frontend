package com.kynsoft.finamer.insis.application.query.objectResponse.importProcess;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.insis.infrastructure.model.enums.ImportProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ImportProcessResponse implements IResponse {

    private UUID id;
    private ImportProcessStatus status;
    private LocalDate bookingDate;
    private LocalDateTime completedAt;
    private int totalBooking;
    private UUID userId;
    private int totalSuccessful;
    private int totalFailed;
}
