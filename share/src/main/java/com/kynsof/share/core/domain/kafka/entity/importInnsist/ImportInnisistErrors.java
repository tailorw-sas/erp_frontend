package com.kynsof.share.core.domain.kafka.entity.importInnsist;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ImportInnisistErrors {
    private UUID importInnsitProcessId;
    private List<Errors> errors;
}
