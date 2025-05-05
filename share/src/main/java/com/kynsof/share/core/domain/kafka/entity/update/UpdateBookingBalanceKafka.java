package com.kynsof.share.core.domain.kafka.entity.update;

import com.kynsof.share.core.domain.kafka.entity.ReplicateBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateBookingBalanceKafka {
    List<ReplicateBookingKafka> bookingsKafka;
}
