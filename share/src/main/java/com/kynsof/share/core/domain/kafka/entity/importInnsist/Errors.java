package com.kynsof.share.core.domain.kafka.entity.importInnsist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Errors {
    private String bookingId;
    private String msg;
}
