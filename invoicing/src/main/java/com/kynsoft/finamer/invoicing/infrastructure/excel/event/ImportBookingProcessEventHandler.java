package com.kynsoft.finamer.invoicing.infrastructure.excel.event;

import com.kynsoft.finamer.invoicing.domain.dto.BookingImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportProcessRedisEntity;
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
        BookingImportProcessDto bookingImportProcessDto = event.getBookingImportProcessDto();
        Optional<BookingImportProcessRedisEntity> importProcessOptional = bookingImportProcessRedisRepository.findByImportProcessId(bookingImportProcessDto.getImportProcessId());
        if (importProcessOptional.isPresent()){
            BookingImportProcessRedisEntity bookingImportProcessRedisEntity = importProcessOptional.get();
            bookingImportProcessRedisEntity.setStatus(bookingImportProcessDto.getStatus());
            bookingImportProcessRedisEntity.setHasError(bookingImportProcessDto.isHasError());
            bookingImportProcessRedisEntity.setExceptionMessage(bookingImportProcessDto.getExceptionMessage());
            bookingImportProcessRedisRepository.save(bookingImportProcessRedisEntity);
        }else{
            bookingImportProcessRedisRepository.save(bookingImportProcessDto.toAggregate());
        }
    }
}
