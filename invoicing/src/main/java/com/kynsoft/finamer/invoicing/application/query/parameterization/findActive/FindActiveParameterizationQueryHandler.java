package com.kynsoft.finamer.invoicing.application.query.parameterization.findActive;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ParameterizationResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ParameterizationDto;
import com.kynsoft.finamer.invoicing.domain.services.IParameterizationService;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FindActiveParameterizationQueryHandler implements IQueryHandler<FindActiveParameterizationQuery, ParameterizationResponse> {

    private final IParameterizationService service;

    public FindActiveParameterizationQueryHandler(IParameterizationService service) {
        this.service = service;
    }

    @Override
    public ParameterizationResponse handle(FindActiveParameterizationQuery query) {
        ParameterizationDto dto = this.service.findActiveParameterization();
        if(Objects.isNull(dto)){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "No active parameterization")));
        }
        return new ParameterizationResponse(dto);
    }
}
