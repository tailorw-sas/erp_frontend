package com.kynsoft.finamer.settings.application.query.managerMerchantConfig.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerMerchantConfigResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantConfigResponseDto;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantConfigService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerMerchantConfigByIdQueryHandler implements IQueryHandler<FindManagerMerchantConfigByIdQuery, ManagerMerchantConfigResponse> {

    private final IManageMerchantConfigService service;

    public FindManagerMerchantConfigByIdQueryHandler(IManageMerchantConfigService service) {
        this.service = service;
    }

    @Override
    public ManagerMerchantConfigResponse handle(FindManagerMerchantConfigByIdQuery query) {
        ManagerMerchantConfigResponseDto response = service.findByIdWithDate(query.getId());
        return new ManagerMerchantConfigResponse(response);
    }
}
