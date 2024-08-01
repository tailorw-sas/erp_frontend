package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;

import java.util.List;
import java.util.UUID;

public interface IManageAgencyService {

    UUID create(ManageAgencyDto dto);

    void update(ManageAgencyDto dto);

    void delete(ManageAgencyDto dto);

    ManageAgencyDto findById(UUID id);

    List<ManageAgencyDto> findByIds(List<UUID> ids);

    boolean existByCode(String agencyCode);


    ManageAgencyDto findByCode(String agencyCode);
}
