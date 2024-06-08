package com.kynsof.identity.application.query.userPermissionBusiness.getbyid;

import com.kynsof.identity.domain.dto.UserPermissionBusinessDto;
import com.kynsof.identity.domain.interfaces.service.IUserPermissionBusinessService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

@Component
public class FindByIdUserRoleBusinessQueryHandler implements IQueryHandler<FindByIdUserRoleBusinessQuery, UserRoleBusinessResponse>  {

    private final IUserPermissionBusinessService serviceImpl;

    public FindByIdUserRoleBusinessQueryHandler(IUserPermissionBusinessService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public UserRoleBusinessResponse handle(FindByIdUserRoleBusinessQuery query) {
        UserPermissionBusinessDto userRoleBusinessDto = serviceImpl.findById(query.getId());

        return new UserRoleBusinessResponse(userRoleBusinessDto);
    }
}
