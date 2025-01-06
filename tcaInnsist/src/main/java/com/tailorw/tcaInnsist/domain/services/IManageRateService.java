package com.tailorw.tcaInnsist.domain.services;

import com.tailorw.tcaInnsist.domain.dto.ManageRateDto;

public interface IManageRateService {
    String create(ManageRateDto manageRateDto);

    ManageRateDto findById(String id);
}
