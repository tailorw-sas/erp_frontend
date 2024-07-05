package com.kynsoft.finamer.settings.application.query.objectResponse.manageHotelGroup;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManageHotelGroupedResponse implements IResponse {

    private List<ManageHotelCountryResponse> response;
}
