package com.kynsof.share.core.domain.kafka.entity.vcc;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplicateManagerMerchantConfig {
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

}
