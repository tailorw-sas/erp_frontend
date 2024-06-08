package com.kynsof.share.core.infrastructure.bus.command;

import com.kynsof.share.core.domain.bus.command.CommandNotRegisteredError;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings(value = "all")
public final class CommandHandlersInformation {

    Map<Class<? extends ICommand>, Class<? extends ICommandHandler>> indexedCommandHandlers;

    public CommandHandlersInformation(ListableBeanFactory beanFactory) {
        indexedCommandHandlers = beanFactory.getBeansOfType(ICommandHandler.class).entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toMap(this::getHandlerParametrizedClass, this::getHandlerClass));
    }

    public Class<? extends ICommandHandler> search(Class<? extends ICommand> commandClass)
            throws CommandNotRegisteredError {
        Class<? extends ICommandHandler> commandHandlerClass = indexedCommandHandlers.get(commandClass);

        if (null == commandHandlerClass) {
            throw new CommandNotRegisteredError(commandClass);
        }
        return commandHandlerClass;
    }

    private Class<? extends ICommandHandler> getHandlerClass(ICommandHandler handler) {
        Class<? extends ICommandHandler> realClass = handler.getClass();
        if (handler instanceof Advised bean) {
            realClass = (Class<? extends ICommandHandler>) bean.getTargetClass();
        }
        return realClass;
    }

    private Class<? extends ICommand> getHandlerParametrizedClass(ICommandHandler handler) {
        Class<? extends ICommandHandler> realClass = getHandlerClass(handler);
        ParameterizedType paramType = (ParameterizedType) realClass.getGenericInterfaces()[0];
        return (Class<? extends ICommand>) paramType.getActualTypeArguments()[0];
    }

}
