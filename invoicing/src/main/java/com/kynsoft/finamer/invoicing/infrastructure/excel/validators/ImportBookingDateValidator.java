package com.kynsoft.finamer.invoicing.infrastructure.excel.validators;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.util.DateUtil;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.BookingImportCacheRedisRepository;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Filter;

public class ImportBookingDateValidator extends ExcelRuleValidator<BookingRow> {

    private final BookingImportCacheRedisRepository bookingImportCacheRedisRepository;
    private final IManageBookingService bookingService;

    public ImportBookingDateValidator(BookingImportCacheRedisRepository bookingImportCacheRedisRepository,
                                      IManageBookingService bookingService) {
        this.bookingImportCacheRedisRepository = bookingImportCacheRedisRepository;
        this.bookingService = bookingService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        return validateDate(obj, errorFieldList) ;
    }

    private boolean validateDate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getBookingDate()) || obj.getBookingDate().isEmpty()) {
            errorFieldList.add(new ErrorField("Booking Date", "Booking Date can't be empty"));
            return false;
        }


        if (!DateUtil.validateDateFormat(obj.getBookingDate())) {
            errorFieldList.add(new ErrorField("Booking Date", "Booking Date has invalid date format"));
            return false;
        }
        return true;
    }


}
