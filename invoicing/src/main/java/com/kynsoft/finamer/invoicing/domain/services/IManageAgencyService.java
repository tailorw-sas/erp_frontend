package com.kynsoft.finamer.invoicing.domain.services;


import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;

import java.util.List;
import java.util.UUID;

public interface IManageAgencyService {

    UUID create(ManageAgencyDto dto);

    void update(ManageAgencyDto dto);

    void delete(ManageAgencyDto dto);

    ManageAgencyDto findById(UUID id);


    Long countByCodeAndNotId(String code, UUID id);

    List<ManageAgencyDto> findByIds(List<UUID> ids);

    boolean existByCode(String manageHotelCode);
}
