package com.kynsof.identity.application.query.userPermissionBusiness.getbyid;

import com.kynsof.identity.application.query.business.search.BusinessResponse;
import com.kynsof.identity.application.query.permission.getById.PermissionResponse;
import com.kynsof.identity.application.query.users.getSearch.UserSystemsResponse;
import com.kynsof.identity.domain.dto.UserPermissionBusinessDto;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class UserRoleBusinessResponse implements IResponse, Serializable {

    private UUID id;
    private UserSystemsResponse user;
    private PermissionResponse permission;
    private BusinessResponse business;

    public UserRoleBusinessResponse(UserPermissionBusinessDto userRoleBusinessDto) {
        this.id = userRoleBusinessDto.getId();
        this.user = new UserSystemsResponse(userRoleBusinessDto.getUser());
        this.permission = new PermissionResponse(userRoleBusinessDto.getPermission());
        this.business = new BusinessResponse(userRoleBusinessDto.getBusiness());
    }
}
