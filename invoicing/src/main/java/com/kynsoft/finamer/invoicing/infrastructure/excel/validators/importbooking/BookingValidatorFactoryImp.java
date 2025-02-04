package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsoft.finamer.invoicing.application.excel.ValidatorFactory;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageNightTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportCacheRedisRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class BookingValidatorFactoryImp extends ValidatorFactory<BookingRow> {

    private final IManageHotelService manageHotelService;
    private final IManageRoomTypeService roomTypeService;

    private final IManageRatePlanService ratePlanService;

    private final IManageAgencyService manageAgencyService;

    private final IManageBookingService manageBookingService;

    private final BookingImportCacheRedisRepository cacheRedisRepository;
    private final IInvoiceCloseOperationService closeOperationService;
    private final IManageNightTypeService nightTypeService;

    public BookingValidatorFactoryImp(ApplicationEventPublisher applicationEventPublisher,
            IManageHotelService manageHotelService,
            IManageRoomTypeService roomTypeService,
            IManageRatePlanService ratePlanService,
            IManageAgencyService manageAgencyService,
            IManageBookingService manageBookingService,
            BookingImportCacheRedisRepository cacheRedisRepository,
            IInvoiceCloseOperationService closeOperationService,
            IManageNightTypeService nightTypeService) {
        super(applicationEventPublisher);
        this.manageHotelService = manageHotelService;
        this.roomTypeService = roomTypeService;
        this.ratePlanService = ratePlanService;
        this.manageAgencyService = manageAgencyService;
        this.manageBookingService = manageBookingService;
        this.cacheRedisRepository = cacheRedisRepository;
        this.closeOperationService = closeOperationService;
        this.nightTypeService = nightTypeService;
    }

    @Override
    public void createValidators(String importType) {
        if (validators.isEmpty()) {
            validators.put(ImportBookingHotelValidator.class.getName(), new ImportBookingHotelValidator(manageHotelService));
            validators.put(ImportBookingDuplicateValidator.class.getName(), new ImportBookingDuplicateValidator(manageBookingService, cacheRedisRepository, manageHotelService));
            validators.put(ImportBookingTransactionDateValidator.class.getName(), new ImportBookingTransactionDateValidator(closeOperationService, manageHotelService));
            validators.put(ImportBookingAgencyValidator.class.getName(), new ImportBookingAgencyValidator(manageAgencyService));
            validators.put(ImportBookingNameValidator.class.getName(), new ImportBookingNameValidator());
            validators.put(ImportBookingCheckInValidator.class.getName(), new ImportBookingCheckInValidator());
            validators.put(ImportBookingCheckOutValidator.class.getName(), new ImportBookingCheckOutValidator());
            validators.put(ImportBookingNightsValidator.class.getName(), new ImportBookingNightsValidator());

            validators.put(ImportBookingAdultsValidator.class.getName(), new ImportBookingAdultsValidator(importType));

            validators.put(ImportBookingInvoiceAmountValidator.class.getName(), new ImportBookingInvoiceAmountValidator(importType));
            validators.put(ImportBookingCouponValidator.class.getName(), new ImportBookingCouponValidator());
            validators.put(ImportBookingRoomTypeValidator.class.getName(), new ImportBookingRoomTypeValidator(roomTypeService));
            validators.put(ImportBookingRatePlanValidator.class.getName(), new ImportBookingRatePlanValidator(ratePlanService));
            validators.put(ImportBookingHotelBookingNoValidator.class.getName(), new ImportBookingHotelBookingNoValidator());
            validators.put(ImportBookingHotelInvoiceNumberValidator.class.getName(), new ImportBookingHotelInvoiceNumberValidator(manageHotelService, manageBookingService, cacheRedisRepository));
            validators.put(ImportBookingAmountPaxValidator.class.getName(), new ImportBookingAmountPaxValidator());
            validators.put(ImportBookingHotelInvoiceAmountValidator.class.getName(), new ImportBookingHotelInvoiceAmountValidator(manageHotelService));
            validators.put(ImportBookingDateValidator.class.getName(), new ImportBookingDateValidator());
            validators.put(ImportBookingTypeValidator.class.getName(), new ImportBookingTypeValidator(importType, manageHotelService));
            validators.put(ImportBookingNightTypeValidator.class.getName(), new ImportBookingNightTypeValidator(nightTypeService, manageAgencyService));
            validators.put(ImportVirtualHotelHotelInvoiceNumberValidator.class.getName(), new ImportVirtualHotelHotelInvoiceNumberValidator(manageHotelService, manageBookingService, cacheRedisRepository));
            validators.put(ImportHotelBookingNumberValidator.class.getName(), new ImportHotelBookingNumberValidator(manageHotelService, manageBookingService, cacheRedisRepository));
        }
    }

    @Override
    public boolean validate(BookingRow bookingRow) {
        validators.forEach((key, value) -> {
            value.validate(bookingRow, errorFieldList);
        });
        this.sendErrorEvent(bookingRow);
        boolean result = errorFieldList.isEmpty();
        this.clearErrors();
        return result;
    }

    @Override
    public boolean validateInsist(List<BookingImportCache> list) {
        ImportInsistAdultsValidator adultsValidator = new ImportInsistAdultsValidator();
        ImportInnsistInvoiceAmountValidator invoiceAmountValidator = new ImportInnsistInvoiceAmountValidator();
        Map<String, List<BookingImportCache>> map = this.groupByInsistImportProcessBookingId(list);

        map.forEach((key, values) -> {
            BookingRow bookingRow = values.get(0).toAggregateImportInsistValidate();
            bookingRow.setImportProcessId(bookingRow.getInsistImportProcessId());
            if (this.checkAdultsCount(values) == 0) {
                adultsValidator.validate(bookingRow, errorFieldList);
                this.sendErrorEvent(bookingRow);
            }

            if(checkInvoiceAmount(values) == 0){
                invoiceAmountValidator.validate(bookingRow, errorFieldList);
                this.sendErrorEvent(bookingRow);
            }
        });

       return errorFieldList.isEmpty();
    }



    private Map<String, List<BookingImportCache>> groupByInsistImportProcessBookingId(List<BookingImportCache> list) {
        return list.stream()
                .collect(Collectors.groupingBy(
                        BookingImportCache::getInsistImportProcessBookingId,
                        Collectors.toList()
                ));
    }

    private double checkAdultsCount(List<BookingImportCache> values) {
       return values.stream()
                .mapToDouble(BookingImportCache::getAdults)
                .sum();
    }

    private double checkInvoiceAmount(List<BookingImportCache> values) {
        return values.stream()
                .mapToDouble(BookingImportCache::getInvoiceAmount)
                .sum();
    }
}
