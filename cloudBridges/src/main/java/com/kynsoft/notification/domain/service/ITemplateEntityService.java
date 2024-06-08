package com.kynsoft.notification.domain.service;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.notification.domain.dto.TemplateDto;
import com.kynsoft.notification.infrastructure.entity.TemplateEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITemplateEntityService {
    UUID create(TemplateDto object);
    void update(TemplateEntity object);
    void delete(TemplateDto object);
    TemplateDto findById(UUID id);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
    TemplateDto findByTemplateCode(String templateCode);
}
