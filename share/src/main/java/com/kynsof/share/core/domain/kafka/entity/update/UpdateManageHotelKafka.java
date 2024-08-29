package com.kynsof.share.core.domain.kafka.entity.update;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

//@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UpdateManageHotelKafka implements Serializable {
    private UUID id;
    private String name;
    private Boolean isApplyByVCC;
    private UUID manageTradingCompany;
    private String status;
    private Boolean isVirtual;
    private boolean requiresFlatRate;
    private Boolean applyByTradingCompany;
    private Boolean autoApplyCredit;
}
