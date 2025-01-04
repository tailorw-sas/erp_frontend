package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.dto.TemplateDto;
import com.kynsoft.finamer.payment.infrastructure.identity.TemplateEntity;
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
