package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@XmlRootElement(name = "Transaction")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "generalData", "supplier", "client", "productList", "totalSummary" })
public class InvoiceXml {

    @XmlAttribute(name = "xmlns:xsd")
    private final String xmlnsXsd = "http://www.w3.org/2001/XMLSchema";

    @XmlAttribute(name = "xmlns:xsi")
    private final String xmlnsXsi = "http://www.w3.org/2001/XMLSchema-instance";

    @XmlElement(name = "GeneralData")
    private GeneralData generalData;

    @XmlElement(name = "Supplier")
    private Supplier supplier;

    @XmlElement(name = "Client")
    private Client client;

    @XmlElementWrapper(name="ProductList")
    @XmlElement(name = "Product")
    private List<Product> productList;

    @XmlElement(name = "TotalSummary")
    private TotalSummary totalSummary;
}
