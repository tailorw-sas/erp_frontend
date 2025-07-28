package com.kynsoft.finamer.invoicing.infrastructure.services.progress;

import com.kynsoft.finamer.invoicing.domain.dto.importresult.ImportProgress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para tracking del progreso de importaciones en tiempo real.
 * Permite seguimiento detallado del estado de cada proceso de importación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportProgressService {

    // Cache en memoria para progreso (en producción podrías usar Redis)
    private final Map<String, ImportProgress> progressCache = new ConcurrentHashMap<>();

    /**
     * Actualiza el progreso de un proceso de importación
     */
    public void updateProgress(String importProcessId, String status, int percentage, String description) {
        ImportProgress progress = progressCache.computeIfAbsent(importProcessId, id ->
                ImportProgress.builder()
                        .importProcessId(id)
                        .startTime(LocalDateTime.now())
                        .totalPhases(6) // Reading, Adapting, Loading, Validating, Processing, Completed
                        .build()
        );

        // Actualizar estado
        progress.setStatus(ImportProgress.ImportStatus.valueOf(status));
        progress.setCurrentPhaseDescription(description);

        // Calcular fase actual basada en porcentaje
        if (percentage <= 10) progress.setCurrentPhase(1);
        else if (percentage <= 25) progress.setCurrentPhase(2);
        else if (percentage <= 50) progress.setCurrentPhase(3);
        else if (percentage <= 75) progress.setCurrentPhase(4);
        else if (percentage <= 95) progress.setCurrentPhase(5);
        else progress.setCurrentPhase(6);

        // Agregar log reciente
        if (progress.getRecentLogs() == null) {
            progress.setRecentLogs(new java.util.ArrayList<>());
        }
        progress.getRecentLogs().add(LocalDateTime.now() + ": " + description);

        // Mantener solo últimos 10 logs
        if (progress.getRecentLogs().size() > 10) {
            progress.getRecentLogs().remove(0);
        }

        log.debug("Progress updated for {}: {}% - {}", importProcessId, percentage, description);
    }

    /**
     * Obtiene el progreso actual de un proceso
     */
    public Mono<ImportProgress> getProgress(String importProcessId) {
        ImportProgress progress = progressCache.get(importProcessId);
        if (progress == null) {
            return Mono.empty();
        }
        return Mono.just(progress);
    }

    /**
     * Limpia el progreso de un proceso completado (después de un tiempo)
     */
    public void cleanupProgress(String importProcessId) {
        progressCache.remove(importProcessId);
        log.debug("Cleaned up progress for process: {}", importProcessId);
    }
}