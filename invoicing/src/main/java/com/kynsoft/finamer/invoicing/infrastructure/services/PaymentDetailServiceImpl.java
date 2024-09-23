package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.PaymentDetailResponse;
import com.kynsoft.finamer.invoicing.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.invoicing.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.PaymentDetail;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManagePaymentDetailWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManagePaymentDetailReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentDetailServiceImpl implements IPaymentDetailService {

    @Autowired
    private ManagePaymentDetailWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManagePaymentDetailReadDataJPARepository repositoryQuery;

    @Override
    public Long create(PaymentDetailDto dto) {
        PaymentDetail data = new PaymentDetail(dto);
        return this.repositoryCommand.save(data).getPaymentDetailId();
    }

    @Override
    public void update(PaymentDetailDto dto) {
        PaymentDetail update = new PaymentDetail(dto);

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(PaymentDetailDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public PaymentDetailDto findById(UUID id) {
        Optional<PaymentDetail> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.PAYMENT_DETAIL_NOT_FOUND, new ErrorField("id", DomainErrorMessage.PAYMENT_DETAIL_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {

        GenericSpecificationsBuilder<PaymentDetail> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<PaymentDetail> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<PaymentDetail> data) {
        List<PaymentDetailResponse> responses = new ArrayList<>();
        for (PaymentDetail p : data.getContent()) {
            responses.add(new PaymentDetailResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
