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
import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceReportType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.services.InvoiceXmlService;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.factory.InvoiceReportProviderFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    public SendInvoiceCommandHandler(IManageInvoiceService service, MailService mailService,
                                     IManageEmployeeService manageEmployeeService, InvoiceXmlService invoiceXmlService,
                                     IManageInvoiceStatusService manageInvoiceStatusService,
                                     FtpService ftpService, InvoiceReportProviderFactory invoiceReportProviderFactory, IInvoiceStatusHistoryService invoiceStatusHistoryService) {

        this.service = service;
        this.mailService = mailService;
        this.manageEmployeeService = manageEmployeeService;
        this.invoiceXmlService = invoiceXmlService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.ftpService = ftpService;
        this.invoiceReportProviderFactory = invoiceReportProviderFactory;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
    }

    @Override
    @Transactional
    public void handle(SendInvoiceCommand command) {
        ManageEmployeeDto manageEmployeeDto = manageEmployeeService.findById(UUID.fromString(command.getEmployee()));
        // Obtener la lista de facturas por sus IDs
        List<ManageInvoiceDto> invoices = this.service.findByIds(command.getInvoice());

        if (invoices.isEmpty()) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.SERVICE_NOT_FOUND,
                       new ErrorField("id", DomainErrorMessage.SERVICE_NOT_FOUND.getReasonPhrase())));
        }
        ManageInvoiceStatusDto manageInvoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.SENT);
        if (invoices.get(0).getAgency().getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("EML")){
            sendEmail(command, invoices.get(0).getAgency(), invoices, manageEmployeeDto, manageInvoiceStatus, manageEmployeeDto.getLastName());
        }
        if (invoices.get(0).getAgency().getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("BVL")){
            bavel(invoices.get(0).getAgency(), invoices, manageInvoiceStatus, manageEmployeeDto.getLastName());
        }
        if (invoices.get(0).getAgency().getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("FTP")){
            sendFtp(command,  invoices, manageInvoiceStatus, manageEmployeeDto.getLastName());
        }

        // Agrupar facturas por agencia
//        Map<ManageAgencyDto, List<ManageInvoiceDto>> invoicesByAgency = new HashMap<>();
//        for (ManageInvoiceDto invoice : invoices) {
//            if (!invoice.getStatus().equals(EInvoiceStatus.SENT) && !invoice.getStatus().equals(EInvoiceStatus.RECONCILED)) {
//                throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.SERVICE_NOT_FOUND,
//                        new ErrorField("id", DomainErrorMessage.SERVICE_NOT_FOUND.getReasonPhrase())));
//            }
//            invoicesByAgency.computeIfAbsent(invoice.getAgency(), k -> new ArrayList<>()).add(invoice);
//        }


