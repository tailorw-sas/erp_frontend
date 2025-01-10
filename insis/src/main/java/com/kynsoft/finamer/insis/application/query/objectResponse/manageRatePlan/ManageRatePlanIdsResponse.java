package com.kynsoft.finamer.insis.application.query.objectResponse.manageRatePlan;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManageRatePlanIdsResponse implements IResponse {
    private Map<String, UUID> ids;
}
