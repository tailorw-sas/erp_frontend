package com.kynsoft.finamer.invoicing.infrastructure.excel.event;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.ProcessStatus;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.ImportProcess;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportProcessRedisRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ImportBookingProcessEventHandler implements ApplicationListener<ImportBookingProcessEvent> {

    private final BookingImportProcessRedisRepository bookingImportProcessRedisRepository;

    public ImportBookingProcessEventHandler(BookingImportProcessRedisRepository bookingImportProcessRedisRepository) {
        this.bookingImportProcessRedisRepository = bookingImportProcessRedisRepository;
    }

    @Override
    public void onApplicationEvent(ImportBookingProcessEvent event) {
        String importProcessId = (String) event.getSource();
        Optional<ImportProcess> importProcessOptional = bookingImportProcessRedisRepository.findByImportProcessId(importProcessId);
        if (importProcessOptional.isPresent()){
            ImportProcess importProcess = importProcessOptional.get();
            importProcess.setStatus(ProcessStatus.RUNNING.name().equals(importProcess.getStatus().name())?ProcessStatus.FINISHED:ProcessStatus.RUNNING);
            bookingImportProcessRedisRepository.save(importProcess);
        }else{
            bookingImportProcessRedisRepository.save(new ImportProcess(null,importProcessId,ProcessStatus.RUNNING));
        }
    }
}
