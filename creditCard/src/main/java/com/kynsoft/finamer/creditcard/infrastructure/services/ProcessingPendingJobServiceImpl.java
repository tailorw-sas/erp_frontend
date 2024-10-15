package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsoft.finamer.creditcard.domain.services.IProcessingPendingJobService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.CardnetJob;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.CardnetJobReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProcessingPendingJobServiceImpl implements IProcessingPendingJobService {

    @Autowired
    private RestTemplate restTemplate; // Necesario para llamar a otro endpoint (Enpoint para verificar si se peude actualizar la transacción)

       @Autowired
    private CardnetJobReadDataJPARepository repositoryQuery;

   public ProcessingPendingJobServiceImpl(CardnetJobReadDataJPARepository repositoryQuery){

        this.repositoryQuery = repositoryQuery;
    }

   public void checkIsProcessedAndCallTransactionStatus(){
       List<CardnetJob> list = repositoryQuery.findByIsProcessedFalse();
       processedCardnetJobListRecursive(list, 0);
   }
    // Método recursivo que procesa la lista
    private void processedCardnetJobListRecursive(List<CardnetJob> list, int index) {

        // Caso base: si hemos llegado al final de la lista, salimos de la recursión
        if (list.size()-1 >= index ) {
            String url = "http://localhost:9095/api/processMerchantCardNetResponse/{id}";
            restTemplate.getForEntity(url, String.class, list.get(index).getSession());

        } else {
            // Acción a ejecutar en cada elemento
            String url = "http://localhost:9094/api/processMerchantCardNetResponse/{id}";
            restTemplate.getForEntity(url, String.class, list.get(index).getSession());
            // Llamada recursiva con el siguiente índice
            processedCardnetJobListRecursive(list, index + 1);
        }
   }
}
