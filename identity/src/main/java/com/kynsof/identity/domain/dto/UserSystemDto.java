package com.kynsof.identity.domain.dto;

import com.kynsof.share.core.domain.EUserType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class UserSystemDto {
    private UUID id;
    private String identification;
    private String email;
    private String userName;
    private String name;
    private String lastName;
    private UserStatus status;
    private LocalDate createdAt;
    private UUID selectedBusiness;


    private String image;
    private EUserType userType;

    public UserSystemDto(UUID id, String userName, String email, String name, String lastName, UserStatus status, String image) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.status = status;
        this.image = image;
    }


}
