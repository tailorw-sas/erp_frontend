package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;

import java.util.UUID;

public interface IManageMerchantService {

    UUID create(ManageMerchantDto dto);

    void update(ManageMerchantDto dto);

    void delete(ManageMerchantDto dto);

    ManageMerchantDto findById(UUID id);
}
