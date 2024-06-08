package com.kynsoft.notification.domain.service;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.notification.domain.dto.MailjetConfigurationDto;
import com.kynsoft.notification.infrastructure.entity.MailjetConfiguration;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IMailjetConfigurationService {
    UUID create(MailjetConfigurationDto object);
    void update(MailjetConfiguration object);
    void delete(MailjetConfigurationDto object);
    MailjetConfigurationDto findById(UUID id);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
