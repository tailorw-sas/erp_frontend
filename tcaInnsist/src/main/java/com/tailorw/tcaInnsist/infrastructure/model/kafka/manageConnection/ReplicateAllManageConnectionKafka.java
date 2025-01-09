package com.tailorw.tcaInnsist.infrastructure.model.kafka.manageConnection;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ReplicateAllManageConnectionKafka implements Serializable {
    List<ReplicateManageConnectionKafka> configurationPropertiesKafkaList;
}
