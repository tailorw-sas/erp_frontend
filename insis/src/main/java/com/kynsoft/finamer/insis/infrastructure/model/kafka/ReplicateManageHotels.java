package com.kynsoft.finamer.insis.infrastructure.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReplicateManageHotels implements Serializable {

    public List<ReplicateManageHotelKafka> manageHotels;

}
