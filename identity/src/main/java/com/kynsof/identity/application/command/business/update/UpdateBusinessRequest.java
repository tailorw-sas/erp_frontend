package com.kynsof.identity.application.command.business.update;

import com.kynsof.identity.domain.dto.enumType.EBusinessStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBusinessRequest {
    private String name;
    private String description;
    private String image;
    private String ruc;
    private EBusinessStatus status;
    private String address;

}