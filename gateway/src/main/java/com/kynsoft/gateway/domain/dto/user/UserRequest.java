package com.kynsoft.gateway.domain.dto.user;

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
