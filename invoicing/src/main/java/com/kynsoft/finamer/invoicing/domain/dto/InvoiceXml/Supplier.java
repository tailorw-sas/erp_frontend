package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "Supplier")
public class Supplier {

    private String supplierID; // Código del proveedor (interno del proveedor, opcional)
    private String customerSupplierID; // Código del proveedor (interno del cliente, opcional)
    private String CIF; // CIF o NIF del proveedor
    private String company; // Razón social del proveedor
    private String address; // Domicilio del proveedor
    private String city; // Población del proveedor
    private String postalCode; // Código postal del proveedor
    private String province; // Provincia del proveedor
    private String country; // País del proveedor (ej. ESP para España)
    private String email; // Email del proveedor (obligatorio solo para proveedores italianos)

    // Getters y Setters

    @XmlElement(name = "SupplierID")
    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    @XmlElement(name = "CustomerSupplierID")
    public String getCustomerSupplierID() {
        return customerSupplierID;
    }

    public void setCustomerSupplierID(String customerSupplierID) {
        this.customerSupplierID = customerSupplierID;
    }

    @XmlElement(name = "CIF")
    public String getCIF() {
        return CIF;
    }

    public void setCIF(String CIF) {
        this.CIF = CIF;
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
}
