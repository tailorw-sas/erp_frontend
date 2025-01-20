package com.kynsoft.finamer.invoicing.infrastructure.excel.validators.importbooking;

import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.application.excel.ExcelRuleValidator;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.booking.BookingImportCacheRedisRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ImportBookingHotelInvoiceNumberValidator extends ExcelRuleValidator<BookingRow> {

    private final IManageHotelService manageHotelService;
    private final IManageBookingService manageBookingService;

    private final BookingImportCacheRedisRepository cacheRedisRepository;

    public ImportBookingHotelInvoiceNumberValidator(IManageHotelService manageHotelService,
            IManageBookingService manageBookingService,
            BookingImportCacheRedisRepository cacheRedisRepository) {
        this.manageHotelService = manageHotelService;
        this.manageBookingService = manageBookingService;
        this.cacheRedisRepository = cacheRedisRepository;
    }

    @Override
    public boolean validate(BookingRow obj, List<ErrorField> errorFieldList) {
        try {
            ManageHotelDto manageHotelDto = manageHotelService.findByCode(upperCaseAndTrim(obj.getManageHotelCode()));
            if (manageHotelDto.isVirtual() && Objects.isNull(obj.getHotelInvoiceNumber())) {
                errorFieldList.add(new ErrorField("HotelInvoiceNumber", " Hotel Invoice Number can't be empty"));
                return false;
            }
            if (!manageHotelService.existByCode(upperCaseAndTrim(obj.getManageHotelCode()))) {
                return false;
            }

            if (manageHotelDto.isVirtual() && (obj.getHotelInvoiceNumber().isEmpty() || Integer.parseInt(obj.getHotelInvoiceNumber()) == 0)) {
                errorFieldList.add(new ErrorField("HotelInvoiceNumber", "Hotel Invoice Number must not 0 if hotel is virtual"));
                return false;
            }

//            List<Optional<BookingImportCache>> list = this.cacheRedisRepository.findBookingImportCacheByHotelInvoiceNumberAndImportProcessId(obj.getHotelInvoiceNumber(), obj.getImportProcessId());
//            if (this.checkDuplicateHotelInvoiceNumbers(list)) {
//                errorFieldList.add(new ErrorField("HotelInvoiceNumber", "The Hotel Invoice Number exists for another date within this import."));
//                return false;
//            }
            if (manageHotelDto.isVirtual() && manageBookingService.existsByHotelInvoiceNumber(obj.getHotelInvoiceNumber(), manageHotelDto.getId())) {
                errorFieldList.add(new ErrorField("HotelInvoiceNumber", "Hotel Invoice Number already exists"));
                return false;
            }
        } catch (Exception e) {
            errorFieldList.add(new ErrorField("hotelCode", " Hotel not found: " + obj.getManageHotelCode()));
            return false;
        }

        return true;
    }

    private String upperCaseAndTrim(String code){
        String value = code.trim();
        return value.toUpperCase();
    }

    public boolean checkDuplicateHotelInvoiceNumbers(List<Optional<BookingImportCache>> list) {
        Map<String, List<BookingImportCache>> groupedByHotelInvoiceNumber = new HashMap<>();

        // Agrupar elementos por hotelInvoiceNumber
        for (Optional<BookingImportCache> optional : list) {
            if (optional.isPresent()) {
                BookingImportCache booking = optional.get();
                String hotelInvoiceNumber = booking.getHotelInvoiceNumber();

                if (!groupedByHotelInvoiceNumber.containsKey(hotelInvoiceNumber)) {
                    groupedByHotelInvoiceNumber.put(hotelInvoiceNumber, new ArrayList<>());
                }
                groupedByHotelInvoiceNumber.get(hotelInvoiceNumber).add(booking);
            }
        }

        // Verificar fechas dentro de cada grupo
        for (List<BookingImportCache> bookings : groupedByHotelInvoiceNumber.values()) {
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

}
