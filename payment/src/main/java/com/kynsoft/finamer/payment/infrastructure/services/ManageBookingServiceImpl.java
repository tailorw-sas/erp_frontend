package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageBookingResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.infrastructure.identity.Booking;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageBookingWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageBookingReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageBookingServiceImpl implements IManageBookingService {

    @Autowired
    private ManageBookingWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageBookingReadDataJPARepository repositoryQuery;

    @Override
    public void create(ManageBookingDto dto) {
        this.repositoryCommand.save(new Booking(dto));
    }

    @Override
    public void update(ManageBookingDto dto) {
        this.repositoryCommand.save(new Booking(dto));
    }

    @Override
    public ManageBookingDto findById(UUID id) {
        Optional<Booking> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BOOKING_NOT_FOUND, new ErrorField("id", DomainErrorMessage.BOOKING_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public ManageBookingDto findByGenId(long id) {
        Optional<Booking> booking = this.repositoryQuery.findManageBookingByBookingId(id);
        if (booking.isPresent()) {
            return booking.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BOOKING_NOT_FOUND, new ErrorField("booking Id", DomainErrorMessage.BOOKING_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Booking> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Booking> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public boolean exitBookingByGenId(long id) {
        return repositoryQuery.existsManageBookingByBookingId(id);
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

    private PaginatedResponse getPaginatedResponse(Page<Booking> data) {
        List<ManageBookingResponse> responses = new ArrayList<>();
        for (Booking p : data.getContent()) {
            responses.add(new ManageBookingResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public void deleteAll() {
        this.repositoryCommand.deleteAll();
    }

}
