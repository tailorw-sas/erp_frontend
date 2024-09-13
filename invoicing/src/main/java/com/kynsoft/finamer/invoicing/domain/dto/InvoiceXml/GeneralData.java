package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import lombok.Setter;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@Setter
@XmlRootElement(name = "GeneralData")
@XmlType(propOrder = {"ref", "type", "date", "language", "currency", "contract", "concept",
        "beginDate", "endDate", "pdfFile", "taxIncluded", "legalText",
        "taxSystemText", "onlyArchive", "printAsCopy", "ncf",
        "serviceType", "exchangeCurrency", "exchangeDate", "exchangeRate"})
public class GeneralData {

    private String ref; // Número de referencia de la factura
    private String type; // Tipo de factura
    private String date; // Fecha de la factura
    private String language; // Idioma de la factura
    private String currency; // Moneda de la factura
    private String contract; // Número de contrato asociado (opcional)
    private String concept; // Concepto global de la factura (opcional)
    private String beginDate; // Fecha de inicio del servicio (opcional)
    private String endDate; // Fecha de fin del servicio (opcional)
    private String pdfFile; // Nombre del archivo PDF adjunto (opcional)
    private Boolean taxIncluded; // Si los importes incluyen impuestos
    private String legalText; // Texto legal (opcional)
    private String taxSystemText; // Régimen fiscal de la factura
    private Boolean onlyArchive; // Si la factura solo debe archivarse y no enviarse
    private Boolean printAsCopy; // Si la factura es una copia
    private String ncf; // Número de comprobante fiscal (opcional)
    private String serviceType; // Tipo de servicio prestado (opcional)
    private String exchangeCurrency; // Moneda usada en el cambio (opcional)
    private String exchangeDate; // Fecha de la tasa de cambio (opcional)
    private String exchangeRate; // Tasa de cambio (opcional)

    // Getters y Setters

    @XmlElement(name = "Ref")
    public String getRef() {
        return ref;
    }

    @XmlElement(name = "Type")
    public String getType() {
        return type;
    }

    @XmlElement(name = "Date")
    public String getDate() {
        return date;
    }

    @XmlElement(name = "Language")
    public String getLanguage() {
        return language;
    }

    @XmlElement(name = "Currency")
    public String getCurrency() {
        return currency;
    }

    @XmlElement(name = "Contract")
    public String getContract() {
        return contract;
    }

    @XmlElement(name = "Concept")
    public String getConcept() {
        return concept;
    }

    @XmlElement(name = "BeginDate")
    public String getBeginDate() {
        return beginDate;
    }

    @XmlElement(name = "EndDate")
    public String getEndDate() {
        return endDate;
    }

    @XmlElement(name = "PDFFile")
    public String getPdfFile() {
        return pdfFile;
    }

    @XmlElement(name = "TaxIncluded")
    public Boolean getTaxIncluded() {
        return taxIncluded;
    }

    @XmlElement(name = "LegalText")
    public String getLegalText() {
        return legalText;
    }

    @XmlElement(name = "TaxSystemText")
    public String getTaxSystemText() {
        return taxSystemText;
    }

    @XmlElement(name = "OnlyArchive")
    public Boolean getOnlyArchive() {
        return onlyArchive;
    }

    @XmlElement(name = "PrintAsCopy")
    public Boolean getPrintAsCopy() {
        return printAsCopy;
    }

    @XmlElement(name = "NCF")
    public String getNcf() {
        return ncf;
    }

    @XmlElement(name = "ServiceType")
    public String getServiceType() {
        return serviceType;
    }

    @XmlElement(name = "ExchangeCurrency")
    public String getExchangeCurrency() {
        return exchangeCurrency;
    }

    @XmlElement(name = "ExchangeDate")
    public String getExchangeDate() {
        return exchangeDate;
    }

    @XmlElement(name = "ExchangeRate")
    public String getExchangeRate() {
        return exchangeRate;
    }

}