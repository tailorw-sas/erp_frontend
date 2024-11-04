package com.kynsoft.finamer.audit.infrastructure.bus;


import com.kynsoft.finamer.audit.domain.bus.command.ICommand;
import com.kynsoft.finamer.audit.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.audit.domain.bus.query.IQuery;
import com.kynsoft.finamer.audit.domain.bus.query.IResponse;

public interface IMediator {
    <M extends ICommandMessage> M send(ICommand command);
    <R extends IResponse> R send(IQuery query);
}
