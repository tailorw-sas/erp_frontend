package com.kynsoft.finamer.creditcard.application.query.managerMerchant.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

@Component
public class FindManagerMerchantByIdQueryHandler implements IQueryHandler<FindManagerMerchantByIdQuery, ManageMerchantResponse> {

    private final IManageMerchantService service;
    private final IManageMerchantConfigService configService;
    private final ITransactionService transactionService;

    public FindManagerMerchantByIdQueryHandler(IManageMerchantService service, IManageMerchantConfigService configService, ITransactionService transactionService) {
        this.service = service;
        this.configService = configService;
        this.transactionService = transactionService;
    }

    @Override
    public ManageMerchantResponse handle(FindManagerMerchantByIdQuery query) {
        ManageMerchantDto response = service.findById(query.getId());
        ManagerMerchantConfigDto merchantConfigDto = configService.findByMerchantID(response.getId());
        TransactionDto transactionDto = transactionService.findByUuid(query.getTransactionUuid());
        ManageMerchantResponse manageMerchantResponse = new ManageMerchantResponse(response);
        manageMerchantResponse.setTransactionDtoResponse(transactionDto);
        manageMerchantResponse.setMerchantConfigResponse(merchantConfigDto);
        return manageMerchantResponse;
    }
}
