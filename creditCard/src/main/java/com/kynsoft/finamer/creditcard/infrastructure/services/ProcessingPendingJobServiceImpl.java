package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageStatusTransaction.update.UpdateManageStatusTransactionCommand;
import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import com.kynsoft.finamer.creditcard.domain.dto.ProcessErrorLogDto;
import com.kynsoft.finamer.creditcard.domain.services.IProcessingPendingJobService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ProcessErrorLog;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ProcessErrorLogWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ProcessErrorLogReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProcessingPendingJobServiceImpl implements IProcessingPendingJobService {

    @Autowired
    private final CardNetJobServiceImpl cardnetJobService;

    @Autowired
    private final ProcessErrorLogWriteDataJPARepository repositoryCradentProcessErrorCommand;

    @Autowired
    private final ProcessErrorLogReadDataJPARepository repositoryCradentProcessErrorQuery;

    @Autowired
    private RestTemplate restTemplate; // Necesario para llamar a otro endpoint (Enpoint para verificar si se peude actualizar la transacción)

    @Autowired
    TransactionServiceImpl transactionService;

    private final ManageTransactionStatusServiceImpl transactionStatusService;

    private final IMediator mediator;

    public ProcessingPendingJobServiceImpl(ProcessErrorLogReadDataJPARepository repositoryCradentProcessErrorQuery, ProcessErrorLogWriteDataJPARepository repositoryCradentProcessErrorCommand, CardNetJobServiceImpl cardnetJobService, IMediator mediator, TransactionServiceImpl transactionService, ManageTransactionStatusServiceImpl transactionStatusService) {
        this.cardnetJobService = cardnetJobService;
        this.mediator = mediator;
        this.repositoryCradentProcessErrorCommand = repositoryCradentProcessErrorCommand;
        this.repositoryCradentProcessErrorQuery = repositoryCradentProcessErrorQuery;
        this.transactionService = transactionService;
        this.transactionStatusService = transactionStatusService;
    }

    @Scheduled(fixedRate = 600000)  // se ejecuta el método cada 600000 ms = 10 minutos
    public void checkIsProcessedAndCallTransactionStatus() {

        LocalDate yesterdaynew = LocalDate.now().minusDays(1);
        LocalDateTime yesterdaydate = yesterdaynew.atStartOfDay();
        List<CardnetJobDto> list = cardnetJobService.listUnProcessedTransactions(yesterdaydate);
        if (!list.isEmpty()) {
            processCardNetPendingTransactions(list);
        }
    }

    // Método recursivo que procesa la lista
    private void processCardNetPendingTransactions(List<CardnetJobDto> list) {
        list.forEach(cardnetJobDto -> {
            if (cardnetJobDto.getNumberOfAttempts() < 4) {
                ProcessErrorLogDto processErrorLogDto = new ProcessErrorLogDto();
                try {
                    // Acción a ejecutar en cada elemento
                    UpdateManageStatusTransactionCommand updateManageStatusTransactionCommandRequest = UpdateManageStatusTransactionCommand.builder()
                            .session(cardnetJobDto.getSession())
                            .build();
                    mediator.send(updateManageStatusTransactionCommandRequest);
                    cardnetJobDto.setNumberOfAttempts(cardnetJobDto.getNumberOfAttempts() + 1);
                    cardnetJobDto.setIsProcessed(true);
                    cardnetJobService.update(cardnetJobDto);
                } catch (Exception e) {
                    cardnetJobDto.setNumberOfAttempts(cardnetJobDto.getNumberOfAttempts() + 1);
                    cardnetJobService.update(cardnetJobDto);
                    // Manejo de la excepción, por ejemplo, registrar el error
                    processErrorLogDto.setSession(cardnetJobDto.getSession());
                    processErrorLogDto.setTransactionId(cardnetJobDto.getTransactionId());
                    processErrorLogDto.setError(e.getMessage());
                    createOrUpdate(processErrorLogDto);
                }
            } else {
                // Cancelar transaccion
                // TransactionDto transactionDto = transactionService.findByUuid(cardnetJobDto.getTransactionId());
                // ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByMerchantResponseStatus(ETransactionResultStatus.CANCELLED);
                // transactionDto.setStatus(transactionStatusDto);
                // transactionService.update(transactionDto);

                // marcarla como procesada
                cardnetJobDto.setIsProcessed(true);
                cardnetJobService.update(cardnetJobDto);
            }

        });
    }

    public void update(ProcessErrorLogDto dto) {
        ProcessErrorLog entity = new ProcessErrorLog(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCradentProcessErrorCommand.save(entity);
    }
    public void create(ProcessErrorLogDto dto) {
        ProcessErrorLog data = new ProcessErrorLog(dto);
        this.repositoryCradentProcessErrorCommand.save(data);
    }

    public void createOrUpdate(ProcessErrorLogDto dto){
        if(repositoryCradentProcessErrorQuery.existsByTransactionId(dto.getTransactionId()))
            update(dto);
            else create(dto);

    }
}