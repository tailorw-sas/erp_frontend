package com.kynsoft.finamer.audit.application.query.configuration.search;


import com.kynsoft.finamer.audit.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@AllArgsConstructor
public class SearchConfigurationResponse implements IResponse {
    private Pageable pageable;
}
