package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileManual;

import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReconcileManualAgencyResponse {
    private String name;

    public ReconcileManualAgencyResponse(ManageAgencyDto dto){
        this.name = dto.getName();
    }
}
