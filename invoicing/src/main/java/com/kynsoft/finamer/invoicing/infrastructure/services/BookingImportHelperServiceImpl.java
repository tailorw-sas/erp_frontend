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
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUtils;
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


    public BookingImportHelperServiceImpl(IManageAgencyService agencyService, IManageHotelService manageHotelService,
                                          IManageInvoiceService invoiceService, IManageRatePlanService ratePlanService,
                                          IManageRoomTypeService roomTypeService, BookingImportCacheRedisRepository repository, BookingImportRowErrorRedisRepository errorRedisRepository) {
        this.agencyService = agencyService;
        this.manageHotelService = manageHotelService;
        this.invoiceService = invoiceService;
        this.ratePlanService = ratePlanService;
        this.roomTypeService = roomTypeService;
        this.repository = repository;
        this.errorRedisRepository = errorRedisRepository;
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
        ManageInvoiceDto manageInvoiceDto = new ManageInvoiceDto();
        manageInvoiceDto.setAgency(agency);
        manageInvoiceDto.setHotel(hotel);
        manageInvoiceDto.setInvoiceType(EInvoiceType.INVOICE);
        manageInvoiceDto.setIsManual(false);

        LocalDateTime[] transactionDate=new LocalDateTime[]{LocalDateTime.now()};
        List<ManageBookingDto> bookingDtos = bookingRowList.stream().map(bookingRow -> {
            LocalDateTime excelDate=DateUtil.parseDateToDateTime(bookingRow.getTransactionDate());
            if(Objects.nonNull(bookingRow.getTransactionDate()) &&
                    Objects.nonNull(excelDate) &&
                    !LocalDate.now().isEqual(excelDate.toLocalDate())){
                transactionDate[0]=excelDate;
            }
            ManageRatePlanDto ratePlanDto = ratePlanService.findByCode(bookingRow.getRatePlan());
            ManageRoomTypeDto roomTypeDto = roomTypeService.findByCode(bookingRow.getRoomType());
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
        manageInvoiceDto.setInvoiceDate(transactionDate[0]);
        manageInvoiceDto.setBookings(bookingDtos);
        manageInvoiceDto.setId(UUID.randomUUID());
        manageInvoiceDto.setStatus(EInvoiceStatus.PROCECSED);
        manageInvoiceDto.setInvoiceAmount(InvoiceUtils.calculateInvoiceAmount(manageInvoiceDto));
        String invoiceNumber = InvoiceType.getInvoiceTypeCode(EInvoiceType.INVOICE);
        if(hotel.getManageTradingCompanies() != null && hotel.getManageTradingCompanies().getIsApplyInvoice()){
            invoiceNumber+= "-" + hotel.getManageTradingCompanies().getCode();
        }else{
            invoiceNumber+= "-" + hotel.getCode();
        }
        manageInvoiceDto.setInvoiceNumber(invoiceNumber);
        invoiceService.create(manageInvoiceDto);
    }
    private void createCache(BookingRow bookingRow,String generationType){
        BookingImportCache bookingImportCache = new BookingImportCache(bookingRow);
        bookingImportCache.setGenerationType(generationType);
        bookingImportCache.setImportProcessId(bookingRow.getImportProcessId());
        repository.save(bookingImportCache);
    }

}
