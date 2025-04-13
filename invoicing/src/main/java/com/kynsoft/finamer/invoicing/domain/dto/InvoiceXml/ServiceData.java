package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;
import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "ServiceData")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceData extends BaseXml {
    @XmlAttribute(name = "SupplierClientID")
    private String supplierClientId;

    @XmlAttribute(name = "SupplierID")
    private String supplierId;

    @XmlAttribute(name = "SupplierName")
    private String supplierName;

    @XmlAttribute(name = "Pax")
    private String pax;

    @XmlAttribute(name = "BeginDate")
    private String beginDate;

    @XmlAttribute(name = "EndDate")
    private String endDate;

    @XmlAttribute(name = "PaxNumber")
    private int paxNumber;

    @XmlAttribute(name = "AdultsNumber")
    private int adultsNumber;

    @XmlAttribute(name = "KidsNumber")
    private int kidsNumber;

    @XmlAttribute(name = "RoomNumber")
    private String roomNumber;

    @XmlAttribute(name = "RoomCategory")
    private String roomCategory = StringUtils.EMPTY;

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate != null ? beginDate.format(DateTimeFormatter.ISO_DATE) : null;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate != null ? endDate.format(DateTimeFormatter.ISO_DATE) : null;
    }

    @Override
    public String toString() {
        return String.format(
                "<ServiceData SupplierClientID=\"%s\" SupplierID=\"%s\" SupplierName=\"%s\" Pax=\"%s\" BeginDate=\"%s\" EndDate=\"%s\" PaxNumber=\"%d\" AdultsNumber=\"%d\" KidsNumber=\"%d\" RoomNumber=\"%s\" RoomCategory=\"%s\"/>",
                safe(supplierClientId),
                safe(supplierId),
                safe(supplierName),
                safe(pax),
                safe(beginDate),
                safe(endDate),
                paxNumber,
                adultsNumber,
                kidsNumber,
                safe(roomNumber),
                safe(roomCategory)
        );
    }
}