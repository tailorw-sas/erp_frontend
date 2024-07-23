package com.kynsoft.finamer.invoicing.infrastructure.excel;

import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.application.excel.IValidatorFactory;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingAdultsValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingAgencyValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingAmountPaxValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingCheckInValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingCheckOutValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingCouponValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingDuplicateValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingHotelInvoiceNumberValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingHotelValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingInvoiceAmountValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingNameValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingNightsValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingRatePlanValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingRoomTypeValidator;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.ImportBookingTransactionDateValidator;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomTypeService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.WeakHashMap;

@Component
public class BookingValidatorFactoryImp implements IValidatorFactory<BookingRow> {

    private  WeakHashMap<String, ExcelRuleValidator<BookingRow>> validators;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final IManageHotelService manageHotelService;
    private final IManageRoomTypeService roomTypeService;

    private final IManageRatePlanService ratePlanService;

    private final IManageAgencyService manageAgencyService;

    private final IManageBookingService manageBookingService;

    public BookingValidatorFactoryImp(ApplicationEventPublisher applicationEventPublisher,
                                      IManageHotelService manageHotelService,
                                      IManageRoomTypeService roomTypeService,
                                      IManageRatePlanService ratePlanService,
                                      IManageAgencyService manageAgencyService,
                                      IManageBookingService manageBookingService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.manageHotelService = manageHotelService;
        this.roomTypeService = roomTypeService;
        this.ratePlanService = ratePlanService;
        this.manageAgencyService = manageAgencyService;
        this.manageBookingService = manageBookingService;
    }

    @Override
    public void createValidators() {
        validators = new WeakHashMap<>();
        validators.put(ImportBookingDuplicateValidator.class.getName(),new ImportBookingDuplicateValidator(applicationEventPublisher,manageBookingService));
        validators.put(ImportBookingTransactionDateValidator.class.getName(),new ImportBookingTransactionDateValidator(applicationEventPublisher));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingHotelValidator(applicationEventPublisher,manageHotelService));
        validators.put(ImportBookingAgencyValidator.class.getName(),new ImportBookingAgencyValidator(applicationEventPublisher,manageAgencyService));
        validators.put(ImportBookingNameValidator.class.getName(),new ImportBookingNameValidator(applicationEventPublisher));
        validators.put(ImportBookingCheckInValidator.class.getName(),new ImportBookingCheckInValidator(applicationEventPublisher));
        validators.put(ImportBookingCheckOutValidator.class.getName(),new ImportBookingCheckOutValidator(applicationEventPublisher));
        validators.put(ImportBookingNightsValidator.class.getName(),new ImportBookingNightsValidator(applicationEventPublisher));
        validators.put(ImportBookingAdultsValidator.class.getName(),new ImportBookingAdultsValidator(applicationEventPublisher));
        validators.put(ImportBookingInvoiceAmountValidator.class.getName(),new ImportBookingInvoiceAmountValidator(applicationEventPublisher));
        validators.put(ImportBookingCouponValidator.class.getName(),new ImportBookingCouponValidator(applicationEventPublisher));
        validators.put(ImportBookingRoomTypeValidator.class.getName(),new ImportBookingRoomTypeValidator(applicationEventPublisher,roomTypeService));
        validators.put(ImportBookingRatePlanValidator.class.getName(),new ImportBookingRatePlanValidator(applicationEventPublisher,ratePlanService));
        validators.put(ImportBookingHotelInvoiceNumberValidator.class.getName(),new ImportBookingHotelInvoiceNumberValidator(applicationEventPublisher,manageHotelService));
        validators.put(ImportBookingAmountPaxValidator.class.getName(),new ImportBookingAmountPaxValidator(applicationEventPublisher));
    }

    @Override
    public boolean validate(BookingRow bookingRow) {
       return validators.entrySet().stream().allMatch(entry->entry.getValue().validate(bookingRow));
    }
}
