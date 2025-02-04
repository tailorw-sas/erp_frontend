package com.kynsoft.finamer.insis.application.query.objectResponse.manageAgency;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManageAgencyIdsResponse implements IResponse {
    private Map<String, UUID> ids;
}
