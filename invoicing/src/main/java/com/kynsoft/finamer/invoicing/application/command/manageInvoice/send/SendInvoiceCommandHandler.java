package com.kynsoft.finamer.invoicing.application.command.manageInvoice.send;

import com.kynsof.share.core.application.ftp.FtpService;
import com.kynsof.share.core.application.mailjet.*;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.service.IFtpService;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.invoicing.infrastructure.services.FileService;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.services.*;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAgencyContact;
import com.kynsoft.finamer.invoicing.infrastructure.services.InvoiceXmlService;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.factory.InvoiceReportProviderFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
    private final IInvoiceCloseOperationService closeOperationService;
    private final FileService fileService;

    private static final Logger log = LoggerFactory.getLogger(SendInvoiceCommandHandler.class);
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    public SendInvoiceCommandHandler(IManageInvoiceService service, MailService mailService,
                                     IManageEmployeeService manageEmployeeService, InvoiceXmlService invoiceXmlService,
                                     IManageInvoiceStatusService manageInvoiceStatusService,
                                     FtpService ftpService, InvoiceReportProviderFactory invoiceReportProviderFactory,
                                     IInvoiceStatusHistoryService invoiceStatusHistoryService,
                                     IManageAgencyContactService manageAgencyContactService,
                                     IInvoiceCloseOperationService closeOperationService,
                                     FileService fileService) {

        this.service = service;
        this.mailService = mailService;
        this.manageEmployeeService = manageEmployeeService;
        this.invoiceXmlService = invoiceXmlService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.ftpService = ftpService;
        this.invoiceReportProviderFactory = invoiceReportProviderFactory;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
        this.manageAgencyContactService = manageAgencyContactService;
        this.closeOperationService = closeOperationService;
        this.fileService = fileService;
    }

    @Override
    @Transactional
    public void handle(SendInvoiceCommand command) {
        ManageEmployeeDto manageEmployeeDto = manageEmployeeService.findById(UUID.fromString(command.getEmployee()));
        List<ManageInvoiceDto> invoices = this.service.findByIds(command.getInvoice());
        LocalDate today = LocalDate.now();

        invoices = invoices.stream()
                .filter(invoice -> {
                    ManageAgencyDto agency = invoice.getAgency();
                    if (agency != null && Boolean.TRUE.equals(agency.getValidateCheckout())) {
                        List<ManageBookingDto> bookings = invoice.getBookings();
                        if (bookings != null && !bookings.isEmpty()) {
                            boolean hasInvalidCheckout = bookings.stream()
                                    .anyMatch(booking -> booking.getCheckOut() != null && booking.getCheckOut().toLocalDate().isAfter(today));

                            if (hasInvalidCheckout) {
                                log.warn("⚠️ Invoice {} is skipped due to future checkout date.", invoice.getInvoiceNumber());
                            }
                            return !hasInvalidCheckout;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        if (invoices.isEmpty()) {
            log.warn("⚠️ No invoices found after checkout validation.");
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.SERVICE_NOT_FOUND,
                    new ErrorField("id", DomainErrorMessage.SERVICE_NOT_FOUND.getReasonPhrase())));
        }

        ManageInvoiceStatusDto manageInvoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.SENT);
        ManageAgencyDto agency = invoices.get(0).getAgency();

        if (agency.getSentB2BPartner() == null) {
            log.info("🔹 No B2B Partner assigned. Updating status without external sending.");
            updateStatusAgency(invoices, manageInvoiceStatus, manageEmployeeDto.getFirstName() + " " + manageEmployeeDto.getLastName());
            return;
        }

        String partnerType = agency.getSentB2BPartner().getB2BPartnerTypeDto().getCode();
        List<CompletableFuture<Void>> asyncTasks = new ArrayList<>();

        switch (partnerType) {
            case "EML":
                asyncTasks.add(sendEmailAsync(command, agency, invoices, manageEmployeeDto, manageInvoiceStatus,
                        manageEmployeeDto.getFirstName() + " " + manageEmployeeDto.getLastName()));
                break;
            case "BVL":
                asyncTasks.add(bavelAsync(agency, invoices, manageInvoiceStatus,
                        manageEmployeeDto.getFirstName() + " " + manageEmployeeDto.getLastName()));
                break;
            case "FTP":
                asyncTasks.add(sendFtpAsync(command, invoices, manageInvoiceStatus,
                        manageEmployeeDto.getFirstName() + " " + manageEmployeeDto.getLastName()));
                break;
            default:
                log.error("❌ Unsupported B2B partner type: {}", partnerType);
                throw new RuntimeException("Unsupported partner type: " + partnerType);
        }

        // Ensure all async tasks complete before proceeding
        CompletableFuture.allOf(asyncTasks.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> {
                    log.error("❌ Async processing failed: {}", ex.getMessage(), ex);
                    return null;
                })
                .join();

        command.setResult(true);
    }

    private void updateStatusAgency(List<ManageInvoiceDto> invoices, ManageInvoiceStatusDto manageInvoiceStatus,
        String employee) {
        log.info("Starting updateStatusAgency for {} invoices", invoices.size());
        for (ManageInvoiceDto manageInvoiceDto : invoices) {
            if (manageInvoiceDto.getStatus().equals(EInvoiceStatus.RECONCILED)) {
                manageInvoiceDto.setStatus(EInvoiceStatus.SENT);
                manageInvoiceDto.setManageInvoiceStatus(manageInvoiceStatus);
                this.invoiceStatusHistoryService.create(
                        new InvoiceStatusHistoryDto(
                                UUID.randomUUID(),
                                manageInvoiceDto,
                                "The invoice data was update.",
                                null,
                                employee,
                                EInvoiceStatus.SENT,
                                0L
                        )
                );
                manageInvoiceDto.setDueDate(LocalDate.now().plusDays(manageInvoiceDto.getAgency().getCreditDay().longValue()));
                this.service.update(manageInvoiceDto);
                log.info("Invoice {} updated to status {}", manageInvoiceDto.getInvoiceNumber(), manageInvoiceDto.getStatus());
            }
            else if (manageInvoiceDto.getStatus().equals(EInvoiceStatus.SENT)){
                manageInvoiceDto.setReSend(true);
                manageInvoiceDto.setReSendDate(LocalDate.now());
                manageInvoiceDto.setDueDate(LocalDate.now().plusDays(manageInvoiceDto.getAgency().getCreditDay().longValue()));
                this.service.update(manageInvoiceDto);
                log.info("Invoice {} marked for resend", manageInvoiceDto.getInvoiceNumber());
            }
        }
    }

    private CompletableFuture<Void> bavelAsync(ManageAgencyDto agency, List<ManageInvoiceDto> invoices,
                                               ManageInvoiceStatusDto manageInvoiceStatus, String employee) {
        return CompletableFuture.runAsync(() -> bavel(agency, invoices, manageInvoiceStatus, employee), executor);
    }

    private void bavel(ManageAgencyDto agency, List<ManageInvoiceDto> invoices, ManageInvoiceStatusDto manageInvoiceStatus,
                       String employee) {
        List<CompletableFuture<String>> uploadFutures = new ArrayList<>();

        for (ManageInvoiceDto invoice : invoices) {
            try {
                var xmlContent = invoiceXmlService.generateInvoiceXml(invoice);
                String bavelCode = invoice.getHotel() != null ?
                        Optional.ofNullable(invoice.getHotel().getBabelCode()).orElse(StringUtils.EMPTY) :
                        StringUtils.EMPTY;

                String _company = Optional.ofNullable(invoice.getAgency())
                        .map(agencyDto -> agencyDto.getName())
                        .orElse(StringUtils.EMPTY)
                        .replace("/", " ");

                // Formateadores de fecha
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                DateTimeFormatter formatterWithSpaces = DateTimeFormatter.ofPattern("dd MM yyyy");

                // Construcción del nombre del archivo
                String nameFile = "Factura " + bavelCode + " " + _company + " " +
                        invoice.getInvoiceNo().toString() + " " +
                        invoice.getInvoiceDate().toLocalDate().format(formatter) + " " +
                        LocalDate.now().format(formatterWithSpaces) + ".xml";
                InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));

                log.info("📤 Initiating async upload for invoice '{}' to Bavel FTP at '{}'", nameFile, agency.getSentB2BPartner().getUrl());

                CompletableFuture<String> uploadFuture = ftpService.sendFile(inputStream, nameFile,
                                agency.getSentB2BPartner().getIp(), agency.getSentB2BPartner().getUserName(),
                                agency.getSentB2BPartner().getPassword(), 21, agency.getSentB2BPartner().getUrl())
                        .handle((response, ex) -> {
                            if (ex == null) {
                                log.info("✅ Invoice '{}' successfully uploaded to Bavel FTP.", nameFile);
                                invoice.setSendStatusError(null); // Clear previous errors
                                service.update(invoice);
                                return response;
                            } else {
                                log.error("❌ Upload failed for invoice '{}': {}", nameFile, ex.getMessage(), ex);
                                invoice.setSendStatusError("Bavel FTP Upload Failed: " + ex.getMessage());
                                service.update(invoice);
                                return "FAILED";
                            }
                        });

                uploadFutures.add(uploadFuture);
            } catch (Exception e) {
                log.error("❌ Failed to generate XML for invoice '{}': {}", invoice.getInvoiceNumber(), e.getMessage(), e);
                invoice.setSendStatusError("XML Generation Failed: " + e.getMessage());
                service.update(invoice);
            }
        }

        // Esperar todas las subidas sin interrumpir la ejecución en caso de error
        CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0])).join();

        // Filtrar solo las facturas que se subieron con éxito
        List<ManageInvoiceDto> successfulInvoices = invoices.stream()
                .filter(invoice -> invoice.getSendStatusError() == null)
                .collect(Collectors.toList());

        if (!successfulInvoices.isEmpty()) {
            updateStatusAgency(successfulInvoices, manageInvoiceStatus, employee);
        }
    }

    private CompletableFuture<Void> sendFtpAsync(SendInvoiceCommand command, List<ManageInvoiceDto> invoices,
                                                 ManageInvoiceStatusDto manageInvoiceStatus, String employee) {
        return CompletableFuture.runAsync(() -> sendFtp(command, invoices, manageInvoiceStatus, employee), executor);
    }

    private void sendFtp(SendInvoiceCommand command, List<ManageInvoiceDto> invoices,
                         ManageInvoiceStatusDto manageInvoiceStatus, String employee) {
        boolean groupByClient = command.isGroupByClient();

        log.info("📤 Initiating FTP process for {} invoices", invoices.size());

        try {
            InvoiceGrouper invoiceGrouper = new InvoiceGrouper(invoiceReportProviderFactory);
            List<GeneratedInvoice> generatedPDFs = invoiceGrouper.generateInvoicesPDFs(invoices, groupByClient, command.isWithAttachment());

            List<CompletableFuture<String>> uploadFutures = new ArrayList<>();

            for (GeneratedInvoice generatedInvoice : generatedPDFs) {
                InputStream pdfStream = new ByteArrayInputStream(generatedInvoice.getPdfStream().toByteArray());
                LocalDateTime currentDate = generateDate(generatedInvoice.getInvoices().get(0).getHotel().getId());
                String monthFormatted = currentDate.format(DateTimeFormatter.ofPattern("MM"));
                String dayFormatted = currentDate.format(DateTimeFormatter.ofPattern("dd"));

                String path = currentDate.getYear() + "/" + monthFormatted + "/" + dayFormatted + "/"
                        + invoices.get(0).getHotel().getCode();

                log.info("📤 Preparing to upload invoice '{}' to FTP at '{}'", generatedInvoice.getNameFile(), path);

                CompletableFuture<String> uploadFuture = ftpService.sendFile(pdfStream, generatedInvoice.getNameFile(),
                                generatedInvoice.getIp(), generatedInvoice.getUserName(),
                                generatedInvoice.getPassword(), 21, path)
                        .handle((response, ex) -> {
                            if (ex == null) {
                                log.info("✅ Invoice '{}' successfully uploaded to FTP.", generatedInvoice.getNameFile());
                                generatedInvoice.getInvoices().forEach(invoice -> {
                                    invoice.setSendStatusError(null);
                                    service.update(invoice);
                                });
                                return response;
                            } else {
                                log.error("❌ FTP upload failed for invoice '{}': {}", generatedInvoice.getNameFile(), ex.getMessage(), ex);
                                generatedInvoice.getInvoices().forEach(invoice -> {
                                    invoice.setSendStatusError("FTP Upload Failed: " + ex.getMessage());
                                    service.update(invoice);
                                });
                                return "FAILED";
                            }
                        });

                uploadFutures.add(uploadFuture);
            }

            // Esperar todas las subidas sin interrumpir la ejecución en caso de error
            CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0])).join();

            // Filtrar solo las facturas que se subieron con éxito
            List<ManageInvoiceDto> successfulInvoices = invoices.stream()
                    .filter(invoice -> invoice.getSendStatusError() == null)
                    .collect(Collectors.toList());

            if (!successfulInvoices.isEmpty()) {
                updateStatusAgency(successfulInvoices, manageInvoiceStatus, employee);
            }
        } catch (Exception e) {
            log.error("❌ Unexpected error while processing invoices for FTP: {}", e.getMessage(), e);
            throw new RuntimeException("Error processing invoices for FTP: " + e.getMessage(), e);
        }
    }

    private CompletableFuture<Void> sendEmailAsync(SendInvoiceCommand command, ManageAgencyDto agency,
                                                   List<ManageInvoiceDto> invoices, ManageEmployeeDto employeeDto,
                                                   ManageInvoiceStatusDto manageInvoiceStatus, String employee) {
        return CompletableFuture.runAsync(() -> sendEmail(command, agency, invoices, employeeDto, manageInvoiceStatus, employee), executor);
    }
    private void sendEmail(SendInvoiceCommand command, ManageAgencyDto agency, List<ManageInvoiceDto> invoices,
       ManageEmployeeDto employeeDto, ManageInvoiceStatusDto manageInvoiceStatus, String employee) {

        log.info("Initiating email process for agency: {}", agency.getName());
        Map<UUID, Map<UUID, List<ManageInvoiceDto>>> invoicesByAgencyAndHotel = invoices.stream()
                .collect(Collectors.groupingBy(
                        invoice -> invoice.getAgency().getId(), // Group by agency
                        Collectors.groupingBy(invoice -> invoice.getHotel().getId()) // Then group by hotel
                ));

        for (Map.Entry<UUID, Map<UUID, List<ManageInvoiceDto>>> agencyEntry : invoicesByAgencyAndHotel.entrySet()) {
            UUID agencyId = agencyEntry.getKey();
            Map<UUID, List<ManageInvoiceDto>> invoicesByHotel = agencyEntry.getValue();

            log.info("📌 Processing {} invoices for agency ID: {}", invoicesByHotel.values().stream().mapToInt(List::size).sum(), agencyId);

            for (Map.Entry<UUID, List<ManageInvoiceDto>> hotelEntry : invoicesByHotel.entrySet()) {
                UUID hotelId = hotelEntry.getKey();
                List<ManageInvoiceDto> invoicesHotel = hotelEntry.getValue();

                log.info("🏨 Sending invoices for hotel ID: {}", hotelId);

                try {
                    SendMailJetEMailRequest request = new SendMailJetEMailRequest();
                    List<MailJetRecipient> recipients = getMailJetRecipients(agency, employeeDto, request, invoicesHotel);
                    request.setRecipientEmail(recipients);

                    List<MailJetVar> vars = getMailJetVars(agency, request, invoicesHotel);
                    request.setMailJetVars(vars);

                    List<MailJetAttachment> attachments = getMailJetAttachments(invoicesHotel, employeeDto);
                    request.setMailJetAttachments(attachments);

                    mailService.sendMail(request);

                    updateInvoices(invoicesHotel, manageInvoiceStatus, employee);
                    log.info("✅ Successfully sent invoices via email for hotel ID: {}", hotelId);

                } catch (Exception e) {
                    log.error("❌ Failed to send invoices via email for hotel ID: {}: {}", hotelId, e.getMessage(), e);
                }
            }
        }
    }

    private List<MailJetAttachment> getMailJetAttachments(List<ManageInvoiceDto> agencyInvoices, ManageEmployeeDto employeeDto) {
        List<MailJetAttachment> attachments = new ArrayList<>();
        List<UUID> ids = agencyInvoices.stream().map(ManageInvoiceDto::getId).toList();
    String base64 = "";
    String fileName;
    try {
        fileName = agencyInvoices.get(0).getAgency().getName() + " Account Statement.xlsx";
    } catch (Exception e) {
        log.warn("⚠️ Failed to retrieve agency name for filename, using default: {}", e.getMessage());
        fileName = "Account Statement.xlsx";
    }
    try {
        log.info("📄 Generating Excel file for invoices...");
        base64 = this.fileService.convertExcelToBase64(ids, employeeDto);
        log.info("✅ Excel file successfully generated for {} invoices.", ids.size());
    } catch (IOException ex) {
        log.error("❌ Failed to generate Excel file: {}", ex.getMessage(), ex);
    }
    MailJetAttachment attachment = new MailJetAttachment(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",  // MIME type for .xlsx
            fileName,
            base64
    );
        attachments.add(attachment);
        return attachments;
    }

    private static List<MailJetVar> getMailJetVars(ManageAgencyDto agency, SendMailJetEMailRequest request,
       List<ManageInvoiceDto> agencyInvoices) {
        request.setSubject("INVOICES for -" + agency.getCode() + "-" + agency.getName());
        double totalAmount = agencyInvoices.stream()
                .mapToDouble(invoice -> invoice.getInvoiceAmount() != null ? invoice.getInvoiceAmount() : 0.0)
                .sum();
        String invoiceAmount = String.format(Locale.US, "%.2f", totalAmount);

        log.info("📧 Preparing email variables for agency '{}' with total invoice amount: {}", agency.getName(), invoiceAmount);

            // Variables para el template de email
            return Arrays.asList(
                    new MailJetVar("invoice_date", new Date().toString()),
                    new MailJetVar("invoice_amount", invoiceAmount)
            );
    }

    private List<MailJetRecipient> getMailJetRecipients(ManageAgencyDto agency, ManageEmployeeDto employeeDto,
        SendMailJetEMailRequest request, List<ManageInvoiceDto> agencyInvoices) {
        request.setTemplateId(6285030); // TODO: Move to configuration variable
        List<MailJetRecipient> recipients = new ArrayList<>();

        try {
            log.info("📧 Adding agency and employee recipients for email.");

            if (agency.getMailingAddress() != null && !agency.getMailingAddress().isBlank()) {
                recipients.add(new MailJetRecipient(agency.getMailingAddress(), agency.getName()));
            } else {
                log.warn("No mailing address provided for agency: {}", agency.getName());
            }

            if (employeeDto.getEmail() != null && !employeeDto.getEmail().isBlank()) {
                recipients.add(new MailJetRecipient(employeeDto.getEmail(), employeeDto.getFirstName() + " " + employeeDto.getLastName()));
            }

            recipients.add(new MailJetRecipient("keimermo1989@gmail.com", "Keimer Montes"));//TODO cambiar esto urgente

            ManageAgencyDto manageAgencyDto = agencyInvoices.get(0).getAgency();
            ManageHotelDto manageHotelDto = agencyInvoices.get(0).getHotel();
            List<ManageAgencyContact> contactList = manageAgencyContactService.findContactsByHotelAndAgency(manageHotelDto.getId(), manageAgencyDto.getId());

            if (!contactList.isEmpty()) {
                log.info("📌 Adding agency contacts to email recipients.");
                String[] emailAddresses = contactList.get(0).getEmailContact().split(";");
                for (String email : emailAddresses) {
                    email = email.trim();
                    if (!email.isEmpty()) {
                        recipients.add(new MailJetRecipient(email, "Contact"));
                    }
                }
            } else {
                log.warn("⚠️ No contacts found for agency '{}' and hotel '{}'.", manageAgencyDto.getName(), manageHotelDto.getName());
            }

            log.info("✅ Total email recipients: {}", recipients.size());
        } catch (Exception e) {
            log.error("❌ Error while generating email recipients: {}", e.getMessage(), e);
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
                                EInvoiceStatus.SENT,
                                0L
                        )
                );
            }if (manageInvoiceDto.getStatus().equals(EInvoiceStatus.SENT)){
                manageInvoiceDto.setReSend(true);
                manageInvoiceDto.setDueDate(LocalDate.now().plusDays(manageInvoiceDto.getAgency().getCreditDay().longValue()));
                manageInvoiceDto.setReSendDate(LocalDate.now());
            }
            log.info("Updating invoice {} with status {}", manageInvoiceDto.getInvoiceNumber(), manageInvoiceDto.getStatus());
            service.update(manageInvoiceDto);
        }

    }

    private LocalDateTime generateDate(UUID hotel) {
        InvoiceCloseOperationDto closeOperationDto = this.closeOperationService.findActiveByHotelId(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return LocalDateTime.now(ZoneId.of("UTC"));
        }
        return LocalDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")));
    }
}