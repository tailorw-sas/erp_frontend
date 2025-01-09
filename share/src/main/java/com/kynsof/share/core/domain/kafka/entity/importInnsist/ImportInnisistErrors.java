package com.kynsof.share.core.domain.kafka.entity.importInnsist;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ImportInnisistErrors {
    private UUID importInnsitProcessId;
    private List<Errors> errors;
}
