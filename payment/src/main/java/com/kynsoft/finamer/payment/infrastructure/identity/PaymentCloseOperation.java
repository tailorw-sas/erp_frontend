package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment_close_operation")
public class PaymentCloseOperation implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_id")
    private ManageHotel hotel;

    private LocalDate beginDate;
    private LocalDate endDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public PaymentCloseOperation(PaymentCloseOperationDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.hotel = dto.getHotel() != null ? new ManageHotel(dto.getHotel()) : null;
        this.beginDate = dto.getBeginDate() != null ? dto.getBeginDate() : null;
        this.endDate = dto.getEndDate() != null ? dto.getEndDate() : null;
    }

    public PaymentCloseOperationDto toAggregate() {
        return new PaymentCloseOperationDto(
                id, 
                status, 
                hotel != null ? hotel.toAggregate() : null, 
                beginDate, 
                endDate
        );
    }
}
