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
@XmlRootElement(name = "Tax")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tax {
    @XmlAttribute(name = "Type")
    private String type;

    @XmlAttribute(name = "Rate")
    private double rate;

    @XmlAttribute(name = "Amount")
    private double amount;

    @XmlAttribute(name = "Description")
    private String description;
}