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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ImportBookingDuplicateValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageBookingService service;

    private final BookingImportCacheRedisRepository cacheRedisRepository;
    private final IManageHotelService manageHotelService;

    public ImportBookingDuplicateValidator(IManageBookingService service,
            BookingImportCacheRedisRepository cacheRedisRepository,
            IManageHotelService manageHotelService) {
        this.service = service;
        this.cacheRedisRepository = cacheRedisRepository;
        this.manageHotelService = manageHotelService;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        if (obj.getHotelBookingNumber() == null) {
            //errorFieldList.add(new ErrorField("Hotel Booking No"," Hotel Booking No. must be not empty"));
            return false;
        }
//        String validate = obj.getHotelBookingNumber()
//                        .split("\\s+")[obj.getHotelBookingNumber()
//                        .split("\\s+").length - 1];
        try {
            if (!manageHotelService.existByCode(InvoiceUtils.upperCaseAndTrim(obj.getManageHotelCode()))) {
                ManageHotelDto hotel = manageHotelService.findByCode(this.upperCaseAndTrim(obj.getManageHotelCode()));
                if (service.existsByExactLastChars(this.removeBlankSpaces(obj.getHotelBookingNumber()), hotel.getId())) {
                    errorFieldList.add(new ErrorField("Hotel Booking Number", "Record has already been imported."));
                    return false;
                }
            }
        } catch (Exception e) {
            //errorFieldList.add(new ErrorField("Hotel", "The Hotel not found."));
            return false;
        }
//        List<Optional<BookingImportCache>> list = this.cacheRedisRepository.findAllBookingImportCacheByHotelBookingNumberAndImportProcessId(obj.getHotelBookingNumber(), obj.getImportProcessId());
//        if (this.checkDuplicateHotelBookingNumbers(list)) {
//            errorFieldList.add(new ErrorField("HotelBookingNumber", "The Hotel Booking Number exists for another date within this import."));
//            return false;
//        }
        return true;
    }

    private String upperCaseAndTrim(String code) {
        String value = code.trim();
        return value.toUpperCase();
    }

    private boolean checkDuplicateHotelBookingNumbers(List<Optional<BookingImportCache>> list) {
        Map<String, List<BookingImportCache>> groupedByHotelBookingNumber = new HashMap<>();

        // Agrupar elementos por hotelInvoiceNumber
        for (Optional<BookingImportCache> optional : list) {
            if (optional.isPresent()) {
                BookingImportCache booking = optional.get();
                String hotelBookingNumber = booking.getHotelBookingNumber();

                if (!groupedByHotelBookingNumber.containsKey(hotelBookingNumber)) {
                    groupedByHotelBookingNumber.put(hotelBookingNumber, new ArrayList<>());
                }
                groupedByHotelBookingNumber.get(hotelBookingNumber).add(booking);
            }
        }

        // Verificar fechas dentro de cada grupo
        for (List<BookingImportCache> bookings : groupedByHotelBookingNumber.values()) {
            Set<String> transactionDates = new HashSet<>();

            for (BookingImportCache booking : bookings) {
                transactionDates.add(booking.getTransactionDate());
            }

            if (transactionDates.size() != bookings.size()) {
                return true; // Se encontró al menos un caso con fecha diferente
            }
        }

        return false; // No se encontraron discrepancias
    }

    private String removeBlankSpaces(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

}
