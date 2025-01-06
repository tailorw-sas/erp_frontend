package com.kynsoft.finamer.insis.infrastructure.model.kafka;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ReplicateTcaConfigurations implements Serializable {
    List<ReplicateManageConnectionKafka> configurationPropertiesKafkaList;
}
