package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManagePaymentSourceResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentSource;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManagePaymentSourceWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManagePaymentSourceReadDataJPARepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ManagePaymentSourceServiceImpl implements IManagePaymentSourceService {

    @Autowired
    private ManagePaymentSourceReadDataJPARepository repositoryQuery;

    @Autowired
    private ManagePaymentSourceWriteDataJPARepository repositoryCommand;

    @Override
    public UUID create(ManagePaymentSourceDto dto) {
        ManagePaymentSource entity = new ManagePaymentSource(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManagePaymentSourceDto dto) {
        ManagePaymentSource update = new ManagePaymentSource(dto);
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

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NOT_FOUND.getReasonPhrase())));
    }
    @Override
    public ManagePaymentSourceDto findByCodeActive(String code) {
        Optional<ManagePaymentSource> optionalEntity = repositoryQuery.findByCodeAndStatus(code,Status.ACTIVE.name());
        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NOT_FOUND, new ErrorField("code", DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManagePaymentSource> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagePaymentSource> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private void filterCriteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {

            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    Status enumValue = Status.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Status: " + filter.getValue());
                }
            }
        }
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagePaymentSource> data) {
        List<ManagePaymentSourceResponse> responses = new ArrayList<>();
        for (ManagePaymentSource p : data.getContent()) {
            responses.add(new ManagePaymentSourceResponse(p.toAggregate()));
        }

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public ManagePaymentSourceDto findByExpense() {
        Optional<ManagePaymentSource> optionalEntity = repositoryQuery.findByExpense();
        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NOT_FOUND.getReasonPhrase())));
        
    }

}
