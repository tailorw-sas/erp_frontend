package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Product")
public class Product {

    private String supplierSKU; // Código del localizador del proveedor
    private String customerSKU; // Número de referencia o reserva proporcionado por el cliente
    private String item; // Descripción del producto o servicio
    private Double qty; // Cantidad del producto o servicio
    private String MU; // Unidad de medida (por ejemplo, "Unidades")
    private Double UP; // Precio unitario bruto (sin descuentos)
    private Double total; // Importe bruto total (cantidad x precio unitario)
    private Double netAmount; // Importe neto (bruto - descuentos)
    private Double discount; // Descuento aplicado
    private Double charge; // Cargos adicionales, si aplica
    private Double taxAmount; // Importe de impuestos aplicados a esta línea
    private String serviceStartDate; // Fecha de inicio del servicio (opcional)
    private String serviceEndDate; // Fecha de fin del servicio (opcional)
    private String comment; // Comentarios adicionales sobre el producto
    private Integer adultsNumber; // Número de adultos
    private Integer childrenNumber; // Número de niños
    private String roomType; // Tipo de habitación (opcional)
    private String ratePlan; // Plan de tarifa asociado (opcional)
    private String roomCategory; // Categoría de la habitación (opcional)

    // Getters y Setters

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
    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    @XmlElement(name = "MU")
    public String getMU() {
        return MU;
    }

    public void setMU(String MU) {
        this.MU = MU;
    }

    @XmlElement(name = "UP")
    public Double getUP() {
        return UP;
    }

    public void setUP(Double UP) {
        this.UP = UP;
    }

    @XmlElement(name = "Total")
    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @XmlElement(name = "NetAmount")
    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    @XmlElement(name = "Discount")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @XmlElement(name = "Charge")
    public Double getCharge() {
        return charge;
    }

    public void setCharge(Double charge) {
        this.charge = charge;
    }

    @XmlElement(name = "TaxAmount")
    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    @XmlElement(name = "ServiceStartDate")
    public String getServiceStartDate() {
        return serviceStartDate;
    }

    public void setServiceStartDate(String serviceStartDate) {
        this.serviceStartDate = serviceStartDate;
    }

    @XmlElement(name = "ServiceEndDate")
    public String getServiceEndDate() {
        return serviceEndDate;
    }

    public void setServiceEndDate(String serviceEndDate) {
        this.serviceEndDate = serviceEndDate;
    }

    @XmlElement(name = "Comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @XmlElement(name = "AdultsNumber")
    public Integer getAdultsNumber() {
        return adultsNumber;
    }

    public void setAdultsNumber(Integer adultsNumber) {
        this.adultsNumber = adultsNumber;
    }

    @XmlElement(name = "ChildrenNumber")
    public Integer getChildrenNumber() {
        return childrenNumber;
    }

    public void setChildrenNumber(Integer childrenNumber) {
        this.childrenNumber = childrenNumber;
    }

    @XmlElement(name = "RoomType")
    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    @XmlElement(name = "RatePlan")
    public String getRatePlan() {
        return ratePlan;
    }

    public void setRatePlan(String ratePlan) {
        this.ratePlan = ratePlan;
    }

    @XmlElement(name = "RoomCategory")
    public String getRoomCategory() {
        return roomCategory;
    }

    public void setRoomCategory(String roomCategory) {
        this.roomCategory = roomCategory;
    }
}