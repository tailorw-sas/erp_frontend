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
public class UserMeResponse implements IResponse, Serializable {
    private UUID userId;
    private String userName;
    private String email;
    private String name;
    private String lastName;
    private String image;
    private UUID selectedBusiness;
    private List<BusinessPermissionResponse> businesses;

}