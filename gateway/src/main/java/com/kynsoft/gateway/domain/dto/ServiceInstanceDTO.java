package com.kynsoft.gateway.domain.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
@Builder
public class ServiceInstanceDTO {
    String serviceId;
    String url;
}