package com.kynsoft.finamer.payment.infrastructure.services.report.content;

import com.kynsof.share.core.domain.service.IReportGenerator;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractReportContentProvider {
    protected final RestTemplate restTemplate;
    protected final IReportGenerator reportGenerator;

    public AbstractReportContentProvider(RestTemplate restTemplate, IReportGenerator reportGenerator) {
        this.restTemplate = restTemplate;
        this.reportGenerator = reportGenerator;
    }

    public abstract Optional<byte[]> getContent(Map<String,Object> parameters);


    protected Optional<byte[]> getRemoteContent(String fileRemotePath){
        return Optional.ofNullable(restTemplate.getForObject(fileRemotePath, byte[].class));
    }
}
