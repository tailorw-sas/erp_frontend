package com.kynsoft.finamer.invoicing.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageContactDto implements Serializable {

    private UUID id;
    private String code;
    private String description;
    private String name;
    private ManageHotelDto manageHotel;
    private String email;
    private String phone;
    private Integer position;
}
