package com.kynsoft.finamer.invoicing.infrastructure.repository.command.custom;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAdjustment;

import java.util.UUID;

public interface ManageAdjustmentWriteCustomRepository {

    UUID insert(ManageAdjustment adjustment);
}
