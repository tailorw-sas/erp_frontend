package com.kynsof.identity.application.command.user.create;


import com.kynsof.share.core.domain.EUserType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserSystemRequest {
    private String userName;
    private String email;
    private String name;
    private String lastName;
    private String password;
    private EUserType userType;
    private String image;
}
