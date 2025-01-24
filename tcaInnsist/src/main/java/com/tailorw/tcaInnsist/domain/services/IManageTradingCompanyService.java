package com.tailorw.tcaInnsist.domain.services;

import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;

import java.util.List;
import java.util.UUID;

public interface IManageTradingCompanyService {

    UUID create(ManageTradingCompanyDto dto);

    void update(ManageTradingCompanyDto dto);

    void delete(UUID id);

    void createMany(List<ManageTradingCompanyDto> list);

    ManageTradingCompanyDto getById(UUID id);

    boolean exists();
}
