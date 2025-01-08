package com.tailorw.tcaInnsist.infrastructure.model.kafka.manageConnection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteManageConnectionKafka {
    private UUID id;
}
