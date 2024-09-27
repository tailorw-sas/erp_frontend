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
import jakarta.xml.bind.JAXBException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    public SendInvoiceCommandHandler(IManageInvoiceService service, MailService mailService,
                                     IManageEmployeeService manageEmployeeService, InvoiceXmlService invoiceXmlService,
                                     IManageInvoiceStatusService manageInvoiceStatusService,
                                     FtpService ftpService, InvoiceReportProviderFactory invoiceReportProviderFactory) {

        this.service = service;
        this.mailService = mailService;
        this.manageEmployeeService = manageEmployeeService;
        this.invoiceXmlService = invoiceXmlService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.ftpService = ftpService;
        this.invoiceReportProviderFactory = invoiceReportProviderFactory;
    }

    @Override
    @Transactional
    public void handle(SendInvoiceCommand command) {
        ManageEmployeeDto manageEmployeeDto = manageEmployeeService.findById(UUID.fromString(command.getEmployee()));
        // Obtener la lista de facturas por sus IDs
        List<ManageInvoiceDto> invoices = this.service.findByIds(command.getInvoice());

        // Agrupar facturas por agencia
        Map<ManageAgencyDto, List<ManageInvoiceDto>> invoicesByAgency = new HashMap<>();
        for (ManageInvoiceDto invoice : invoices) {
            if (!invoice.getStatus().equals(EInvoiceStatus.SENT) && !invoice.getStatus().equals(EInvoiceStatus.RECONCILED)) {
                throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.SERVICE_NOT_FOUND,
                        new ErrorField("id", DomainErrorMessage.SERVICE_NOT_FOUND.getReasonPhrase())));
            }
            invoicesByAgency.computeIfAbsent(invoice.getAgency(), k -> new ArrayList<>()).add(invoice);
        }

       ManageInvoiceStatusDto manageInvoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.SENT);
        // Enviar correos agrupados por agencia
        for (Map.Entry<ManageAgencyDto, List<ManageInvoiceDto>> entry : invoicesByAgency.entrySet()) {
            ManageAgencyDto agency = entry.getKey();
            List<ManageInvoiceDto> agencyInvoices = entry.getValue();
            if (agency.getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("EML")) {
                sendEmail(command, agency, agencyInvoices, manageEmployeeDto, manageInvoiceStatus);
            }

            if (agency.getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("BVL")) {
                try {
                    bavel(agency, agencyInvoices, manageInvoiceStatus);
                } catch (DocumentException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (agency.getSentB2BPartner().getB2BPartnerTypeDto().getCode().equals("FTP")) {

                try {
                    sendFtp(agency, agencyInvoices, manageInvoiceStatus);
                } catch (DocumentException | IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        command.setResult(true);
    }

    private void updateStatusAgency(ManageInvoiceDto invoice, ManageInvoiceStatusDto manageInvoiceStatus) {
        invoice.setStatus(EInvoiceStatus.SENT);
        invoice.setManageInvoiceStatus(manageInvoiceStatus);
        if (!invoice.getStatus().equals(EInvoiceStatus.SENT)) {
            invoice.setReSend(true);
        }
        this.service.update(invoice);
    }

    private void bavel(ManageAgencyDto agency, List<ManageInvoiceDto> invoices, ManageInvoiceStatusDto manageInvoiceStatus) throws DocumentException, IOException {
        for (ManageInvoiceDto invoice : invoices) {
            try {
                var xmlContent = invoiceXmlService.generateInvoiceXml(invoice);
                String nameFile = invoice.getInvoiceNumber() + ".xml";
                InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
                ftpService.sendFile(inputStream, nameFile, agency.getSentB2BPartner().getIp(),
                        agency.getSentB2BPartner().getUserName(), agency.getSentB2BPartner().getPassword(), 21);
                updateStatusAgency(invoice, manageInvoiceStatus);
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendFtp(ManageAgencyDto agency, List<ManageInvoiceDto> invoices, ManageInvoiceStatusDto manageInvoiceStatus) throws DocumentException, IOException {
        for (ManageInvoiceDto invoice : invoices) {
            Optional<ByteArrayOutputStream> invoiceBooking = getInvoicesBooking(invoice.getId().toString());
            if (invoiceBooking.isPresent()) {
                String nameFile = invoice.getInvoiceNumber() + ".pdf";

                // Convertir ByteArrayOutputStream a InputStream directamente
                //TODO capturar los datos de conexcion del ftp que viene en el b2B parnet
                try (InputStream inputStream = new ByteArrayInputStream(invoiceBooking.get().toByteArray())) {
                    ftpService.sendFile(inputStream, nameFile, agency.getSentB2BPartner().getIp(),
                            agency.getSentB2BPartner().getUserName(), agency.getSentB2BPartner().getPassword(), 21);
                    updateStatusAgency(invoice, manageInvoiceStatus);
                    System.out.println("Archivo subido exitosamente al FTP.");
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Error al subir el archivo al servidor FTP: " + e.getMessage(), e);
                }
            } else {
                System.out.println("No se pudo obtener el archivo de las facturas.");
            }
        }
    }

    private void sendEmail(SendInvoiceCommand command, ManageAgencyDto agency, List<ManageInvoiceDto> invoices, ManageEmployeeDto employeeDto, ManageInvoiceStatusDto manageInvoiceStatus) {

        SendMailJetEMailRequest request = new SendMailJetEMailRequest();

        request.setTemplateId(6285030); // Cambiar en configuración



        // Recipients
        List<MailJetRecipient> recipients = new ArrayList<>();
        recipients.add(new MailJetRecipient(agency.getMailingAddress(), agency.getName()));
        recipients.add(new MailJetRecipient(employeeDto.getEmail(), employeeDto.getFirstName() + " " + employeeDto.getLastName()));
        recipients.add(new MailJetRecipient("keimermo1989@gmail.com", "Keimer Montes"));
        recipients.add(new MailJetRecipient("enrique.muguercia2016@gmail.com", "Enrique Basto"));
        recipients.add(new MailJetRecipient("reimardelgado@gmail.com", "Enrique Basto"));
        //TODO send email employee
        request.setRecipientEmail(recipients);
        // Adjuntar todas las facturas de la agencia

        for (ManageInvoiceDto invoice : invoices) {
            try {
                request.setSubject("INVOICES for " + agency.getName() + "-" + invoice.getInvoiceNo());
                // Variables para el template de email
                List<MailJetVar> vars = Arrays.asList(
                        new MailJetVar("invoice_date", new Date().toString()),
                        new MailJetVar("invoice_amount", invoice.getInvoiceAmount().toString())
                );

                request.setMailJetVars(vars);
                List<MailJetAttachment> attachments = new ArrayList<>();
                // Crear el adjunto con el XML
                String nameFileXml = invoice.getInvoiceNumber() + ".xml"; // Cambiar la extensión a .xml
                // MailJetAttachment attachmentXML = new MailJetAttachment("application/xml", nameFileXml, base64Xml);
                //   attachments.add(attachmentXML);
                Optional<ByteArrayOutputStream> invoiceBooking = getInvoicesBooking(invoice.getId().toString());
                String nameFile = invoice.getInvoiceNumber() + ".pdf";
                byte[] pdfBytes = invoiceBooking.get().toByteArray();

                // Codificar los bytes en Base64
                String base64EncodedPDF = Base64.getEncoder().encodeToString(pdfBytes);

                MailJetAttachment attachment = new MailJetAttachment("application/pdf", nameFile, base64EncodedPDF);
                attachments.add(attachment);
                request.setMailJetAttachments(attachments);
                try {
                    mailService.sendMail(request);
                    updateStatusAgency(invoice, manageInvoiceStatus);
                } catch (Exception ignored) {
                }
            } catch (DocumentException | IOException e) {
                throw new RuntimeException(e);
            }

        }


    }


    private Optional<ByteArrayOutputStream> getInvoicesBooking(String invoiceIds) throws DocumentException, IOException {
        // Configurar los servicios del reporte.
        Map<EInvoiceReportType, IInvoiceReport> services = new HashMap<>();
        services.put(EInvoiceReportType.INVOICE_AND_BOOKING,
                invoiceReportProviderFactory.getInvoiceReportService(EInvoiceReportType.INVOICE_AND_BOOKING));

        // Obtener el contenido del reporte
        Optional<Map<String, byte[]>> response = getReportContent(services, invoiceIds);

        // Verificar si la respuesta está presente y tiene contenido
        if (response.isPresent() && !response.get().isEmpty()) {
            // Tomar el primer contenido del mapa de bytes y convertirlo a ByteArrayOutputStream
            byte[] content = response.get().values().iterator().next(); // Obtiene el primer valor del mapa

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                outputStream.write(content);
                return Optional.of(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
                // Manejar la excepción según corresponda
                return Optional.empty();
            }
        } else {
            // Si no hay contenido, devolver Optional.empty()
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


        // Retornar el mapa de resultados si no está vacío
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    private List<Optional<byte[]>> getOrderReportContent(Map<EInvoiceReportType, Optional<byte[]>> content) {
        List<Optional<byte[]>> orderedContent = new LinkedList<>();
        orderedContent.add(content.getOrDefault(EInvoiceReportType.INVOICE_AND_BOOKING, Optional.empty()));
        orderedContent.add(content.getOrDefault(EInvoiceReportType.INVOICE_SUPPORT, Optional.empty()));
        return orderedContent;
    }

}
