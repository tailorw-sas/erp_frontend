package com.kynsof.identity.domain.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class UserPermissionBusinessDto {
    private UUID id;
    private UserSystemDto user;
    private PermissionDto permission;
    private BusinessDto business;
    private boolean deleted = false;

    public UserPermissionBusinessDto(UUID id, UserSystemDto user, PermissionDto role, BusinessDto business) {
        this.id = id;
        this.user = user;
        this.permission = role;
        this.business = business;
    }

}
