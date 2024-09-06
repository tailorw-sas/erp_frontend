package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

public interface IManageInvoiceService {

    ManageInvoiceDto create(ManageInvoiceDto dto);

    void calculateInvoiceAmount(ManageInvoiceDto dto);

    public void exportInvoiceList(Pageable pageable, List<FilterCriteria> filterCriteria, ByteArrayOutputStream outputStream);

    void update(ManageInvoiceDto dto);

    void delete(ManageInvoiceDto dto);

    ManageInvoiceDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);

    List<ManageInvoiceDto> findByIds(List<UUID> ids);

    List<ManageInvoiceDto> findAllToReplicate();

    Double findSumOfAmountByParentId(UUID parentId);
}
