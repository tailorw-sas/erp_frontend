package com.kynsof.identity.application.query.users.userMe;

import com.kynsof.identity.domain.interfaces.service.IUserMeService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserMeQueryHandler implements IQueryHandler<UserMeQuery, UserMeResponse>  {

    private final IUserMeService serviceImpl;

    public UserMeQueryHandler(IUserMeService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public UserMeResponse handle(UserMeQuery query) {
        return serviceImpl.getUserInfo(query.getId());
    }
}
