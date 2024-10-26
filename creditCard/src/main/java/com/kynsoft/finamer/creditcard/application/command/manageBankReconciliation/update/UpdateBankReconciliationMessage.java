package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UpdateBankReconciliationMessage implements ICommandMessage {

    private final UUID id;

    private final List<Long> adjustmentTransactionIds;

    private final String command = "UPDATE_BANK_RECONCILIATION";
}
