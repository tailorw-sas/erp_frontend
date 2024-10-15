package com.kynsoft.finamer.audit.domain.bus.command;

public interface ICommandHandler<T extends ICommand> {
    void handle(T command);
}
