package com.kynsof.share.core.domain.kafka.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalConsultationByBusinessKafka {
    private List<ExternalConsultationByBusiness> externalConsultationByBusinesses;
}
