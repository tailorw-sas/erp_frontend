package com.kynsoft.finamer.payment.application.command.shareFile.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.service.IFtpService;
import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentShareFileDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.services.IAttachmentStatusHistoryService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentShareFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class CreatePaymentShareFileCommandHandler implements ICommandHandler<CreatePaymentShareFileCommand> {

    private final IPaymentService paymentService;
    private final IPaymentShareFileService paymentShareFileService;
    private final IFtpService ftpService;
    private final IManagePaymentAttachmentStatusService paymentAttachmentStatusService;
    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;

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

    public CreatePaymentShareFileCommandHandler(IPaymentService paymentService, IPaymentShareFileService paymentShareFileService, IFtpService ftpService,
            IManagePaymentAttachmentStatusService paymentAttachmentStatusService, IMasterPaymentAttachmentService masterPaymentAttachmentService,
            IAttachmentStatusHistoryService attachmentStatusHistoryService) {
        this.paymentService = paymentService;
        this.paymentShareFileService = paymentShareFileService;
        this.ftpService = ftpService;
        this.paymentAttachmentStatusService = paymentAttachmentStatusService;
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
    }

    @Override
    public void handle(CreatePaymentShareFileCommand command) {

        PaymentDto paymentDto = this.paymentService.findById(command.getPaymentId());
        InputStream inputStream = new ByteArrayInputStream(command.getFileData());
        LocalDate currentDate = LocalDate.now();

        // Crear un formateador para "MM" (mes con dos dígitos) y "dd" (día con dos dígitos)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

        // Aplicar el formato a la fecha actual
        String monthAndDayFormatted = currentDate.format(formatter);

        // Desglosar los valores en separado si se necesita
        String monthFormatted = currentDate.format(DateTimeFormatter.ofPattern("MM"));
        String dayFormatted = currentDate.format(DateTimeFormatter.ofPattern("dd"));

        String path = ftpBasePath + "/" + currentDate.getYear() + "/" + monthFormatted + "/" + dayFormatted + "/"
                + paymentDto.getHotel().getCode() + "/" + paymentDto.getClient().getName();

        ftpService.sendFile(inputStream, command.getFileName(), ftpServerAddress,
                ftpUsername, ftpPassword, ftpServerPort, path);

        paymentShareFileService.create(new PaymentShareFileDto(
                command.getId(),
                paymentDto,
                command.getFileName(),
                path + "/" + command.getFileName()
        ));

        paymentDto.setEAttachment(EAttachment.ATTACHMENT_WITHOUT_ERROR);
        paymentDto.setAttachmentStatus(this.paymentAttachmentStatusService.findByPatWithAttachment());
        this.paymentService.update(paymentDto);
        //createAttachmentStatusHistory(paymentDto, share);
    }

    //Preguntar si eso se va a poner como un attachment siguiendo la logica del sistema. Aun cuando va a un FTP y no a la propia infra.
    private void createAttachmentStatusHistory(PaymentDto payment, PaymentShareFileDto share) {

        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("An attachment share file to the payment was inserted. The file name: " + share.getFileName());
        attachmentStatusHistoryDto.setEmployee(null);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getAttachmentStatus().getCode() + "-" + payment.getAttachmentStatus().getName());
        //attachmentStatusHistoryDto.setAttachmentId(share.getId());

        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);

    }
}
