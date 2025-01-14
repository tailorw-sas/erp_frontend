package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder = {
        "supplierClientID", "supplierID", "supplierName", "pax", "beginDate", "endDate",
        "paxNumber", "adultsNumber", "kidsNumber", "roomNumber", "roomCategory"
})
public class ServiceData {
    private String supplierClientID;
    private String supplierID;
    private String supplierName;
    private String pax;
    private String beginDate;
    private String endDate;
    private int paxNumber;
    private int adultsNumber;
    private int kidsNumber;
    private String roomNumber;
    private String roomCategory;

    @XmlElement(name = "SupplierClientID")
    public String getSupplierClientID() {
        return supplierClientID;
    }

    public void setSupplierClientID(String supplierClientID) {
        this.supplierClientID = supplierClientID;
    }

    @XmlElement(name = "SupplierID")
    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }

    @XmlElement(name = "SupplierName")
    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @XmlElement(name = "Pax")
    public String getPax() {
        return pax;
    }

    public void setPax(String pax) {
        this.pax = pax;
    }

    @XmlElement(name = "BeginDate")
    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    @XmlElement(name = "EndDate")
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @XmlElement(name = "PaxNumber")
    public int getPaxNumber() {
        return paxNumber;
    }

    public void setPaxNumber(int paxNumber) {
        this.paxNumber = paxNumber;
    }

    @XmlElement(name = "AdultsNumber")
    public int getAdultsNumber() {
        return adultsNumber;
    }

    public void setAdultsNumber(int adultsNumber) {
        this.adultsNumber = adultsNumber;
    }

    @XmlElement(name = "KidsNumber")
    public int getKidsNumber() {
        return kidsNumber;
    }

    public void setKidsNumber(int kidsNumber) {
        this.kidsNumber = kidsNumber;
    }

    @XmlElement(name = "RoomNumber")
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @XmlElement(name = "RoomCategory")
    public String getRoomCategory() {
        return roomCategory;
    }

    public void setRoomCategory(String roomCategory) {
        this.roomCategory = roomCategory;
    }
}