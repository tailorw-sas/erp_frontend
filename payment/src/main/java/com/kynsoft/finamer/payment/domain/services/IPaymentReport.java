package com.kynsoft.finamer.payment.domain.services;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public interface IPaymentReport {

    Optional<byte[]> generateReport(UUID paymentId) throws IOException;
}
