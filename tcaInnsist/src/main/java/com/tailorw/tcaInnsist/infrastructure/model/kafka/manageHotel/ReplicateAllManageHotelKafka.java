package com.tailorw.tcaInnsist.infrastructure.model.kafka.manageHotel;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ReplicateAllManageHotelKafka implements Serializable {
    public List<ReplicateManageHotelKafka> manageHotels;
}
