package com.kynsoft.notification.infrastructure.service;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.FileDto;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.notification.application.query.file.search.FileResponse;
import com.kynsoft.notification.domain.service.IAFileService;
import com.kynsoft.notification.infrastructure.entity.AFile;
import com.kynsoft.notification.infrastructure.repository.command.FileWriteDataJPARepository;
import com.kynsoft.notification.infrastructure.repository.query.FileReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AFileServiceImpl implements IAFileService {

    private final FileWriteDataJPARepository commandRepository;

    private final FileReadDataJPARepository queryRepository;

    public AFileServiceImpl(FileWriteDataJPARepository commandRepository, FileReadDataJPARepository queryRepository) {
        this.commandRepository = commandRepository;
        this.queryRepository = queryRepository;
    }

    @Override
    public UUID create(FileDto object) {
        AFile file = this.commandRepository.save(new AFile(object));
        return file.getId();
    }

    @Override
    public void update(FileDto object) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(FileDto object) {
        AFile delete = new AFile(object);
        delete.setName(delete.getName() + " + " + UUID.randomUUID());

        this.commandRepository.save(delete);
    }

    @Override
    public FileDto findById(UUID id) {
        Optional<AFile> file = this.queryRepository.findById(id);
        return file.map(AFile::toAggregate).orElse(null);
    }

    @Override
    public List<FileDto> findByIds(List<UUID> ids) {
        List<AFile> files = this.queryRepository.findAllById(ids);
        return files.stream().map(AFile::toAggregate).toList();
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<FileResponse> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<AFile> data = this.queryRepository.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<AFile> data) {
        List<FileResponse> files = new ArrayList<>();
        for (AFile o : data.getContent()) {
            files.add(new FileResponse(o.toAggregate()));
        }
        return new PaginatedResponse(files, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
