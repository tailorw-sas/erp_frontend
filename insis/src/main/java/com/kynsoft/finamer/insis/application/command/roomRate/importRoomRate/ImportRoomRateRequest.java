package com.kynsoft.finamer.insis.application.command.roomRate.importRoomRate;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ImportRoomRateRequest {
    public UUID id;
    public UUID userId;
    public List<UUID> roomRates;
}
