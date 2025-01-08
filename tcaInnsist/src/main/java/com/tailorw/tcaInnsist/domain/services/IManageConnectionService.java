package com.tailorw.tcaInnsist.domain.services;

import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;

import java.util.List;
import java.util.UUID;

public interface IManageConnectionService {

    UUID create(ManageConnectionDto dto);

    void update(ManageConnectionDto dto);

    void delete(UUID id);

    void createMany(List<ManageConnectionDto> dtoList);

    ManageConnectionDto getById(UUID id);

    boolean exists();
}
