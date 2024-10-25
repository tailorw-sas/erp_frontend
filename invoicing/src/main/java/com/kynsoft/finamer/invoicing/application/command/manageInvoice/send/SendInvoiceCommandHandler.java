package com.kynsoft.finamer.invoicing.application.command.manageInvoice.send;

import com.itextpdf.text.DocumentException;
import com.kynsof.share.core.application.ftp.FtpService;
import com.kynsof.share.core.application.mailjet.*;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.service.IFtpService;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceReportType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgencyContact;
import com.kynsoft.finamer.invoicing.infrastructure.services.AccountStatementService;
import com.kynsoft.finamer.invoicing.infrastructure.services.InvoiceXmlService;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.factory.InvoiceReportProviderFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Component
@Transactional
public class SendInvoiceCommandHandler implements ICommandHandler<SendInvoiceCommand> {

    private final IManageInvoiceService service;
    private final MailService mailService;
    private final IManageEmployeeService manageEmployeeService;
    private final InvoiceXmlService invoiceXmlService;
    private final IManageInvoiceStatusService manageInvoiceStatusService;
    private final IFtpService ftpService;
    private final InvoiceReportProviderFactory invoiceReportProviderFactory;
    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;
    private final IManageAgencyContactService manageAgencyContactService;
    private final AccountStatementService accountStatementService;
    private final IInvoiceCloseOperationService closeOperationService;

    public SendInvoiceCommandHandler(IManageInvoiceService service, MailService mailService,
                                     IManageEmployeeService manageEmployeeService, InvoiceXmlService invoiceXmlService,
                                     IManageInvoiceStatusService manageInvoiceStatusService,
                                     FtpService ftpService, InvoiceReportProviderFactory invoiceReportProviderFactory,
                                     IInvoiceStatusHistoryService invoiceStatusHistoryService,
                                     IManageAgencyContactService manageAgencyContactService,
                                     AccountStatementService accountStatementService,
                                     IInvoiceCloseOperationService closeOperationService) {

        this.service = service;
        this.mailService = mailService;
        this.manageEmployeeService = manageEmployeeService;
        this.invoiceXmlService = invoiceXmlService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.ftpService = ftpService;
        this.invoiceReportProviderFactory = invoiceReportProviderFactory;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
        this.manageAgencyContactService = manageAgencyContactService;
        this.accountStatementService = accountStatementService;
        this.closeOperationService = closeOperationService;
    }

