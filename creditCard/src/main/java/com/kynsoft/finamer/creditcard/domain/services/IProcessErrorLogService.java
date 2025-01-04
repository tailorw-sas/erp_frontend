package com.kynsoft.finamer.creditcard.domain.services;

import com.kynsoft.finamer.creditcard.domain.dto.ProcessErrorLogDto;


public interface IProcessErrorLogService {

    ProcessErrorLogDto create(ProcessErrorLogDto dto);

}
