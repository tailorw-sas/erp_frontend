package com.kynsof.identity.application.query.users.getSearch;

import com.kynsof.identity.domain.dto.UserStatus;
import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.share.core.domain.EUserType;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class UserSystemsResponse implements IResponse {

    private UUID id;
    private String userName;
    private String email;
    private String name;
    private String lastName;
    private UserStatus status;
    private EUserType userType;
    private String image;
    private LocalDate createdAt;

    public UserSystemsResponse(UserSystemDto contactInfoDto) {
        this.id = contactInfoDto.getId();
        this.userName = contactInfoDto.getUserName();
        this.email = contactInfoDto.getEmail();
        this.name = contactInfoDto.getName();
        this.lastName = contactInfoDto.getLastName();
        this.status = contactInfoDto.getStatus();
        this.image = contactInfoDto.getImage();
        this.userType = contactInfoDto.getUserType() != null ? contactInfoDto.getUserType() : EUserType.UNDEFINED;
        this.createdAt = contactInfoDto.getCreatedAt();
    }

}
