package com.kynsoft.notification.infrastructure.service;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.notification.application.query.advertisingcontent.getById.AdvertisingContentResponse;
import com.kynsoft.notification.domain.dto.AdvertisingContentDto;
import com.kynsoft.notification.domain.dto.ContentType;
import com.kynsoft.notification.domain.service.IAdvertisingContentService;
import com.kynsoft.notification.infrastructure.entity.AdvertisingContent;
import com.kynsoft.notification.infrastructure.repository.command.AdvertisingContentWriteDataJPARepository;
import com.kynsoft.notification.infrastructure.repository.query.AdvertisingContentReadDataJPARepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class AdvertisingContentServiceImpl implements IAdvertisingContentService {

    @Autowired
    private AdvertisingContentWriteDataJPARepository commandRepository;

    @Autowired
    private AdvertisingContentReadDataJPARepository queryRepository;

    @Override
    public void create(AdvertisingContentDto object) {
        this.commandRepository.save(new AdvertisingContent(object));
    }

    @Override
    public void update(AdvertisingContentDto object) {
        this.commandRepository.save(new AdvertisingContent(object));
    }

    @Override
    public void delete(AdvertisingContentDto object) {
        AdvertisingContent delete = new AdvertisingContent(object);
        delete.setDeleted(Boolean.TRUE);
        delete.setTitle(delete.getTitle() + " + " + UUID.randomUUID());

        this.commandRepository.save(delete);
    }

    @Override
    public AdvertisingContentDto findById(UUID id) {
        
        Optional<AdvertisingContent> object = this.queryRepository.findById(id);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND, new ErrorField("id", "AdvertisingContent not found.")));

    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCreteria(filterCriteria);
        GenericSpecificationsBuilder<AdvertisingContent> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<AdvertisingContent> data = this.queryRepository.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    private void filterCreteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {
            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    ContentType enumValue = ContentType.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Empresa: " + filter.getValue());
                }
            }
        }
    }

    private PaginatedResponse getPaginatedResponse(Page<AdvertisingContent> data) {
        List<AdvertisingContentResponse> patients = new ArrayList<>();
        for (AdvertisingContent o : data.getContent()) {
            patients.add(new AdvertisingContentResponse(o.toAggregate()));
        }
        return new PaginatedResponse(patients, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
