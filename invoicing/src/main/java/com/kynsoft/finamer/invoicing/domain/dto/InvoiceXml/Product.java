package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Product")
@XmlAccessorType(XmlAccessType.FIELD)
public class Product extends BaseXml {
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
    private String comment= StringUtils.EMPTY;

    @XmlElementWrapper(name="Taxes")
    @XmlElement(name = "Tax")
    private List<Tax> taxes;

    @XmlElementWrapper(name="ServicesData")
    @XmlElement(name = "ServiceData")
    private List<ServiceData> serviceDatas;

    @Override
    public String toString() {
        String taxesXml = "";
        if (taxes != null && !taxes.isEmpty()) {
            taxesXml = taxes.stream()
                    .map(Tax::toString)
                    .collect(Collectors.joining("\n", "<Taxes>\n", "\n</Taxes>"));
        }

        String servicesXml = "";
        if (serviceDatas != null && !serviceDatas.isEmpty()) {
            servicesXml = serviceDatas.stream()
                    .map(ServiceData::toString)
                    .collect(Collectors.joining("\n", "<ServicesData>\n", "\n</ServicesData>"));
        }

        return String.format(Locale.US,
                "<Product SupplierSKU=\"%s\" CustomerSKU=\"%s\" Item=\"%s\" Qty=\"%d\" MU=\"%s\" UP=\"%.2f\" Total=\"%.2f\" Comment=\"%s\">%s%s\n</Product>",
                safe(supplierSku),
                safe(customerSku),
                safe(item),
                qty,
                safe(mu),
                up,
                total,
                safe(comment),
                taxesXml.isEmpty() ? "" : "\n" + taxesXml,
                servicesXml.isEmpty() ? "" : "\n" + servicesXml
        );
    }
}