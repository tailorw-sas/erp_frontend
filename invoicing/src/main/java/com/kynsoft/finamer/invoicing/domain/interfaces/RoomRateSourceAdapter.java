package com.kynsoft.finamer.invoicing.domain.interfaces;

import com.kynsoft.finamer.invoicing.domain.dto.roomrate.UnifiedRoomRateDto;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Interface para adaptadores que convierten diferentes fuentes de datos
 * (Excel, APIs externas, etc.) al formato unificado UnifiedRoomRateDto
 */
public interface RoomRateSourceAdapter {

    /**
     * Adapta datos de una fuente específica al formato unificado
     *
     * @param source Los datos de origen (puede ser List<BookingRow>, List<ImportInnsistBookingKafka>, etc.)
     * @param importProcessId ID único del proceso de importación
     * @return Lista de UnifiedRoomRateDto listos para procesamiento
     */
    Mono<List<UnifiedRoomRateDto>> adaptToUnified(Object source, String importProcessId);

    /**
     * Retorna el tipo de fuente que maneja este adaptador
     *
     * @return Tipo de fuente ("EXCEL", "EXTERNAL_SYSTEM_A", etc.)
     */
    String getSourceType();

    /**
     * Valida que los datos de origen son del tipo esperado para este adaptador
     *
     * @param source Los datos a validar
     * @return true si puede procesar estos datos
     */
    boolean canHandle(Object source);

    /**
     * Retorna información sobre el formato esperado para este adaptador
     *
     * @return Descripción del formato de datos esperado
     */
    AdapterInfo getAdapterInfo();
}
