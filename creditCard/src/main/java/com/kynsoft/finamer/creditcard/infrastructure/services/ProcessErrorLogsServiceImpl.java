package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsoft.finamer.creditcard.domain.dto.ProcessErrorLogDto;
import com.kynsoft.finamer.creditcard.domain.services.IProcessErrorLogService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ProcessErrorLog;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ProcessErrorLogWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ProcessErrorLogReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessErrorLogsServiceImpl implements IProcessErrorLogService {

    @Autowired
    private final ProcessErrorLogWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ProcessErrorLogReadDataJPARepository repositoryQuery;

    public ProcessErrorLogsServiceImpl(ProcessErrorLogWriteDataJPARepository repositoryCommand, ProcessErrorLogReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public ProcessErrorLogDto create(ProcessErrorLogDto dto) {
        ProcessErrorLog processErrorLog = new ProcessErrorLog(dto);
        return this.repositoryCommand.save(processErrorLog).toAggregate();
    }

}
