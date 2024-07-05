package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "manage_transaction_status_relations",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id")
    )
    private List<ManageTransactionStatus> navigate = new ArrayList<>();

    private Boolean enablePayment;
    private Boolean visible;

    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updateAt;

    public ManageTransactionStatus(ManageTransactionStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.enablePayment = dto.getEnablePayment();
        this.visible = dto.getVisible();
        this.navigate = dto.getNavigate() != null ? dto.getNavigate().stream()
                    .map(ManageTransactionStatus::new)
                    .collect(Collectors.toList()) : null;
    }

    public ManageTransactionStatusDto toAggregate() {

        return new ManageTransactionStatusDto(id, code, name, description, navigate != null ?
                navigate.stream().map(ManageTransactionStatus::toAggregateSample).toList() : null, enablePayment, visible, status);
    }

    public ManageTransactionStatusDto toAggregateSample() {
        return new ManageTransactionStatusDto(id, code, name, description, null, enablePayment, visible, status);
    }

}