package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.EMailjetType;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.templateEntity.getById.TemplateEntityResponse;
import com.kynsoft.finamer.creditcard.domain.dto.TemplateDto;
import com.kynsoft.finamer.creditcard.domain.services.ITemplateEntityService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.TemplateEntity;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.TemplateEntityWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.TemplateEntityReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TemplateEntityServiceImpl implements ITemplateEntityService {

    @Autowired
    private TemplateEntityWriteDataJPARepository commandRepository;

    @Autowired
    private TemplateEntityReadDataJPARepository queryRepository;

    @Override
    public UUID create(TemplateDto object) {
        TemplateEntity entity = this.commandRepository.save(new TemplateEntity(object));
        return entity.getId();
    }

    @Override
    public void update(TemplateEntity object) {
        this.commandRepository.save(object);
    }

    @Override
    public void delete(TemplateDto object) {
        TemplateEntity delete = new TemplateEntity(object);
        delete.setDeleted(Boolean.TRUE);
        delete.setTemplateCode(delete.getTemplateCode() + " + " + UUID.randomUUID());
        delete.setName(delete.getName()+ " + " + UUID.randomUUID());

        this.commandRepository.save(delete);
    }

    @Override
    public TemplateDto findById(UUID id) {
        Optional<TemplateEntity> templateEntity = this.queryRepository.findById(id);
        return templateEntity.map(TemplateEntity::toAggregate).orElse(null);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<TemplateEntity> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<TemplateEntity> data = this.queryRepository.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    @Override
    public TemplateDto findByTemplateCode(String templateCode) {
        Optional<TemplateEntity> templateEntity = this.queryRepository.findByTemplateCode(templateCode);
        return templateEntity.map(TemplateEntity::toAggregate).orElse(null);
    }

    @Override
    public TemplateDto findByLanguageCodeAndType(String languageCode, EMailjetType type) {
        List<TemplateEntity> templateEntities = this.queryRepository.findByLanguageCodeAndType(languageCode, type.name());
        if (!templateEntities.isEmpty()) {
            return templateEntities.get(0).toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MAILJET_TEMPLATE_NOT_FOUND, new ErrorField("type, language", DomainErrorMessage.MAILJET_TEMPLATE_NOT_FOUND.getReasonPhrase())));
    }

    private PaginatedResponse getPaginatedResponse(Page<TemplateEntity> data) {
        List<TemplateEntityResponse> response = new ArrayList<>();
        for (TemplateEntity p : data.getContent()) {
            response.add(new TemplateEntityResponse(p.toAggregate()));
        }
        return new PaginatedResponse(response, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
