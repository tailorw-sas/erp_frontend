package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
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
    private LocalDateTime invoiceDate;

    @Column(columnDefinition = "boolean DEFAULT FALSE")
    private Boolean hasAttachment;

    @ManyToOne(fetch = FetchType.EAGER)
    private ManageInvoice parent;

    @Enumerated(EnumType.STRING)
    private EInvoiceType invoiceType;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<ManageBooking> bookings;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_hotel")
    private ManageHotel hotel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_agency")
    private ManageAgency agency;

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
        this.parent = dto.getParent() != null ? new ManageInvoice(dto.getParent()) : null;
        this.invoiceDate = dto.getInvoiceDate();
        this.hotel = dto.getHotel() != null ? new ManageHotel(dto.getHotel()) : null;
        this.agency = dto.getAgency() != null ? new ManageAgency(dto.getAgency()) : null;
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
                hasAttachment,
                null,
                invoiceDate,
                Objects.nonNull(hotel)? hotel.toAggregate():null,
               Objects.nonNull(agency)? agency.toAggregate():null

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
                hasAttachment,
                parent != null ? parent.toAggregateSample() : null,
                invoiceDate,
                Objects.nonNull(hotel)? hotel.toAggregate():null,
                Objects.nonNull(agency)? agency.toAggregate():null
        );
    }

}
