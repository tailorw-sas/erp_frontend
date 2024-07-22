package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePaymentSourceResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagePaymentSource;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagePaymentSourceWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagePaymentSourceReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagePaymentSourceServiceImpl implements IManagePaymentSourceService {

    @Autowired
    private ManagePaymentSourceReadDataJPARepository repositoryQuery;

    @Autowired
    private ManagePaymentSourceWriteDataJPARepository repositoryCommand;

    @Override
    public UUID create(ManagePaymentSourceDto dto) {
        ManagePaymentSource entity = new ManagePaymentSource(dto);
        ManagePaymentSource saved = repositoryCommand.save(entity);

        return saved.getId();
    }

    @Override
    public void update(ManagePaymentSourceDto dto) {
        ManagePaymentSource update = new ManagePaymentSource(dto);
        update.setUpdatedAt(LocalDateTime.now());
        repositoryCommand.save(update);
    }

    @Override
    public void delete(ManagePaymentSourceDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagePaymentSourceDto findById(UUID id) {
        Optional<ManagePaymentSource> optionalEntity = repositoryQuery.findById(id);
        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NOT_FOUND, new ErrorField("id", "The manager payment source not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManagePaymentSource> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagePaymentSource> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagePaymentSource> data) {
        List<ManagePaymentSourceResponse> responseList = new ArrayList<>();
        for (ManagePaymentSource entity : data.getContent()) {
            responseList.add(new ManagePaymentSourceResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public List<ManagePaymentSourceDto> findAllToReplicate() {
        List<ManagePaymentSource> objects = this.repositoryQuery.findAll();
        List<ManagePaymentSourceDto> objectDtos = new ArrayList<>();

        for (ManagePaymentSource object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

}
