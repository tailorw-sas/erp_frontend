package com.tailorw.tcaInnsist.application.service.configuration;

import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;
import com.tailorw.tcaInnsist.domain.services.IManageConnectionService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SearchConfigurationService {

    private final IManageConnectionService configurationService;

    public SearchConfigurationService(IManageConnectionService configurationService){
        this.configurationService = configurationService;
    }

    public ManageConnectionDto searchConnection(ManageTradingCompanyDto tradingCompanyDto){
        ManageConnectionDto connection = configurationService.getById(tradingCompanyDto.getConnectionId());
        if(Objects.isNull(connection)){
            throw new IllegalArgumentException(String.format("The Connection ID %s does not exist in the TcaInnsist database.", tradingCompanyDto.getConnectionId()));
        }
        return connection;
    }
}
