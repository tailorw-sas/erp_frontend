package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.validation.DuplicateValidationResult;
import com.kynsoft.finamer.invoicing.domain.dto.validation.HotelBookingCombinationDto;
import com.kynsoft.finamer.invoicing.domain.dto.validation.HotelInvoiceCombinationDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Booking;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IManageBookingService {

    UUID create(ManageBookingDto dto);

    UUID insert(ManageBookingDto dto);

    List<ManageBookingDto> createAll(List<ManageBookingDto> bookingDtoList);

    void update(ManageBookingDto dto);

    void calculateInvoiceAmount(ManageBookingDto dto);

    void delete(ManageBookingDto dto);

    ManageBookingDto findById(UUID id);

    boolean existsByExactLastChars(String lastChars, UUID hotelId);

    void calculateHotelAmount(ManageBookingDto dto);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<ManageBookingDto> findByIds(List<UUID> ids);

    List<ManageBookingDto> findBookingsWithRoomRatesByInvoiceIds(List<UUID> invoiceIds);

    List<ManageBookingDto> findAllToReplicate();

    void deleteInvoice(ManageBookingDto dto);

    boolean existsByHotelInvoiceNumber(String hotelInvoiceNumber, UUID hotelId);

    ManageBookingDto findBookingId(Long bookingId);

    void updateAll(List<ManageBookingDto> bookingList);

    void insertAll(List<Booking> bookins);

    /**
     * Valida combinaciones Hotel+BookingNumber de forma optimizada
     *
     * @param combinations Lista de combinaciones a validar
     * @param importType Tipo de importación (VIRTUAL, NO_VIRTUAL, INNSIST)
     * @return Resultado con información detallada de duplicados encontrados
     */
    DuplicateValidationResult validateHotelBookingCombinations(List<HotelBookingCombinationDto> combinations, ImportType importType);

    /**
     * Valida combinaciones Hotel+InvoiceNumber de forma optimizada
     * SOLO aplica para hoteles virtuales
     *
     * @param combinations Lista de combinaciones a validar
     * @param importType Tipo de importación (normalmente VIRTUAL)
     * @return Resultado con información detallada de duplicados encontrados
     */
    DuplicateValidationResult validateHotelInvoiceCombinations(List<HotelInvoiceCombinationDto> combinations, ImportType importType);

    /**
     * Verifica si existe una combinación específica Hotel+BookingNumber
     * Método de conveniencia para validaciones individuales
     *
     * @param hotelCode Código del hotel
     * @param bookingNumber Número de booking
     * @return true si la combinación ya existe en BD
     */
    boolean existsByHotelCodeAndBookingNumber(String hotelCode, String bookingNumber);

    /**
     * Verifica si existe una combinación específica Hotel+InvoiceNumber
     * SOLO para hoteles virtuales
     *
     * @param hotelCode Código del hotel (debe ser virtual)
     * @param invoiceNumber Número de invoice del hotel
     * @return true si la combinación ya existe en BD
     */
    boolean existsByHotelCodeAndInvoiceNumber(String hotelCode, String invoiceNumber);
}
