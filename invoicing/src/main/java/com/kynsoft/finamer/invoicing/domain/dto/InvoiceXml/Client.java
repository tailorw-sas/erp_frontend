package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Client")
public class Client {

    private String clientID; // Código del cliente (interno del cliente, opcional)
    private String supplierClientID; // Código del cliente pagador (interno del proveedor)
    private String supplierCustomerID; // Código de un centro/oficina/agencia del cliente (opcional)
    private String CIF; // CIF o NIF del cliente
    private String taxID2; // CIF secundario del cliente (opcional, para países con dos identificadores fiscales)
    private String company; // Razón social del cliente
    private String address; // Domicilio del cliente
    private String city; // Población del cliente
    private String postalCode; // Código postal del cliente
    private String province; // Provincia del cliente
    private String country; // País del cliente (ej. ESP para España)
    private String email; // Email del cliente
    private Boolean isPublicAdministration; // Indica si el cliente pertenece a la administración pública (true/false)
    private Boolean B2C; // Indica si el cliente es un particular o una empresa (true/false)

    // Getters y Setters

    @XmlElement(name = "ClientID")
    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    @XmlElement(name = "SupplierClientID")
    public String getSupplierClientID() {
        return supplierClientID;
    }

    public void setSupplierClientID(String supplierClientID) {
        this.supplierClientID = supplierClientID;
    }

    @XmlElement(name = "SupplierCustomerID")
    public String getSupplierCustomerID() {
        return supplierCustomerID;
    }

    public void setSupplierCustomerID(String supplierCustomerID) {
        this.supplierCustomerID = supplierCustomerID;
    }

    @XmlElement(name = "CIF")
    public String getCIF() {
        return CIF;
    }

    public void setCIF(String CIF) {
        this.CIF = CIF;
    }

    @XmlElement(name = "TaxID2")
    public String getTaxID2() {
        return taxID2;
    }

    public void setTaxID2(String taxID2) {
        this.taxID2 = taxID2;
    }

    @XmlElement(name = "Company")
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @XmlElement(name = "Address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlElement(name = "City")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @XmlElement(name = "PC")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @XmlElement(name = "Province")
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @XmlElement(name = "Country")
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @XmlElement(name = "Email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @XmlElement(name = "IsPublicAdministration")
    public Boolean getIsPublicAdministration() {
        return isPublicAdministration;
    }

    public void setIsPublicAdministration(Boolean isPublicAdministration) {
        this.isPublicAdministration = isPublicAdministration;
    }

    @XmlElement(name = "B2C")
    public Boolean getB2C() {
        return B2C;
    }

    public void setB2C(Boolean B2C) {
        this.B2C = B2C;
    }
}