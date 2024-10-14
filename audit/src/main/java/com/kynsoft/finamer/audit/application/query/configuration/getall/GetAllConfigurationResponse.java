package com.kynsoft.finamer.audit.application.query.configuration.getall;


import com.kynsoft.finamer.audit.domain.bus.query.IResponse;
import com.kynsoft.finamer.audit.domain.response.PaginatedResponse;

public record GetAllConfigurationResponse(PaginatedResponse paginatedResponse) implements IResponse {
}
