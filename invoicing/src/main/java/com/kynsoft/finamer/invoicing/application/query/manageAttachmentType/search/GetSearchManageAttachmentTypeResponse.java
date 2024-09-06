package com.kynsoft.finamer.invoicing.application.query.manageAttachmentType.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GetSearchManageAttachmentTypeResponse implements IResponse {
    private UUID id;

    private String code;

    private String name;

    private String status;

    private boolean defaults;
}
