package com.kynsoft.finamer.invoicing.infrastructure.excel;

import com.kynsoft.finamer.invoicing.application.excel.ValidatorFactory;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageNightTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingAdultsValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingAgencyValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingAmountPaxValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingCheckInValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingCheckOutValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingCouponValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingDateValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingDuplicateValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingHotelBookingNoValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingHotelInvoiceAmountValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingHotelInvoiceNumberValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingHotelValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingInvoiceAmountValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingNameValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingNightTypeValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingNightsValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingRatePlanValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingRoomTypeValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingTransactionDateValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingTypeValidator;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportCacheRedisRepository;
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
            validators.put(ImportBookingDuplicateValidator.class.getName(), new ImportBookingDuplicateValidator(manageBookingService, cacheRedisRepository));
            validators.put(ImportBookingTransactionDateValidator.class.getName(), new ImportBookingTransactionDateValidator(closeOperationService, manageHotelService));
            validators.put(ImportBookingHotelValidator.class.getName(), new ImportBookingHotelValidator(manageHotelService));
            validators.put(ImportBookingAgencyValidator.class.getName(), new ImportBookingAgencyValidator(manageAgencyService));
            validators.put(ImportBookingNameValidator.class.getName(), new ImportBookingNameValidator());
            validators.put(ImportBookingCheckInValidator.class.getName(), new ImportBookingCheckInValidator());
            validators.put(ImportBookingCheckOutValidator.class.getName(), new ImportBookingCheckOutValidator());
            validators.put(ImportBookingNightsValidator.class.getName(), new ImportBookingNightsValidator());
            validators.put(ImportBookingAdultsValidator.class.getName(), new ImportBookingAdultsValidator());
            validators.put(ImportBookingInvoiceAmountValidator.class.getName(), new ImportBookingInvoiceAmountValidator());
            validators.put(ImportBookingCouponValidator.class.getName(), new ImportBookingCouponValidator());
            validators.put(ImportBookingRoomTypeValidator.class.getName(), new ImportBookingRoomTypeValidator(roomTypeService));
            validators.put(ImportBookingRatePlanValidator.class.getName(), new ImportBookingRatePlanValidator(ratePlanService));
            validators.put(ImportBookingHotelBookingNoValidator.class.getName(), new ImportBookingHotelBookingNoValidator());
            validators.put(ImportBookingHotelInvoiceNumberValidator.class.getName(), new ImportBookingHotelInvoiceNumberValidator(manageHotelService));
            validators.put(ImportBookingAmountPaxValidator.class.getName(), new ImportBookingAmountPaxValidator());
            validators.put(ImportBookingHotelInvoiceAmountValidator.class.getName(), new ImportBookingHotelInvoiceAmountValidator(manageHotelService));
            validators.put(ImportBookingDateValidator.class.getName(), new ImportBookingDateValidator());
            validators.put(ImportBookingTypeValidator.class.getName(), new ImportBookingTypeValidator(importType, manageHotelService));
            validators.put(ImportBookingNightTypeValidator.class.getName(), new ImportBookingNightTypeValidator(nightTypeService,manageAgencyService));
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

}
