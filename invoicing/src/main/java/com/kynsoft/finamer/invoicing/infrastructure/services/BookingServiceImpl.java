package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.importbooking.ImportBookingFromFileRequest;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingProcessStatusResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingErrorRequest;
import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingRequest;
import com.kynsoft.finamer.invoicing.infrastructure.identity.excel.ImportProcess;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.infrastructure.identity.excel.BookingRowError;
import com.kynsoft.finamer.invoicing.domain.excel.validators.ImportBookingProcessStatusRequest;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.IValidatorFactory;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import com.kynsoft.finamer.invoicing.domain.services.ImportBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.excel.event.ImportBookingProcessEvent;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportProcessRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportRowErrorRedisRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImpl implements ImportBookingService {

    private final IValidatorFactory<BookingRow> validatorFactory;
    private final IManageBookingService manageBookingService;

    private final IManageRoomTypeService manageRoomTypeService;

    private final IManageRatePlanService manageRatePlanService;

    private final BookingImportProcessRedisRepository bookingImportProcessRedisRepository;

    private final BookingImportRowErrorRedisRepository bookingImportRowErrorRedisRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    public BookingServiceImpl(IValidatorFactory<BookingRow> validatorFactory,
                              IManageBookingService manageBookingService,
                              IManageRoomTypeService manageRoomTypeService,
                              IManageRatePlanService manageRatePlanService,
                              BookingImportProcessRedisRepository bookingImportProcessRedisRepository,
                              BookingImportRowErrorRedisRepository bookingImportRowErrorRedisRepository,
                              ApplicationEventPublisher applicationEventPublisher
    ) {
        this.validatorFactory = validatorFactory;
        this.manageBookingService = manageBookingService;
        this.manageRoomTypeService = manageRoomTypeService;
        this.manageRatePlanService = manageRatePlanService;
        this.bookingImportProcessRedisRepository = bookingImportProcessRedisRepository;
        this.bookingImportRowErrorRedisRepository = bookingImportRowErrorRedisRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Async
    public void importBookingFromFile(ImportBookingFromFileRequest importBookingFromFileRequest) {
        ReaderConfiguration readerConfiguration = new ReaderConfiguration();
        readerConfiguration.setIgnoreHeaders(true);
        ImportBookingRequest request = importBookingFromFileRequest.getRequest();
        InputStream inputStream = new ByteArrayInputStream(request.getFile());
        readerConfiguration.setInputStream(inputStream);
        readerConfiguration.setReadLastActiveSheet(true);
        ExcelBeanReader<BookingRow> reader = new ExcelBeanReader<>(readerConfiguration, BookingRow.class);
        ExcelBean<BookingRow> excelBean = new ExcelBean<>(reader);
        validatorFactory.createValidators();
        applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(request.getImportProcessId()));
        for (BookingRow bookingRow : excelBean) {
            bookingRow.setImportProcessId(request.getImportProcessId());
            if (validatorFactory.validate(bookingRow)) {
                ManageBookingDto manageBookingDto = bookingRow.toAgregate();
                ManageRoomTypeDto manageRoomTypeDto = manageRoomTypeService.findByCode(bookingRow.getRoomType());
                ManageRatePlanDto manageRatePlanDto = manageRatePlanService.findByCode(bookingRow.getRatePlan());
                manageBookingDto.setRoomType(manageRoomTypeDto);
                manageBookingDto.setRatePlan(manageRatePlanDto);
                manageBookingDto.setId(UUID.randomUUID());
                manageBookingService.create(manageBookingDto);

            }
        }
        applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(request.getImportProcessId()));

    }

    @Override
    public PaginatedResponse getImportError(ImportBookingErrorRequest importBookingErrorRequest) {
        Page<BookingRowError> importErrorPages = bookingImportRowErrorRedisRepository.findAllByIAndImportProcessId(importBookingErrorRequest.getImportProcessId(), importBookingErrorRequest.getPageable());
        return new PaginatedResponse(importErrorPages.getContent(),
                importErrorPages.getTotalPages(),
                importErrorPages.getNumberOfElements(),
                importErrorPages.getTotalElements(),
                importErrorPages.getSize(),
                importErrorPages.getNumber());
    }

    @Override
    public ImportBookingProcessStatusResponse getImportBookingProcessStatus(ImportBookingProcessStatusRequest importBookingProcessStatusRequest) {
        Optional<ImportProcess> importProcessOptional = bookingImportProcessRedisRepository.findByImportProcessId(importBookingProcessStatusRequest.getImportProcessId());
        return importProcessOptional.map(importProcess -> new ImportBookingProcessStatusResponse(importProcess.getStatus().name())).orElseGet(() -> new ImportBookingProcessStatusResponse(""));
    }


}
