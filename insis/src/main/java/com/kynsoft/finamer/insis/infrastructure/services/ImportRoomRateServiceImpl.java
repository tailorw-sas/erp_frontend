package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.roomRate.RoomRateResponse;
import com.kynsoft.finamer.insis.domain.dto.ImportRoomRateDto;
import com.kynsoft.finamer.insis.domain.dto.RoomRateDto;
import com.kynsoft.finamer.insis.domain.services.IImportRoomRateService;
import com.kynsoft.finamer.insis.infrastructure.model.ImportRoomRate;
import com.kynsoft.finamer.insis.infrastructure.model.RoomRate;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import com.kynsoft.finamer.insis.infrastructure.repository.command.ImportRoomRateWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ImportRoomRateReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImportRoomRateServiceImpl implements IImportRoomRateService {

    private final ImportRoomRateWriteDataJPARepository writeRepository;
    private final ImportRoomRateReadDataJPARepository readRepository;

    public ImportRoomRateServiceImpl(ImportRoomRateWriteDataJPARepository writeRepository,
                                     ImportRoomRateReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ImportRoomRateDto dto) {
        ImportRoomRate importRoomRate = new ImportRoomRate(dto);
        return writeRepository.save(importRoomRate).getId();
    }

    @Override
    public List<ImportRoomRateDto> createMany(List<ImportRoomRateDto> dtoList) {
        List<ImportRoomRate> importRoomRateList = dtoList.stream()
                .map(ImportRoomRate::new)
                .toList();

        importRoomRateList = writeRepository.saveAll(importRoomRateList);

        return importRoomRateList.stream()
                .map(ImportRoomRate::toAggregate)
                .toList();
    }

    @Override
    public void update(ImportRoomRateDto dto) {
        ImportRoomRate importRoomRate = new ImportRoomRate(dto);
        writeRepository.save(importRoomRate);
    }

    @Override
    public void updateMany(List<ImportRoomRateDto> dtoList) {
        List<ImportRoomRate> importRoomRateList = dtoList.stream()
                .map(ImportRoomRate::new)
                .toList();

        writeRepository.saveAll(importRoomRateList);
    }

    @Override
    public List<ImportRoomRateDto> findByImportProcessId(UUID importProcessId) {
        return readRepository.findByImportProcess_Id(importProcessId).stream()
                .map(ImportRoomRate::toAggregate)
                .toList();
    }

    @Override
    public List<ImportRoomRateDto> findByImportProcessIdAndRoomRateId(UUID importProcessId, UUID roomRateId) {
        return readRepository.findByImportProcess_IdAndRoomRate_Id(importProcessId, roomRateId).stream()
                .map(ImportRoomRate::toAggregate)
                .toList();
    }

    @Override
    public List<ImportRoomRateDto> findByImportProcessIdAndRoomRates(UUID importProcessId, List<RoomRateDto> roomRatesDtoList) {
        List<RoomRate> roomRates = roomRatesDtoList.stream().
                map(RoomRate::new).toList();

        return readRepository.findByImportProcess_IdAndRoomRateIn(importProcessId, roomRates).stream()
                .map(ImportRoomRate::toAggregate)
                .toList();
    }

    @Override
    public PaginatedResponse getRoomRateErrorsByImportProcessId(UUID importProcessId, Pageable pageable) {
        List<ImportRoomRateDto> importRoomRatesDtos = readRepository.findByImportProcess_Id(importProcessId)
                .stream()
                .filter(importRoomRate -> importRoomRate.getRoomRate().getStatus().equals(RoomRateStatus.FAILED))
                .map(ImportRoomRate::toAggregate).toList();

        Page<ImportRoomRateDto> page = convertListToPage(importRoomRatesDtos, pageable);
        return getPaginatedResponse(page);
    }

    public static <T> Page<T> convertListToPage(List<T> list, Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<T> pageContent;

        if (list.size() < startItem) {
            pageContent = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, list.size());
            pageContent = list.subList(startItem, toIndex);
        }

        return new PageImpl<>(pageContent, pageable, list.size());
    }

    private PaginatedResponse getPaginatedResponse(Page<ImportRoomRateDto> data) {
        List<RoomRateResponse> responseList = data.getContent().stream()
                .map(importRoomRate -> {
                    RoomRateResponse roomRateResponse = new RoomRateResponse(importRoomRate.getRoomRate());
                    roomRateResponse.setMessage(importRoomRate.getErrorMessage());
                    return roomRateResponse;
                })
                .toList();

        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
