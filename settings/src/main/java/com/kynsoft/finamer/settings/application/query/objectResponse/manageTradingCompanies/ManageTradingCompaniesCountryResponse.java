package com.kynsoft.finamer.settings.application.query.objectResponse.manageTradingCompanies;

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
public class ManageTradingCompaniesCountryResponse implements IResponse {

    private String country;
    private List<ManageTradingCompaniesBasicResponse> tradingCompanies;
}
