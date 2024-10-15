package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.shareFile.search.PaymentShareFileResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentShareFileDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IPaymentShareFileService;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentShareFile;
import com.kynsoft.finamer.payment.infrastructure.repository.command.PaymentShareFileWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.PaymentShareFileReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentShareFileServiceImpl implements IPaymentShareFileService {

    private final PaymentShareFileWriteDataJPARepository repositoryCommand;

    private final PaymentShareFileReadDataJPARepository repositoryQuery;

    public PaymentShareFileServiceImpl(PaymentShareFileWriteDataJPARepository repositoryCommand, PaymentShareFileReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(PaymentShareFileDto dto) {
        PaymentShareFile data = new PaymentShareFile(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(PaymentShareFileDto dto) {
        PaymentShareFile update = new PaymentShareFile(dto);

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(PaymentShareFileDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public PaymentShareFileDto findById(UUID id) {
        Optional<PaymentShareFile> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.ATTACHMENT_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.ATTACHMENT_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<PaymentShareFile> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<PaymentShareFile> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<PaymentShareFile> data) {
        List<PaymentShareFileResponse> responses = new ArrayList<>();
        for (PaymentShareFile p : data.getContent()) {
            responses.add(new PaymentShareFileResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
