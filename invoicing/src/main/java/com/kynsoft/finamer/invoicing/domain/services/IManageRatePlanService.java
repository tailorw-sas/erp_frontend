package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;

import java.util.UUID;

public interface IManageRatePlanService {

    UUID create(ManageRatePlanDto dto);

    void update(ManageRatePlanDto dto);

    void delete(ManageRatePlanDto dto);

    ManageRatePlanDto findById(UUID id);



    Long countByCodeAndNotId(String code, UUID id);
}
