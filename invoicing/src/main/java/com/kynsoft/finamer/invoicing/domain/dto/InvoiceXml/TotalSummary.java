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
@XmlRootElement(name = "TotalSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class TotalSummary {
    @XmlAttribute(name = "GrossAmount")
    private double grossAmount;

    @XmlAttribute(name = "Discounts")
    private double discounts;

    @XmlAttribute(name = "SubTotal")
    private double subTotal;

    @XmlAttribute(name = "Tax")
    private double tax;

    @XmlAttribute(name = "Total")
    private double total;
}