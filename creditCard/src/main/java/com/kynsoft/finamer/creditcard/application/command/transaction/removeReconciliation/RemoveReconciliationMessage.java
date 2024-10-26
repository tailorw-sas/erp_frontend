package com.kynsoft.finamer.creditcard.application.command.transaction.removeReconciliation;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RemoveReconciliationMessage implements ICommandMessage {

    private final String command = "REMOVE_RECONCILIATIONS";
    private final UUID bankReconciliation;
    private final List<Long> transactionsIds;
}
