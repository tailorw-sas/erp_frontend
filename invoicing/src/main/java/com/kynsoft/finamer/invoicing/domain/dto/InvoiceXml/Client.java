package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Client")
@XmlAccessorType(XmlAccessType.FIELD)
public class Client extends BaseXml {
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

    @Override
    public String toString(){
        return String.format(
                "<Client SupplierClientID=\"%s\" CIF=\"%s\" Company=\"%s\" Address=\"%s\" City=\"%s\" PC=\"%s\" Province=\"%s\" Country=\"%s\" Email=\"%s\"/>",
                safe(code), safe(cif), safe(company), safe(address), safe(city), safe(zipCode), safe(cityState), safe(country), safe(email));
    }
}