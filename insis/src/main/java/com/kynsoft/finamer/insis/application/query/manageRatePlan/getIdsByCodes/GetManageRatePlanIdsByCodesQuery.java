package com.kynsoft.finamer.insis.application.query.manageRatePlan.getIdsByCodes;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class GetManageRatePlanIdsByCodesQuery implements IQuery {

    private UUID hotel;
    private List<String> codes;
}
