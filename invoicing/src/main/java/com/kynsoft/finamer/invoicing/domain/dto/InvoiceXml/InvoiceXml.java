package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "Transaction", namespace = "http://www.w3.org/2001/XMLSchema")
@XmlType(propOrder = {"generalData", "supplier", "client", "productList", "totalSummary"})
public class InvoiceXml {

    private GeneralData generalData;
    private Supplier supplier;
    private Client client;
    private List<Product> productList;
    private TotalSummary totalSummary;

    @XmlElement(name = "GeneralData", required = true)
    public GeneralData getGeneralData() {
        return generalData != null ? generalData : new GeneralData();
    }

    public void setGeneralData(GeneralData generalData) {
        this.generalData = generalData;
    }

    @XmlElement(name = "Supplier", required = true)
    public Supplier getSupplier() {
        return supplier != null ? supplier : new Supplier();
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @XmlElement(name = "Client", required = true)
    public Client getClient() {
        return client != null ? client : new Client();
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @XmlElement(name = "ProductList", required = true)
    public List<Product> getProductList() {
        return productList != null ? productList : List.of();
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @XmlElement(name = "TotalSummary", required = true)
    public TotalSummary getTotalSummary() {
        return totalSummary != null ? totalSummary : new TotalSummary();
    }

    public void setTotalSummary(TotalSummary totalSummary) {
        this.totalSummary = totalSummary;
    }
}