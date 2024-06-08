package com.kynsof.share.core.application.payment.domain.placeToPlay.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Shipping {
    private String Name;
    private String Email;
    private String DocumentType;
    private String Document;
    private String Surname;
    private String Company;
    private Address Address;
    private String Mobile;
}
