package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_invoice")
public class ManageInvoice {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(columnDefinition = "serial", name = "invoice_gen_id")
    @Generated(GenerationTime.INSERT)
    private Long invoice_id;

    @Generated(GenerationTime.INSERT)
    @Column(columnDefinition = "serial", name = "invoice_number")
    private Long invoiceNumber;

    private LocalDateTime invoiceDate;

    private Boolean isManual;
    private Boolean autoRec;

    private Double invoiceAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_hotel")
    private ManageHotel hotel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_agency")
    private ManageAgency agency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_invoice_type")
    private ManageInvoiceType invoiceType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_invoice_status")
    private ManageInvoiceStatus invoiceStatus;

    @Column(nullable = true)
    private Boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    public ManageInvoice(ManageInvoiceDto dto) {
        this.id = dto.getId();

        this.invoiceDate = dto.getInvoiceDate();
        this.isManual = dto.getIsManual();
        this.invoiceAmount = dto.getInvoiceAmount();
        this.hotel = dto.getHotel() != null ? new ManageHotel(dto.getHotel()) : null;
        this.agency = dto.getAgency() != null ? new ManageAgency(dto.getAgency()) : null;
        this.invoiceType = dto.getInvoiceType() != null ? new ManageInvoiceType(dto.getInvoiceType()) : null;
        this.invoiceStatus = dto.getStatus() != null ? new ManageInvoiceStatus(dto.getStatus()) : null;
        this.autoRec = false;

    }

    public ManageInvoiceDto toAggregate() {
        return new ManageInvoiceDto(id, invoice_id, invoiceNumber, invoiceDate, isManual, invoiceAmount,
                hotel.toAggregate(), agency.toAggregate(), invoiceType.toAggregate(), invoiceStatus.toAggregate(),
                autoRec);
    }

}
