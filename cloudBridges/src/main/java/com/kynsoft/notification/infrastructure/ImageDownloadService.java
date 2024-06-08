package com.kynsoft.notification.infrastructure;


import com.kynsof.share.core.infrastructure.redis.CacheConfig;
import com.kynsoft.notification.domain.dto.AFileDto;
import com.kynsoft.notification.domain.service.IAFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.UUID;

@Service
public class ImageDownloadService {

    private final RestTemplate restTemplate;
    private final IAFileService fileService;

    @Autowired
    public ImageDownloadService(RestTemplate restTemplate, IAFileService fileService) {
        this.restTemplate = restTemplate;
        this.fileService = fileService;
    }
    @Cacheable(cacheNames = CacheConfig.IMAGE_CACHE, unless = "#result == null")
    public byte[] downloadImage(UUID id) {
        AFileDto fileSave = this.fileService.findById(id);

        ResponseEntity<byte[]> response = restTemplate.getForEntity(fileSave.getUrl(), byte[].class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to download image from: " + fileSave.getUrl());
        }
    }

    public String downloadImageAsBase64(UUID id) {
        byte[] imageBytes = downloadImage(id);
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}