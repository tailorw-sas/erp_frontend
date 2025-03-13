package com.kynsoft.finamer.invoicing.application.query.collections.invoice;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InvoiceCollectionsSummaryQueryHandler implements IQueryHandler<InvoiceCollectionsSummaryQuery, InvoiceCollectionResponse> {

    private final IManageInvoiceService manageInvoiceService;

    public InvoiceCollectionsSummaryQueryHandler(IManageInvoiceService manageInvoiceService) {
        this.manageInvoiceService = manageInvoiceService;
    }

    @Override
    public InvoiceCollectionResponse handle(InvoiceCollectionsSummaryQuery query) {

        // Obtener la página de facturas
        Page<ManageInvoiceDto> invoiceForSummary = manageInvoiceService.getInvoiceForSummary(
                query.getPageable(), query.getFilter(), query.getEmployeeId()
        );

        // Lista de facturas
        List<ManageInvoiceDto> invoices = invoiceForSummary.getContent();

        // Obtener la fecha actual
        LocalDate today = LocalDate.now();

        // Agrupar las facturas por rango de antigüedad (aging)
        Map<Integer, List<ManageInvoiceDto>> agingGroups = invoices.stream()
                .collect(Collectors.groupingBy(invoice -> {
                    long aging = ChronoUnit.DAYS.between(invoice.getDueDate(), today);
                    if (aging > 0 && aging <= 30) return 30;
                    else if (aging > 30 && aging <= 60) return 60;
                    else if (aging > 60 && aging <= 90) return 90;
                    else if (aging > 90) return 120;
                    else return 0; // Facturas que aún no vencen
                }));

        // Contar facturas en cada categoría
        long totalInvoiceWith30 = agingGroups.getOrDefault(30, List.of()).size();
        long totalInvoiceWith60 = agingGroups.getOrDefault(60, List.of()).size();
        long totalInvoiceWith90 = agingGroups.getOrDefault(90, List.of()).size();
        long totalInvoiceWith120 = agingGroups.getOrDefault(120, List.of()).size();
        long totalInvoiceWithAging0 = agingGroups.getOrDefault(0, List.of()).size();

        // Calcular totales y porcentajes
        int totalInvoices = invoices.size();
        long percentAgingWith30 = totalInvoices > 0 ? (totalInvoiceWith30 * 100 / totalInvoices) : 0;
        long percentAgingWith60 = totalInvoices > 0 ? (totalInvoiceWith60 * 100 / totalInvoices) : 0;
        long percentAgingWith90 = totalInvoices > 0 ? (totalInvoiceWith90 * 100 / totalInvoices) : 0;
        long percentAgingWith120 = totalInvoices > 0 ? (totalInvoiceWith120 * 100 / totalInvoices) : 0;

        // Calcular balances por grupo de aging
        double balanceInvoiceWithAging30 = agingGroups.getOrDefault(30, List.of())
                .stream().mapToDouble(ManageInvoiceDto::getDueAmount).sum();
        double balanceInvoiceWithAging60 = agingGroups.getOrDefault(60, List.of())
                .stream().mapToDouble(ManageInvoiceDto::getDueAmount).sum();
        double balanceInvoiceWithAging90 = agingGroups.getOrDefault(90, List.of())
                .stream().mapToDouble(ManageInvoiceDto::getDueAmount).sum();
        double balanceInvoiceWithAging120 = agingGroups.getOrDefault(120, List.of())
                .stream().mapToDouble(ManageInvoiceDto::getDueAmount).sum();

        // Calcular total de montos
        double totalInvoiceAmount = invoices.stream().mapToDouble(ManageInvoiceDto::getInvoiceAmount).sum();
        double totalInvoiceDue = invoices.stream().mapToDouble(ManageInvoiceDto::getDueAmount).sum();

        // Crear objeto de respuesta resumida
        InvoiceCollectionsSummaryResponse summaryResponse = InvoiceCollectionsSummaryResponse.builder()
                .totalInvoiceWith30(totalInvoiceWith30)
                .totalInvoiceWithAging90(totalInvoiceWith90)
                .totalInvoiceWithAging60(totalInvoiceWith60)
                .totalInvoiceWithAging120(totalInvoiceWith120)
                .totalInvoiceWithAging0(totalInvoiceWithAging0)
                .totalInvoicePercentWithAging30(percentAgingWith30)
                .totalInvoicePercentWithAging60(percentAgingWith60)
                .totalInvoicePercentWithAging90(percentAgingWith90)
                .totalInvoicePercentWithAging120(percentAgingWith120)
                .totalInvoiceBalanceWithAging30(balanceInvoiceWithAging30)
                .totalInvoiceBalanceWithAging60(balanceInvoiceWithAging60)
                .totalInvoiceBalanceWithAging90(balanceInvoiceWithAging90)
                .totalInvoiceBalanceWithAging120(balanceInvoiceWithAging120)
                .totalInvoiceAmount(totalInvoiceAmount)
                .totalInvoiceDueAmount(totalInvoiceDue)
                .build();

        // Crear objeto de paginación
        PaginatedResponse paginatedResponse = new PaginatedResponse(
                invoices,
                invoiceForSummary.getTotalPages(),
                invoiceForSummary.getNumberOfElements(),
                invoiceForSummary.getTotalElements(),
                invoiceForSummary.getSize(),
                invoiceForSummary.getNumber()
        );

        return new InvoiceCollectionResponse(paginatedResponse, summaryResponse);
    }
}
