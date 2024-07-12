package com.kynsoft.finamer.invoicing.infrastructure.excel;

import com.kynsoft.finamer.invoicing.domain.excel.validators.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.IValidatorFactory;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingAdultsValidator;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingAmountPaxValidator;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingCheckInValidator;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingCheckOutValidator;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingCouponValidator;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingHotelInvoiceNumberValidator;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingHotelValidator;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingInvoiceAmountValidator;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingNightsValidator;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingRatePlanValidator;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingRoomTypeValidator;
import com.kynsoft.finamer.invoicing.domain.excel.validators.importbooking.ImportBookingTransactionDateValidator;
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

    public BookingValidatorFactoryImp(ApplicationEventPublisher applicationEventPublisher,
                                      IManageHotelService manageHotelService,
                                      IManageRoomTypeService roomTypeService,
                                      IManageRatePlanService ratePlanService,
                                      IManageAgencyService manageAgencyService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.manageHotelService = manageHotelService;
        this.roomTypeService = roomTypeService;
        this.ratePlanService = ratePlanService;
        this.manageAgencyService = manageAgencyService;
    }

    @Override
    public void createValidators() {
        validators = new WeakHashMap<>();
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingHotelValidator(applicationEventPublisher,manageHotelService));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingCheckInValidator(applicationEventPublisher));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingNightsValidator(applicationEventPublisher));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingCouponValidator(applicationEventPublisher));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingHotelInvoiceNumberValidator(applicationEventPublisher,manageHotelService));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingRatePlanValidator(applicationEventPublisher,ratePlanService));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingCheckOutValidator(applicationEventPublisher));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingTransactionDateValidator(applicationEventPublisher));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingInvoiceAmountValidator(applicationEventPublisher));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingAdultsValidator(applicationEventPublisher));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingAmountPaxValidator(applicationEventPublisher));
        validators.put(ImportBookingHotelValidator.class.getName(),new ImportBookingRoomTypeValidator(applicationEventPublisher,roomTypeService));
    }

    @Override
    public boolean validate(BookingRow bookingRow) {
       return validators.entrySet().stream().allMatch(entry->entry.getValue().validate(bookingRow));
    }
}
