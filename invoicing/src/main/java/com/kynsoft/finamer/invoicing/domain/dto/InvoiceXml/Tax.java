package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Tax")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tax extends BaseXml {
    @XmlAttribute(name = "Type")
    private String type = "EXENTO";

    @XmlAttribute(name = "Rate")
    private double rate = 0.0;

    @XmlAttribute(name = "Amount")
    private double amount = 0.0;

    @XmlAttribute(name = "Description")
    private String description = StringUtils.EMPTY;

    @Override
    public String toString() {
        return String.format("<Tax Type=\"%s\" Rate=\"%.2f\" Amount=\"%.2f\" Description=\"%s\"/>",
                safe(type), rate, amount, safe(description));
    }
}