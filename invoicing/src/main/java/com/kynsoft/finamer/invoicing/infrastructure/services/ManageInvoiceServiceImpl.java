package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.application.excel.writer.ExcelWriter;
import com.kynsof.share.core.application.excel.writer.WriterConfiguration;
import com.kynsof.share.core.domain.EWorkbookFormat;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanWriter;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.manageInvoice.search.ManageInvoiceSearchResponse;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageInvoiceResponse;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageInvoiceToPaymentResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.excel.ExportInvoiceRow;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageBooking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageInvoice;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageInvoiceWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageInvoiceReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageInvoiceServiceImpl implements IManageInvoiceService {

    @Autowired
    private final ManageInvoiceWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageInvoiceReadDataJPARepository repositoryQuery;

    @Autowired
    private final IInvoiceCloseOperationService closeOperationService;

    public ManageInvoiceServiceImpl(ManageInvoiceWriteDataJPARepository repositoryCommand,
                                    ManageInvoiceReadDataJPARepository repositoryQuery, IInvoiceCloseOperationService closeOperationService) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
        this.closeOperationService = closeOperationService;
    }

    public Long getInvoiceNumberSequence(String invoiceNumber) {
        Long lastInvoiceNo = this.repositoryQuery.findByInvoiceNumber(invoiceNumber);

        lastInvoiceNo += 1;
        return lastInvoiceNo;
    }

    @Override
    public void calculateInvoiceAmount(ManageInvoiceDto dto) {
        Double InvoiceAmount = 0.00;

        if (dto.getBookings() != null) {

            for (int i = 0; i < dto.getBookings().size(); i++) {

                InvoiceAmount += dto.getBookings().get(i).getInvoiceAmount();

            }

            dto.setInvoiceAmount(InvoiceAmount);
            dto.setDueAmount(InvoiceAmount);

            this.update(dto);
        }
    }

    @Override
    public ManageInvoiceDto create(ManageInvoiceDto dto) {
        ManageInvoice entity = new ManageInvoice(dto);
        Long lastInvoiceNo = this.getInvoiceNumberSequence(dto.getInvoiceNumber());
        String invoiceNumber = dto.getInvoiceNumber() + "-" + lastInvoiceNo;
        entity.setInvoiceNumber(invoiceNumber);
        entity.setInvoiceNo(lastInvoiceNo);
        dto.setInvoiceNo(lastInvoiceNo);
        String invoicePrefix = InvoiceType.getInvoiceTypeCode(dto.getInvoiceType()) + "-" +lastInvoiceNo;
        entity.setInvoiceNumberPrefix(invoicePrefix);

        return this.repositoryCommand.saveAndFlush(entity).toAggregate();
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageInvoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageInvoice> data = repositoryQuery.findAll(specifications, pageable);
        //getPaginatedResponseTest(example);
        //Page<ManageInvoice> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public PaginatedResponse sendList(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageInvoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageInvoice> data = repositoryQuery.findAll(specifications, pageable);
        //getPaginatedResponseTest(example);
        //Page<ManageInvoice> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedSendListResponse(data);
    }

    @Override
    public PaginatedResponse searchToPayment(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageInvoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageInvoice> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponseToPayment(data);
    }

    @Override
    public void exportInvoiceList(Pageable pageable, List<FilterCriteria> filterCriteria, ByteArrayOutputStream outputStream) {
        List<ManageInvoiceResponse> data = this.search(pageable, filterCriteria).getData();

        List<ExportInvoiceRow> rows = new ArrayList<>();
        List<String> sheets = new ArrayList<>();

        rows.add(new ExportInvoiceRow(0, "Id", "Inv. No", "Due Date", "Manual", "Amount", "Hotel", "Agency", "Type", "Status", null));

        for (int i = 0; i < data.size(); i++) {
            ManageInvoiceResponse invoice = data.get(i);
            rows.add(new ExportInvoiceRow(0, invoice.getInvoiceId() != null ? invoice.getInvoiceId().toString() : "", invoice.getInvoiceNumber(), invoice.getInvoiceDate() != null ? Date.from(invoice.getInvoiceDate().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()).toString() : "", invoice.getIsManual() != null ? invoice.getIsManual().toString() : "false", invoice.getInvoiceAmount() != null ? invoice.getInvoiceAmount().toString() : "", invoice.getHotel() != null ? invoice.getHotel().getCode() + "-" + invoice.getHotel().getName() : "", invoice.getAgency() != null ? invoice.getAgency().getCode() + "-" + invoice.getAgency().getName() : "", invoice.getInvoiceType() != null ? InvoiceType.getInvoiceTypeCode(invoice.getInvoiceType()) + "-" + invoice.getInvoiceType() : "", invoice.getStatus() != null ? InvoiceStatus.getInvoiceStatusCode(invoice.getStatus()) + "-" + invoice.getStatus() : "", null));

        }

        sheets.add("Invoice List");

        WriterConfiguration<ExportInvoiceRow> config = new WriterConfiguration.WriterConfigurationBuilder<ExportInvoiceRow>()
                .withType(ExportInvoiceRow.class)
                .withWorkbookFormat(EWorkbookFormat.XLSX)
                .withSheetNames(sheets)
                .withRows(rows)
                .build();

        ExcelWriter<ExportInvoiceRow> writter = new ExcelBeanWriter<ExportInvoiceRow>(config);

        writter.write(outputStream);

    }

    private PaginatedResponse getPaginatedResponse(Page<ManageInvoice> data) {
        List<ManageInvoiceSearchResponse> responseList = new ArrayList<>();
        for (ManageInvoice entity : data.getContent()) {
            try {
                Boolean isCloseOperation = entity.getHotel().getCloseOperation() != null
                        && !(entity.getInvoiceDate().toLocalDate().isBefore(entity.getHotel().getCloseOperation().getBeginDate())
                        || entity.getInvoiceDate().toLocalDate().isAfter(entity.getHotel().getCloseOperation().getEndDate()));
                ManageInvoiceSearchResponse response = new ManageInvoiceSearchResponse(entity.toAggregateSearch(),
                        entity.getHasAttachments(), isCloseOperation);
                responseList.add(response);
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    private PaginatedResponse getPaginatedSendListResponse(Page<ManageInvoice> data) {
        List<ManageInvoiceSearchResponse> responseList = data.getContent().stream()
                .filter(entity -> {
                    ManageAgency agency = entity.getAgency();
                    List<ManageBooking> bookings = entity.getBookings();
                    return (agency != null && !agency.getValidateCheckout()) || (bookings != null && !hasPastDueBooking(bookings));
                })
                .map(entity -> new ManageInvoiceSearchResponse(entity.toAggregateSearch(), null, null))
                .collect(Collectors.toList());

        return new PaginatedResponse(
                responseList,
                data.getTotalPages(),
                data.getNumberOfElements(),
                data.getTotalElements(),
                data.getSize(),
                data.getNumber()
        );
    }

    public boolean hasPastDueBooking(List<ManageBooking> bookings) {
        LocalDateTime currentDate = LocalDateTime.now(); // Obtener la fecha y hora actual
        if (bookings != null && !bookings.isEmpty()) {
            for (ManageBooking booking : bookings) {
                if (booking.getCheckOut() != null &&
                        (booking.getCheckOut().isBefore(currentDate) || booking.getCheckOut().isEqual(currentDate))) {
                    return true; // Si checkOut es antes o igual a currentDate, devolver true
                }
            }
        }

        return false; // Si no se encontró ningún booking con checkOut anterior o igual, devolver false
    }

    private PaginatedResponse getPaginatedResponseToPayment(Page<ManageInvoice> data) {
        List<ManageInvoiceToPaymentResponse> responseList = new ArrayList<>();

        for (ManageInvoice entity : data.getContent()) {
            ManageInvoiceToPaymentResponse response = new ManageInvoiceToPaymentResponse(entity.toAggregate());
            responseList.add(response);
        }

        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public void update(ManageInvoiceDto dto) {
        ManageInvoice entity = new ManageInvoice(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageInvoiceDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageInvoiceDto findById(UUID id) {
        Optional<ManageInvoice> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND,
                new ErrorField("id", "The invoice not found.")));

    }

    @Override
    public List<ManageInvoiceDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageInvoice::toAggregate).toList();
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
    public List<ManageInvoiceDto> findAllToReplicate() {
        List<ManageInvoice> objects = this.repositoryQuery.findAll();
        List<ManageInvoiceDto> objectDtos = new ArrayList<>();

        for (ManageInvoice object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

    @Override
    public Double findSumOfAmountByParentId(UUID parentId) {
        return this.repositoryQuery.findSumOfAmountByParentId(parentId).orElse(0.0);
    }

    @Override
    public ManageInvoiceDto findByInvoiceId(long id) {
        return repositoryQuery.findByInvoiceId(id)
                .map(ManageInvoice::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND,
                        new ErrorField("invoiceId", "The invoice not found."))));
    }

    @Override
    public boolean existManageInvoiceByInvoiceId(long invoiceId) {
        return repositoryQuery.existsByInvoiceId(invoiceId);
    }

}
