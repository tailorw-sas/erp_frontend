package com.kynsof.identity.application.command.business.create;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateBusinessRequest {
    private String name;
    private String latitude;
    private String longitude;
    private String description;
   // private  byte [] image;
    private  String image;
    private String ruc;
    private String address;;
    private UUID geographicLocation;

}
