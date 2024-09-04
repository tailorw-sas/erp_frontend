package com.kynsof.share.core.domain.kafka.entity;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentKafka implements Serializable {

    private UUID id;
    private UUID employee;
    private String fileName;
    private String fileWeight;
    private String path;
    private String remark;
}
