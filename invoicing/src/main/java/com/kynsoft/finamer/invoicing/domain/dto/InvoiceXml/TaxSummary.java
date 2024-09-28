package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "TaxSummary")
public class TaxSummary {

    private String type; // Tipo de impuesto (ej. IVA, VAT, IGIC)
    private Double rate; // Tasa del impuesto en porcentaje o unidad
    private Double base; // Base imponible total sobre la cual se aplica el impuesto
    private Double amount; // Importe total del impuesto
    private String description; // Descripción del tipo de impuesto
    private String senderName; // Nombre o código del tipo de impuesto según el proveedor (opcional)
    private Boolean payableToSupplier; // Indica si el importe del impuesto es pagadero al proveedor (opcional)

    // Getters y Setters

    @XmlElement(name = "Type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "Rate")
    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    @XmlElement(name = "Base")
    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    @XmlElement(name = "Amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @XmlElement(name = "Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlElement(name = "SenderName")
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @XmlElement(name = "PayableToSupplier")
    public Boolean getPayableToSupplier() {
        return payableToSupplier;
    }

    public void setPayableToSupplier(Boolean payableToSupplier) {
        this.payableToSupplier = payableToSupplier;
    }
}