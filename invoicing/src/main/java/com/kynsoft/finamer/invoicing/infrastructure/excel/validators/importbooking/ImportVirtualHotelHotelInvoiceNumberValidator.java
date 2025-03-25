package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EImportType;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportCacheRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUtils;

import java.util.List;
import java.util.Objects;

public class ImportVirtualHotelHotelInvoiceNumberValidator extends ExcelRuleValidator<BookingRow> {

    private final String importType;
    private final IManageHotelService manageHotelService;
    private final IManageBookingService manageBookingService;

    private final BookingImportCacheRedisRepository cacheRedisRepository;

    public ImportVirtualHotelHotelInvoiceNumberValidator(String importType, IManageHotelService manageHotelService,
                                                         IManageBookingService manageBookingService,
                                                         BookingImportCacheRedisRepository cacheRedisRepository) {
        this.importType = importType;
        this.manageHotelService = manageHotelService;
        this.manageBookingService = manageBookingService;
        this.cacheRedisRepository = cacheRedisRepository;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {

        if (Objects.nonNull(obj.getManageHotelCode()) && !obj.getManageHotelCode().isEmpty() && manageHotelService.existByCode(InvoiceUtils.upperCaseAndTrim(obj.getManageHotelCode()))) {
            ManageHotelDto manageHotelDto = manageHotelService.findByCode(InvoiceUtils.upperCaseAndTrim(obj.getManageHotelCode()));
            if (EImportType.VIRTUAL.name().equals(importType) && manageHotelDto.isVirtual()) {
                if (Objects.isNull(obj.getHotelInvoiceNumber())) {
                    errorFieldList.add(new ErrorField("HotelInvoiceNumber", "The Hotel Invoice Number can't be empty."));
                    return false;
                }

                List<BookingImportCache> list = this.cacheRedisRepository.findBookingImportCacheByHotelInvoiceNumberAndImportProcessId(obj.getHotelInvoiceNumber(), obj.getImportProcessId());
                if (!this.isValidInsertion(list, obj)) {
                    errorFieldList.add(new ErrorField("HotelInvoiceNumber", "The Hotel Invoice Number exists for another date within this import."));
                    return false;
                }

                if (manageHotelDto.isVirtual() && manageBookingService.existsByHotelInvoiceNumber(obj.getHotelInvoiceNumber(), manageHotelDto.getId())) {
                    errorFieldList.add(new ErrorField("HotelInvoiceNumber", "Hotel Invoice Number already exists"));
                    return false;
                }
            }
        }else{
            errorFieldList.add(new ErrorField("Hotel", " Hotel not found."));
            return false;
        }

        return true;
    }

    public boolean isValidInsertion(List<BookingImportCache> list, BookingRow newElement) {
        if (list != null && !list.isEmpty()) {
            if (!list.get(0).getTransactionDate().equals(newElement.getTransactionDate())) {
                return !list.get(0).getManageHotelCode().equals(newElement.getManageHotelCode());
            }
        }

        return true;
    }

}
