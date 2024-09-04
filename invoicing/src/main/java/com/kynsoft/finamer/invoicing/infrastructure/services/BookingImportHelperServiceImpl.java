package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.*;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.bean.GroupBy;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportCacheRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportRowErrorRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookingImportHelperServiceImpl implements IBookingImportHelperService {

    private final IManageAgencyService agencyService;

    private final IManageHotelService manageHotelService;

    private final IManageInvoiceService invoiceService;

    private final IManageRatePlanService ratePlanService;

    private final IManageRoomTypeService roomTypeService;

    private final BookingImportCacheRedisRepository repository;

    private final BookingImportRowErrorRedisRepository errorRedisRepository;

    private final ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService;

    private final IParameterizationService parameterizationService;

    private final IManageInvoiceStatusService manageInvoiceStatusService;

    public BookingImportHelperServiceImpl(IManageAgencyService agencyService,
                                          IManageHotelService manageHotelService,
                                          IManageInvoiceService invoiceService,
                                          IManageRatePlanService ratePlanService,
                                          IManageRoomTypeService roomTypeService,
                                          BookingImportCacheRedisRepository repository,
                                          BookingImportRowErrorRedisRepository errorRedisRepository,
                                          ProducerReplicateManageInvoiceService producerReplicateManageInvoiceService, IParameterizationService parameterizationService, IManageInvoiceStatusService manageInvoiceStatusService) {
        this.agencyService = agencyService;
        this.manageHotelService = manageHotelService;
        this.invoiceService = invoiceService;
        this.ratePlanService = ratePlanService;
        this.roomTypeService = roomTypeService;
        this.repository = repository;
        this.errorRedisRepository = errorRedisRepository;
        this.producerReplicateManageInvoiceService = producerReplicateManageInvoiceService;
        this.parameterizationService = parameterizationService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
    }

    @Override
    public void groupAndCachingImportBooking(BookingRow bookingRow, EImportType importType) {
        ManageAgencyDto manageAgencyDto = agencyService.findByCode(bookingRow.getManageAgencyCode());
        this.createCache(bookingRow,manageAgencyDto.getGenerationType().name());
    }

    @Override
    public void createInvoiceFromGroupedBooking(String importProcessId) {
        if(!errorRedisRepository.existsByImportProcessId(importProcessId)) {
            this.createInvoiceGroupingByCoupon(importProcessId);
            this.createInvoiceGroupingByBooking(importProcessId);
        }
    }

    @Override
    public void removeAllImportCache(String importProcessId) {
       List<BookingImportCache> cache =repository.findAllByImportProcessId(importProcessId);
       repository.deleteAll(cache);
    }

    @Override
    public boolean canImportRow(BookingRow bookingRow,EImportType importType) {
       if(!manageHotelService.existByCode(bookingRow.getManageHotelCode())){
           //En caso de que el hotel no exista , retornamos que se puede importar,
           //para que los se muestren en los errores la ausencia del hotel
           return true;
       }
        ManageHotelDto hotelDto= manageHotelService.findByCode(bookingRow.getManageHotelCode());
        if (importType.equals(EImportType.VIRTUAL)){
            return hotelDto.isVirtual();
        }else{
            return !hotelDto.isVirtual();
        }

    }

    private void createInvoiceGroupingByCoupon(String importProcessId) {
        Map<GroupBy, List<BookingRow>> grouped;
        List<BookingImportCache> bookingImportCacheStream = repository.findAllByGenerationTypeAndImportProcessId(EGenerationType.ByCoupon.name(), importProcessId);
        grouped = bookingImportCacheStream.stream().map(BookingImportCache::toAggregate)
                .collect(Collectors.groupingBy(bookingRow ->
                        new GroupBy(bookingRow.getManageAgencyCode(), bookingRow.getManageHotelCode(), bookingRow.getCoupon()))
                );

        if (!grouped.isEmpty()) {
            grouped.forEach((key, value) -> {
                ManageAgencyDto agency = agencyService.findByCode(key.getAgency());
                ManageHotelDto hotel = manageHotelService.findByCode(key.getHotel());
                this.createInvoiceWithBooking(agency, hotel, value);

            });
        }
    }

    private void createInvoiceGroupingByBooking(String importProcessId) {
        List<BookingImportCache> bookingImportCacheStream = repository.findAllByGenerationTypeAndImportProcessId(EGenerationType.ByBooking.name(), importProcessId);
        bookingImportCacheStream.forEach(bookingImportCache -> {
            ManageAgencyDto agency = agencyService.findByCode(bookingImportCache.toAggregate().getManageAgencyCode());
            ManageHotelDto hotel = manageHotelService.findByCode(bookingImportCache.toAggregate().getManageHotelCode());
            this.createInvoiceWithBooking(agency, hotel, List.of(bookingImportCache.toAggregate()));
        });
    }


    private void createInvoiceWithBooking(ManageAgencyDto agency, ManageHotelDto hotel, List<BookingRow> bookingRowList) {
        ManageInvoiceStatusDto invoiceStatus = null;
        try{
            ParameterizationDto parameterization = this.parameterizationService.findActiveParameterization();
            invoiceStatus = parameterization != null ? this.manageInvoiceStatusService.findByCode(parameterization.getProcessed()) : null;
        } catch (Exception ignored){
        }
        ManageInvoiceDto manageInvoiceDto = new ManageInvoiceDto();
        manageInvoiceDto.setAgency(agency);
        manageInvoiceDto.setHotel(hotel);
        manageInvoiceDto.setInvoiceType(EInvoiceType.INVOICE);
        manageInvoiceDto.setIsManual(false);
        manageInvoiceDto.setInvoiceDate(getInvoiceDate(bookingRowList.get(0)));
        manageInvoiceDto.setBookings(createBooking(bookingRowList));
        manageInvoiceDto.setId(UUID.randomUUID());
        manageInvoiceDto.setStatus(EInvoiceStatus.PROCECSED);
        manageInvoiceDto.setManageInvoiceStatus(invoiceStatus);
        manageInvoiceDto.setInvoiceAmount(calculateInvoiceAmount(bookingRowList));
        manageInvoiceDto.setDueAmount(calculateInvoiceAmount(bookingRowList));
        String invoiceNumber = InvoiceType.getInvoiceTypeCode(EInvoiceType.INVOICE);
        if(hotel.getManageTradingCompanies() != null && hotel.getManageTradingCompanies().getIsApplyInvoice()){
            invoiceNumber+= "-" + hotel.getManageTradingCompanies().getCode();
        }else{
            invoiceNumber+= "-" + hotel.getCode();
        }
        manageInvoiceDto.setInvoiceNumber(invoiceNumber);
        invoiceService.create(manageInvoiceDto);

        //TODO: aqui se envia a crear el invoice con sun booking en payment
        try {
            this.producerReplicateManageInvoiceService.create(manageInvoiceDto);
        } catch (Exception e) {
        }
    }

    private List<ManageBookingDto> createBooking(List<BookingRow> bookingRowList){
        return bookingRowList.stream().map(bookingRow -> {
            ManageRatePlanDto ratePlanDto = Objects.nonNull(bookingRow.getRatePlan())?
                    ratePlanService.findByCode(bookingRow.getRatePlan()):null;
            ManageRoomTypeDto roomTypeDto =Objects.nonNull(bookingRow.getRoomType())?
                    roomTypeService.findByCode(bookingRow.getRoomType()):null;
            ManageBookingDto bookingDto = bookingRow.toAggregate();
            bookingDto.setRatePlan(ratePlanDto);
            bookingDto.setRoomType(roomTypeDto);
            bookingDto.setId(UUID.randomUUID());
            ManageRoomRateDto manageRoomRateDto = new ManageRoomRateDto();
            manageRoomRateDto.setId(UUID.randomUUID());
            manageRoomRateDto.setAdults(bookingDto.getAdults());
            manageRoomRateDto.setChildren(bookingDto.getChildren());
            manageRoomRateDto.setCheckIn(bookingDto.getCheckIn());
            manageRoomRateDto.setCheckOut(bookingDto.getCheckOut());
            manageRoomRateDto.setHotelAmount(bookingDto.getHotelAmount());
            manageRoomRateDto.setNights(bookingDto.getNights());
            manageRoomRateDto.setRoomNumber(bookingDto.getRoomNumber());
            bookingDto.setRoomRates(List.of(manageRoomRateDto));
            return bookingDto;
        }).toList();
    }

    private double calculateInvoiceAmount(List<BookingRow> bookingRowList){
       return bookingRowList.stream().mapToDouble(BookingRow::getInvoiceAmount).sum();
    }

    private LocalDateTime getInvoiceDate(BookingRow bookingRow){
        LocalDateTime excelDate=DateUtil.parseDateToDateTime(bookingRow.getTransactionDate());
        LocalDateTime transactionDate=LocalDateTime.now();
        if(Objects.nonNull(bookingRow.getTransactionDate()) &&
                Objects.nonNull(excelDate) &&
                !LocalDate.now().isEqual(excelDate.toLocalDate())){
            transactionDate=excelDate;
        }
        return transactionDate;
    }
    private void createCache(BookingRow bookingRow,String generationType){
        BookingImportCache bookingImportCache = new BookingImportCache(bookingRow);
        bookingImportCache.setGenerationType(generationType);
        bookingImportCache.setImportProcessId(bookingRow.getImportProcessId());
        repository.save(bookingImportCache);
    }

}
