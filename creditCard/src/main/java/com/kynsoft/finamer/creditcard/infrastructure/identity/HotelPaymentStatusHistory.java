package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.HotelPaymentStatusHistoryDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hotel_payment_status_history")
public class HotelPaymentStatusHistory implements Serializable {

    @Id
    private UUID id;

    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private String employee;

    @ManyToOne(fetch = FetchType.EAGER)
    private HotelPayment hotelPayment;

    @ManyToOne(fetch = FetchType.EAGER)
    private ManagePaymentTransactionStatus status;

    public HotelPaymentStatusHistory(HotelPaymentStatusHistoryDto dto){
        this.id = dto.getId();
        this.description = dto.getDescription();
        this.createdAt = dto.getCreatedAt();
        this.employee = dto.getEmployee();
        this.hotelPayment = dto.getHotelPayment() != null ? new HotelPayment(dto.getHotelPayment()) : null;
        this.status = dto.getStatus() != null ? new ManagePaymentTransactionStatus(dto.getStatus()) : null;
    }

    public HotelPaymentStatusHistoryDto toAggregate(){
        return new HotelPaymentStatusHistoryDto(
                id, description, createdAt, employee,
                hotelPayment != null ? hotelPayment.toAggregateSimple() : null,
                status != null ? status.toAggregateSimple() : null
        );
    }
}
