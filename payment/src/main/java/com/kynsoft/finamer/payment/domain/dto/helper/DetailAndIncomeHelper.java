package com.kynsoft.finamer.payment.domain.dto.helper;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DetailAndIncomeHelper implements Serializable {

    private UUID incomeId;
    private UUID paymentDetailId;
}
