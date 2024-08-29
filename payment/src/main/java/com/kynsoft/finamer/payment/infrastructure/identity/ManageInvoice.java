package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private Long invoiceId;
    private Long invoiceNo;
    private String invoiceNumber;
    private Double invoiceAmount;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean hasAttachment;

    @Enumerated(EnumType.STRING)
    private EInvoiceType invoiceType;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<ManageBooking> bookings;

    public ManageInvoice(ManageInvoiceDto dto) {
        this.id = dto.getId();
        this.invoiceId = dto.getInvoiceId();
        this.invoiceNumber = dto.getInvoiceNumber();
        this.invoiceType = dto.getInvoiceType();
        this.invoiceAmount = dto.getInvoiceAmount();
        this.bookings = dto.getBookings() != null ? dto.getBookings().stream().map(_booking -> {
            ManageBooking booking = new ManageBooking(_booking);
            booking.setInvoice(this);
            return booking;
        }).collect(Collectors.toList()) : null;
        this.invoiceNo = dto.getInvoiceNo();
        this.hasAttachment = dto.getHasAttachment();
    }

    public ManageInvoiceDto toAggregateSample() {
        return new ManageInvoiceDto(
                id,
                invoiceId,
                invoiceNo,
                invoiceNumber,
                invoiceType,
                invoiceAmount,
                null,
                hasAttachment
        );
    }

    public ManageInvoiceDto toAggregate() {
        return new ManageInvoiceDto(
                id,
                invoiceId,
                invoiceNo,
                invoiceNumber,
                invoiceType,
                invoiceAmount,
                bookings != null ? bookings.stream().map(b -> {
                            return b.toAggregateSimple();
                        }).collect(Collectors.toList()) : null,
                hasAttachment
        );
    }

}
