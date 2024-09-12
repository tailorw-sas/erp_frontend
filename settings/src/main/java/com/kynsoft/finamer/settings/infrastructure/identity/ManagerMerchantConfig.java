package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantConfigResponseDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Method;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "manager_merchant_config")
public class ManagerMerchantConfig implements Serializable {

    @Id
    @Column(name = "id") // Cambia este campo si el identificador real es otro
    private UUID id;

    @OneToOne
    @JoinColumn(name = "manager_merchat_id", referencedColumnName = "id") // Propietario de la relación
    private ManagerMerchant managerMerchant;

    @Column(name = "url")
    private String url;

    @Column(name = "alt_url")
    private String altUrl;

    @Column(name = "success_url")
    private String successUrl;

    @Column(name = "error_url")
    private String errorUrl;

    @Column(name = "declined_url")
    private String declinedUrl;

    @Column(name = "merchant_type")
    private String merchantType;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "method")
    private Method method;

    @Column(name = "institution_code")
    private String institutionCode;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true, updatable = true)
    private LocalDateTime updatedAt;

    @Column(name = "merchant_number")
    private String merchantNumber;

    @Column(name = "merchant_terminal")
    private String merchantTerminal;

    public ManagerMerchantConfig(ManagerMerchantConfigDto dto) {
        this.id = dto.getId();
        this.managerMerchant = new ManagerMerchant(dto.getManagerMerchantDto());
        this.url = dto.getUrl();
        this.altUrl = dto.getAltUrl();
        this.successUrl = dto.getSuccessUrl();
        this.errorUrl = dto.getErrorUrl();
        this.declinedUrl = dto.getDeclinedUrl();
        this.merchantType = dto.getMerchantType();
        this.name = dto.getName();
        this.method = dto.getMethod();
        this.institutionCode = dto.getInstitutionCode();
        this.merchantNumber = dto.getMerchantNumber();
        this.merchantTerminal = dto.getMerchantTerminal();
    }

    public ManagerMerchantConfig(ManagerMerchantConfigResponseDto dto) {
        this.id = dto.getId();
        this.managerMerchant = new ManagerMerchant(dto.getManagerMerchantDto());
        this.url = dto.getUrl();
        this.altUrl = dto.getAltUrl();
        this.successUrl = dto.getSuccessUrl();
        this.errorUrl = dto.getErrorUrl();
        this.declinedUrl = dto.getDeclinedUrl();
        this.merchantType = dto.getMerchantType();
        this.name = dto.getName();
        this.method = dto.getMethod();
        this.institutionCode = dto.getInstitutionCode();
        this.createdAt = dto.getCreatedAt();
        this.updatedAt = dto.getUpdatedAt();
        this.merchantNumber = dto.getMerchantNumber();
        this.merchantTerminal = dto.getMerchantTerminal();
    }

    public ManagerMerchantConfigDto toAggregate() {
        return new ManagerMerchantConfigDto(
                id, 
                managerMerchant != null ? managerMerchant.toAggregate() : null, 
                url, 
                altUrl, 
                successUrl, 
                errorUrl, 
                declinedUrl, 
                merchantType, 
                name, 
                method, 
                institutionCode,
                merchantNumber,
                merchantTerminal
        );
    }

    public ManagerMerchantConfigResponseDto toAggregateWithDate() {
        return new ManagerMerchantConfigResponseDto(
                id, 
                managerMerchant != null ? managerMerchant.toAggregate() : null, 
                url, 
                altUrl, 
                successUrl, 
                errorUrl, 
                declinedUrl, 
                merchantType, 
                name, 
                method, 
                institutionCode, 
                createdAt, 
                updatedAt,
                merchantNumber,
                merchantTerminal
        );
    }
}
