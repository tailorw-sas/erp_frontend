package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageBookingResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageBooking;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageBookingWriteDataJpaRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageBookingReadDataJPARepository;
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
public class ManageBookingServiceImpl implements IManageBookingService {

    @Autowired
    private final ManageBookingWriteDataJpaRepository repositoryCommand;

    @Autowired
    private final ManageBookingReadDataJPARepository repositoryQuery;

    public ManageBookingServiceImpl(ManageBookingWriteDataJpaRepository repositoryCommand,
            ManageBookingReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }
    @Override
    public void calculateInvoiceAmount(ManageBookingDto dto){
        Double InvoiceAmount = 0.00;


        

        if (dto.getRoomRates() != null) {

            for (int i = 0; i < dto.getRoomRates().size(); i++) {

                InvoiceAmount += dto.getRoomRates().get(i).getInvoiceAmount();

            }

            dto.setInvoiceAmount(InvoiceAmount);
            dto.setDueAmount(InvoiceAmount);

            this.update(dto);
        }
    }

    @Override
    public void calculateHotelAmount(ManageBookingDto dto){
        Double HotelAmount = 0.00;




        if (dto.getRoomRates() != null) {

            for (int i = 0; i < dto.getRoomRates().size(); i++) {

                HotelAmount += dto.getRoomRates().get(i).getHotelAmount();

            }

            dto.setHotelAmount(HotelAmount);

            this.update(dto);
        }
    }

    @Override
    public UUID create(ManageBookingDto dto) {
        ManageBooking entity = new ManageBooking(dto);
        return repositoryCommand.saveAndFlush(entity).getId();
    }

    @Override
    public void update(ManageBookingDto dto) {
        ManageBooking entity = new ManageBooking(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageBooking> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageBooking> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public boolean existsByExactLastChars(String lastChars, UUID hotelId) {
        boolean exists = this.repositoryQuery.existsByExactLastChars(lastChars, hotelId);

        return exists;
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageBooking> data) {
        List<ManageBookingResponse> responseList = new ArrayList<>();
        for (ManageBooking entity : data.getContent()) {
            responseList.add(new ManageBookingResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public void delete(ManageBookingDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public boolean existByBookingHotelNumber(String bookingHotelNumber) {
        return repositoryQuery.existsByHotelBookingNumber(bookingHotelNumber);
    }


    @Override
    public ManageBookingDto findById(UUID id) {
        Optional<ManageBooking> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND,
                new ErrorField("id", "The source not found.")));

    }

    @Override
    public List<ManageBookingDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageBooking::toAggregate).toList();
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

            if ("dueAmount".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    filter.setValue(Double.valueOf(filter.getValue().toString()));
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Status: " + filter.getValue());
                }
            }
        }
    }

}
