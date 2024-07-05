package com.kynsoft.finamer.settings.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageModuleDto {
    protected UUID id;
    private String name;
    private String code;
    private String status;
}
