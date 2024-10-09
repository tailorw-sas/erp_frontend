package com.kynsoft.finamer.creditcard.application.command.manageRedirectTransactionPayment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateRedirectTransactionPaymentRequest {
    private String token;
   }
