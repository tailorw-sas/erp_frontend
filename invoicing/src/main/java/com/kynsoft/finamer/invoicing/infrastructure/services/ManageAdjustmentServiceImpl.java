package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageAdjustmentResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageAdjustmentService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAdjustment;

import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageAdjustmentWriteDataJpaRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageAdjustmentReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageAdjustmentServiceImpl implements IManageAdjustmentService {

    @Autowired
    private final ManageAdjustmentWriteDataJpaRepository repositoryCommand;

    @Autowired
    private final ManageAdjustmentReadDataJPARepository repositoryQuery;

    public ManageAdjustmentServiceImpl(ManageAdjustmentWriteDataJpaRepository repositoryCommand, ManageAdjustmentReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageAdjustmentDto dto) {
        ManageAdjustment entity = new ManageAdjustment(dto);
        return repositoryCommand.saveAndFlush(entity).getId();
    }

    @Override
    public UUID insert(ManageAdjustmentDto dto) {
        ManageAdjustment adjustment = new ManageAdjustment(dto);
        this.insert(adjustment);

        dto.setId(adjustment.getId());
        dto.setAdjustmentId(adjustment.getAdjustmentId());
        return dto.getId();
    }

    @Override
    public List<ManageAdjustmentDto> insertAll(List<ManageAdjustmentDto> dtos) {
        for(ManageAdjustmentDto adjustmentDto : dtos){
            this.insert(adjustmentDto);
        }
        return dtos;
    }

    @Override
    public void update(ManageAdjustmentDto dto) {
        ManageAdjustment entity = new ManageAdjustment(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageAdjustmentDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageAdjustment> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageAdjustment> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageAdjustment> data) {
        List<ManageAdjustmentResponse> responseList = new ArrayList<>();
        for (ManageAdjustment entity : data.getContent()) {
            responseList.add(new ManageAdjustmentResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public ManageAdjustmentDto findById(UUID id) {
        Optional<ManageAdjustment> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.INVOICE_ADJUSTMENT_NOT_FOUND, new ErrorField("id", DomainErrorMessage.INVOICE_ADJUSTMENT_NOT_FOUND.getReasonPhrase())));

    }

    @Override
    public List<ManageAdjustmentDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageAdjustment::toAggregate).toList();
    }

    @Override
    public void deleteByIds(List<UUID> ids) {
        try {
            this.repositoryCommand.deleteAllById(ids);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public void deleteInvoice(ManageAdjustmentDto dto) {
        ManageAdjustment entity = new ManageAdjustment(dto);
        entity.setDeleteInvoice(true);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public List<ManageAdjustmentDto> findByRoomRateId(UUID roomRateId) {
        return this.repositoryQuery.findByRoomRateId(roomRateId).stream().map(ManageAdjustment::toAggregate).collect(Collectors.toList());
    }

    private void insert(ManageAdjustment adjustment){
        //this.repositoryCommand.insert(adjustment);
        Map<String, Object> result = repositoryCommand.insertAdjustment(
                adjustment.getId(),
                adjustment.getAmount(),
                adjustment.getDate(),
                adjustment.isDeleteInvoice(),
                adjustment.getDeleted(),
                adjustment.getDeletedAt(),
                adjustment.getDescription(),
                adjustment.getEmployee(),
                adjustment.getUpdatedAt(),
                adjustment.getPaymentTransactionType() != null ? adjustment.getPaymentTransactionType().getId() : null,
                adjustment.getRoomRate() != null ? adjustment.getRoomRate().getId() : null,
                adjustment.getTransaction() != null ? adjustment.getTransaction().getId() : null
        );

        // Recuperar valor OUT
        if (result != null && result.containsKey("p_adjustment_gen_id")) {
            Integer adjId = (Integer) result.get("p_adjustment_gen_id");
            adjustment.setAdjustmentId(adjId != null ? adjId.longValue() : null);
        }
    }

    @Override
    public void createAll(List<ManageAdjustment> adjustments) {
        for (ManageAdjustment adjustment : adjustments){
            this.insert(adjustment);
        }
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

    @Override
    public void create(List<ManageAdjustmentDto> dtos) {
        this.repositoryCommand.saveAll(dtos.stream()
                .map(ManageAdjustment::new)
                .collect(Collectors.toList()));
    }

}
