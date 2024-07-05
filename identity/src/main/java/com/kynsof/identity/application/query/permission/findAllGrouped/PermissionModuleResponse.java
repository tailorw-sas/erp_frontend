package com.kynsof.identity.application.query.permission.findAllGrouped;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionModuleResponse implements IResponse {

    private String module;
    private List<PermissionBasicResponse> permissions;
}
