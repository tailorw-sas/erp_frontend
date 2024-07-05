package com.kynsoft.finamer.payment.domain.services;

import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;

import java.util.UUID;

public interface IManagePaymentStatusService {

    UUID create(ManagePaymentStatusDto dto);

    void update(ManagePaymentStatusDto dto);

    void delete(ManagePaymentStatusDto dto);

    ManagePaymentStatusDto findById(UUID uuid);
}
