package com.kynsof.share.core.infrastructure.bus.query;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.bus.query.QueryNotRegisteredError;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings(value = "all")
public final class QueryHandlersInformation {

    Map<Class<? extends IQuery>, Class<? extends IQueryHandler>> indexedQueryHandlers;

    public QueryHandlersInformation(ListableBeanFactory beanFactory) {
        indexedQueryHandlers = beanFactory.getBeansOfType(IQueryHandler.class).entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toMap(this::getHandlerParametrizedClass, this::getHandlerClass));
    }

    public Class<? extends IQueryHandler> search(Class<? extends IQuery> queryClass) throws QueryNotRegisteredError {
        Class<? extends IQueryHandler> queryHandlerClass = indexedQueryHandlers.get(queryClass);

        if (null == queryHandlerClass) {
            throw new QueryNotRegisteredError(queryClass);
        }

        return queryHandlerClass;
    }

    private Class<? extends IQueryHandler> getHandlerClass(IQueryHandler handler) {
        Class<? extends IQueryHandler> realClass = handler.getClass();
        if (handler instanceof Advised bean) {
            realClass = (Class<? extends IQueryHandler>) bean.getTargetClass();
        }
        return realClass;
    }

    private Class<? extends IQuery> getHandlerParametrizedClass(IQueryHandler handler) {
        Class<? extends IQueryHandler> realClass = getHandlerClass(handler);
        ParameterizedType paramType = (ParameterizedType) realClass.getGenericInterfaces()[0];
        return (Class<? extends IQuery>) paramType.getActualTypeArguments()[0];
    }
}