    @Override
    @Transactional
    public void handle(SendInvoiceCommand command) {
        ManageEmployeeDto manageEmployeeDto = manageEmployeeService.findById(UUID.fromString(command.getEmployee()));
        List<ManageInvoiceDto> invoices = this.service.findByIds(command.getInvoice());

        if (invoices.isEmpty()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.SERVICE_NOT_FOUND,
                    new ErrorField("id", DomainErrorMessage.SERVICE_NOT_FOUND.getReasonPhrase())));
        }
        ManageInvoiceStatusDto manageInvoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.SENT);
        if (invoices.get(0).getAgency().getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("EML")) {
            sendEmail(command, invoices.get(0).getAgency(), invoices, manageEmployeeDto, manageInvoiceStatus, manageEmployeeDto.getLastName());
        }

        if (invoices.get(0).getAgency().getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("BVL")) {
            bavel(invoices.get(0).getAgency(), invoices, manageInvoiceStatus, manageEmployeeDto.getLastName());
        }
        if (invoices.get(0).getAgency().getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("FTP")) {
            sendFtp(command, invoices, manageInvoiceStatus, manageEmployeeDto.getLastName());
        }

        command.setResult(true);
    }

    private void updateStatusAgency(List<ManageInvoiceDto> invoices, ManageInvoiceStatusDto manageInvoiceStatus, String employee) {
        for (ManageInvoiceDto manageInvoiceDto : invoices) {
            if (manageInvoiceDto.getStatus().equals(EInvoiceStatus.RECONCILED)) {
                manageInvoiceDto.setStatus(EInvoiceStatus.SENT);
                manageInvoiceDto.setManageInvoiceStatus(manageInvoiceStatus);
                this.invoiceStatusHistoryService.create(
                        new InvoiceStatusHistoryDto(
                                UUID.randomUUID(),
                                manageInvoiceDto,
                                "The invoice data was inserted.",
                                null,
                                employee,
                                EInvoiceStatus.SENT
                        )
                );
                this.service.update(manageInvoiceDto);
            }
            else if (manageInvoiceDto.getStatus().equals(EInvoiceStatus.SENT)){
                manageInvoiceDto.setReSend(true);
                manageInvoiceDto.setReSendDate(LocalDate.now());
                this.service.update(manageInvoiceDto);
            }

        }
    }

    private void bavel(ManageAgencyDto agency, List<ManageInvoiceDto> invoices, ManageInvoiceStatusDto manageInvoiceStatus, String employee) {
        for (ManageInvoiceDto invoice : invoices) {
            try {
                var xmlContent = invoiceXmlService.generateInvoiceXml(invoice);
                String nameFile = invoice.getInvoiceNumber() + ".xml";
                InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
                ftpService.sendFile(inputStream, nameFile, agency.getSentB2BPartner().getIp(),
                        agency.getSentB2BPartner().getUserName(), agency.getSentB2BPartner().getPassword(), 21, "bvl");

            } catch (Exception e) {
                invoice.setSendStatusError(e.getMessage());
                service.update(invoice);
            }
        }
        updateStatusAgency(invoices, manageInvoiceStatus, employee);
    }

    private void sendFtp(SendInvoiceCommand command, List<ManageInvoiceDto> invoices, ManageInvoiceStatusDto manageInvoiceStatus, String employee) {
        // Paso 2: Definir si quieres agrupar por cliente o no
        boolean groupByClient = command.isGroupByClient(); // O false, según lo que necesites

        // Paso 3: Llamar al método para generar los PDFs
        try {
            InvoiceGrouper invoiceGrouper = new InvoiceGrouper(invoiceReportProviderFactory);
            List<GeneratedInvoice> generatedPDFs = invoiceGrouper.generateInvoicesPDFs(invoices, groupByClient, command.isWithAttachment());

            // Paso 4: Procesar los PDFs generados (por ejemplo, guardarlos o enviarlos)
            for (GeneratedInvoice generatedInvoice : generatedPDFs) {
                // Aquí puedes guardar o enviar el PDF, por ejemplo, a un FTP, por correo, o guardarlo en disco
                InputStream pdfStream = new ByteArrayInputStream(generatedInvoice.getPdfStream().toByteArray());
                LocalDate currentDate = LocalDate.now();

                // Crear un formateador para "MM" (mes con dos dígitos) y "dd" (día con dos dígitos)
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

                // Aplicar el formato a la fecha actual
                String monthAndDayFormatted = currentDate.format(formatter);

                // Desglosar los valores en separado si se necesita
                String monthFormatted = currentDate.format(DateTimeFormatter.ofPattern("MM"));
                String dayFormatted = currentDate.format(DateTimeFormatter.ofPattern("dd"));

                String path = currentDate.getYear() + "/" + monthFormatted + "/" + dayFormatted + "/"
                        + invoices.get(0).getHotel().getCode();
                ftpService.sendFile(pdfStream, generatedInvoice.getNameFile(), generatedInvoice.getIp(),
                        generatedInvoice.getUserName(), generatedInvoice.getPassword(), 21, path);
                updateStatusAgency( generatedInvoice.getInvoices(), manageInvoiceStatus, employee);

//                savePDF(pdf);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendEmail(SendInvoiceCommand command, ManageAgencyDto agency, List<ManageInvoiceDto> invoices, ManageEmployeeDto employeeDto, ManageInvoiceStatusDto manageInvoiceStatus, String employee) {

//        // Agrupar facturas por agencia
//        Map<UUID, List<ManageInvoiceDto>> invoicesByAgency = invoices.stream()
//                .collect(Collectors.groupingBy(invoice -> invoice.getAgency().getId()));

        Map<UUID, Map<UUID, List<ManageInvoiceDto>>> invoicesByAgencyAndHotel = invoices.stream()
                .collect(Collectors.groupingBy(
                        invoice -> invoice.getAgency().getId(), // Primero agrupamos por agencia
                        Collectors.groupingBy(invoice -> invoice.getHotel().getId()) // Luego agrupamos por hotel
                ));

        for (Map.Entry<UUID, Map<UUID, List<ManageInvoiceDto>>> agencyEntry : invoicesByAgencyAndHotel.entrySet()) {
            UUID agencyId = agencyEntry.getKey(); // ID de la agencia
            Map<UUID, List<ManageInvoiceDto>> invoicesByHotel = agencyEntry.getValue(); // Facturas agrupadas por hotel

            System.out.println("Agencia ID: " + agencyId);

            for (Map.Entry<UUID, List<ManageInvoiceDto>> hotelEntry : invoicesByHotel.entrySet()) {
                UUID hotelId = hotelEntry.getKey(); // ID del hotel
                List<ManageInvoiceDto> invoicesHotel = hotelEntry.getValue(); // Lista de facturas para este hotel


                SendMailJetEMailRequest request = new SendMailJetEMailRequest();
                List<MailJetRecipient> recipients = getMailJetRecipients(agency, employeeDto, request, invoicesHotel);
                request.setRecipientEmail(recipients);
                //Var
                List<MailJetVar> vars = getMailJetVars(agency, request, invoicesHotel);

                request.setMailJetVars(vars);

                //Adjuntos
                List<MailJetAttachment> attachments = getMailJetAttachments(invoicesHotel);

                request.setMailJetAttachments(attachments);
                mailService.sendMail(request);
                updateInvoices(invoices, manageInvoiceStatus, employee);
//                System.out.println("\tHotel ID: " + hotelId);
//                for (ManageInvoiceDto invoice : invoicesHotel) {
//                    // Aquí puedes procesar cada factura individualmente
//                    System.out.println("\t\tFactura: " + invoice);
//                }
            }
        }
//        for (Map.Entry<UUID, List<ManageInvoiceDto>> agencyEntry : invoicesByAgency.entrySet()) {
//            List<ManageInvoiceDto> agencyInvoices = agencyEntry.getValue();
//
//            SendMailJetEMailRequest request = new SendMailJetEMailRequest();
//            List<MailJetRecipient> recipients = getMailJetRecipients(agency, employeeDto, request, agencyInvoices);
//            request.setRecipientEmail(recipients);
//            //Var
//            List<MailJetVar> vars = getMailJetVars(agency, request, agencyInvoices);
//
//            request.setMailJetVars(vars);
//
//            //Adjuntos
//            List<MailJetAttachment> attachments = getMailJetAttachments(agencyInvoices);
//
//            request.setMailJetAttachments(attachments);
//            mailService.sendMail(request);
//            updateInvoices(invoices);
//        }
    }

    private List<MailJetAttachment> getMailJetAttachments(List<ManageInvoiceDto> agencyInvoices) {
        List<MailJetAttachment> attachments = new ArrayList<>();
        List<UUID> ids = agencyInvoices.stream().map(ManageInvoiceDto::getId).toList();
        SendAccountStatementRequest sendAccountStatementRequest = new SendAccountStatementRequest(ids);
        SendAccountStatementResponse sendAccountStatementResponse = accountStatementService.sendAccountStatement(sendAccountStatementRequest);

        MailJetAttachment attachment = new MailJetAttachment(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // Content-Type para archivo .xlsx
                "AccountStatement.xlsx",  // Nombre del archivo
                sendAccountStatementResponse.getFile()
        );
        attachments.add(attachment);
        return attachments;
    }

    private static List<MailJetVar> getMailJetVars(ManageAgencyDto agency, SendMailJetEMailRequest request, List<ManageInvoiceDto> agencyInvoices) {
        request.setSubject("INVOICES for -" + agency.getCode() + "-" + agency.getName());
        double totalAmount = agencyInvoices.stream()
                .mapToDouble(invoice -> invoice.getInvoiceAmount() != null ? invoice.getInvoiceAmount() : 0.0)
                .sum();
        String invoiceAmount = String.format("%.2f", totalAmount);

        // Variables para el template de email
        return Arrays.asList(
                new MailJetVar("invoice_date", new Date().toString()),
                new MailJetVar("invoice_amount", invoiceAmount)
        );
    }

    private List<MailJetRecipient> getMailJetRecipients(ManageAgencyDto agency, ManageEmployeeDto employeeDto, SendMailJetEMailRequest request, List<ManageInvoiceDto> agencyInvoices) {
        request.setTemplateId(6285030);
        List<MailJetRecipient> recipients = new ArrayList<>();
        recipients.add(new MailJetRecipient(agency.getMailingAddress(), agency.getName()));
        recipients.add(new MailJetRecipient(employeeDto.getEmail(), employeeDto.getFirstName() + " " + employeeDto.getLastName()));
        recipients.add(new MailJetRecipient("keimermo1989@gmail.com", "Keimer Montes"));
        recipients.add(new MailJetRecipient(agency.getMailingAddress(), agency.getName()));

        ManageAgencyDto manageAgencyDto = agencyInvoices.get(0).getAgency();
        ManageHotelDto manageHotelDto = agencyInvoices.get(0).getHotel();
        List<ManageAgencyContact> contactList = manageAgencyContactService.findContactsByHotelAndAgency(manageHotelDto.getId(), manageAgencyDto.getId());
        if (!contactList.isEmpty()) {
            // Dividimos la cadena en un array de correos
            String[] emailAddresses = contactList.get(0).getEmailContact().split(";");
            for (String email : emailAddresses) {
                email = email.trim();
                if (!email.isEmpty()) {
                    recipients.add(new MailJetRecipient(email, "Contact"));
                }
            }
        }
        return recipients;
    }

    private  void updateInvoices(List<ManageInvoiceDto> invoices, ManageInvoiceStatusDto manageInvoiceStatus, String employee) {
        for (ManageInvoiceDto manageInvoiceDto : invoices) {
            if (manageInvoiceDto.getStatus().equals(EInvoiceStatus.RECONCILED)) {
                manageInvoiceDto.setStatus(EInvoiceStatus.SENT);
                manageInvoiceDto.setManageInvoiceStatus(manageInvoiceStatus);
                this.invoiceStatusHistoryService.create(
                        new InvoiceStatusHistoryDto(
                                UUID.randomUUID(),
                                manageInvoiceDto,
                                "The invoice data was inserted.",
                                null,
                                employee,
                                EInvoiceStatus.SENT
                        )
                );
            }if (manageInvoiceDto.getStatus().equals(EInvoiceStatus.SENT)){
                manageInvoiceDto.setReSend(true);
                manageInvoiceDto.setReSendDate(LocalDate.now());
            }
            service.update(manageInvoiceDto);
        }

    }

    private Optional<ByteArrayOutputStream> getInvoicesBooking(String invoiceIds, SendInvoiceCommand command) throws DocumentException, IOException {
        EInvoiceReportType reportType = command.isWithAttachment()
                ? EInvoiceReportType.INVOICE_AND_BOOKING
                : EInvoiceReportType.INVOICE_SUPPORT;
        Map<EInvoiceReportType, IInvoiceReport> services = new HashMap<>();
        services.put(reportType, invoiceReportProviderFactory.getInvoiceReportService(reportType));

        Optional<Map<String, byte[]>> response = getReportContent(services, invoiceIds);

        if (response.isPresent() && !response.get().isEmpty()) {
            byte[] content = response.get().values().iterator().next();

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                outputStream.write(content);
                return Optional.of(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        } else {
            System.out.println("No se pudo obtener el contenido del reporte.");
            return Optional.empty();
        }
    }

    private Optional<Map<String, byte[]>> getReportContent(Map<EInvoiceReportType, IInvoiceReport> reportService, String invoiceId) throws DocumentException, IOException {
        Map<String, byte[]> result = new HashMap<>();

        Map<EInvoiceReportType, Optional<byte[]>> reportContent = reportService.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().generateReport(invoiceId)
                ));

        List<InputStream> finalContent = getOrderReportContent(reportContent).stream()
                .filter(Optional::isPresent)
                .map(content -> new ByteArrayInputStream(content.get()))
                .map(InputStream.class::cast)
                .toList();
        if (!finalContent.isEmpty()) {
            result.put(invoiceId, PDFUtils.mergePDFtoByte(finalContent));
        }

        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    private List<Optional<byte[]>> getOrderReportContent(Map<EInvoiceReportType, Optional<byte[]>> content) {
        List<Optional<byte[]>> orderedContent = new LinkedList<>();
        orderedContent.add(content.getOrDefault(EInvoiceReportType.INVOICE_AND_BOOKING, Optional.empty()));
        orderedContent.add(content.getOrDefault(EInvoiceReportType.INVOICE_SUPPORT, Optional.empty()));
        return orderedContent;
    }

    private LocalDateTime generateDate(UUID hotel) {
        InvoiceCloseOperationDto closeOperationDto = this.closeOperationService.findActiveByHotelId(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return LocalDateTime.now(ZoneId.of("UTC"));
        }
        return LocalDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")));
    }

}