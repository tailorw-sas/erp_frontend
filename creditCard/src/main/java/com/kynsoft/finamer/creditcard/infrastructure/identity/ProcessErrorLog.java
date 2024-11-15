package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsof.audit.infrastructure.core.annotation.RemoteAudit;
import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsoft.finamer.creditcard.domain.dto.ProcessErrorLogDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity

@Table(name = "vcc_process_error_logs")
@EntityListeners(AuditEntityListener.class)
@RemoteAudit(name = "vcc_process_error_logs",id="7b2ea5e8-e34c-47eb-a811-25a54fe2c604")
public class ProcessErrorLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id")
    private UUID transactionId;

    private String session;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String error;

    public ProcessErrorLog(ProcessErrorLogDto dto) {
        this.id = dto.getId();
        this.transactionId = dto.getTransactionId();
        this.session = dto.getSession();
        this.error = dto.getError();

    }
    public ProcessErrorLogDto toAggregate(){
        return new ProcessErrorLogDto(
                id,transactionId, session, error
        );
    }

}
