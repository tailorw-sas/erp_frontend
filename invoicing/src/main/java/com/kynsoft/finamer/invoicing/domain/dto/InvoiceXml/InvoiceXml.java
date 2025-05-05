package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Override
    public String toString() {
        String productsXml = "";
        if (productList != null && !productList.isEmpty()) {
            productsXml = productList.stream()
                    .map(Product::toString)
                    .collect(Collectors.joining("\n", "<ProductList>\n", "\n</ProductList>"));
        }

        return String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<Transaction xmlns:xsd=\"%s\" xmlns:xsi=\"%s\">\n" +
                        "%s\n%s\n%s\n%s\n%s\n</Transaction>",
                xmlnsXsd,
                xmlnsXsi,
                generalData.toString(),
                supplier.toString(),
                client.toString(),
                productsXml,
                totalSummary.toString()
        );
    }

}
