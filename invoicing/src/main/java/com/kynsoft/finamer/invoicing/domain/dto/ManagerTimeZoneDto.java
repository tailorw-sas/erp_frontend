package com.kynsoft.finamer.invoicing.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagerTimeZoneDto implements Serializable {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private Double elapse;
    private String status;

}
