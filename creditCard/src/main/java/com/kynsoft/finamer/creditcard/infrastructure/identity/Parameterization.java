package com.kynsoft.finamer.creditcard.infrastructure.identity;

import com.kynsoft.finamer.creditcard.domain.dto.ParameterizationDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "parameterization")
public class Parameterization implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    private Boolean isActive;

    @Column(name = "transaction_status_code")
    private String transactionStatusCode;

    @Column(name = "transaction_category")
    private String transactionCategory;

    @Column(name = "transaction_sub_category")
    private String transactionSubCategory;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Parameterization(ParameterizationDto dto){
        this.id = dto.getId();
        this.isActive = dto.getIsActive();
        this.transactionStatusCode = dto.getTransactionStatusCode();
        this.transactionCategory = dto.getTransactionCategory();
        this.transactionSubCategory = dto.getTransactionSubCategory();
    }

    public ParameterizationDto toAggregate(){
        return new ParameterizationDto(
                id, isActive, transactionStatusCode, transactionCategory, transactionSubCategory
        );
    }

}
