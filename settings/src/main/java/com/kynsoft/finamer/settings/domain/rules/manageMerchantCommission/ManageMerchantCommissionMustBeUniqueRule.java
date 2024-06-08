package com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantCommissionService;
import java.time.LocalDate;

import java.util.UUID;

public class ManageMerchantCommissionMustBeUniqueRule extends BusinessRule {

    private final IManageMerchantCommissionService service;

    private final UUID id;

    private final UUID managerMerchant;

    private final UUID manageCreditCartType;

    private final LocalDate fromCheckDate; 

    private final LocalDate toCheckDate;

    public ManageMerchantCommissionMustBeUniqueRule(IManageMerchantCommissionService service, UUID id, UUID managerMerchant, UUID manageCreditCartType, LocalDate fromCheckDate, LocalDate toCheckDate) {
        super(
                DomainErrorMessage.MANAGER_MERCHANT_CURRENCY_MUST_BY_UNIQUE, 
                new ErrorField("id", "Data entered overlaps with others, please check")
        );
        this.service = service;
        this.id = id;
        this.managerMerchant = managerMerchant;
        this.manageCreditCartType = manageCreditCartType;
        this.fromCheckDate = fromCheckDate;
        this.toCheckDate = toCheckDate;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByManagerMerchantANDManagerCreditCartTypeANDDateRange(id, managerMerchant, manageCreditCartType, fromCheckDate, toCheckDate) > 0;
    }

}
