package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.importInnsist;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsoft.finamer.invoicing.domain.dto.importresult.ImportResult;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.ImportType;
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
    public void listen(ImportInnsistKafka objKafka) {
        log.info("Starting Innsist import - Process: {}, Type: {}, Employee: {} Count: {}",
                objKafka.getImportProcessId(), ImportType.INSIST.toString(),
                objKafka.getEmployee(), objKafka.getImportList().size());

        validateInnsistRequest(objKafka)
                .flatMap(valid -> {

                    return orchestrator.processImport(objKafka.getImportList(), "INNSIST", ImportType.INSIST, objKafka.getImportProcessId().toString(),
                            objKafka.getEmployee());
                })
                .doOnSuccess(result -> log.info("Innsist import completed: {}", result.isSuccess()))
                .onErrorResume(this::handleError)
                .subscribe(); // Para Kafka listeners, usa subscribe en lugar de retornar Mono
    }

    /**
     * Handle errors consistently
     */
    private Mono<ImportResult> handleError(Throwable error) {
        log.error("Error in room rate import", error);

        ImportResult errorResult = ImportResult.builder()
                .success(false)
                .message("Import failed: " + error.getMessage())
                .build();


        return Mono.just(errorResult);
    }

    private Mono<Boolean> validateInnsistRequest(ImportInnsistKafka objKafka) {
        return Mono.fromCallable(() -> {
            if (objKafka.getImportProcessId() == null) {
                throw new IllegalArgumentException("Import process ID is required");
            }
            if (objKafka.getEmployee() == null || objKafka.getEmployee().trim().isEmpty()) {
                throw new IllegalArgumentException("Employee ID is required");
            }
            if (objKafka.getImportList() == null || objKafka.getImportList().isEmpty()) {
                throw new IllegalArgumentException("Import data is required");
            }
            return true;
        });
    }
}
