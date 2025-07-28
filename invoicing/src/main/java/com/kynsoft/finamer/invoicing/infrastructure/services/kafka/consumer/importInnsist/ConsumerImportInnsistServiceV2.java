package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.importInnsist;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsoft.finamer.invoicing.domain.dto.importresult.ImportResult;
import com.kynsoft.finamer.invoicing.infrastructure.services.orchestrator.UnifiedRoomRateImportOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsumerImportInnsistServiceV2 {

    private final UnifiedRoomRateImportOrchestrator orchestrator;

    @KafkaListener(topics = "import-innsist-v2", groupId = "invoicing-entity-replica")
    public Mono<ImportResult> listen(ImportInnsistKafka objKafka) {
        log.info("Starting Innsist import - Process: {}, Type: {}, Employee: {} Count: {}",
                objKafka.getImportProcessId(), objKafka.getImportType(), objKafka.getEmployee(), objKafka.getImportList().size());

        return orchestrator.processImport(objKafka.getImportList(), "", objKafka.getImportProcessId().toString(),
                        objKafka.getEmployee(), objKafka.getImportType())
                .map(result -> result)
                .onErrorResume(this::handleError);
    }

    /**
     * Maneja errores de forma consistente
     */
    private Mono<ImportResult> handleError(Throwable error) {
        log.error("Error in room rate import", error);

        ImportResult errorResult = ImportResult.builder()
                .success(false)
                .message("Import failed: " + error.getMessage())
                .build();


        return Mono.just(errorResult);
    }
}
