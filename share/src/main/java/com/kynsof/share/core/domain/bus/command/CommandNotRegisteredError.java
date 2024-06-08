package com.kynsof.share.core.domain.bus.command;

public final class CommandNotRegisteredError extends Exception {
    public CommandNotRegisteredError(Class<? extends ICommand> command) {
        super(String.format("The command <%s> hasn't a command handler associated", command.toString()));
    }
}
