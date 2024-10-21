package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageStatusTransaction.update.UpdateManageStatusTransactionCommand;
import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import com.kynsoft.finamer.creditcard.domain.dto.CardnetProcessErrorLogDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionResultStatus;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.ETransactionStatus;
import com.kynsoft.finamer.creditcard.domain.services.IProcessingPendingJobService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetJob;
import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetProcessErrorLog;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.CardnetProcessErrorLogWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.CardnetJobReadDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.CardnetProcessErrorLogReadDataJPARepository;
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
    private final CardnetProcessErrorLogWriteDataJPARepository repositoryCradentProcessErrorCommand;

    @Autowired
    private final CardnetProcessErrorLogReadDataJPARepository repositoryCradentProcessErrorQuery;

    @Autowired
    private RestTemplate restTemplate; // Necesario para llamar a otro endpoint (Enpoint para verificar si se peude actualizar la transacción)

    @Autowired
    TransactionServiceImpl transactionService;

    private final ManageTransactionStatusServiceImpl transactionStatusService;

    private final IMediator mediator;

    public ProcessingPendingJobServiceImpl(CardnetProcessErrorLogReadDataJPARepository repositoryCradentProcessErrorQuery, CardnetProcessErrorLogWriteDataJPARepository repositoryCradentProcessErrorCommand, CardNetJobServiceImpl cardnetJobService, IMediator mediator, TransactionServiceImpl transactionService, ManageTransactionStatusServiceImpl transactionStatusService) {
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
                CardnetProcessErrorLogDto cardnetProcessErrorLogDto = new CardnetProcessErrorLogDto();
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
                    cardnetProcessErrorLogDto.setSession(cardnetJobDto.getSession());
                    cardnetProcessErrorLogDto.setTransactionId(cardnetJobDto.getTransactionId());
                    cardnetProcessErrorLogDto.setError(e.getMessage());
                    createOrUpdate(cardnetProcessErrorLogDto);
                }
            } else {
                // Cancelar transaccion y marcarla como procesada
                TransactionDto transactionDto = transactionService.findByUuid(cardnetJobDto.getTransactionId());
                ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByMerchantResponseStatus(ETransactionResultStatus.CANCELLED);
                transactionDto.setStatus(transactionStatusDto);
                transactionService.update(transactionDto);

                cardnetJobDto.setIsProcessed(true);
                cardnetJobService.update(cardnetJobDto);
            }

        });
    }

    public void update(CardnetProcessErrorLogDto dto) {
        CardnetProcessErrorLog entity = new CardnetProcessErrorLog(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCradentProcessErrorCommand.save(entity);
    }
    public void create(CardnetProcessErrorLogDto dto) {
        CardnetProcessErrorLog data = new CardnetProcessErrorLog(dto);
        this.repositoryCradentProcessErrorCommand.save(data);
    }

    public void createOrUpdate(CardnetProcessErrorLogDto dto){
        if(repositoryCradentProcessErrorQuery.existsByTransactionId(dto.getTransactionId()))
            update(dto);
            else create(dto);

    }
}