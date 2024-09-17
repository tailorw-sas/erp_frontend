package com.kynsoft.finamer.creditcard.application.query.managerMerchant.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerMerchantByIdQueryHandler implements IQueryHandler<FindManagerMerchantByIdQuery, ManageMerchantResponse>  {

    private final IManageMerchantService service;
    private final IManageMerchantConfigService configService;

    public FindManagerMerchantByIdQueryHandler(IManageMerchantService service, IManageMerchantConfigService configService) {
        this.service = service;
        this.configService = configService;
    }

    @Override
    public ManageMerchantResponse handle(FindManagerMerchantByIdQuery query) {
        ManageMerchantDto response = service.findById(query.getId());
        ManagerMerchantConfigDto merchantConfigDto = configService.findByMerchantID(response.getId());
        ManageMerchantResponse manageMerchantResponse= new ManageMerchantResponse(response);
        manageMerchantResponse.setMerchantConfigResponse(merchantConfigDto);
        return manageMerchantResponse;
    }
}
