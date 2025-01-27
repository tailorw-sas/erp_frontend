package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportCacheRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUtils;

import java.util.List;

public class ImportVirtualHotelHotelInvoiceNumberValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageHotelService manageHotelService;
    private final IManageBookingService manageBookingService;

    private final BookingImportCacheRedisRepository cacheRedisRepository;

    public ImportVirtualHotelHotelInvoiceNumberValidator(IManageHotelService manageHotelService,
            IManageBookingService manageBookingService,
            BookingImportCacheRedisRepository cacheRedisRepository) {
        this.manageHotelService = manageHotelService;
        this.manageBookingService = manageBookingService;
        this.cacheRedisRepository = cacheRedisRepository;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {

        if (!manageHotelService.existByCode(InvoiceUtils.upperCaseAndTrim(obj.getManageHotelCode()))) {
            //errorFieldList.add(new ErrorField("Hotel", " Hotel not found."));
            return false;
        }

        ManageHotelDto hotelDto = this.manageHotelService.findByCode(obj.getManageHotelCode());
        List<BookingImportCache> list = this.cacheRedisRepository.findBookingImportCacheByHotelInvoiceNumberAndImportProcessId(obj.getHotelInvoiceNumber(), obj.getImportProcessId());
        if (hotelDto.isVirtual()) {
            if (this.isValidInsertion(list, obj)) {
                errorFieldList.add(new ErrorField("HotelInvoiceNumber", "The Hotel Invoice Number exists for another date within this import."));
                return false;
            }
        }
        return true;
    }

    public boolean isValidInsertion(List<BookingImportCache> list, BookingRow newElement) {
        if (list != null && !list.isEmpty()) {
            if (!list.get(0).getTransactionDate().equals(newElement.getTransactionDate())) {
                if (list.get(0).getManageHotelCode().equals(newElement.getManageHotelCode())) {
                    return true;
                }
            }
        }

        return false;
    }

}
