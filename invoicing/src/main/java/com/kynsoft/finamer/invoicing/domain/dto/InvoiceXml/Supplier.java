package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@XmlRootElement(name = "Supplier")
@XmlAccessorType(XmlAccessType.FIELD)
public class Supplier {
    @XmlAttribute(name = "SupplierID")
    private String code;

    @XmlAttribute(name = "CustomerSupplierID")
    private String customerSupplierId;

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
}