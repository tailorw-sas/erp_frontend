package com.kynsoft.finamer.insis.infrastructure.services.kafka.consumer.manageHotel;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.request.SortTypeEnum;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.query.manageHotel.search.GetSearchManageHotelQuery;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageHotel.ManageHotelResponse;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageHotelKafka;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageHotels;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageHotel.ProducerReplicaAllManageHotelService;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsumerRequestReplicateManageHotelService {

    private final IMediator mediator;
    public final ProducerReplicaAllManageHotelService producerReplicaManageHotelService;

    public ConsumerRequestReplicateManageHotelService(IMediator mediator, ProducerReplicaAllManageHotelService producerReplicaManageHotelService){
        this.mediator = mediator;
        this.producerReplicaManageHotelService = producerReplicaManageHotelService;
    }

    /*
    @KafkaListener(topics = "finamer-tcaInnsist-replicate-all-manage-hotels", groupId = "innsist-hotel-sync")
    public void listen(String mensaje){
        System.out.println("Recibí mensaje en kafka: " + mensaje);

        SearchRequest request = new SearchRequest();
        request.setFilter(new ArrayList<>());
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        request.setSortType(SortTypeEnum.ASC);
        request.setSortBy("code");

        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchManageHotelQuery getQuery = new GetSearchManageHotelQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(getQuery);

        ReplicateManageHotels replicateManageHotels = createReplicateManageHotels(data.getData());
        producerReplicaManageHotelService.create(replicateManageHotels);
    }

    private ReplicateManageHotels createReplicateManageHotels(List data){
        List<ReplicateManageHotelKafka> hotels = new ArrayList<>();
        for (Object item : data){
            hotels.add(convertHotelResponseToHotelKafka((ManageHotelResponse) item));
        }
        return new ReplicateManageHotels(hotels);
    }

    private static ReplicateManageHotelKafka convertHotelResponseToHotelKafka(ManageHotelResponse hotelResponse){
        ReplicateManageHotelKafka manageHotelKafka = new ReplicateManageHotelKafka(hotelResponse);
        return manageHotelKafka;
    }*/
}
