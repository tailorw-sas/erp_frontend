package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ParameterizationDto;

import java.util.UUID;

public interface IParameterizationService {

    UUID create(ParameterizationDto dto);

    void delete(ParameterizationDto dto);

    ParameterizationDto findActiveParameterization();

    ParameterizationDto findById(UUID id);
}
