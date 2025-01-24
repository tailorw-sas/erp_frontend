package com.tailorw.finamer.scheduler.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.tailorw.finamer.scheduler.domain.dto.SchedulerLogDto;
import com.tailorw.finamer.scheduler.domain.services.ISchedulerLogService;
import com.tailorw.finamer.scheduler.infrastructure.model.SchedulerLog;
import com.tailorw.finamer.scheduler.infrastructure.model.enums.ProcessStatus;
import com.tailorw.finamer.scheduler.infrastructure.repository.command.SchedulerLogWriteDataJPARepository;
import com.tailorw.finamer.scheduler.infrastructure.repository.query.SchedulerLogReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SchedulerLogServiceImpl implements ISchedulerLogService {

    private final SchedulerLogWriteDataJPARepository writeRepository;
    private final SchedulerLogReadDataJPARepository readRepository;

    public SchedulerLogServiceImpl(SchedulerLogWriteDataJPARepository writeRepository,
                                   SchedulerLogReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(SchedulerLogDto dto) {
        SchedulerLog log = new SchedulerLog(dto);
        return writeRepository.save(log).getId();
    }

    @Override
    public void update(SchedulerLogDto dto) {
        SchedulerLog log = new SchedulerLog(dto);
        writeRepository.save(log);
    }

    @Override
    public SchedulerLogDto getById(UUID id) {
        Optional<SchedulerLog> log = readRepository.findById(id);
        if(log.isPresent()){
            return log.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Scheduler Log not found.")));
    }
}
