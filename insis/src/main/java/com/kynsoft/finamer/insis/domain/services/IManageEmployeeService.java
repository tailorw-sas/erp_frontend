package com.kynsoft.finamer.insis.domain.services;

import com.kynsoft.finamer.insis.domain.dto.ManageEmployeeDto;

import java.util.UUID;

public interface IManageEmployeeService {

    UUID create(ManageEmployeeDto dto);

    void update(ManageEmployeeDto dto);

    ManageEmployeeDto findById(UUID id);

}
