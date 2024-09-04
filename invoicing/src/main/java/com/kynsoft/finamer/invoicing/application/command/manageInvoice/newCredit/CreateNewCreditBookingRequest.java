package com.kynsoft.finamer.invoicing.application.command.manageInvoice.newCredit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNewCreditBookingRequest {

    private UUID id;
    private Double amount;
}
