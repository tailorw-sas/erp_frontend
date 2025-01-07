package com.kynsoft.finamer.insis.application.query.importProcess.getErrorResults;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class GetErrorResultsImportProcessQuery implements IQuery {
    private UUID importProcessId;
    private Pageable pageable;
}
