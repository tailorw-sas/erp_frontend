package com.kynsoft.finamer.settings.domain.dto.me;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessModulePermissionsDto {
    private String name;
    private Set<PermissionInfo> uniquePermissions;
}
