package com.kynsof.share.core.domain.bus.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public interface ICommandHandler<T extends ICommand> {
    void handle(T command);
}
