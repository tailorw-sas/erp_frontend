package com.kynsoft.finamer.payment.infrastructure.services.helpers.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentDetailsRequest {

    private List<CreatePaymentDetail> paymentDetails;
}
