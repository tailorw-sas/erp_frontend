package com.kynsof.identity.domain.dto.moduleDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ModuleNodeDto {
    private String key;
    private ModuleDataDto data;
    private List<ModuleNodeDto> children;
}
