package com.kynsoft.finamer.invoicing.infrastructure.identity;

import com.kynsoft.finamer.invoicing.domain.dto.IncomeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "income")
public class Income implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(columnDefinition = "serial", name = "adjustment_gen_id")
    @Generated(event = EventType.INSERT)
    private Long incomeId;

    @Column(columnDefinition = "serial", name = "invoice_number_gen_id")
    @Generated(event = EventType.INSERT)
    private Long invoiceNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private Boolean manual;
    private Boolean reSend;
    private LocalDate reSendDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agency_id")
    private ManageAgency agency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel hotel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_type_id")
    private ManageInvoiceType invoiceType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_status_id")
    private ManageInvoiceStatus invoiceStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manage_booking")
    private ManageBooking booking;

    private Double incomeAmount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public Income(IncomeDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.invoiceDate = dto.getInvoiceDate();
        this.manual = dto.getManual();
        this.agency = dto.getAgency() != null ? new ManageAgency(dto.getAgency()) : null;
        this.hotel = dto.getHotel() != null ? new ManageHotel(dto.getHotel()) : null;
        this.invoiceType = dto.getInvoiceType() != null ? new ManageInvoiceType(dto.getInvoiceType()) : null;
        this.invoiceStatus = dto.getInvoiceStatus() != null ? new ManageInvoiceStatus(dto.getInvoiceStatus()) : null;
        this.booking = dto.getBooking() != null ? new ManageBooking(dto.getBooking()) : null;
        this.incomeAmount = dto.getIncomeAmount();
        this.invoiceNumber = dto.getInvoiceNumber();
        this.dueDate = dto.getDueDate();
        this.reSend = dto.getReSend();
        this.reSendDate = dto.getReSendDate();
    }

    public IncomeDto toAggregate() {
        return new IncomeDto(
                id, 
                incomeId, 
                invoiceNumber != null ? invoiceNumber : null,
                status, 
                invoiceDate, 
                dueDate != null ? dueDate : null,
                manual, 
                reSend != null ? reSend : null,
                reSendDate != null ? reSendDate : null,
                agency != null ? agency.toAggregate() : null, 
                hotel != null ? hotel.toAggregate() : null, 
                invoiceType != null ? invoiceType.toAggregate() : null, 
                invoiceStatus != null ? invoiceStatus.toAggregate() : null, 
                incomeAmount,
                booking != null ? booking.toAggregate() : null
        );
    }
}
