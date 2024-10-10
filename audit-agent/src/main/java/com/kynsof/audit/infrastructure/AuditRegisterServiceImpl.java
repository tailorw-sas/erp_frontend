package com.kynsof.audit.infrastructure;

import com.kynsof.audit.application.service.IAuditRegisterService;
import com.kynsof.audit.domain.dto.AuditRegisterDto;
import com.kynsof.audit.domain.dto.AuditRegisterRecordDto;
import com.kynsof.audit.infrastructure.redis.query.AuditRegisterRepository;
import com.kynsof.share.core.domain.audit.redis.AuditRegisterEntity;
import org.springframework.stereotype.Service;

@Service
public class AuditRegisterServiceImpl implements IAuditRegisterService {

    private final AuditRegisterRepository repository;

    public AuditRegisterServiceImpl(AuditRegisterRepository auditRegisterRepository) {
        this.repository = auditRegisterRepository;
    }

    @Override
    public void registerEntity(AuditRegisterDto auditRegisterDto) {
        AuditRegisterEntity auditRegisterEntity = new AuditRegisterEntity(null,auditRegisterDto.getEntityName(),false,false,false);
        repository.save(auditRegisterEntity);
    }


    @Override
    public AuditRegisterRecordDto getAuditRegisterService(String entityName) {

       return repository.findAuditRegisterEntityByServiceName(entityName)
               .map(auditRegisterEntity -> new AuditRegisterRecordDto(auditRegisterEntity.getId(),
                auditRegisterEntity.getEntityName(),
                auditRegisterEntity.isAuditUpdate(),
                auditRegisterEntity.isAuditCreate(),
                auditRegisterEntity.isAuditDelete()
        )).orElse(null);
    }
}
