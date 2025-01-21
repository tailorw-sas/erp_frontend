package com.kynsoft.finamer.insis.application.query.objectResponse.manageRoomType;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ManageRoomTypeIdsResponse implements IResponse {

    private Map<String, UUID> ids;
}
