package com.kynsof.identity.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
@Getter
@Setter
@AllArgsConstructor
public class LocationHierarchyDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final ProvinceDto provinceDto;
    private final CantonDto cantonDto;
    private final ParroquiaDto parroquiaDto;
}
