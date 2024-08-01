package com.kynsoft.finamer.settings.application.query.userMe;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.domain.services.IManageEmployeeService;
import org.springframework.stereotype.Component;

@Component
public class UserMeQueryHandler implements IQueryHandler<UserMeQuery, UserMeResponse>  {

    private final IManageEmployeeService serviceImpl;

    public UserMeQueryHandler(IManageEmployeeService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public UserMeResponse handle(UserMeQuery query) {
        return serviceImpl.me(query.getId());
    }
}
