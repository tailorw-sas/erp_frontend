package com.kynsoft.finamer.invoicing.infrastructure.repository.command.custom;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomRate;

import java.util.UUID;

public interface ManageRoomRateWriteCustomRepository {

    UUID insert(ManageRoomRate roomRate);
}
