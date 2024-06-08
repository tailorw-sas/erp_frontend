package com.kynsoft.gateway.domain.dto.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoleRequest {
    public String name;
    public String description;
}
