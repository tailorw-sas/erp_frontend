package com.kynsof.share.core.domain.kafka.entity.importInnsist;

import com.kynsof.share.core.domain.response.ErrorField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RoomRateResponseKafka {
    private UUID innsistBookingId;
    private UUID innsistRoomRateId;
    private UUID invoiceId;
    private List<ErrorField> errorFields;
}
