package com.kynsoft.finamer.invoicing.domain.services;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

public interface IManageInvoiceService {

    ManageInvoiceDto create(ManageInvoiceDto dto);

    void calculateInvoiceAmount(ManageInvoiceDto dto);

    void exportInvoiceList(Pageable pageable, List<FilterCriteria> filterCriteria, ByteArrayOutputStream outputStream);

    ManageInvoiceDto update(ManageInvoiceDto dto);
    List<ManageInvoiceDto> updateAll(List<ManageInvoiceDto> dtoList);

    void delete(ManageInvoiceDto dto);

    ManageInvoiceDto findById(UUID id);

    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria, UUID employeeId);
    PaginatedResponse sendList(Pageable pageable, List<FilterCriteria> filterCriteria);

    PaginatedResponse searchToPayment(Pageable pageable, List<FilterCriteria> filterCriteria);

    Page<ManageInvoiceDto> getInvoiceForSummary(Pageable pageable, List<FilterCriteria>filterCriteria, UUID employeeId);

    List<ManageInvoiceDto> findByIds(List<UUID> ids);

    List<ManageInvoiceDto> findInvoicesWithoutBookings(List<UUID> ids);

    List<ManageInvoiceDto> findAllToReplicate();

    Double findSumOfAmountByParentId(UUID parentId);

    ManageInvoiceDto findByInvoiceId(long id);

    boolean existManageInvoiceByInvoiceId(long invoiceId);

    void deleteInvoice(ManageInvoiceDto dto);

    ManageInvoiceDto changeInvoiceStatus(ManageInvoiceDto dto, ManageInvoiceStatusDto status);

    ManageInvoiceDto findByBookingId(UUID bookingId);

    List<ManageInvoiceDto> findInvoicesByBookingIds(List<UUID> bookingIds);
}
