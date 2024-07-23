package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EImportType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.bean.GroupBy;
import com.kynsoft.finamer.invoicing.domain.services.IBookingImportHelperService;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportCacheRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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


    public BookingImportHelperServiceImpl(IManageAgencyService agencyService, IManageHotelService manageHotelService,
                                          IManageInvoiceService invoiceService, IManageRatePlanService ratePlanService, IManageRoomTypeService roomTypeService,
                                          BookingImportCacheRedisRepository repository) {
        this.agencyService = agencyService;
        this.manageHotelService = manageHotelService;
        this.invoiceService = invoiceService;
        this.ratePlanService = ratePlanService;
        this.roomTypeService = roomTypeService;
        this.repository = repository;
    }

    @Override
    public void groupAndCachingImportBooking(BookingRow bookingRow, EImportType importType) {
        ManageAgencyDto manageAgencyDto = agencyService.findByCode(bookingRow.getManageAgencyCode());
        this.createCache(bookingRow,manageAgencyDto.getGenerationType().name());
    }

    @Transactional
    @Override
    public void createInvoiceFromGroupedBooking(String importProcessId) {
        this.createInvoiceGroupingByCoupon(importProcessId);
        this.createInvoiceGroupingByBooking(importProcessId);
    }

    @Override
    public void removeAllImportCache(String importProcessId) {
       List<BookingImportCache> cache =repository.findAllByImportProcessId(importProcessId);
       repository.deleteAll(cache);
    }

    @Override
    public boolean canImportRow(BookingRow bookingRow,EImportType importType) {
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
        grouped = bookingImportCacheStream.stream().parallel().map(BookingImportCache::getBookingRow)
                .collect(Collectors.groupingByConcurrent(bookingRow ->
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
        bookingImportCacheStream.stream().parallel().forEach(bookingImportCache -> {
            ManageAgencyDto agency = agencyService.findByCode(bookingImportCache.getBookingRow().getManageAgencyCode());
            ManageHotelDto hotel = manageHotelService.findByCode(bookingImportCache.getBookingRow().getManageHotelCode());
            this.createInvoiceWithBooking(agency, hotel, List.of(bookingImportCache.getBookingRow()));
        });
    }

    private void createInvoiceWithBooking(ManageAgencyDto agency, ManageHotelDto hotel, List<BookingRow> bookingRowList) {
        ManageInvoiceDto manageInvoiceDto = new ManageInvoiceDto();
        manageInvoiceDto.setAgency(agency);
        manageInvoiceDto.setHotel(hotel);
        manageInvoiceDto.setInvoiceType(EInvoiceType.INVOICE);
        manageInvoiceDto.setIsManual(false);
        manageInvoiceDto.setInvoiceDate(LocalDate.now());
        List<ManageBookingDto> bookingDtos = bookingRowList.stream().map(bookingRow -> {
            ManageRatePlanDto ratePlanDto = ratePlanService.findByCode(bookingRow.getRatePlan());
            ManageRoomTypeDto roomTypeDto = roomTypeService.findByCode(bookingRow.getRoomType());
            ManageBookingDto bookingDto = bookingRow.toAggregate();
            bookingDto.setRatePlan(ratePlanDto);
            bookingDto.setRoomType(roomTypeDto);
            bookingDto.setId(UUID.randomUUID());
            return bookingDto;
        }).toList();
        manageInvoiceDto.setBookings(bookingDtos);
        manageInvoiceDto.setId(UUID.randomUUID());
        manageInvoiceDto.setStatus(EInvoiceStatus.PROCECSED);
        manageInvoiceDto.setInvoiceAmount(InvoiceUtils.calculateInvoiceAmount(manageInvoiceDto));
        invoiceService.create(manageInvoiceDto);


    }
    private void createCache(BookingRow bookingRow,String generationType){
        BookingImportCache bookingImportCache = new BookingImportCache();
        bookingImportCache.setBookingRow(bookingRow);
        bookingImportCache.setGenerationType(generationType);
        bookingImportCache.setImportProcessId(bookingRow.getImportProcessId());
        repository.save(bookingImportCache);
    }
}
