package com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateBankReconciliationMessage implements ICommandMessage {

    private final UUID id;

    private final Long reconciliationId;

    private final List<Long> adjustmentTransactions;

    private final String command = "CREATE_BANK_RECONCILIATION";

}
