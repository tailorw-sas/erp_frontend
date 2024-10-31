package com.kynsoft.finamer.settings.domain.rules.manageMerchantCommission;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantCommissionService;

import java.time.LocalDate;
import java.util.UUID;

public class ManageMerchantCommissionMustNotOverlapRule extends BusinessRule {

    private final IManageMerchantCommissionService service;
    private final UUID id;
    private final UUID managerMerchant;
    private final UUID manageCreditCartType;
    private final LocalDate fromDate;
    private final LocalDate toDate;
    private final Double commission;
    private final String calculationType;

    public ManageMerchantCommissionMustNotOverlapRule(IManageMerchantCommissionService service,UUID id, UUID managerMerchant, UUID manageCreditCartType, LocalDate fromDate, LocalDate toDate, Double commission, String calculationType) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS,
                new ErrorField("dateRange", "Item already exists.")
        );
        this.service = service;

        this.id = id;
        this.managerMerchant = managerMerchant;
        this.manageCreditCartType = manageCreditCartType;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.commission = commission;
        this.calculationType = calculationType;
    }

    @Override
    public boolean isBroken() {
        return this.service.hasOverlappingRecords(id, managerMerchant, manageCreditCartType, fromDate, toDate, commission, calculationType);
    }

}