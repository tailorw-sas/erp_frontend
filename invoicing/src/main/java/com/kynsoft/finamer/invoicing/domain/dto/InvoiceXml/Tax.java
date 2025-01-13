package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"type", "rate", "amount", "description"})
public class Tax {
    private String type;        // Tipo de impuesto (e.g., EXENTO, IVA, etc.)
    private double rate;        // Tasa del impuesto
    private double amount;      // Monto del impuesto
    private String description; // Descripción del impuesto

    // Constructor vacío
    public Tax() {
    }

    // Constructor con parámetros
    public Tax(String type, double rate, double amount, String description) {
        this.type = type;
        this.rate = rate;
        this.amount = amount;
        this.description = description;
    }

    @XmlElement(name = "Type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "Rate")
    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @XmlElement(name = "Amount")
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @XmlElement(name = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}