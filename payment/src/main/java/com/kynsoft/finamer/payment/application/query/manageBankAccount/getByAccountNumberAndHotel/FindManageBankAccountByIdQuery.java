package com.kynsoft.finamer.payment.application.query.manageBankAccount.getByAccountNumberAndHotel;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class FindManageBankAccountByIdQuery  implements IQuery {

    private final String accountNumber;
    private final String hotelCode;

    public FindManageBankAccountByIdQuery(String accountNumber, String hotelCode) {
        this.accountNumber = accountNumber;
        this.hotelCode = hotelCode;
    }

}
