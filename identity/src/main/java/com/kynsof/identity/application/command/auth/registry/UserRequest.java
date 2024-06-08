package com.kynsof.identity.application.command.auth.registry;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Value
@RequiredArgsConstructor
@Builder
public class UserRequest implements Serializable {
    String userName;
    String email;
    String name;
    String lastName;
    String password;
    //List<String> roles;
}
