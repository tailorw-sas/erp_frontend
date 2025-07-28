package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.invoicing.domain.dto.importresult.ImportProgress;
import com.kynsoft.finamer.invoicing.domain.dto.importresult.ImportResult;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportCache;
import com.kynsoft.finamer.invoicing.infrastructure.services.orchestrator.UnifiedRoomRateImportOrchestrator;
import com.kynsoft.finamer.invoicing.infrastructure.services.progress.ImportProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controlador moderno para importación de Room Rates desde múltiples fuentes.
 * Reemplaza la funcionalidad del BookingController con arquitectura mejorada.
 */
@RestController
@RequestMapping("/api/v1/room-rate-import")
@RequiredArgsConstructor
@Slf4j
public class RoomRateImportController {

    private final UnifiedRoomRateImportOrchestrator orchestrator;
    private final ImportProgressService progressService;

    /**
     * Importa room rates desde archivo Excel
     */
    @PostMapping(path = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<ImportResult>> importFromExcel(
            @RequestPart("file") FilePart filePart,
            @RequestPart("importProcessId") String importProcessId,
            @RequestPart("importType") String importType,
            @RequestPart("employee") String employee) {

        log.info("Starting Excel import - Process: {}, Type: {}, Employee: {}",
                importProcessId, importType, employee);

        return validateExcelRequest(filePart, importProcessId, employee)
                .flatMap(valid -> processExcelImport(filePart, importProcessId, importType, employee))
                .map(result -> ResponseEntity.accepted().body(result))
                .onErrorResume(this::handleError);
    }

    /**
     * Obtiene el estado actual de un proceso de importación
     */
    @GetMapping("/status/{importProcessId}")
    public Mono<ResponseEntity<ImportProgress>> getImportStatus(@PathVariable String importProcessId) {
        return progressService.getProgress(importProcessId)
                .map(progress -> ResponseEntity.ok(progress))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Lista adaptadores disponibles para diferentes fuentes
     */
    @GetMapping("/adapters")
    public ResponseEntity<Map<String, String>> getAvailableAdapters() {
        Map<String, String> adapters = orchestrator.getAvailableAdapters();
        return ResponseEntity.ok(adapters);
    }

    /**
     * Cancela un proceso de importación en curso
     */
    @PostMapping("/cancel/{importProcessId}")
    public Mono<ResponseEntity<String>> cancelImport(@PathVariable String importProcessId) {
        // Implementar lógica de cancelación
        return Mono.just(ResponseEntity.ok("Import cancellation requested for: " + importProcessId));
    }

    // === MÉTODOS PRIVADOS ===

    /**
     * Valida la request de Excel
     */
    private Mono<Boolean> validateExcelRequest(FilePart filePart, String importProcessId, String employee) {
        return Mono.fromCallable(() -> {
            if (filePart == null) {
                throw new IllegalArgumentException("File is required");
            }
            if (importProcessId == null || importProcessId.trim().isEmpty()) {
                throw new IllegalArgumentException("Import process ID is required");
            }
            if (employee == null || employee.trim().isEmpty()) {
                throw new IllegalArgumentException("Employee ID is required");
            }
            return true;
        });
    }

    /**
     * Procesa la importación de Excel usando la infraestructura existente
     */
    private Mono<ImportResult> processExcelImport(FilePart filePart, String importProcessId, String importType, String employee) {

        return readExcelFile(filePart)
                .doOnNext(bookingRows ->
                        log.info("Successfully read {} rows from Excel for process: {}", bookingRows.size(), importProcessId))
                .flatMap(bookingRows ->
                        orchestrator.processImport(bookingRows, "EXCEL", importProcessId, employee, importType));
    }

    /**
     * Lee el archivo Excel de forma completamente reactiva
     */
    private Mono<List<BookingRow>> readExcelFile(FilePart filePart) {
        return filePart.content()
            .collectList()
            .flatMap(dataBuffers -> {
                try {
                    // Convertir DataBuffers a byte array
                    int totalLength = dataBuffers.stream()
                            .mapToInt(dataBuffer -> dataBuffer.readableByteCount())
                            .sum();

                    byte[] bytes = new byte[totalLength];
                    int currentIndex = 0;

                    for (org.springframework.core.io.buffer.DataBuffer dataBuffer : dataBuffers) {
                        int length = dataBuffer.readableByteCount();
                        dataBuffer.read(bytes, currentIndex, length);
                        currentIndex += length;
                        org.springframework.core.io.buffer.DataBufferUtils.release(dataBuffer);
                    }

                    // Ahora leer el Excel desde los bytes
                    return readExcelFromBytes(bytes, filePart.filename());

                } catch (Exception e) {
                    return Mono.error(new RuntimeException("Error reading file content", e));
                }
            })
            .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Lee Excel desde byte array usando ExcelBeanReader
     */
    private Mono<List<BookingRow>> readExcelFromBytes(byte[] fileBytes, String filename) {
        return Mono.fromCallable(() -> {

            if (fileBytes == null || fileBytes.length == 0) {
                throw new IllegalArgumentException("Excel file is empty or invalid");
            }

            List<BookingRow> bookingRows = new ArrayList<>();

            try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {

                // Configurar ExcelBeanReader usando la infraestructura existente
                ReaderConfiguration readerConfiguration = new ReaderConfiguration();
                readerConfiguration.setIgnoreHeaders(true);
                readerConfiguration.setInputStream(inputStream);
                readerConfiguration.setReadLastActiveSheet(true);

                // Crear el reader con la clase BookingRow existente
                ExcelBeanReader<BookingRow> reader = new ExcelBeanReader<>(readerConfiguration, BookingRow.class);
                ExcelBean<BookingRow> excelBean = new ExcelBean<>(reader);

                // Leer todas las filas
                for (BookingRow bookingRow : excelBean) {
                    if (bookingRow != null) {
                        // Limpiar espacios en blanco como en el código original
                        removeBlankSpacesInBookingRow(bookingRow);
                        bookingRows.add(bookingRow);
                    }
                }

                reader.close();

                log.info("Successfully read {} booking rows from Excel file: {}", bookingRows.size(), filename);
                return bookingRows;

            } catch (Exception e) {
                log.error("Error reading Excel file: {}", filename, e);
                throw new RuntimeException("Error processing Excel file: " + e.getMessage(), e);
            }
        })
        .subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Limpia espacios en blanco de BookingRow (migrado del código original)
     */
    private void removeBlankSpacesInBookingRow(BookingRow bookingRow) {
        if (bookingRow.getManageHotelCode() != null) {
            bookingRow.setManageHotelCode(bookingRow.getManageHotelCode().trim());
        }
        if (bookingRow.getManageAgencyCode() != null) {
            bookingRow.setManageAgencyCode(bookingRow.getManageAgencyCode().trim());
        }
        if (bookingRow.getFirstName() != null) {
            bookingRow.setFirstName(bookingRow.getFirstName().trim());
        }
        if (bookingRow.getLastName() != null) {
            bookingRow.setLastName(bookingRow.getLastName().trim());
        }
        if (bookingRow.getCoupon() != null) {
            bookingRow.setCoupon(bookingRow.getCoupon().trim());
        }
        if (bookingRow.getHotelBookingNumber() != null) {
            bookingRow.setHotelBookingNumber(bookingRow.getHotelBookingNumber().trim());
        }
        if (bookingRow.getRoomType() != null) {
            bookingRow.setRoomType(bookingRow.getRoomType().trim());
        }
        if (bookingRow.getRatePlan() != null) {
            bookingRow.setRatePlan(bookingRow.getRatePlan().trim());
        }
        if (bookingRow.getHotelInvoiceNumber() != null) {
            bookingRow.setHotelInvoiceNumber(bookingRow.getHotelInvoiceNumber().trim());
        }
        if (bookingRow.getRoomNumber() != null) {
            bookingRow.setRoomNumber(bookingRow.getRoomNumber().trim());
        }
        if (bookingRow.getNightType() != null) {
            bookingRow.setNightType(bookingRow.getNightType().trim());
        }
    }

    /**
     * Maneja errores de forma consistente
     */
    private Mono<ResponseEntity<ImportResult>> handleError(Throwable error) {
        log.error("Error in room rate import", error);

        ImportResult errorResult = ImportResult.builder()
                .success(false)
                .message("Import failed: " + error.getMessage())
                .build();

        HttpStatus status = determineHttpStatus(error);
        return Mono.just(ResponseEntity.status(status).body(errorResult));
    }

    /**
     * Determina el status HTTP apropiado para el error
     */
    private HttpStatus determineHttpStatus(Throwable error) {
        if (error instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        } else if (error instanceof SecurityException) {
            return HttpStatus.FORBIDDEN;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
