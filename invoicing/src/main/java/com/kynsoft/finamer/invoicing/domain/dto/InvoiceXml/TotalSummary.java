package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TotalSummary")
public class TotalSummary {

    private Double grossAmount; // Suma de los importes brutos por línea de la factura (sin descuentos ni impuestos)
    private Double discounts; // Sumatorio de los descuentos aplicados (opcional)
    private Double subTotal; // Base imponible: importe neto de la factura incluyendo descuentos aplicados
    private Double tax; // Sumatorio de todos los impuestos aplicados
    private Double total; // Importe total a pagar: base imponible + impuestos

    // Getters y Setters

    @XmlElement(name = "GrossAmount")
    public Double getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(Double grossAmount) {
        this.grossAmount = grossAmount;
    }

    @XmlElement(name = "Discounts")
    public Double getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Double discounts) {
        this.discounts = discounts;
    }

    @XmlElement(name = "SubTotal")
    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    @XmlElement(name = "Tax")
    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    @XmlElement(name = "Total")
    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
