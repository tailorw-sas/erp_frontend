package com.kynsoft.finamer.audit.application.query.configuration.getall;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@AllArgsConstructor
public class GetAllConfigurationQuery implements IQuery {
    private Pageable pageable;
}
