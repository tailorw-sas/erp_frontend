package com.kynsoft.finamer.invoicing.application.command.manageInvoice.partialClone;

import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartialCloneInvoiceAdjustmentRelation {
    private UUID roomRate;
    private CreateAdjustmentCommand adjustment;
    
}
