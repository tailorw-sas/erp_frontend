package com.kynsof.identity.application.query.userPermissionBusiness.getPermissionsForUserAndBusiness;

import com.kynsof.identity.application.query.permission.getById.PermissionResponse;
import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
public class GetPermissionsForUserAndBusinessResponse implements IResponse, Serializable {

    private List<PermissionResponse> permissions;

    public GetPermissionsForUserAndBusinessResponse(List<PermissionDto> permissionDtos) {
        this.permissions = permissionDtos.stream()
                .map(PermissionResponse::new)
                .collect(Collectors.toList());
    }
}
