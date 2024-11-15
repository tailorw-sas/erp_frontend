package com.kynsoft.finamer.payment.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentShareFileDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_share_file")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "payment_share_file",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class PaymentShareFile implements Serializable {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    public PaymentShareFile(PaymentShareFileDto dto) {
        this.id = dto.getId();
        this.fileName = dto.getFileName();
        this.fileUrl = dto.getFileUrl();
        this.payment = new Payment(dto.getPayment());
    }

    public PaymentShareFileDto toAggregate(){
        PaymentShareFileDto paymentShareFileDto = new PaymentShareFileDto(this.id,this.payment.toAggregate(), this.fileName, this.fileUrl);
        paymentShareFileDto.setShareFileYear(createdAt.getYear());  // Obtener el año
        paymentShareFileDto.setShareFileMonth(createdAt.getMonthValue());
        return paymentShareFileDto;
    }

}