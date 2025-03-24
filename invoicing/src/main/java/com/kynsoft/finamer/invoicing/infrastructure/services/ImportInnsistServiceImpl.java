package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.*;
import com.kynsoft.finamer.invoicing.application.excel.ValidatorFactory;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.*;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.excel.event.ImportBookingProcessEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingRowError;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.importInnsist.response.ProducerResponseImportInnsistService;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;

@Service
public class ImportInnsistServiceImpl {

    private final IManageAgencyService agencyService;
    private final IManageHotelService manageHotelService;
    private final IBookingImportHelperService bookingImportHelperService;
    private final ValidatorFactory<BookingRow> validatorFactory;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ManageEmployeeReadDataJPARepository employeeReadDataJPARepository;
    private final ProducerResponseImportInnsistService producerResponseImportInnsistService;
    private final ImportBookingService importBookingService;

    public ImportInnsistServiceImpl(IManageAgencyService agencyService,
                                    IManageHotelService manageHotelService,
                                    IBookingImportHelperService bookingImportHelperService,
                                    ValidatorFactory<BookingRow> validatorFactory,
                                    ApplicationEventPublisher applicationEventPublisher,
                                    ManageEmployeeReadDataJPARepository employeeReadDataJPARepository,
                                    ProducerResponseImportInnsistService producerResponseImportInnsistService,
                                    ImportBookingService importBookingService) {
        this.agencyService = agencyService;
        this.manageHotelService = manageHotelService;
        this.bookingImportHelperService = bookingImportHelperService;
        this.validatorFactory = validatorFactory;
        this.applicationEventPublisher = applicationEventPublisher;
        this.employeeReadDataJPARepository = employeeReadDataJPARepository;
        this.producerResponseImportInnsistService = producerResponseImportInnsistService;
        this.importBookingService = importBookingService;
    }

    @Async
    public void createInvoiceFromGroupedBooking(ImportInnsistKafka request) {
        this.createCache(request);
    }

    private void createCache(ImportInnsistKafka request) {
        try {
            UUID importProcessId = UUID.randomUUID();

            //Genera el log del proceso en redis al inicio de todo.
            BookingImportProcessDto start = BookingImportProcessDto.builder().importProcessId(request.getImportInnsitProcessId().toString())
                    .status(EProcessStatus.RUNNING)
                    .total(0)
                    .build();
            applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, start));

            //Guarda en cache los rates
            List<BookingImportCache> list = this.create(request.getImportList(), request.getImportInnsitProcessId(), importProcessId);

            boolean validateInsist = validatorFactory.validateInsist(list);
            validatorFactory.createValidators(EImportType.INNSIST.name());

            int rowNumber = 0;
            boolean stop = false;

            List<UUID> agencies = this.employeeReadDataJPARepository.findAgencyIdsByEmployeeId(UUID.fromString(request.getEmployee()));
            List<UUID> hotels = this.employeeReadDataJPARepository.findHotelsIdsByEmployeeId(UUID.fromString(request.getEmployee()));

            for (BookingImportCache bookingImportCache : list) {
                BookingRow row = bookingImportCache.toAggregateImportInsist();
                row.setRowNumber(rowNumber);
                row.setImportProcessId(row.getInsistImportProcessId());
                row.setAgencies(agencies);
                row.setHotels(hotels);
                if(!validatorFactory.validate(row)){
                    stop = true;
                }
                rowNumber++;
            }
            validatorFactory.removeValidators();
            if (validateInsist && !stop) {
                this.bookingImportHelperService.createInvoiceGroupingByCoupon(importProcessId.toString(), request.getEmployee(), true);
                this.bookingImportHelperService.createInvoiceGroupingByBooking(importProcessId.toString(), request.getEmployee(), true);
                list = this.bookingImportHelperService.findAllByImportProcess(importProcessId.toString());
                replicateResponseOk(list, request.getImportInnsitProcessId());
            }else{
                List<BookingRowError> errors = importBookingService.getImportError(request.getImportInnsitProcessId().toString());
                replicateResponseError(errors, request.getImportInnsitProcessId());
            }

            list.clear();
            BookingImportProcessDto end = BookingImportProcessDto.builder().importProcessId(request.getImportInnsitProcessId().toString())
                    .status(EProcessStatus.FINISHED)
                    .total(list.size())
                    .build();
            applicationEventPublisher.publishEvent(new ImportBookingProcessEvent(this, end));
            bookingImportHelperService.removeAllImportCache(request.getImportInnsitProcessId().toString());
            bookingImportHelperService.removeAllImportCache(importProcessId.toString());
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

