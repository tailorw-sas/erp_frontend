package com.kynsoft.finamer.invoicing.infrastructure.adapters.excel;

import com.kynsoft.finamer.invoicing.domain.dto.roomrate.UnifiedRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.interfaces.RoomRateSourceAdapter;
import com.kynsoft.finamer.invoicing.domain.interfaces.AdapterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Adaptador que convierte BookingRow (que en realidad son Room Rates del Excel)
 * al formato unificado UnifiedRoomRateDto.
 *
 * Migra la lógica actual manteniendo compatibilidad con el sistema existente.
 */
@Component("excelRoomRateAdapter")
@Slf4j
public class ExcelRoomRateAdapter implements RoomRateSourceAdapter {

    private static final String SOURCE_TYPE = "EXCEL";

    @Override
    public Mono<List<UnifiedRoomRateDto>> adaptToUnified(Object source, String importProcessId) {
        log.debug("Adapting Excel data to unified format for process: {}", importProcessId);

        return Mono.fromCallable(() -> {
            if (!canHandle(source)) {
                throw new IllegalArgumentException("Source is not a List<BookingRow>");
            }

            @SuppressWarnings("unchecked")
            List<BookingRow> bookingRows = (List<BookingRow>) source;

            return bookingRows.stream()
                    .map(bookingRow -> adaptSingleRow(bookingRow, importProcessId))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        })
        .doOnSuccess(result -> log.info("Adapted {} Excel rows to unified format", result.size()))
        .doOnError(error -> log.error("Error adapting Excel data", error));
    }

    /**
     * Convierte un BookingRow individual a UnifiedRoomRateDto
     */
    private UnifiedRoomRateDto adaptSingleRow(BookingRow bookingRow, String importProcessId) {
        try {
            return UnifiedRoomRateDto.builder()
                // === Metadatos de Origen ===
                .importProcessId(importProcessId)
                .sourceType(SOURCE_TYPE)
                .sourceIdentifier("Row " + bookingRow.getRowNumber())
                .rowReference(String.valueOf(bookingRow.getRowNumber()))

                // === Datos del Room Rate (migrados desde BookingRow) ===
                .transactionDate(cleanString(bookingRow.getTransactionDate()))
                .hotelCode(cleanString(bookingRow.getManageHotelCode()))
                .agencyCode(cleanString(bookingRow.getManageAgencyCode()))
                .firstName(cleanString(bookingRow.getFirstName()))
                .lastName(cleanString(bookingRow.getLastName()))
                .checkIn(cleanString(bookingRow.getCheckIn()))
                .checkOut(cleanString(bookingRow.getCheckOut()))
                .nights(bookingRow.getNights())
                .adults(bookingRow.getAdults())
                .children(bookingRow.getChildren())
                .invoiceAmount(bookingRow.getInvoiceAmount())
                .coupon(cleanString(bookingRow.getCoupon()))
                .hotelBookingNumber(cleanString(bookingRow.getHotelBookingNumber()))
                .roomType(cleanString(bookingRow.getRoomType()))
                .ratePlan(cleanString(bookingRow.getRatePlan()))
                .hotelInvoiceNumber(cleanString(bookingRow.getHotelInvoiceNumber()))
                .remarks(cleanString(bookingRow.getRemarks()))
                .amountPAX(bookingRow.getAmountPAX())
                .roomNumber(cleanString(bookingRow.getRoomNumber()))
                .hotelInvoiceAmount(bookingRow.getHotelInvoiceAmount())
                .bookingDate(cleanString(bookingRow.getBookingDate()))
                .hotelType(cleanString(bookingRow.getHotelType()))
                .nightType(cleanString(bookingRow.getNightType()))

                // === Datos Calculados ===
                .bookingGroupKey(calculateBookingGroupKey(bookingRow))
                .invoiceGroupKey(calculateInvoiceGroupKey(bookingRow))

                .build();

        } catch (Exception e) {
            log.error("Error adapting row {}: {}", bookingRow.getRowNumber(), e.getMessage());
            return null; // Filtrado en el stream principal
        }
    }

    /**
     * Calcula la clave de agrupación para bookings basada en reglas de negocio actuales
     */
    private String calculateBookingGroupKey(BookingRow bookingRow) {
        // Usar la misma lógica que el sistema actual
        return String.format("%s|%s|%s|%s",
                cleanString(bookingRow.getManageHotelCode()),
                cleanString(bookingRow.getManageAgencyCode()),
                cleanString(bookingRow.getHotelBookingNumber()),
                cleanString(bookingRow.getTransactionDate()));
    }

    /**
     * Calcula la clave de agrupación para invoices
     */
    private String calculateInvoiceGroupKey(BookingRow bookingRow) {
        return String.format("%s|%s|%s",
                cleanString(bookingRow.getManageHotelCode()),
                cleanString(bookingRow.getManageAgencyCode()),
                cleanString(bookingRow.getTransactionDate()));
    }

    /**
     * Limpia strings removiendo espacios extra y manejando nulls
     */
    private String cleanString(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }

    @Override
    public String getSourceType() {
        return SOURCE_TYPE;
    }

    @Override
    public boolean canHandle(Object source) {
        if (source == null) {
            return false;
        }

        if (!(source instanceof List)) {
            return false;
        }

        List<?> list = (List<?>) source;
        if (list.isEmpty()) {
            return true; // Lista vacía es válida
        }

        // Verificar que el primer elemento sea BookingRow
        return list.get(0) instanceof BookingRow;
    }

    @Override
    public AdapterInfo getAdapterInfo() {
        return AdapterInfo.create(
                SOURCE_TYPE,
                "Adapter for Excel files containing room rate data (currently named BookingRow)",
                List.class // Esperamos List<BookingRow>
        );
    }
}