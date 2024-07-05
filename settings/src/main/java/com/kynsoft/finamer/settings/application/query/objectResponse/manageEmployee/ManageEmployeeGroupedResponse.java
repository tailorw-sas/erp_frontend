package com.kynsoft.finamer.settings.application.query.objectResponse.manageEmployee;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeGroupedResponse implements IResponse {
    private UUID employeeId;
    private List<ManageEmployeeCountryClientAgencyResponse> agencies;
    private List<ManageEmployeeCountryHotelResponse> hotels;
}
