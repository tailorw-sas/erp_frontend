package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileManual;

import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReconcileManualHotelResponse {
    private String code;
    private String name;

    public ReconcileManualHotelResponse(ManageHotelDto dto){
        this.code = dto.getCode();
        this.name = dto.getName();
    }
}
