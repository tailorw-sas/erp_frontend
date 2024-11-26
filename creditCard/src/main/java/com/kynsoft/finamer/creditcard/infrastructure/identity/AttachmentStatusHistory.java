package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.AttachmentStatusHistoryDto;
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
@Table(name = "attachment_status_history")
public class AttachmentStatusHistory implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private String description;

    private Long attachmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.EAGER)
    private HotelPayment hotelPayment;

    private String employee;

    private UUID employeeId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public AttachmentStatusHistory(AttachmentStatusHistoryDto dto) {
        this.id = dto.getId();
        this.description = dto.getDescription();
        this.attachmentId = dto.getAttachmentId();
        this.transaction = dto.getTransaction() != null ? new Transaction(dto.getTransaction()) : null;
        this.employee = dto.getEmployee();
        this.employeeId = dto.getEmployeeId();
        this.hotelPayment = dto.getHotelPayment() != null ? new HotelPayment(dto.getHotelPayment()) : null;
    }

    public AttachmentStatusHistoryDto toAggregate(){
        return  new AttachmentStatusHistoryDto(
                id,description, attachmentId,
                transaction != null ? transaction.toAggregate() : null,
                employee, employeeId, createdAt, updatedAt,
                hotelPayment != null ? hotelPayment.toAggregate() : null
        );
    }
}
