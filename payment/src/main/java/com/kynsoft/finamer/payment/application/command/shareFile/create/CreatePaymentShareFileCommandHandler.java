package com.kynsoft.finamer.payment.application.command.shareFile.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.service.IFtpService;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentShareFileDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentShareFileService;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CreatePaymentShareFileCommandHandler implements ICommandHandler<CreatePaymentShareFileCommand> {

    private final IPaymentService paymentService;
    private final IPaymentShareFileService paymentShareFileService;
    private final IFtpService ftpService;
    public CreatePaymentShareFileCommandHandler(IPaymentService paymentService, IPaymentShareFileService paymentShareFileService, IFtpService ftpService) {
        this.paymentService = paymentService;
        this.paymentShareFileService = paymentShareFileService;
        this.ftpService = ftpService;
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

        String path = "payment/"+currentDate.getYear() + "/" + monthFormatted + "/" + dayFormatted + "/"
                + paymentDto.getHotel().getCode()+"/"+paymentDto.getClient().getName();

        ftpService.sendFile(inputStream, command.getFileName(), "172.20.41.96",
                "usrftp01", "usuarioftp01*", 21, path);

        paymentShareFileService.create(new PaymentShareFileDto(
                command.getId(),
                 paymentDto,
                command.getFileName(),
                path+"/"+command.getFileName()
        ));
    }
}
