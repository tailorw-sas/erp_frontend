package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.MerchantLanguageCodeResponse;
import com.kynsoft.finamer.creditcard.domain.dto.MerchantLanguageCodeDto;
import com.kynsoft.finamer.creditcard.domain.services.IMerchantLanguageCodeService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.MerchantLanguageCode;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.MerchantLanguageCodeWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.MerchantLanguageCodeReadDataJPARepository;
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
public class MerchantLanguageCodeServiceImpl implements IMerchantLanguageCodeService {

    @Autowired
    private final MerchantLanguageCodeWriteDataJPARepository repositoryCommand;

    @Autowired
    private final MerchantLanguageCodeReadDataJPARepository repositoryQuery;

    public MerchantLanguageCodeServiceImpl(MerchantLanguageCodeWriteDataJPARepository repositoryCommand, MerchantLanguageCodeReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public MerchantLanguageCodeDto create(MerchantLanguageCodeDto dto) {
        MerchantLanguageCode entity = new MerchantLanguageCode(dto);
        return this.repositoryCommand.save(entity).toAggregate();
    }

    @Override
    public void update(MerchantLanguageCodeDto dto) {
        MerchantLanguageCode entity = new MerchantLanguageCode(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        this.repositoryCommand.save(entity);
    }

    @Override
    public void delete(MerchantLanguageCodeDto dto) {
        MerchantLanguageCode entity = new MerchantLanguageCode(dto);
        try{
            this.repositoryCommand.delete(entity);
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public MerchantLanguageCodeDto findById(UUID id) {
        Optional<MerchantLanguageCode> optional = this.repositoryQuery.findById(id);
        if (optional.isPresent()){
            return optional.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MERCHANT_LANGUAGE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MERCHANT_LANGUAGE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<MerchantLanguageCode> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<MerchantLanguageCode> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByManageMerchantAndMerchantLanguageAndNotId(UUID manageMerchant, UUID manageLanguage, UUID id) {
        return this.repositoryQuery.countByManageMerchantAndMerchantLanguageAndNotId(manageMerchant, manageLanguage, id);
    }

    private PaginatedResponse getPaginatedResponse(Page<MerchantLanguageCode> data) {
        List<MerchantLanguageCodeResponse> responses = new ArrayList<>();
        for (MerchantLanguageCode p : data.getContent()) {
            responses.add(new MerchantLanguageCodeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
