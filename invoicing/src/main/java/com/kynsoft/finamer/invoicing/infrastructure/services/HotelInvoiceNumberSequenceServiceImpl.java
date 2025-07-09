package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.HotelInvoiceNumberSequenceResponse;
import com.kynsoft.finamer.invoicing.domain.dto.HotelInvoiceNumberSequenceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.invoicing.domain.services.IHotelInvoiceNumberSequenceService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.HotelInvoiceNumberSequence;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.HotelInvoiceNumberSequenceWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageHotelInvoiceNumberSequenceReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class HotelInvoiceNumberSequenceServiceImpl implements IHotelInvoiceNumberSequenceService {

    private final HotelInvoiceNumberSequenceWriteDataJPARepository repositoryCommand;

    private final ManageHotelInvoiceNumberSequenceReadDataJPARepository repositoryQuery;

    public HotelInvoiceNumberSequenceServiceImpl(HotelInvoiceNumberSequenceWriteDataJPARepository repositoryCommand, ManageHotelInvoiceNumberSequenceReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(HotelInvoiceNumberSequenceDto dto) {
        HotelInvoiceNumberSequence entity = new HotelInvoiceNumberSequence(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(HotelInvoiceNumberSequenceDto dto) {
        Optional<HotelInvoiceNumberSequence> optionalEntity = repositoryQuery.findById(dto.getId());

        if (optionalEntity.isPresent()) {
            HotelInvoiceNumberSequence entity = optionalEntity.get();
            entity.setInvoiceNo(dto.getInvoiceNo());
            repositoryCommand.save(entity);
        }

    }

    @Override
    public HotelInvoiceNumberSequenceDto findById(UUID id) {
        Optional<HotelInvoiceNumberSequence> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.HOTEL_INVOICE_NUMBER_SEQUENCE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.HOTEL_INVOICE_NUMBER_SEQUENCE_NOT_FOUND.getReasonPhrase())));

    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {

        GenericSpecificationsBuilder<ManageAgency> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<HotelInvoiceNumberSequence> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<HotelInvoiceNumberSequence> data) {
        List<HotelInvoiceNumberSequenceResponse> userSystemsResponses = new ArrayList<>();
        for (HotelInvoiceNumberSequence p : data.getContent()) {
            userSystemsResponses.add(new HotelInvoiceNumberSequenceResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public HotelInvoiceNumberSequenceDto getByHotelCodeAndInvoiceType(String code, EInvoiceType invoiceType) {
        Optional<HotelInvoiceNumberSequence> optionalEntity = repositoryQuery.getByHotelCodeAndInvoiceType(code, invoiceType);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.HOTEL_INVOICE_NUMBER_SEQUENCE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.HOTEL_INVOICE_NUMBER_SEQUENCE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public HotelInvoiceNumberSequenceDto getByTradingCompanyCodeAndInvoiceType(String code, EInvoiceType invoiceType) {
        Optional<HotelInvoiceNumberSequence> optionalEntity = repositoryQuery.getByTradingCompanyCodeAndInvoiceType(code, invoiceType);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.HOTEL_INVOICE_NUMBER_SEQUENCE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.HOTEL_INVOICE_NUMBER_SEQUENCE_NOT_FOUND.getReasonPhrase())));
    }

}
