package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@XmlRootElement(name = "Client")
@XmlAccessorType(XmlAccessType.FIELD)
public class Client {
    @XmlAttribute(name = "SupplierClientID")
    private String code;

    @XmlAttribute(name = "CIF")
    private String cif;

    @XmlAttribute(name = "Company")
    private String company;

    @XmlAttribute(name = "Address")
    private String address;

    @XmlAttribute(name = "City")
    private String city;

    @XmlAttribute(name = "PC")
    private String zipCode;

    @XmlAttribute(name = "Province")
    private String cityState;

    @XmlAttribute(name = "Country")
    private String country;

    @XmlAttribute(name = "Email")
    private String email;
}