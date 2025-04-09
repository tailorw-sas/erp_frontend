package com.kynsoft.finamer.invoicing.domain.dto.InvoiceXml;
import jakarta.xml.bind.annotation.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@XmlRootElement(name = "ServiceData")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceData {
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
}