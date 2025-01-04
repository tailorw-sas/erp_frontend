package com.kynsof.share.core.domain.kafka.entity.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateManageB2BPartnerKafka implements Serializable {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String status;
    private UUID b2BPartnerTypeDto;
}
