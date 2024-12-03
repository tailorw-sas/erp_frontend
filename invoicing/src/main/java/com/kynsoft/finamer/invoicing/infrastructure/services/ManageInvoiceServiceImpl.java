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
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageInvoiceToPaymentResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.InvoiceType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.excel.ExportInvoiceRow;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceCloseOperationService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Booking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Invoice;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageInvoiceWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageInvoiceReadDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
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
        InvoiceUtils.establishDueDate(dto);
        InvoiceUtils.calculateInvoiceAging(dto);
        Invoice entity = new Invoice(dto);
        if (dto.getHotel().isVirtual()) {
            String invoiceNumber = dto.getInvoiceNumber() + "-" + dto.getHotelInvoiceNumber();
            entity.setInvoiceNumber(invoiceNumber);
            entity.setInvoiceNo(dto.getHotelInvoiceNumber());
            dto.setInvoiceNo(dto.getHotelInvoiceNumber());
            String invoicePrefix = InvoiceType.getInvoiceTypeCode(dto.getInvoiceType()) + "-" + dto.getHotelInvoiceNumber();
            entity.setInvoiceNumberPrefix(invoicePrefix);
        } else {
            Long lastInvoiceNo = this.getInvoiceNumberSequence(dto.getInvoiceNumber());
            String invoiceNumber = dto.getInvoiceNumber() + "-" + lastInvoiceNo;
            entity.setInvoiceNumber(invoiceNumber);
            entity.setInvoiceNo(lastInvoiceNo);
            dto.setInvoiceNo(lastInvoiceNo);
            String invoicePrefix = InvoiceType.getInvoiceTypeCode(dto.getInvoiceType()) + "-" + lastInvoiceNo;
            entity.setInvoiceNumberPrefix(invoicePrefix);
        }

        return this.repositoryCommand.saveAndFlush(entity).toAggregate();
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Invoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Invoice> data = repositoryQuery.findAll(specifications, pageable);
        //getPaginatedResponseTest(example);
        //Page<ManageInvoice> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public PaginatedResponse sendList(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Invoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Invoice> data = repositoryQuery.findAll(specifications, pageable);
        //getPaginatedResponseTest(example);
        //Page<ManageInvoice> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedSendListResponse(data);
    }

    @Override
    public PaginatedResponse searchToPayment(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Invoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Invoice> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponseToPayment(data);
    }

    @Override
    public Page<ManageInvoiceDto> getInvoiceForSummary(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);
        GenericSpecificationsBuilder<Invoice> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        return repositoryQuery.findAll(specifications, pageable).map(Invoice::toAggregate);
    }

    @Override
    public void exportInvoiceList(Pageable pageable, List<FilterCriteria> filterCriteria, ByteArrayOutputStream outputStream) {
        List<ManageInvoiceSearchResponse> data = this.search(pageable, filterCriteria).getData();

        List<ExportInvoiceRow> rows = new ArrayList<>();
        List<String> sheets = new ArrayList<>();

        rows.add(new ExportInvoiceRow(
                0,
                "Has Attachment",
                "Id",
                "Type",
                "Hotel",
                "Agency Cd",
                "Agency",
                "Inv. No",
                "Gen. Date",
                "Status",
                "Manual",
                "Amount",
                "Due Amount",
                "Auto Rec",
                null
        ));

        double cant = 0.00;
        double totalsAmount = 0.00;

        double cantPro = 0.00;
        double totalsAmountPro = 0.00;

        double cantWai = 0.00;
        double totalsAmountWai = 0.00;

        double cantRec = 0.00;
        double totalsAmountRec = 0.00;

        double cantCan = 0.00;
        double totalsAmountCan = 0.00;

        double cantSen = 0.00;
        double totalsAmountSen = 0.00;

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        for (int i = 0; i < data.size(); i++) {
            ManageInvoiceSearchResponse invoice = data.get(i);
            cant++;
            totalsAmount = totalsAmount + invoice.getInvoiceAmount();
            String status = "";
            switch (invoice.getStatus()) {
                case PROCECSED -> {
                    cantPro++;
                    totalsAmountPro = totalsAmountPro + invoice.getInvoiceAmount();
                    status = "PROCESSED";
                }
                case RECONCILED -> {
                    cantRec++;
                    totalsAmountRec = totalsAmountRec + invoice.getInvoiceAmount();
                    status = "RECONCILED";
                }
                case SENT -> {
                    cantSen++;
                    totalsAmountSen = totalsAmountSen + invoice.getInvoiceAmount();
                    status = "SENT";
                }
                case CANCELED -> {
                    cantCan++;
                    totalsAmountCan = totalsAmountCan + invoice.getInvoiceAmount();
                    status = "CANCELED";
                }
                default -> {
                    status = "";
                    System.out.print("Other Status");
                }
            }
            rows.add(new ExportInvoiceRow(
                    0,
                    invoice.getHasAttachments() ? "true" : "false",
                    invoice.getInvoiceId() != null ? invoice.getInvoiceId().toString() : "", //Id
                    invoice.getInvoiceType() != null ? InvoiceType.getInvoiceTypeCode(invoice.getInvoiceType()) + "-" + invoice.getInvoiceType() : "", //Type
                    invoice.getHotel() != null ? invoice.getHotel().getCode() + "-" + invoice.getHotel().getName() : "", //Hotel
                    invoice.getAgency() != null ? invoice.getAgency().getCode() : "",//Agency,//Agency Cd
                    invoice.getAgency() != null ? invoice.getAgency().getName() : "",//Agency
                    invoice.getInvoiceNumber(),//Inv. No
                    invoice.getInvoiceDate() != null ? Date.from(invoice.getInvoiceDate().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()).toString() : "",//Gen. Date
                    //invoice.getStatus() != null ? InvoiceStatus.getInvoiceStatusCode(invoice.getStatus()) + "-" + invoice.getStatus() : "", //Status
                    status, //Status
                    invoice.getIsManual() ? "1" : "0", //Manual
                    decimalFormat.format(invoice.getInvoiceAmount() != null ? invoice.getInvoiceAmount() : "0.00"),//Amount
                    decimalFormat.format(invoice.getDueAmount() != null ? invoice.getDueAmount() : "0.00"),//Due Amount
                    //invoice.getInvoiceAmount() != null ? invoice.getInvoiceAmount().toString() : "", //Amount
                    //invoice.getDueAmount() != null ? invoice.getDueAmount().toString() : "", //Due Amount
                    invoice.getAutoRec() ? "1" : "0", //Auto Rec
                    null
            ));
        }

        rows.add(new ExportInvoiceRow(
                0,
                "",
                "Totals",
                "#" + cant,//totals
                "",
                "",
                "Pro #" + cantPro,
                "Wai #" + cantWai,
                "Rec #" + cantRec,
                "Can #" + cantCan,
                "Sen #" + cantSen,
                "",
                "",
                "",
                null
        ));

        rows.add(new ExportInvoiceRow(
                0,
                "",
                "Totals",
                "$" + totalsAmount,//totals
                "",
                "",
                "Pro $" + decimalFormat.format(totalsAmountPro),
                "Wai $" + decimalFormat.format(totalsAmountWai),
                "Rec $" + decimalFormat.format(totalsAmountRec),
                "Can $" + decimalFormat.format(totalsAmountCan),
                "Sen $" + decimalFormat.format(totalsAmountSen),
                "",
                "",
                "",
                null
        ));

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

    private PaginatedResponse getPaginatedResponse(Page<Invoice> data) {
        List<ManageInvoiceSearchResponse> responseList = new ArrayList<>();
        for (Invoice entity : data.getContent()) {
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

    private PaginatedResponse getPaginatedSendListResponse(Page<Invoice> data) {
        // Filtramos el contenido de acuerdo a las condiciones
        List<ManageInvoiceSearchResponse> filteredResponseList = data.getContent().stream()
                .filter(entity -> {
                    ManageAgency agency = entity.getAgency();
                    List<Booking> bookings = entity.getBookings();
                    return (agency != null && !agency.getValidateCheckout()) || (bookings != null && !hasPastDueBooking(bookings));
                })
                .map(entity -> new ManageInvoiceSearchResponse(entity.toAggregateSearch(), null, null))
                .collect(Collectors.toList());

        // Calculamos el total de elementos después del filtrado
        int totalFilteredElements = filteredResponseList.size();

        // Calculamos el total de páginas según el tamaño original de la página
        int pageSize = data.getSize();
        int totalPages = (int) Math.ceil((double) totalFilteredElements / pageSize);

        // Devolvemos la respuesta paginada con los datos filtrados y calculados
        return new PaginatedResponse(
                filteredResponseList, // Lista de datos filtrados
                totalPages, // Total de páginas basado en los datos filtrados
                totalFilteredElements, // Total de elementos después del filtrado
                (long) totalFilteredElements, // Total de elementos
                pageSize, // Tamaño de la página original
                data.getNumber() // Número de la página actual (0-indexado)
        );
    }

    public boolean hasPastDueBooking(List<Booking> bookings) {
        LocalDate currentDate = LocalDate.now(); // Obtener la fecha actual (sin hora)
        if (bookings != null && !bookings.isEmpty()) {
            for (Booking booking : bookings) {
                if (booking.getCheckOut() != null
                        && (booking.getCheckOut().toLocalDate().isBefore(currentDate)
                        || booking.getCheckOut().toLocalDate().isEqual(currentDate))) {
                    return true; // Si checkOut es antes o igual a currentDate, devolver true
                }
            }
        }

        return false; // Si no se encontró ningún booking con checkOut anterior o igual, devolver false
    }

    private PaginatedResponse getPaginatedResponseToPayment(Page<Invoice> data) {
        List<ManageInvoiceToPaymentResponse> responseList = new ArrayList<>();

        for (Invoice entity : data.getContent()) {
            ManageInvoiceToPaymentResponse response = new ManageInvoiceToPaymentResponse(entity.toAggregate());
            responseList.add(response);
        }

        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public void update(ManageInvoiceDto dto) {
        Invoice entity = new Invoice(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void deleteInvoice(ManageInvoiceDto dto) {
        Invoice entity = new Invoice(dto);
        entity.setDeleteInvoice(true);
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
        Optional<Invoice> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND,
                new ErrorField("id", "The invoice not found.")));

    }

    @Override
    public List<ManageInvoiceDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(Invoice::toAggregate).toList();
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
        List<Invoice> objects = this.repositoryQuery.findAll();
        List<ManageInvoiceDto> objectDtos = new ArrayList<>();

        for (Invoice object : objects) {
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
                .map(Invoice::toAggregate)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_TYPE_NOT_FOUND,
                new ErrorField("invoiceId", "The invoice not found."))));
    }

    @Override
    public boolean existManageInvoiceByInvoiceId(long invoiceId) {
        return repositoryQuery.existsByInvoiceId(invoiceId);
    }

}
