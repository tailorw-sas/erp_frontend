package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Product")
@XmlAccessorType(XmlAccessType.FIELD)
public class Product {
    @XmlAttribute(name = "SupplierSKU")
    private String supplierSku;

    @XmlAttribute(name = "CustomerSKU")
    private String customerSku;

    @XmlAttribute(name = "Item")
    private String item;

    @XmlAttribute(name = "Qty")
    private int qty;

    @XmlAttribute(name = "MU")
    private String mu = "Unidades";

    @XmlAttribute(name = "UP")
    private double up;

    @XmlAttribute(name = "Total")
    private double total;

    @XmlAttribute(name = "Comment")
    private String comment;

    @XmlElement(name = "Taxes")
    private List<Tax> taxes;

    @XmlElement(name = "ServicesData")
    private List<ServiceData> serviceDatas;
}