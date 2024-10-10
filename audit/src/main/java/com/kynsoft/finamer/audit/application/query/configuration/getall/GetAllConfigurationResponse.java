package com.kynsoft.finamer.audit.application.query.configuration.getall;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;


public record GetAllConfigurationResponse(PaginatedResponse paginatedResponse) implements IResponse {
}
