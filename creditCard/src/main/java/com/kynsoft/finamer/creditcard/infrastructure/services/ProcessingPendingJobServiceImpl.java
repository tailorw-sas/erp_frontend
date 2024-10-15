package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsoft.finamer.creditcard.domain.services.IProcessingPendingJobService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetJob;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.CardnetJobReadDataJPARepository;
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
    private RestTemplate restTemplate; // Necesario para llamar a otro endpoint (Enpoint para verificar si se peude actualizar la transacción)

    @Autowired
    private CardnetJobReadDataJPARepository repositoryQuery;


    public ProcessingPendingJobServiceImpl(CardnetJobReadDataJPARepository repositoryQuery) {

        this.repositoryQuery = repositoryQuery;
    }

    @Scheduled(fixedRate = 600000)  // se ejecuta el método cada 600000 ms = 10 minutos
    public void checkIsProcessedAndCallTransactionStatus() {

        LocalDate yesterdaynew = LocalDate.now().minusDays(1);
        LocalDateTime yesterdaydate = yesterdaynew.atStartOfDay();
        List<CardnetJob> list = repositoryQuery.findByIsProcessedFalse(yesterdaydate);
        processedCardnetJobListRecursive(list, 0);
    }

    // Método recursivo que procesa la lista
    private void processedCardnetJobListRecursive(List<CardnetJob> list, int index) {
        // Verificamos si hemos llegado al final de la lista(Condición de parada)
        if (index < list.size()) {
            try {
                // Acción a ejecutar en cada elemento
                String url = "http://localhost:9094/api/processMerchantCardNetResponse/{id}";
                restTemplate.getForEntity(url, String.class, list.get(index).getSession());
            } catch (Exception e) {
                // Manejo de la excepción, por ejemplo, registrar el error
                System.err.println("Error al procesar el índice " + index + ": " + e.getMessage());
            } finally {
                // Llamada recursiva con el siguiente índice, ya sea que haya ocurrido o no una excepción
                processedCardnetJobListRecursive(list, index + 1);
            }
        }
    }
}