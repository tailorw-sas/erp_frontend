package com.kynsof.share.core.domain.kafka.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalConsultationByBusiness {
    private UUID businessID;
    private Long cantExternalConsultation;
}
