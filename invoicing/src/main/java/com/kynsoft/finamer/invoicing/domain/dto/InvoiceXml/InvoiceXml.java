package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "Transaction")
@XmlType(propOrder = {"generalData", "supplier", "client", "productList", "taxSummary", "totalSummary"})
public class InvoiceXml {

    private GeneralData generalData;
    private Supplier supplier;
    private Client client;
    private List<Product> productList;
    private TaxSummary taxSummary;
    private TotalSummary totalSummary;

    // Getters y Setters
    @XmlElement(name = "GeneralData")
    public GeneralData getGeneralData() {
        return generalData;
    }

    public void setGeneralData(GeneralData generalData) {
        this.generalData = generalData;
    }

    @XmlElement(name = "Supplier")
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @XmlElement(name = "Client")
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @XmlElement(name = "ProductList")
    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @XmlElement(name = "TaxSummary")
    public TaxSummary getTaxSummary() {
        return taxSummary;
    }

    public void setTaxSummary(TaxSummary taxSummary) {
        this.taxSummary = taxSummary;
    }

    @XmlElement(name = "TotalSummary")
    public TotalSummary getTotalSummary() {
        return totalSummary;
    }

    public void setTotalSummary(TotalSummary totalSummary) {
        this.totalSummary = totalSummary;
    }
}