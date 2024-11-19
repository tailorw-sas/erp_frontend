package com.kynsoft.finamer.creditcard.application.query.resourceType.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
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
