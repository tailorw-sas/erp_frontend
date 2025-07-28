package com.kynsoft.finamer.invoicing.infrastructure.adapters.innsist;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistRoomRateKafka;
import com.kynsoft.finamer.invoicing.domain.dto.roomrate.UnifiedRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.interfaces.RoomRateSourceAdapter;
import com.kynsoft.finamer.invoicing.domain.interfaces.AdapterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Adaptador que convierte ImportInnsistBookingKafka (que en realidad son Room Rates de Innsist) al formato unificado UnifiedRoomRateDto.
 *
 * Migra la lógica actual manteniendo compatibilidad con el sistema existente.
 */
@Component("innsistRoomRateAdapter")
@Slf4j
public class InnsistRoomRateAdapter implements RoomRateSourceAdapter {

    private static final String SOURCE_TYPE = "INNSIST";

    @Override
    public Mono<List<UnifiedRoomRateDto>> adaptToUnified(Object source, String importProcessId) {
        log.debug("Adapting Innsist data to unified format for process: {}", importProcessId);

        return Mono.fromCallable(() -> {
                    if (!canHandle(source)) {
                        throw new IllegalArgumentException("Source is not a List<BookingRow>");
                    }

                    @SuppressWarnings("unchecked")
                    List<ImportInnsistBookingKafka> bookingRows = (List<ImportInnsistBookingKafka>) source;

                    return bookingRows.stream()
                            .map(bookingRow -> adaptSingleRow(bookingRow, importProcessId))
                            .filter(Objects::nonNull)
                            .flatMap(List::stream)
                            .collect(Collectors.toList());
                })
                .doOnSuccess(result -> log.info("Adapted {} Innsist rows to unified format", result.size()))
                .doOnError(error -> log.error("Error adapting Innsist data", error));
    }

    /**
     * Convierte un BookingRow individual a UnifiedRoomRateDto
     */
    private List<UnifiedRoomRateDto> adaptSingleRow(ImportInnsistBookingKafka innsistBookingKafka, String importProcessId) {
        try {
            List<UnifiedRoomRateDto> result = new ArrayList<>();
            for (ImportInnsistRoomRateKafka roomRate : innsistBookingKafka.getRoomRates()) {
                result.add(
                    UnifiedRoomRateDto.builder()
                    // === Metadatos de Origen ===
                        .importProcessId(importProcessId)
                        .sourceType(SOURCE_TYPE)
                        .sourceIdentifier("Row " + innsistBookingKafka.getId())
                        .rowReference(innsistBookingKafka.getId().toString())

                    // === Datos del Room Rate (migrados desde BookingRow) ===
                        .transactionDate(innsistBookingKafka.getInvoiceDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .hotelCode(innsistBookingKafka.getManageHotelCode())
                        .agencyCode(innsistBookingKafka.getManageAgencyCode())
                        .firstName(innsistBookingKafka.getFirstName())
                        .lastName(innsistBookingKafka.getLastName())
                        .coupon(innsistBookingKafka.getCouponNumber())
                        .hotelBookingNumber(innsistBookingKafka.getHotelBookingNumber())
                        .roomType(innsistBookingKafka.getRoomTypeCode())
                        .ratePlan(innsistBookingKafka.getRatePlanCode())
                        .hotelInvoiceNumber(innsistBookingKafka.getHotelInvoiceNumber().toString())
                        .bookingDate(innsistBookingKafka.getBookingDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .nightType(innsistBookingKafka.getNightTypeCode())
                        .checkIn(roomRate.getCheckIn().toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .checkOut(roomRate.getCheckOut().toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                        .nights(roomRate.getNights().doubleValue())
                        .adults(roomRate.getAdults().doubleValue())
                        .children(roomRate.getChildren().doubleValue())
                        .invoiceAmount(roomRate.getInvoiceAmount())
                        .remarks(roomRate.getRemark())
                        .amountPAX(roomRate.getAdults().doubleValue() + roomRate.getChildren().doubleValue())
                        .roomNumber(roomRate.getRoomNumber())
                        .hotelInvoiceAmount(roomRate.getHotelAmount().doubleValue())
                        .folioNumber(innsistBookingKafka.getFolioNumber())
                        //.hotelType(cleanString(bookingRow.getHotelType()))

                        // === Datos Calculados ===
                        .bookingGroupKey(calculateBookingGroupKey(innsistBookingKafka))
                        .invoiceGroupKey(calculateInvoiceGroupKey(innsistBookingKafka))

                        .build());

            }

            return result;
        } catch (Exception e) {
            log.error("Error adapting id {}: {}", innsistBookingKafka.getId(), e.getMessage());
            return null; // Filtrado en el stream principal
        }
    }

    /**
     * Calcula la clave de agrupación para bookings basada en reglas de negocio actuales
     */
    private String calculateBookingGroupKey(ImportInnsistBookingKafka bookingRow) {
        // Usar la misma lógica que el sistema actual
        return String.format("%s|%s|%s|%s",
                cleanString(bookingRow.getManageHotelCode()),
                cleanString(bookingRow.getManageAgencyCode()),
                cleanString(bookingRow.getHotelBookingNumber()),
                cleanString(bookingRow.getInvoiceDate().toString()));
    }

    /**
     * Calcula la clave de agrupación para invoices
     */
    private String calculateInvoiceGroupKey(ImportInnsistBookingKafka bookingRow) {
        return String.format("%s|%s|%s",
                cleanString(bookingRow.getManageHotelCode()),
                cleanString(bookingRow.getManageAgencyCode()),
                cleanString(bookingRow.getInvoiceDate().format(DateTimeFormatter.BASIC_ISO_DATE)));
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
                "Adapter for Innsist files containing room rate data (currently named BookingRow)",
                List.class // Esperamos List<ImportInnsistBookingKafka>
        );
    }
}