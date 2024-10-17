package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageStatusTransaction.update.UpdateManageStatusTransactionCommand;
import com.kynsoft.finamer.creditcard.domain.dto.CardnetJobDto;
import com.kynsoft.finamer.creditcard.domain.dto.CardnetProcessErrorLogDto;
import com.kynsoft.finamer.creditcard.domain.services.IProcessingPendingJobService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetJob;
import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetProcessErrorLog;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.CardnetProcessErrorLogWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.CardnetJobReadDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.CardnetProcessErrorLogReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private CardnetJobReadDataJPARepository repositoryQuery;

    @Autowired
    TransactionServiceImpl transactionService;

    private final IMediator mediator;

    public ProcessingPendingJobServiceImpl(CardnetProcessErrorLogReadDataJPARepository repositoryCradentProcessErrorQuery,CardnetProcessErrorLogWriteDataJPARepository repositoryCradentProcessErrorCommand, CardNetJobServiceImpl cardnetJobService, CardnetJobReadDataJPARepository repositoryQuery, IMediator mediator, TransactionServiceImpl transactionService) {
        this.cardnetJobService = cardnetJobService;
        this.repositoryQuery = repositoryQuery;
        this.mediator = mediator;
        this.repositoryCradentProcessErrorCommand = repositoryCradentProcessErrorCommand;
        this.repositoryCradentProcessErrorQuery = repositoryCradentProcessErrorQuery;
        this.transactionService = transactionService;
    }

//    @Scheduled(fixedRate = 600000)  // se ejecuta el método cada 600000 ms = 10 minutos
    public void checkIsProcessedAndCallTransactionStatus() {

        LocalDate yesterdaynew = LocalDate.now().minusDays(1);
        LocalDateTime yesterdaydate = yesterdaynew.atStartOfDay();
        List<CardnetJob> list = repositoryQuery.findByIsProcessedFalse(yesterdaydate);
        if (!list.isEmpty()) {
            processedCardnetJobListRecursive(list, 0);
        }
    }

    // Método recursivo que procesa la lista
    private void processedCardnetJobListRecursive(List<CardnetJob> list, int index) {
        CardnetJobDto cardnetJobDto = cardnetJobService.findByTransactionId(list.get(index).getTransactionId());
        CardnetProcessErrorLogDto cardnetProcessErrorLogDto = new CardnetProcessErrorLogDto();
        // Verificamos si hemos llegado al final de la lista(Condición de parada)
        if (index < list.size()) {
            try {
                // Acción a ejecutar en cada elemento
                UpdateManageStatusTransactionCommand updateManageStatusTransactionCommandRequest = UpdateManageStatusTransactionCommand.builder()
                        .session(list.get(index).getSession())
                        .build();
                mediator.send(updateManageStatusTransactionCommandRequest);
                cardnetJobDto.setNumberOfAttempts(list.get(index).getNumberOfAttempts() + 1);
                cardnetJobService.update(cardnetJobDto);
                cardnetProcessErrorLogDto.setSession(list.get(index).getSession());
                cardnetProcessErrorLogDto.setTransactionId(list.get(index).getTransactionId());
                cardnetProcessErrorLogDto.setError("There is no mistake in this iteration");
                createOrUpdate(cardnetProcessErrorLogDto);

                //Verificamos que ya la transaccion este en estado RECIVIDO, si es TRUE mandamos el correo
                if(transactionService.confirmCreateTransaction(list.get(index).getTransactionId())){
                    transactionService.confirmTransactionMail(list.get(index).getTransactionId());
                }

            } catch (Exception e) {
                // Manejo de la excepción, por ejemplo, registrar el error
                cardnetProcessErrorLogDto.setSession(list.get(index).getSession());
                cardnetProcessErrorLogDto.setTransactionId(list.get(index).getTransactionId());
                cardnetProcessErrorLogDto.setError(e.getMessage());
                createOrUpdate(cardnetProcessErrorLogDto);

            } finally {
                // Llamada recursiva con el siguiente índice, ya sea que haya ocurrido o no una excepción

                processedCardnetJobListRecursive(list, index + 1);
            }
        }

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