//        for (Map.Entry<ManageAgencyDto, List<ManageInvoiceDto>> entry : invoicesByAgency.entrySet()) {
//            ManageAgencyDto agency = entry.getKey();
//            List<ManageInvoiceDto> agencyInvoices = entry.getValue();
//            if (agency.getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("EML")) {
//               sendEmail(command, agency, agencyInvoices, manageEmployeeDto, manageInvoiceStatus, manageEmployeeDto.getLastName());
//                continue;
//            }
//
//            if (agency.getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("BVL")) {
//                try {
//                    bavel(agency, agencyInvoices, manageInvoiceStatus, manageEmployeeDto.getLastName());
//                } catch (DocumentException | IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            if (agency.getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("FTP")) {
//                try {
//                    sendFtp(command, agency, agencyInvoices, manageInvoiceStatus, manageEmployeeDto.getLastName());
//                } catch (DocumentException | IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
        command.setResult(true);
    }

    private void updateStatusAgency(ManageInvoiceDto invoice, ManageInvoiceStatusDto manageInvoiceStatus, String employee) {
        invoice.setStatus(EInvoiceStatus.SENT);
        invoice.setManageInvoiceStatus(manageInvoiceStatus);
        if (!invoice.getStatus().equals(EInvoiceStatus.SENT)) {
            invoice.setReSend(true);
        }
        this.service.update(invoice);
        this.invoiceStatusHistoryService.create(
                new InvoiceStatusHistoryDto(
                        UUID.randomUUID(),
                        invoice,
                        "The invoice data was inserted.",
                        null,
                        employee,
                        EInvoiceStatus.SENT
                )
        );
    }

    private void bavel(ManageAgencyDto agency, List<ManageInvoiceDto> invoices, ManageInvoiceStatusDto manageInvoiceStatus, String employee)  {
        for (ManageInvoiceDto invoice : invoices) {
            try {
                var xmlContent = invoiceXmlService.generateInvoiceXml(invoice);
                String nameFile = invoice.getInvoiceNumber() + ".xml";
                InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
                ftpService.sendFile(inputStream, nameFile, agency.getSentB2BPartner().getIp(),
                        agency.getSentB2BPartner().getUserName(), agency.getSentB2BPartner().getPassword(), 21, "bvl");
                updateStatusAgency(invoice, manageInvoiceStatus, employee);
            } catch (Exception e) {
                invoice.setSendStatusError(e.getMessage());
                service.update(invoice);
            }
        }
    }

    private void sendFtp(SendInvoiceCommand command, List<ManageInvoiceDto> invoices, ManageInvoiceStatusDto manageInvoiceStatus,String employee) {


        // Paso 2: Definir si quieres agrupar por cliente o no
        boolean groupByClient = command.isGroupByClient(); // O false, según lo que necesites

        // Paso 3: Llamar al método para generar los PDFs
        try {
            InvoiceGrouper invoiceGrouper = new InvoiceGrouper(invoiceReportProviderFactory);
            List<GeneratedInvoice> generatedPDFs = invoiceGrouper.generateInvoicesPDFs(invoices, groupByClient);

            // Paso 4: Procesar los PDFs generados (por ejemplo, guardarlos o enviarlos)
            for (GeneratedInvoice generatedInvoice : generatedPDFs) {
                // Aquí puedes guardar o enviar el PDF, por ejemplo, a un FTP, por correo, o guardarlo en disco
                InputStream pdfStream = new ByteArrayInputStream(generatedInvoice.getPdfStream().toByteArray());
                String path= LocalDate.now().getYear()+"/"+LocalDate.now().getMonth()+"/"+LocalDate.now().getDayOfMonth()+"/" +invoices.get(0).getHotel().getCode();
                ftpService.sendFile(pdfStream, generatedInvoice.getNameFile(), generatedInvoice.getIp(),
                      generatedInvoice.getUserName(), generatedInvoice.getPassword(), 21, path);
                for (ManageInvoiceDto manageInvoiceDto : generatedInvoice.getInvoices()) {
                                updateStatusAgency(manageInvoiceDto, manageInvoiceStatus, employee);
                }

//                savePDF(pdf);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(SendInvoiceCommand command, ManageAgencyDto agency, List<ManageInvoiceDto> invoices, ManageEmployeeDto employeeDto, ManageInvoiceStatusDto manageInvoiceStatus, String employee) {
        SendMailJetEMailRequest request = new SendMailJetEMailRequest();
        request.setTemplateId(6285030); // Cambiar en configuración

        // Recipients
        List<MailJetRecipient> recipients = new ArrayList<>();
        recipients.add(new MailJetRecipient(agency.getMailingAddress(), agency.getName()));
        recipients.add(new MailJetRecipient(employeeDto.getEmail(), employeeDto.getFirstName() + " " + employeeDto.getLastName()));
        recipients.add(new MailJetRecipient("keimermo1989@gmail.com", "Keimer Montes"));
        recipients.add(new MailJetRecipient("enrique.muguercia2016@gmail.com", "Enrique Basto"));
        recipients.add(new MailJetRecipient("reimardelgado@gmail.com", "Enrique Basto"));
        recipients.add(new MailJetRecipient(agency.getMailingAddress(), agency.getName()));

        request.setRecipientEmail(recipients);

        for (ManageInvoiceDto invoice : invoices) {
            try {
                request.setSubject("INVOICES for " + agency.getName() + "-" + invoice.getInvoiceNo());

                String invoiceAmount = (invoice.getInvoiceAmount() != null)
                        ? invoice.getInvoiceAmount().toString()
                        : "0.00";

                // Variables para el template de email
                List<MailJetVar> vars = Arrays.asList(
                        new MailJetVar("invoice_date", new Date().toString()),
                        new MailJetVar("invoice_amount", invoiceAmount)
                );
                request.setMailJetVars(vars);

                List<MailJetAttachment> attachments = new ArrayList<>();
                Optional<ByteArrayOutputStream> invoiceBooking = getInvoicesBooking(invoice.getId().toString(), command);

                if (invoiceBooking.isPresent()) {
                    String nameFile = invoice.getInvoiceNumber() + ".pdf";
                    byte[] pdfBytes = invoiceBooking.get().toByteArray();
                    String base64EncodedPDF = Base64.getEncoder().encodeToString(pdfBytes);

                    MailJetAttachment attachment = new MailJetAttachment("application/pdf", nameFile, base64EncodedPDF);
                    attachments.add(attachment);
                    request.setMailJetAttachments(attachments);
                } else {
                    invoice.setSendStatusError("No se pudo obtener el archivo PDF.");
                    service.update(invoice);
                    continue; // Continuar con el siguiente invoice
                }

                try {
                    mailService.sendMail(request);
                    updateStatusAgency(invoice, manageInvoiceStatus, employee);
                } catch (Exception e) {
                    invoice.setSendStatusError(e.getMessage());
                    service.update(invoice);
                }

            } catch (DocumentException | IOException e) {
                invoice.setSendStatusError(e.getMessage());
                service.update(invoice);
            }
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

    private static void savePDF(ByteArrayOutputStream pdfStream) {
        // Aquí debes implementar la lógica para guardar el PDF
        // Por ejemplo, escribir el archivo en el disco
        try (FileOutputStream fos = new FileOutputStream("invoice.pdf")) {

            pdfStream.writeTo(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}