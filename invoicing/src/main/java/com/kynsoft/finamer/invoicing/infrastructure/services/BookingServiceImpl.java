package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.importbooking.ImportBookingFromFileRequest;
import com.kynsoft.finamer.invoicing.application.excel.ValidatorFactory;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingErrorRequest;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingProcessStatusRequest;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingProcessStatusResponse;
import com.kynsoft.finamer.invoicing.domain.dto.BookingImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingRequest;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IBookingImportHelperService;
import com.kynsoft.finamer.invoicing.domain.services.ImportBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.excel.event.ImportBookingProcessEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingRowError;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportProcessRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportProcessRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportRowErrorRedisRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements ImportBookingService {

    private final ValidatorFactory<BookingRow> validatorFactory;

    private final BookingImportProcessRedisRepository bookingImportProcessRedisRepository;

    private final BookingImportRowErrorRedisRepository bookingImportRowErrorRedisRepository;

    private final IBookingImportHelperService bookingImportHelperService;

    private final ApplicationEventPublisher applicationEventPublisher;

    public BookingServiceImpl(ValidatorFactory<BookingRow> validatorFactory,
            BookingImportProcessRedisRepository bookingImportProcessRedisRepository,
            BookingImportRowErrorRedisRepository bookingImportRowErrorRedisRepository, IBookingImportHelperService bookingImportHelperService,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.validatorFactory = validatorFactory;
        this.bookingImportProcessRedisRepository = bookingImportProcessRedisRepository;
        this.bookingImportRowErrorRedisRepository = bookingImportRowErrorRedisRepository;
        this.bookingImportHelperService = bookingImportHelperService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Async
    public void importBookingFromFile(ImportBookingFromFileRequest importBookingFromFileRequest) {
        ImportBookingRequest request = importBookingFromFileRequest.getRequest();
        try {
            ReaderConfiguration readerConfiguration = new ReaderConfiguration();
            readerConfiguration.setIgnoreHeaders(true);
            InputStream inputStream = new ByteArrayInputStream(request.getFile());
            readerConfiguration.setInputStream(inputStream);
            readerConfiguration.setReadLastActiveSheet(true);
            ExcelBeanReader<BookingRow> reader = new ExcelBeanReader<>(readerConfiguration, BookingRow.class);
            ExcelBean<BookingRow> excelBean = new ExcelBean<>(reader);
            validatorFactory.createValidators(request.getImportType().name());
            BookingImportProcessDto start = BookingImportProcessDto.builder().importProcessId(request.getImportProcessId())
                    .status(EProcessStatus.RUNNING)
                    .total(0)
                    .build();
            applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, start));
            for (BookingRow bookingRow : excelBean) {
                bookingRow.setImportProcessId(request.getImportProcessId());
                if (validatorFactory.validate(bookingRow)) {
                    bookingImportHelperService.groupAndCachingImportBooking(bookingRow,
                            importBookingFromFileRequest.getRequest().getImportType());
                }
            }
            validatorFactory.removeValidators();
            bookingImportHelperService.createInvoiceFromGroupedBooking(request);
            BookingImportProcessDto end = BookingImportProcessDto.builder().importProcessId(request.getImportProcessId())
                    .status(EProcessStatus.FINISHED)
                    .total(reader.totalRows())
                    .build();
            applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, end));
            bookingImportHelperService.removeAllImportCache(request.getImportProcessId());
        } catch (Exception e) {
            BookingImportProcessDto bookingImportProcessDto = BookingImportProcessDto.builder().importProcessId(request.getImportProcessId())
                    .hasError(true)
                    .exceptionMessage(e.getMessage())
                    .status(EProcessStatus.FINISHED)
                    .total(0)
                    .build();
            applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, bookingImportProcessDto));
        }

    }

    @Override
    public PaginatedResponse getImportError(ImportBookingErrorRequest importBookingErrorRequest) {
        Page<BookingRowError> importErrorPages = bookingImportRowErrorRedisRepository.
                findAllByImportProcessId(importBookingErrorRequest.getImportProcessId(),
                        importBookingErrorRequest.getPageable());
        return new PaginatedResponse(
                importErrorPages.getContent().stream().sorted(Comparator.comparingInt(BookingRowError::getRowNumber)).collect(Collectors.toList()),
                //importErrorPages.getContent(),
                importErrorPages.getTotalPages(),
                importErrorPages.getNumberOfElements(),
                importErrorPages.getTotalElements(),
                importErrorPages.getSize(),
                importErrorPages.getNumber()
        );
    }

    @Override
    public ImportBookingProcessStatusResponse getImportBookingProcessStatus(ImportBookingProcessStatusRequest importBookingProcessStatusRequest) {
        BookingImportProcessDto statusDtp
                = bookingImportProcessRedisRepository.findByImportProcessId(importBookingProcessStatusRequest.getImportProcessId())
                        .map(BookingImportProcessRedisEntity::toAgreggate).get();

        if (statusDtp.isHasError()) {
            throw new ExcelException(statusDtp.getExceptionMessage());
        }
        return new ImportBookingProcessStatusResponse(statusDtp.getStatus().name(), statusDtp.getTotal());

    }

}
