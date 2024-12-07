package com.kynsof.share.core.domain.kafka.entity.importInnsist;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ImportInnsistKafka implements Serializable {
    private UUID importInnsitProcessId;
    private String employee;// Esviarme el nombre del employee
    private List<ImportInnsistBookingKafka> importList;
}
