package com.kynsof.share.core.domain.kafka.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplicateManagerMerchantConfigKafka {
    private UUID id;
    private UUID manageMerchant;
    private String url;
    private String altUrl;
    private String successUrl;
    private String errorUrl;
    private String declinedUrl;
    private String merchantType;
    private String name;
    private String method;
    private String institutionCode;
    private String merchantNumber;
    private String merchantTerminal;


    public ReplicateManagerMerchantConfigKafka(UUID id, UUID manageMerchant, String url, String successUrl, String errorUrl, String declinedUrl, String merchantType, String name, String method, String institutionCode, String merchantNumber, String merchantTerminal) {
        this.id = id;
        this.manageMerchant = manageMerchant;
        this.url = url;
        this.successUrl = successUrl;
        this.errorUrl = errorUrl;
        this.declinedUrl = declinedUrl;
        this.merchantType = merchantType;
        this.name = name;
        this.method=method;
        this.institutionCode=institutionCode;
        this.merchantNumber = merchantNumber;
        this.merchantTerminal = merchantTerminal;

    }
}
