package com.kynsoft.finamer.payment.application.command.shareFile.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.service.IFtpService;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentShareFileDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentShareFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

@Component
public class CreatePaymentShareFileCommandHandler implements ICommandHandler<CreatePaymentShareFileCommand> {

    private static final Logger log = LoggerFactory.getLogger(CreatePaymentShareFileCommandHandler.class);

    // Configuración avanzada del ThreadPoolExecutor
    private static final ExecutorService executorService = new ThreadPoolExecutor(
            10, 50, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

    private final IPaymentService paymentService;
    private final IPaymentShareFileService paymentShareFileService;
    private final IFtpService ftpService;
    private final IManagePaymentAttachmentStatusService paymentAttachmentStatusService;

    @Value("${ftp.server.address}")
    private String ftpServerAddress;

    @Value("${ftp.server.port}")
    private int ftpServerPort;

    @Value("${ftp.username}")
    private String ftpUsername;

    @Value("${ftp.password}")
    private String ftpPassword;

    @Value("${ftp.base.path}")
    private String ftpBasePath;

    public CreatePaymentShareFileCommandHandler(IPaymentService paymentService,
                                                IPaymentShareFileService paymentShareFileService,
                                                IFtpService ftpService,
                                                IManagePaymentAttachmentStatusService paymentAttachmentStatusService) {
        this.paymentService = paymentService;
        this.paymentShareFileService = paymentShareFileService;
        this.ftpService = ftpService;
        this.paymentAttachmentStatusService = paymentAttachmentStatusService;
    }

    @Override
    public void handle(CreatePaymentShareFileCommand command) {
        PaymentDto paymentDto = this.paymentService.findById(command.getPaymentId());
        InputStream inputStream = new ByteArrayInputStream(command.getFileData());
        LocalDate currentDate = LocalDate.now();

        // Construcción del path
        String monthFormatted = currentDate.format(DateTimeFormatter.ofPattern("MM"));
        String dayFormatted = currentDate.format(DateTimeFormatter.ofPattern("dd"));
        String path = ftpBasePath + "/" + currentDate.getYear() + "/" + monthFormatted + "/" + dayFormatted + "/"
                + paymentDto.getHotel().getCode() + "/" + paymentDto.getClient().getName();

        log.info("📤 Initiating async upload for payment file '{}' to FTP at '{}'", command.getFileName(), path);

        CompletableFuture.supplyAsync(() -> ftpService.sendFile(inputStream, command.getFileName(),
                        ftpServerAddress, ftpUsername, ftpPassword, ftpServerPort, path), executorService)
                .thenCompose(future -> future) // Unwrap nested CompletableFuture
                .thenAccept(response -> {
                    log.info("✅ File '{}' successfully uploaded to FTP. Response: {}", command.getFileName(), response);

                    // Asegurar actualización segura del estado de la factura
                    synchronized (paymentDto) {
                        paymentShareFileService.create(new PaymentShareFileDto(
                                command.getId(), paymentDto, command.getFileName(), path + "/" + command.getFileName()
                        ));
                        paymentDto.setEAttachment(EAttachment.ATTACHMENT_WITHOUT_ERROR);
                        paymentDto.setAttachmentStatus(this.paymentAttachmentStatusService.findByPatWithAttachment());
                        this.paymentService.update(paymentDto);
                    }
                })
                .exceptionally(ex -> {
                    log.error("❌ FTP upload failed for payment file '{}': {}", command.getFileName(), ex.getMessage(), ex);
                    synchronized (paymentDto) {
                        paymentDto.setEAttachment(EAttachment.ATTACHMENT_WITH_ERROR);
                        this.paymentService.update(paymentDto);
                    }
                    return null;
                });
    }
}