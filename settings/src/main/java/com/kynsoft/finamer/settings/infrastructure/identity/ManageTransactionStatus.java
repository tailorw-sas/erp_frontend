package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Navigate;
import com.kynsoft.finamer.settings.domain.dtoEnum.NavigateTransactionStatus;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manage_transaction_status")
public class ManageTransactionStatus implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;
    @Column(unique = true)
    private String code;

    private String name;
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "navigate_transaction_status", joinColumns = @JoinColumn(name = "navigation_id"))
    @Column(name = "navigate_transaction_status")
    private Set<NavigateTransactionStatus> navigate;

    @Column(nullable = true)
    private Boolean deleted = false;
    private Boolean enablePayment;
    private Boolean visible;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime deleteAt;

    public ManageTransactionStatus(ManageTransactionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.navigate = dto.getNavigate();
        this.status = dto.getStatus();
        this.enablePayment = dto.getEnablePayment();
        this.visible = dto.getVisible();
    }

    public ManageTransactionStatusDto toAggregate() {
        return new ManageTransactionStatusDto(id, code, name, description, navigate, enablePayment, visible, status);
    }

}