package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.insis.domain.dto.ImportProcessDto;
import com.kynsoft.finamer.insis.domain.services.IImportProcessService;
import com.kynsoft.finamer.insis.infrastructure.model.ImportProcess;
import com.kynsoft.finamer.insis.infrastructure.repository.command.ImportProcessWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ImportProcessReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ImportProcessServiceImpl implements IImportProcessService {

    private final ImportProcessWriteDataJPARepository writeRepository;
    private final ImportProcessReadDataJPARepository readRepository;

    public ImportProcessServiceImpl(ImportProcessWriteDataJPARepository writeRepository,
                                    ImportProcessReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public ImportProcessDto create(ImportProcessDto dto) {
        ImportProcess importBookingProcess = new ImportProcess(dto);
        return writeRepository.save(importBookingProcess).toAggregate();
    }

    @Override
    public void update(ImportProcessDto dto) {
        ImportProcess importBookingProcess = new ImportProcess(dto);
        writeRepository.save(importBookingProcess);
    }

    @Override
    public ImportProcessDto findById(UUID id) {
        Optional<ImportProcess> importBookingProcess = readRepository.findById(id);
        if(importBookingProcess.isPresent()){
            return importBookingProcess.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Import process Id not found.")));
    }
}
