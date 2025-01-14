package com.tailorw.tcaInnsist.infrastructure.service.batch;

import com.tailorw.tcaInnsist.domain.services.ITcaCatalogService;
import com.tailorw.tcaInnsist.domain.services.IManageConnectionService;
import com.tailorw.tcaInnsist.domain.services.IManageHotelService;
import com.tailorw.tcaInnsist.infrastructure.service.kafka.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TcaCatalogServiceImpl implements ITcaCatalogService {

    private final IManageHotelService manageHotelService;
    private final IManageConnectionService tcaConfigurationService;
    private final ProducerRequestReplicateManageHotelsService producerManageHotelsService;
    private final ProducerRequestReplicateTcaConfigurationPropertiesService producerTcaConfigurationPropertiesService;
    private static final Logger logger = LoggerFactory.getLogger(TcaCatalogServiceImpl.class);

    public TcaCatalogServiceImpl (IManageHotelService manageHotelService,
                                  IManageConnectionService tcaConfigurationService,
                                  ProducerRequestReplicateManageHotelsService producerManageHotelsService,

                                  ProducerRequestReplicateTcaConfigurationPropertiesService producerTcaConfigurationPropertiesService){
        this.manageHotelService = manageHotelService;
        this.tcaConfigurationService = tcaConfigurationService;
        this.producerManageHotelsService = producerManageHotelsService;
        this.producerTcaConfigurationPropertiesService = producerTcaConfigurationPropertiesService;
    }

    @Override
    public void validateCatalog() {
        //validateHotelsInMemory();
        //validateTcaConfigurationPropertiesInMemory();
    }

    @Override
    //@Scheduled(fixedRate = 300000, initialDelay = 10000)
    public void validateIfManageHotelExists() {
        validateHotelsInMemory();
    }

    @Override
    //@Scheduled(fixedRate = 300000, initialDelay = 10000)
    public void validateIfTcaConfigurationPropertiesExist() {
        validateTcaConfigurationPropertiesInMemory();
    }

    private void validateHotelsInMemory() {
        if(!manageHotelService.existsHotels()){
            sendKafkaMessageForHotels();
        }
    }

    private void validateTcaConfigurationPropertiesInMemory(){
        if(!tcaConfigurationService.exists()){
            sendKafkaMessageForTcaConfigurationProperties();
        }
    }

    public void sendKafkaMessageForHotels(){
        try{
            producerManageHotelsService.create();
        }catch (Exception ex){
            logger.error("Error al enviar mensaje a Kafka en sendKafkaMessageForHotels", ex);
        }
    }

    public void sendKafkaMessageForTcaConfigurationProperties(){
        try{
            producerTcaConfigurationPropertiesService.create();
        }catch (Exception ex){
            logger.error("Error al enviar mensaje a Kafka en sendKafkaMessageForTcaConfigurationProperties", ex);
        }
    }
}
