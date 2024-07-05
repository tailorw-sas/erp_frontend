package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import java.util.UUID;

public interface IManagePaymentTransactionTypeService {

    UUID create(ManagePaymentTransactionTypeDto dto);

    void update(ManagePaymentTransactionTypeDto dto);

    void delete(ManagePaymentTransactionTypeDto dto);

    ManagePaymentTransactionTypeDto findById(UUID id);

}
