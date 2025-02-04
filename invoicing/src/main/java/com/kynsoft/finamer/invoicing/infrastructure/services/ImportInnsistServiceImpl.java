package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistRoomRateKafka;
import com.kynsoft.finamer.invoicing.application.excel.ValidatorFactory;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.*;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.excel.event.ImportBookingProcessEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportCacheRedisRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;

@Service
public class ImportInnsistServiceImpl {

    private final IManageAgencyService agencyService;

    private final IManageHotelService manageHotelService;

    private final BookingImportCacheRedisRepository repository;
    private final IBookingImportHelperService bookingImportHelperService;
    private final ValidatorFactory<BookingRow> validatorFactory;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ImportInnsistServiceImpl(IManageAgencyService agencyService,
            IManageHotelService manageHotelService,
            BookingImportCacheRedisRepository repository,
            IBookingImportHelperService bookingImportHelperService,
            ValidatorFactory<BookingRow> validatorFactory,
            ApplicationEventPublisher applicationEventPublisher) {
        this.agencyService = agencyService;
        this.manageHotelService = manageHotelService;
        this.repository = repository;
        this.bookingImportHelperService = bookingImportHelperService;
        this.validatorFactory = validatorFactory;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private void createCache(ImportInnsistKafka request) {
        try {
            UUID insistImportProcessId = UUID.randomUUID();
            //Construye el objeto usado en el excel y guarda en cache.
            this.create(request.getImportList(), request.getImportInnsitProcessId(), insistImportProcessId);

            //Obtengo de cache
            List<BookingImportCache> list = this.repository.findAllByImportProcessId(insistImportProcessId.toString());
            boolean validateInsist = validatorFactory.validateInsist(list);
            validatorFactory.createValidators(EImportType.INNSIST.name());

            BookingImportProcessDto start = BookingImportProcessDto.builder().importProcessId(request.getImportInnsitProcessId().toString())
                    .status(EProcessStatus.RUNNING)
                    .total(0)
                    .build();
            applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, start));
            int rowNumber = 0;
            boolean stop = true;
            for (BookingImportCache bookingImportCache : list) {
                BookingRow row = bookingImportCache.toAggregateImportInsist();
                row.setRowNumber(rowNumber);
                row.setImportProcessId(row.getInsistImportProcessId());
                if (validatorFactory.validate(row)) {
                    bookingImportHelperService.groupAndCachingImportBooking(row, EImportType.NO_VIRTUAL);
                } else {
                    stop = false;
                }
                rowNumber++;
            }
            list.clear();
            validatorFactory.removeValidators();
            if (validateInsist && stop) {
                this.bookingImportHelperService.createInvoiceGroupingByCoupon(request.getImportInnsitProcessId().toString(), request.getEmployee(), true);
                this.bookingImportHelperService.createInvoiceGroupingByBooking(request.getImportInnsitProcessId().toString(), request.getEmployee(), true);
            }
            BookingImportProcessDto end = BookingImportProcessDto.builder().importProcessId(request.getImportInnsitProcessId().toString())
                    .status(EProcessStatus.FINISHED)
                    .total(list.size())
                    .build();
            applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, end));
            bookingImportHelperService.removeAllImportCache(request.getImportInnsitProcessId().toString());
            bookingImportHelperService.removeAllImportCache(insistImportProcessId.toString());
            this.clearCache();

        } catch (Exception e) {
            BookingImportProcessDto bookingImportProcessDto = BookingImportProcessDto.builder().importProcessId(request.getImportInnsitProcessId().toString())
                    .hasError(true)
                    .exceptionMessage(e.getMessage())
                    .status(EProcessStatus.FINISHED)
                    .total(0)
                    .build();
            applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, bookingImportProcessDto));
            this.clearCache();
        }
    }

    private List<BookingRow> create(List<ImportInnsistBookingKafka> bookingRowList, UUID importInnsitProcessId, UUID insistImportProcessId) {
        List<BookingRow> bookingRows = new ArrayList<>();
        for (ImportInnsistBookingKafka importInnsistBookingKafka : bookingRowList) {
            for (ImportInnsistRoomRateKafka roomRate : importInnsistBookingKafka.getRoomRates()) {
                BookingRow bookingRow = new BookingRow();
                //bookingRow.setImportProcessId(importInnsitProcessId.toString());
                bookingRow.setImportProcessId(insistImportProcessId.toString());// este es para escribir en redis.
                bookingRow.setInsistImportProcessBookingId(importInnsistBookingKafka.getId().toString());
                //bookingRow.setInsistImportProcessId(insistImportProcessId.toString());
                bookingRow.setInsistImportProcessId(importInnsitProcessId.toString());//Este es el que le tengo que dar al flujo de validacion. que viene en la request.
                bookingRow.setTransactionDate(importInnsistBookingKafka.getInvoiceDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                bookingRow.setManageHotelCode(importInnsistBookingKafka.getManageHotelCode());
                bookingRow.setManageAgencyCode(importInnsistBookingKafka.getManageAgencyCode());
                bookingRow.setFirstName(importInnsistBookingKafka.getFirstName());
                bookingRow.setLastName(importInnsistBookingKafka.getLastName());
                bookingRow.setCoupon(importInnsistBookingKafka.getCouponNumber());
                bookingRow.setHotelBookingNumber(importInnsistBookingKafka.getHotelBookingNumber());
                bookingRow.setRoomType(importInnsistBookingKafka.getRoomTypeCode());
                bookingRow.setRatePlan(importInnsistBookingKafka.getRatePlanCode());
                bookingRow.setHotelInvoiceNumber(importInnsistBookingKafka.getHotelInvoiceNumber().toString());
                bookingRow.setBookingDate(importInnsistBookingKafka.getBookingDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                bookingRow.setNightType(importInnsistBookingKafka.getNightTypeCode());

                //Rate
                bookingRow.setCheckIn(roomRate.getCheckIn().toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                bookingRow.setCheckOut(roomRate.getCheckOut().toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                bookingRow.setNights(roomRate.getNights().doubleValue());
                bookingRow.setAdults(roomRate.getAdults().doubleValue());
                bookingRow.setChildren(roomRate.getChildren().doubleValue());
                bookingRow.setInvoiceAmount(roomRate.getInvoiceAmount());
                bookingRow.setRemarks(roomRate.getRemark());
                bookingRow.setAmountPAX(roomRate.getAdults().doubleValue() + roomRate.getChildren().doubleValue());
                bookingRow.setRoomNumber(roomRate.getRoomNumber());
                bookingRow.setHotelInvoiceAmount(roomRate.getHotelAmount());
                this.bookingImportHelperService.saveCachingImportBooking(bookingRow);
            }
        }
        return bookingRows;
    }

    private void clearCache() {
        this.manageHotelService.clearManageHotelCache();
        this.agencyService.clearManageHotelCache();
    }

    @Async
    public void createInvoiceFromGroupedBooking(ImportInnsistKafka request) {
        this.createCache(request);
    }
}