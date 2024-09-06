package com.kynsoft.finamer.invoicing.application.query.resourceType;

import com.kynsof.share.core.domain.bus.query.IResponse;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
@Getter
@Builder
public class GetSearchResourceTypeResponse implements IResponse {

    private UUID id;
    private String code;
    private String name;
}