    private List<BookingImportCache> create(List<ImportInnsistBookingKafka> bookingRowList, UUID importInnsitProcessId, UUID importProcessId) {
        List<ManageAgencyDto> agencies = getAgencies(bookingRowList);
        List<BookingRow> bookingRows = bookingRowList.stream()
                .flatMap(importInnsistBookingKafka -> importInnsistBookingKafka.getRoomRates().stream()
                        .map(roomRate -> {
                            BookingRow bookingRow = new BookingRow();
                            bookingRow.setImportProcessId(importProcessId.toString());
                            bookingRow.setInsistImportProcessBookingId(importInnsistBookingKafka.getId().toString());
                            bookingRow.setInsistImportProcessId(importInnsitProcessId.toString());
                            bookingRow.setTransactionDate(importInnsistBookingKafka.getInvoiceDate()
                                    .toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                            bookingRow.setManageHotelCode(importInnsistBookingKafka.getManageHotelCode());
                            bookingRow.setManageAgencyCode(importInnsistBookingKafka.getManageAgencyCode());
                            bookingRow.setFirstName(importInnsistBookingKafka.getFirstName());
                            bookingRow.setLastName(importInnsistBookingKafka.getLastName());
                            bookingRow.setCoupon(importInnsistBookingKafka.getCouponNumber());
                            bookingRow.setHotelBookingNumber(importInnsistBookingKafka.getHotelBookingNumber());
                            bookingRow.setRoomType(importInnsistBookingKafka.getRoomTypeCode());
                            bookingRow.setRatePlan(importInnsistBookingKafka.getRatePlanCode());
                            bookingRow.setHotelInvoiceNumber(importInnsistBookingKafka.getHotelInvoiceNumber().toString());
                            bookingRow.setBookingDate(importInnsistBookingKafka.getBookingDate()
                                    .toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                            bookingRow.setNightType(importInnsistBookingKafka.getNightTypeCode());

                            // Rate Details
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

                            return bookingRow;
                        })
                )
                .toList();

        return this.bookingImportHelperService.saveCachingImportBooking(bookingRows, agencies);
    }

    private List<ManageAgencyDto> getAgencies(List<ImportInnsistBookingKafka> bookingRowList){
        List<String> agencyCodes = bookingRowList.stream()
                .map(ImportInnsistBookingKafka::getManageAgencyCode).toList();
        return agencyService.findByCodes(agencyCodes);
    }

    private void clearCache() {
        this.manageHotelService.clearManageHotelCache();
        this.agencyService.clearManageHotelCache();
    }

    private void replicateResponseOk(List<BookingImportCache> bookingImportCacheList, UUID innsistImportId){
        List<RoomRateResponseKafka> roomRateResponses = bookingImportCacheList.stream()
                .map(this::bookingImportCacheToRoomRateKafka)
                .toList();
        ImportInnsistResponseKafka importInnsistResponseKafka = new ImportInnsistResponseKafka(innsistImportId,
                roomRateResponses, true);
        producerResponseImportInnsistService.create(importInnsistResponseKafka);
    }

    private void replicateResponseError(List<BookingRowError> bookingRowErrors, UUID innsistImportId){
        List<RoomRateResponseKafka> roomRateResponses = bookingRowErrors.stream()
                .map(this::bookingErrorResponseToRoomRateKafka)
                .toList();
        ImportInnsistResponseKafka importInnsistResponseKafka = new ImportInnsistResponseKafka(innsistImportId,
                roomRateResponses, false);
        producerResponseImportInnsistService.create(importInnsistResponseKafka);
    }

    private RoomRateResponseKafka bookingImportCacheToRoomRateKafka(BookingImportCache booking){
        return new RoomRateResponseKafka(
                null,
                UUID.fromString(booking.getInsistImportProcessBookingId()),
                booking.getInvoiceId(),
                null
        );
    }

    private RoomRateResponseKafka bookingErrorResponseToRoomRateKafka(BookingRowError bookingRowError){
        return new RoomRateResponseKafka(
                null,
                UUID.fromString(bookingRowError.getRow().getInsistImportProcessBookingId()),
                null,
                bookingRowError.getErrorFields()
        );
    }
}