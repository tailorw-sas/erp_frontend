package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "Supplier")
@XmlAccessorType(XmlAccessType.FIELD)
public class Supplier extends BaseXml {
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

    @Override
    public String toString(){
        return String.format("<Supplier SupplierID=\"%s\" CustomerSupplierID=\"%s\" CIF=\"%s\" Company=\"%s\" Address=\"%s\" City=\"%s\" PC=\"%s\" Province=\"%s\" Country=\"%s\"/>",
                safe(code), safe(customerSupplierId), safe(cif), safe(company), safe(address), safe(city), safe(zipCode), safe(cityState), safe(country));
    }
}