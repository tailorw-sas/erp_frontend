package com.kynsof.share.core.application.ftp;

import com.kynsof.share.core.application.FileRequest;
import com.kynsof.share.core.domain.service.IFtpService;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class FtpService implements IFtpService {

    private static final Logger log = LoggerFactory.getLogger(FtpService.class);

    @Value("${ftp.api.url:http://localhost:8097/api/ftp}")
    private String ftpApiUrl;
    @Value("${ftp.api.url.maxRetries:3}")
    private int maxRetries;

    public CompletableFuture<String> sendFile(byte[] fileBytes, String fileName, String server, String user, String password, int port, String path) {
        return uploadFile(fileBytes, fileName, server, user, password, port, path)
            .handle((result, ex) -> {
                if (ex == null) {
                    return CompletableFuture.completedFuture(result);
                } else {
                    log.warn("⚠️ Initial upload attempt failed for '{}', applying retry strategy...", fileName);
                    return retryableFuture(() -> uploadFile(fileBytes, fileName, server, user, password, port, path), maxRetries);
                }
            })
            .thenCompose(result -> result); // Flatten nested CompletableFuture
    }

    public CompletableFuture<String> sendFiles(List<FileRequest> files, String server, String user, String password, int port, String path) {
        return CompletableFuture.supplyAsync(() -> {
            String uploadUrl = this.ftpApiUrl + "/batch-upload";
            log.info("🔗 Connecting to Cloud Bridges for batch upload: {}", uploadUrl);
            log.info("📂 Uploading {} files to FTP server '{}' at path '{}'", files.size(), server, path);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(uploadUrl);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                for (FileRequest fileRequest : files) {
                    builder.addBinaryBody("files", fileRequest.getFile(), ContentType.DEFAULT_BINARY, fileRequest.getFileName());
                    builder.addTextBody("objectId", fileRequest.getObjectId().toString());
                }

                builder.addTextBody("server", server)
                       .addTextBody("user", user)
                       .addTextBody("password", password)
                       .addTextBody("path", path)
                       .addTextBody("port", String.valueOf(port));

                post.setEntity(builder.build());

                HttpClientResponseHandler<String> responseHandler = (response) -> {
                    int statusCode = response.getCode();
                    String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8) : "No response";
                    if (statusCode == 200) {
                        return responseBody;
                    } else {
                        log.error("❌ Batch file upload failed | Status Code: {} | Response: {}", statusCode, responseBody);
                        throw new RuntimeException("Cloud Bridges batch upload error: Status " + statusCode + " - " + responseBody);
                    }
                };

                return client.execute(post, responseHandler);
            } catch (Exception e) {
                log.error("❌ Communication error while uploading files: {}", e.getMessage(), e);
                throw new RuntimeException("Communication error while uploading files: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<String> uploadFile(byte[] fileBytes, String fileName, String server, String user, String password, int port, String path) {
        return CompletableFuture.supplyAsync(() -> {
            String uploadUrl = this.ftpApiUrl + "/upload";
            log.info("🔗 Connecting to Cloud Bridges: {}", uploadUrl);
            log.info("📂 Uploading file '{}' to FTP server '{}' at path '{}'", fileName, server, path);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = this.createHttpPost(uploadUrl, fileBytes, fileName, server, user, password, port, path);
                HttpClientResponseHandler<String> responseHandler = (response) -> {
                    int statusCode = response.getCode();
                    String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8) : "No response";
                    if (statusCode == 200) {
                        return responseBody;
                    } else {
                        log.error("❌ File upload failed '{}' | Status Code: {} | Response: {}", fileName, statusCode, responseBody);
                        throw new RuntimeException("Cloud Bridges error: Status " + statusCode + " - " + responseBody);
                    }
                };
                return client.execute(post, responseHandler);
            } catch (Exception e) {
                log.error("❌ Communication error while uploading file '{}': {}", fileName, e.getMessage(), e);
                throw new RuntimeException("Communication error while uploading file: " + e.getMessage(), e);
            }
        });
    }

    private CompletableFuture<String> retryableFuture(Supplier<CompletableFuture<String>> task, int maxRetries) {
        CompletableFuture<String> future = new CompletableFuture<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        attempt(task, future, maxRetries, scheduler, 1);
        return future;
    }

    private void attempt(Supplier<CompletableFuture<String>> task, CompletableFuture<String> future, int maxRetries,
                         ScheduledExecutorService scheduler, int attempt) {
        task.get().whenComplete((result, ex) -> {
            if (ex == null) {
                future.complete(result);
                scheduler.shutdown();
            } else if (attempt < maxRetries) {
                long nextDelay = (long) Math.pow(2, attempt); // Exponential backoff
                log.warn("⚠️ Attempt {} failed. Retrying in {} seconds...", attempt, nextDelay);
                scheduler.schedule(() -> attempt(task, future, maxRetries, scheduler, attempt + 1),
                        nextDelay, TimeUnit.SECONDS);
            } else {
                log.error("❌ All {} attempts failed. Upload aborted.", maxRetries);
                future.completeExceptionally(ex);
                scheduler.shutdown();
            }
        });
    }

    private HttpPost createHttpPost(String url, byte[] fileBytes, String fileName, String server, String user, String password, int port, String path) {
        HttpPost post = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .addBinaryBody("file", fileBytes, ContentType.DEFAULT_BINARY, fileName)
                .addTextBody("server", server)
                .addTextBody("user", user)
                .addTextBody("password", password)
                .addTextBody("path", path)
                .addTextBody("port", String.valueOf(port));

        post.setEntity(builder.build());
        return post;
    }
}