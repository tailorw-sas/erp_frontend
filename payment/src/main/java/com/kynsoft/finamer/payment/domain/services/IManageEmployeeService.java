package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;

import java.util.UUID;

public interface IManageEmployeeService {
    UUID create(ManageEmployeeDto dto);

    void update(ManageEmployeeDto dto);

    void delete(ManageEmployeeDto dto);

    ManageEmployeeDto findById(UUID id);
}
