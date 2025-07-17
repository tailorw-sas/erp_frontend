package com.tailorw.tcaInnsist.application.service.manageTradingCompany;

import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;
import com.tailorw.tcaInnsist.domain.services.IManageTradingCompanyService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SearchManageTradingCompanyService {

    private final IManageTradingCompanyService tradingCompanyService;

    public SearchManageTradingCompanyService(IManageTradingCompanyService tradingCompanyService){
        this.tradingCompanyService = tradingCompanyService;
    }

    public ManageTradingCompanyDto searchManageTradingCompany(ManageHotelDto hotelDto){
        ManageTradingCompanyDto tradingCompany = tradingCompanyService.getById(hotelDto.getTradingCompanyId());

        if(Objects.isNull(tradingCompany)){
            throw new IllegalArgumentException(String.format("The trading company %s does not exist in the TcaInnsist database.", hotelDto.getTradingCompanyId()));
        }

        if(Objects.isNull(tradingCompany.getConnectionId()) ){
            throw new IllegalArgumentException(String.format("The trading company %s does not have a valid connection.", tradingCompany.getCode()));
        }

        return tradingCompany;

    }
}
