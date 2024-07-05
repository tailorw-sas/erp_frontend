package com.kynsoft.finamer.settings.domain.rules.manageMechantHotelEnrolle;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantHotelEnrolleService;

import java.util.UUID;

public class ManagerMerchantHotelEnrolleMustBeUniqueByIdRule extends BusinessRule {

    private final IManageMerchantHotelEnrolleService service;

    private final UUID managerMerchant;

    private final UUID managerCurrency;

    private final UUID managerHotel;

    private final String enrrolle;

    private final UUID id;

    public ManagerMerchantHotelEnrolleMustBeUniqueByIdRule(IManageMerchantHotelEnrolleService service, UUID managerMerchant, UUID managerCurrency, UUID managerHotel, String enrrolle, UUID id) {
        super(
                DomainErrorMessage.ITEM_ALREADY_EXITS, 
                new ErrorField("id", "Item already exists.")
        );
        this.service = service;
        this.managerMerchant = managerMerchant;
        this.managerCurrency = managerCurrency;
        this.managerHotel = managerHotel;
        this.enrrolle = enrrolle;
        
        this.id = id;
    }

    @Override
    public boolean isBroken() {
        return this.service.countByManageMerchantAndManageHotelNotId(id, managerMerchant,managerHotel) > 0;
    }

}
