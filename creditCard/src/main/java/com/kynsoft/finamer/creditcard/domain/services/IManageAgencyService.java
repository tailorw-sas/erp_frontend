package com.kynsoft.finamer.creditcard.domain.services;


import com.kynsoft.finamer.creditcard.domain.dto.ManageAgencyDto;

import java.util.List;
import java.util.UUID;

public interface IManageAgencyService {

    UUID create(ManageAgencyDto dto);

    void update(ManageAgencyDto dto);

    void delete(UUID id);

    ManageAgencyDto findById(UUID id);

    List<ManageAgencyDto> findByIds(List<UUID> ids);

    List<ManageAgencyDto> findAll();
}
