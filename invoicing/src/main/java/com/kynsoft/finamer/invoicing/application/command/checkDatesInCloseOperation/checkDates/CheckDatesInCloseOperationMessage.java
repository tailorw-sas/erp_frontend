package com.kynsoft.finamer.invoicing.application.command.checkDatesInCloseOperation.checkDates;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckDatesInCloseOperationMessage implements ICommandMessage {

    private final String command = "OK";
}
