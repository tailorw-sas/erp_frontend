package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;

public interface IImportInnsistService {

    void createInvoiceFromGroupedBooking(ImportInnsistKafka request);
}
