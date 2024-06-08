package com.kynsof.identity.application.query.userPermissionBusiness.getPermissionsForUserAndBusiness;

import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.interfaces.service.IUserPermissionBusinessService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetPermissionsForUserAndBusinessQueryHandler implements IQueryHandler<GetPermissionsForUserAndBusinessQuery, GetPermissionsForUserAndBusinessResponse>  {

    private final IUserPermissionBusinessService serviceImpl;

    public GetPermissionsForUserAndBusinessQueryHandler(IUserPermissionBusinessService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public GetPermissionsForUserAndBusinessResponse handle(GetPermissionsForUserAndBusinessQuery query) {
        List<PermissionDto> userRoleBusinessDto = serviceImpl.getPermissionsForUserAndBusiness(query.getUserId(), query.getBusinessId());
        return new GetPermissionsForUserAndBusinessResponse(userRoleBusinessDto);
    }
}
