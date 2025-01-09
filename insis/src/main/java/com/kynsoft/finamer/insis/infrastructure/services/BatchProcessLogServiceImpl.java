package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.insis.domain.dto.BatchProcessLogDto;
import com.kynsoft.finamer.insis.domain.services.IBatchProcessLogService;
import com.kynsoft.finamer.insis.infrastructure.model.BatchProcessLog;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchStatus;
import com.kynsoft.finamer.insis.infrastructure.repository.command.BatchProcessLogWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.BatchProcessLogReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BatchProcessLogServiceImpl implements IBatchProcessLogService {

    private final BatchProcessLogWriteDataJPARepository writeRepository;
    private final BatchProcessLogReadDataJPARepository readRepository;

    public BatchProcessLogServiceImpl(BatchProcessLogWriteDataJPARepository writeRepository,
                                      BatchProcessLogReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }


    @Override
    public UUID create(BatchProcessLogDto dto) {
        BatchProcessLog batchProcessLog = new BatchProcessLog(dto);
        return writeRepository.save(batchProcessLog).getId();
    }

    @Override
    public void update(BatchProcessLogDto dto) {
        BatchProcessLog batchProcessLog = new BatchProcessLog(dto);
        writeRepository.save(batchProcessLog);
    }

    @Override
    public void delete(BatchProcessLogDto dto) {
        try{
            writeRepository.deleteById(dto.getId());
        }catch (Exception ex){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public BatchProcessLogDto findById(UUID id) {
        Optional<BatchProcessLog> batchProcessLog = readRepository.findById(id);
        if(batchProcessLog.isPresent()){
            return batchProcessLog.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "BatchProcessLog not found.")));
    }

    @Override
    public Long countByStatus(BatchStatus status) {
        return readRepository.countByStatus(status);
    }
}
