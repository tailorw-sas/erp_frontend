package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.List;

@XmlType(propOrder = {"supplierSKU", "customerSKU", "item", "qty", "mu", "up", "total", "comment", "taxes", "servicesData"})
public class Product {
    private String supplierSKU;
    private String customerSKU;
    private String item;
    private double qty;
    private String mu;
    private double up;
    private double total;
    private String comment;
    private List<Tax> taxes;
    private List<ServiceData> servicesData;

    @XmlElement(name = "SupplierSKU")
    public String getSupplierSKU() {
        return supplierSKU;
    }

    public void setSupplierSKU(String supplierSKU) {
        this.supplierSKU = supplierSKU;
    }

    @XmlElement(name = "CustomerSKU")
    public String getCustomerSKU() {
        return customerSKU;
    }

    public void setCustomerSKU(String customerSKU) {
        this.customerSKU = customerSKU;
    }

    @XmlElement(name = "Item")
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @XmlElement(name = "Qty")
    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    @XmlElement(name = "MU")
    public String getMU() {
        return mu;
    }

    public void setMU(String mu) {
        this.mu = mu;
    }

    @XmlElement(name = "UP")
    public double getUP() {
        return up;
    }

    public void setUP(double up) {
        this.up = up;
    }

    @XmlElement(name = "Total")
    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @XmlElement(name = "Comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @XmlElement(name = "Taxes")
    public List<Tax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml.Tax> taxes) {
        this.taxes = taxes;
    }

    @XmlElement(name = "ServicesData")
    public List<ServiceData> getServicesData() {
        return servicesData;
    }

    public void setServicesData(List<ServiceData> servicesData) {
        this.servicesData = servicesData;
    }
}