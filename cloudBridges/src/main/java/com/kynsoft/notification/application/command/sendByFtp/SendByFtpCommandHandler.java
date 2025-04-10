//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kynsoft.notification.application.command.sendByFtp;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.response.FileDto;
import com.kynsof.share.core.domain.response.ResponseStatus;
import com.kynsof.share.core.domain.response.UploadFileResponse;
import com.kynsof.share.core.domain.service.IAmazonClient;
import com.kynsoft.notification.domain.service.IFTPService;
import com.kynsoft.notification.infrastructure.service.kafka.producer.ProducerSendByFtpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component
public class SendByFtpCommandHandler implements ICommandHandler<SendByFtpCommand> {
    private static final Logger logger = Logger.getLogger(SendByFtpCommandHandler.class.getName());
    private final IAmazonClient amazonClient;
    private final IFTPService ftpService;
    private final ProducerSendByFtpResponse producerSendByFtpResponse;

    public SendByFtpCommandHandler(IAmazonClient amazonClient, IFTPService ftpService, ProducerSendByFtpResponse producerSendByFtpResponse) {
        this.amazonClient = amazonClient;
        this.ftpService = ftpService;
        this.producerSendByFtpResponse = producerSendByFtpResponse;
    }

    public void handle(SendByFtpCommand command) {
        if (command.getFileDtos() != null && !command.getFileDtos().isEmpty()) {
            List<FileDto> successfulDownloads = new ArrayList<>();
            List<FileDto> failedDownloads = new ArrayList<>();
            this.amazonClient.setBucketName(command.getBucketName());

            for(FileDto fileDto : command.getFileDtos()) {
                try {
                byte[] fileContent = this.amazonClient.downloadFile(fileDto.getUrl());
                    if (fileContent != null && fileContent.length > 0) {
                        fileDto.setFileContent(fileContent);
                        successfulDownloads.add(fileDto);
                    } else {
                        fileDto.setUploadFileResponse(new UploadFileResponse(ResponseStatus.ERROR_RESPONSE,
                                "Failed downloading file from common storage"));
                        failedDownloads.add(fileDto);
                    }
                } catch (Exception e) {
                    fileDto.setUploadFileResponse(new UploadFileResponse(ResponseStatus.ERROR_RESPONSE,
                            "Failed downloading file from common storage"));
                    failedDownloads.add(fileDto);
                }
            }

            if (!failedDownloads.isEmpty()) {
                this.producerSendByFtpResponse.create(failedDownloads);
            }

            if (!successfulDownloads.isEmpty()) {
                this.ftpService.uploadFilesBatch(command.getUrl(), successfulDownloads, command.getServer(), command.getUserName(),
                        command.getPassword()).doOnSuccess((unused) -> {
                    logger.info("✅ FTP batch upload completed. Sending successful uploads to Kafka.");
                    this.producerSendByFtpResponse.create(successfulDownloads);
                }).doOnError((error) -> {
                    logger.severe("❌ FTP batch upload failed: " + error.getMessage());
                    this.producerSendByFtpResponse.create(successfulDownloads);
                }).subscribe();
            }

        } else {
            logger.warning("No files to process in SendByFtpCommand.");
        }
    }
}
