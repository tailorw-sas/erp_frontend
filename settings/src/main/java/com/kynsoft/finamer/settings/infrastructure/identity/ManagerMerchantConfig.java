package com.kynsoft.finamer.settings.infrastructure.identity;

import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.settings.domain.dto.me.ManagerMerchantConfigResponseDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Method;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "manager_merchant_config")
public class ManagerMerchantConfig implements Serializable {

    @Id
    @Column(name = "id") // Cambia este campo si el identificador real es otro
    private UUID uuid;

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

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;



    public ManagerMerchantConfig(ManagerMerchantConfigDto dto) {
        this.uuid = dto.getUuid();
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
    }
    public ManagerMerchantConfig(ManagerMerchantConfigResponseDto dto) {
        this.uuid = dto.getUuid();
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
    }

    public ManagerMerchantConfigDto toAggregate() {
        return new ManagerMerchantConfigDto(uuid,managerMerchant != null ? managerMerchant.toAggregate() : null,url, altUrl,successUrl,errorUrl,declinedUrl,merchantType,name,method,institutionCode);
    }
    public ManagerMerchantConfigResponseDto toAggregateWithDate() {
        return new ManagerMerchantConfigResponseDto(uuid,managerMerchant != null ? managerMerchant.toAggregate() : null,url, altUrl,successUrl,errorUrl,declinedUrl,merchantType,name,method,institutionCode,createdAt,updatedAt);
    }
}
