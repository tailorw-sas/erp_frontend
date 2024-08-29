package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageRoomRateResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageRoomRateService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageBooking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRoomRate;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageRoomRateWriteDataJPARepository;
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

    public ManageRoomRateServiceImpl(ManageRoomRateWriteDataJPARepository repositoryCommand, ManageRoomRateReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
     public void calculateInvoiceAmount(ManageRoomRateDto dto, Double adjustmentOldAmount, Double adjustmentNewAmount){
        Double InvoiceAmount = dto.getInvoiceAmount() != null ? dto.getInvoiceAmount() : 0;



        if(adjustmentOldAmount != null && adjustmentNewAmount != null){

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
    public List<ManageRoomRate> findByBooking(ManageBooking booking){
        return this.repositoryQuery.findByBooking(booking);
    }

    @Override
    public void delete(ManageRoomRateDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageRoomRateDto findById(UUID id) {
        Optional<ManageRoomRate> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND, new ErrorField("id", "The source not found.")));

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
