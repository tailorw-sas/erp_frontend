package com.kynsof.identity.domain.dto.moduleDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDataDto {
    private String name;
    private String type;
    private String code;
}