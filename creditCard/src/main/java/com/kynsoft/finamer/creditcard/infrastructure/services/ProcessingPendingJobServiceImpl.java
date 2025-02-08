package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageStatusTransaction.update.UpdateManageStatusTransactionCommand;
import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dto.ProcessErrorLogDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionResultStatus;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.IManageStatusTransactionService;
import com.kynsoft.finamer.creditcard.domain.services.IProcessingPendingJobService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ProcessErrorLog;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ProcessErrorLogWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ProcessErrorLogReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProcessingPendingJobServiceImpl implements IProcessingPendingJobService {

    private static final Logger log = LoggerFactory.getLogger(ProcessingPendingJobServiceImpl.class);
    @Autowired
    private final CardNetJobServiceImpl cardNetJobService;

    @Autowired
    private final ProcessErrorLogWriteDataJPARepository repositoryCardNetProcessErrorCommand;

    @Autowired
    private final ProcessErrorLogReadDataJPARepository repositoryCardNetProcessErrorQuery;

    @Autowired
    TransactionServiceImpl transactionService;

    private final IMediator mediator;
    private final IManageStatusTransactionService statusTransactionService;

    public ProcessingPendingJobServiceImpl(ProcessErrorLogReadDataJPARepository repositoryCardNetProcessErrorQuery,
                                           ProcessErrorLogWriteDataJPARepository repositoryCardNetProcessErrorCommand,
                                           CardNetJobServiceImpl cardNetJobService, IMediator mediator,
                                           TransactionServiceImpl transactionService,
                                           IManageStatusTransactionService statusTransactionService) {
        this.cardNetJobService = cardNetJobService;
        this.mediator = mediator;
        this.repositoryCardNetProcessErrorCommand = repositoryCardNetProcessErrorCommand;
        this.repositoryCardNetProcessErrorQuery = repositoryCardNetProcessErrorQuery;
        this.transactionService = transactionService;
        this.statusTransactionService = statusTransactionService;
    }

    @Scheduled(fixedRate = 600000)  // se ejecuta el método cada 600000 ms = 10 minutos
    public void checkIsProcessedAndCallTransactionStatus() {

        LocalDate yesterdaynew = LocalDate.now().minusDays(1);
        LocalDateTime yesterdaydate = yesterdaynew.atStartOfDay();
        List<CardnetJobDto> list = cardNetJobService.listUnProcessedTransactions(yesterdaydate);
        if (!list.isEmpty()) {
            processCardNetPendingTransactions(list);
        }
    }

    private void processCardNetPendingTransactions(List<CardnetJobDto> list) {
        list.forEach(cardnetJobDto -> {
            ProcessErrorLogDto processErrorLogDto = new ProcessErrorLogDto();

            try {
                // Verificar si la transacción ya fue procesada correctamente
                if (cardnetJobDto.getIsProcessed()) {
                    log.info("✅ Transacción {} ya ha sido procesada, no se intentará nuevamente.", cardnetJobDto.getTransactionId());
                    return;
                }

                // Intentar obtener el estado de la transacción en CardNet
                UpdateManageStatusTransactionCommand updateCommand = UpdateManageStatusTransactionCommand.builder()
                        .session(cardnetJobDto.getSession())
                        .build();
                mediator.send(updateCommand);

                // Si llega aquí, la transacción fue exitosa
                cardnetJobDto.setNumberOfAttempts(cardnetJobDto.getNumberOfAttempts() + 1);
                cardnetJobDto.setIsProcessed(true);
                cardNetJobService.update(cardnetJobDto);
                log.info("✅ Transacción {} procesada correctamente en el intento {}.",
                        cardnetJobDto.getTransactionId(), cardnetJobDto.getNumberOfAttempts());

            } catch (Exception e) {
                log.error("❌ Error al procesar transacción {} en intento {}: {}",
                        cardnetJobDto.getTransactionId(), cardnetJobDto.getNumberOfAttempts() + 1, e.getMessage());

                // Incrementar el número de intentos
                cardnetJobDto.setNumberOfAttempts(cardnetJobDto.getNumberOfAttempts() + 1);

                // Si el intento final falla, cancelar la transacción
                if (cardnetJobDto.getNumberOfAttempts() >= 4) {
                    handleFailedTransaction(cardnetJobDto);
                } else {
                    cardNetJobService.update(cardnetJobDto);
                }

                // Guardar el error en la base de datos para auditoría
                processErrorLogDto.setSession(cardnetJobDto.getSession());
                processErrorLogDto.setTransactionId(cardnetJobDto.getTransactionId());
                processErrorLogDto.setError(e.getMessage());
                createOrUpdate(processErrorLogDto);

                // Esperar 1 minuto antes del próximo intento para evitar sobrecarga en CardNet
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    /**
     * Maneja una transacción que ha fallado en todos sus intentos y la cancela.
     */
    private void handleFailedTransaction(CardnetJobDto cardnetJobDto) {
        log.warn("⚠️ Transacción {} ha fallado en los 4 intentos y debera ser cancelada pero no se hace.", cardnetJobDto.getTransactionId());

        // Marcar la transacción como fallida
        cardnetJobDto.setIsProcessed(true);
        cardNetJobService.update(cardnetJobDto);

        // Registrar en logs o enviar notificación de alerta
        notifyTransactionFailure(cardnetJobDto);
    }

    /**
     * Notifica sobre una transacción fallida.
     */
    private void notifyTransactionFailure(CardnetJobDto cardnetJobDto) {
        log.warn("⚠️ Transacción {} ha sido marcada como fallida.", cardnetJobDto.getTransactionId());
    }


    public void update(ProcessErrorLogDto dto) {
        ProcessErrorLog entity = new ProcessErrorLog(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCardNetProcessErrorCommand.save(entity);
    }
    public void create(ProcessErrorLogDto dto) {
        ProcessErrorLog data = new ProcessErrorLog(dto);
        this.repositoryCardNetProcessErrorCommand.save(data);
    }

    public void createOrUpdate(ProcessErrorLogDto dto){
        if(repositoryCardNetProcessErrorQuery.existsByTransactionId(dto.getTransactionId()))
            update(dto);
            else create(dto);

    }
}