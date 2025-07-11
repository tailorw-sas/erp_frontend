package com.kynsoft.finamer.insis.application.command.roomRate.updateResponseImportRoomRate;

import com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResponseImportRoomRateRequest {
    private UUID importProcessId;
    private List<ErrorResponse> errorResponses;
    private Boolean processed;
}
