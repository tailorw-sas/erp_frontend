package com.kynsof.identity.application.query.users.userMe;


import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class BusinessPermissionResponse implements IResponse, Serializable {
    private UUID businessId;
    private String name;
    private List<String> permissions;
}