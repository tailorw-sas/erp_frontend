package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;

import java.util.UUID;

public interface IManageRatePlanService {

    UUID create(ManageRatePlanDto dto);

    void update(ManageRatePlanDto dto);

    void delete(ManageRatePlanDto dto);

    ManageRatePlanDto findById(UUID id);

    ManageRatePlanDto findByCode(String code);

    boolean existByCode(String code);

    Long countByCodeAndNotId(String code, UUID id);
}
