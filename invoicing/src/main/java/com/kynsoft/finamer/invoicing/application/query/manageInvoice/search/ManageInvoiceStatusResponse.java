package com.kynsoft.finamer.invoicing.application.query.manageInvoice.search;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class ManageInvoiceStatusResponse {
        private UUID id;
        private String code;
        private String name;
        private Boolean showClone;

        public ManageInvoiceStatusResponse(ManageInvoiceStatusDto projection) {
            this.id = projection.getId();
            this.code = projection.getCode();
            this.name = projection.getName();
            this.showClone = projection.getShowClone();
        }
}
