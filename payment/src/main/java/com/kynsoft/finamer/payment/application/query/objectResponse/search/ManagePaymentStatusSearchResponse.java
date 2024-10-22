package com.kynsoft.finamer.payment.application.query.objectResponse.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManagePaymentStatusSearchResponse implements IResponse {
    private UUID id;
    private String code;
    private String name;
    private boolean confirmed;
    private Boolean applied;
    private boolean cancelled;
    private boolean transit;

    public ManagePaymentStatusSearchResponse(ManagePaymentStatusDto dto) {
        this.id = dto.getId();
        this.code = dto.getCode();
        this.name = dto.getName();
        this.confirmed = dto.isConfirmed();
        this.applied = dto.getApplied();
        this.cancelled = dto.isCancelled();
        this.transit = dto.isTransit();
    }

}
