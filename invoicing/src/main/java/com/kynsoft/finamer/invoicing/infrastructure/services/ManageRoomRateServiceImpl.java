package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageRoomRateResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageAdjustmentService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Booking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAdjustment;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomRate;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageRoomRateWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageBookingReadDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageRoomRateReadDataJPARepository;
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
public class ManageRoomRateServiceImpl implements IManageRoomRateService {

    @Autowired
    private final ManageRoomRateWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageRoomRateReadDataJPARepository repositoryQuery;

    private final ManageBookingReadDataJPARepository manageBookingReadDataJPARepository;

    private final IManageAdjustmentService adjustmentService;

    public ManageRoomRateServiceImpl(ManageRoomRateWriteDataJPARepository repositoryCommand, ManageRoomRateReadDataJPARepository repositoryQuery,
                                     ManageBookingReadDataJPARepository manageBookingReadDataJPARepository,
                                     IManageAdjustmentService adjustmentService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.manageBookingReadDataJPARepository = manageBookingReadDataJPARepository;
        this.adjustmentService = adjustmentService;
    }

    @Override
    public void calculateInvoiceAmount(ManageRoomRateDto dto, Double adjustmentOldAmount, Double adjustmentNewAmount) {
        Double InvoiceAmount = dto.getInvoiceAmount() != null ? dto.getInvoiceAmount() : 0;

        if (adjustmentOldAmount != null && adjustmentNewAmount != null) {

            InvoiceAmount -= adjustmentOldAmount;
            InvoiceAmount += adjustmentNewAmount;

            dto.setInvoiceAmount(InvoiceAmount);

            this.update(dto);
            return;
        }

        if (dto.getAdjustments() != null) {

            for (int i = 0; i < dto.getAdjustments().size(); i++) {

                InvoiceAmount += dto.getAdjustments().get(i).getAmount();

            }

            dto.setInvoiceAmount(InvoiceAmount);

            this.update(dto);
        }
    }

    @Override
    public UUID create(ManageRoomRateDto dto) {
        ManageRoomRate entity = new ManageRoomRate(dto);
        return repositoryCommand.saveAndFlush(entity).getId();
    }

    @Override
    public UUID insert(ManageRoomRateDto dto){
        ManageRoomRate roomRate = new ManageRoomRate(dto);
        this.repositoryCommand.insert(roomRate);

        dto.setRoomRateId(roomRate.getRoomRateId());
        dto.setId(roomRate.getId());

        if(dto.getAdjustments() != null && !dto.getAdjustments().isEmpty()){
            dto.setAdjustments(this.adjustmentService.insertAll(dto.getAdjustments()));
        }
        return roomRate.getId();
    }

    @Override
    public List<ManageRoomRateDto> insertAll(List<ManageRoomRateDto> roomRateList) {
        for(ManageRoomRateDto roomRateDto : roomRateList){
            this.insert(roomRateDto);
        }
        return roomRateList;
    }

    @Override
    public void update(ManageRoomRateDto dto) {
        ManageRoomRate entity = new ManageRoomRate(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageRoomRate> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageRoomRate> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageRoomRate> data) {
        List<ManageRoomRateResponse> responseList = new ArrayList<>();
        for (ManageRoomRate entity : data.getContent()) {
            responseList.add(new ManageRoomRateResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<ManageRoomRateDto> findByBooking(UUID bookingId) {
        Booking booking = this.manageBookingReadDataJPARepository.findById(bookingId).orElse(null);
        return this.repositoryQuery.findByBooking(booking).stream().map(manageRoomRate -> {
            return new ManageRoomRateDto(manageRoomRate.toAggregate());
        }).toList();
    }

    @Override
    public void delete(ManageRoomRateDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageRoomRateDto findById(UUID id) {
        Optional<ManageRoomRate> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.ROOM_RATE_NOT_FOUND_, new ErrorField("id", "The Room Rate not found.")));

    }

    @Override
    public void deleteInvoice(ManageRoomRateDto dto) {
        ManageRoomRate entity = new ManageRoomRate(dto);
        entity.setDeleteInvoice(true);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    private void insert(ManageRoomRate roomRate){
        this.repositoryCommand.insert(roomRate);

        if(roomRate.getAdjustments() != null && !roomRate.getAdjustments().isEmpty()){
            this.adjustmentService.createAll(roomRate.getAdjustments());
        }
    }

    @Override
    public void createAll(List<ManageRoomRate> roomRates) {
        for(ManageRoomRate roomRate : roomRates){
            this.insert(roomRate);
        }
    }

    @Override
    public List<ManageRoomRateDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageRoomRate::toAggregate).toList();
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

}
