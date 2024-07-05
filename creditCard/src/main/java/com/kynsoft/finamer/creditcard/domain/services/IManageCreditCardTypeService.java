package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import java.util.UUID;

public interface IManageCreditCardTypeService {
    UUID create(ManageCreditCardTypeDto dto);

    void update(ManageCreditCardTypeDto dto);

    void delete(ManageCreditCardTypeDto dto);

    ManageCreditCardTypeDto findById(UUID id);

}
