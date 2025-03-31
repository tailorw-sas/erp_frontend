package com.kynsoft.notification.domain.service;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.FileDto;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IAFileService {
    UUID create(FileDto object);
    void update(FileDto object);
    void delete(FileDto object);
    FileDto findById(UUID id);
    List<FileDto> findByIds(List<UUID> ids);
    PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria);
}
