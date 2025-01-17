package com.kynsoft.finamer.payment.domain.rules.payment;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;

public class PaymentValidateBankAccountAndHotelRule extends BusinessRule {

    private final ManageHotelDto hotelDto;
    private final ManageBankAccountDto bankAccountDto;

    public PaymentValidateBankAccountAndHotelRule(ManageHotelDto hotelDto, ManageBankAccountDto bankAccountDto) {
        super(
                DomainErrorMessage.HOTEL_BANK_ACCOUNT_NOT_FOUND, 
                new ErrorField("accountNumber", DomainErrorMessage.HOTEL_BANK_ACCOUNT_NOT_FOUND.getReasonPhrase())
        );
        this.hotelDto = hotelDto;
        this.bankAccountDto = bankAccountDto;
    }

    @Override
    public boolean isBroken() {
        return !hotelDto.getCode().equals(bankAccountDto.getManageHotelDto().getCode());
    }

}
