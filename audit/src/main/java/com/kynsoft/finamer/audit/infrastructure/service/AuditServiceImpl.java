package com.kynsoft.finamer.audit.infrastructure.service;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.audit.application.query.audit.getall.AuditResponse;
import com.kynsoft.finamer.audit.application.service.AuditService;
import com.kynsoft.finamer.audit.domain.dto.AuditRecordDto;
import com.kynsoft.finamer.audit.domain.elastic.Audit;
import com.kynsoft.finamer.audit.infrastructure.repository.AuditElasticsearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditElasticsearchRepository repository;

    public AuditServiceImpl(AuditElasticsearchRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(AuditRecordDto auditRecordDto) {
        Audit audit = new Audit(null,auditRecordDto.getEntityName(),
                auditRecordDto.getAction(),
                auditRecordDto.getUsername(),
                auditRecordDto.getData(),
                LocalDateTime.now(),
                auditRecordDto.getTag());
        repository.save(audit);
    }

    @Override
    public PaginatedResponse findAll(Pageable pageable) {
        return getPaginatedResponse(repository.findAll(pageable));
    }

    private PaginatedResponse getPaginatedResponse(Page<Audit> data) {
        List<AuditResponse> patients = new ArrayList<>();
        for (Audit o : data.getContent()) {
            AuditRecordDto auditRecordDto = new AuditRecordDto(o.getEntityName(),o.getUsername(),o.getAction(),o.getData(),o.getTag());
            patients.add(new AuditResponse(auditRecordDto));
        }
        return new PaginatedResponse(patients, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
