package com.kynsof.identity.application.query.permission.getById;

import com.kynsof.identity.domain.dto.RoleDto;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse implements IResponse {
    private UUID id;
    private String name;

    public RoleResponse(RoleDto role) {
        this.id = role.getId();
        this.name = role.getName();
    }

}
