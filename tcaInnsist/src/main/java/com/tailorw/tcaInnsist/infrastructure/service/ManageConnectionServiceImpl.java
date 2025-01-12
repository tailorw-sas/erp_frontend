package com.tailorw.tcaInnsist.infrastructure.service;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.domain.services.IManageConnectionService;
import com.tailorw.tcaInnsist.infrastructure.model.ManageConnection;
import com.tailorw.tcaInnsist.infrastructure.repository.command.ManageConnectionWriteDataJPARepository;
import com.tailorw.tcaInnsist.infrastructure.repository.query.ManageConnectionReadDataJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageConnectionServiceImpl implements IManageConnectionService {

    private final ManageConnectionWriteDataJPARepository writRepository;
    private final ManageConnectionReadDataJPARepository readRepository;

    @Override
    public UUID create(ManageConnectionDto dto){
        ManageConnection configuration = new ManageConnection(dto);
        return writRepository.save(configuration).getId();
    }

    @Override
    public void update(ManageConnectionDto dto) {
        ManageConnection configuration = new ManageConnection(dto);
        writRepository.save(configuration);
    }

    @Override
    public void delete(UUID id) {
        try{
            writRepository.deleteById(id);
        }catch (Exception ex){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("ManageConnection_id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public void createMany(List<ManageConnectionDto> dtoList) {
        try{
            writRepository.deleteAll();

            List<ManageConnection> configurations = dtoList.stream()
                    .map(ManageConnection::new)
                    .collect(Collectors.toList());
            writRepository.saveAll(configurations);
        }catch (Exception ex){
            Logger.getLogger(ManageConnectionServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ManageConnectionDto getById(UUID id) {
        return readRepository.findById(id)
                .map(ManageConnection::toAggregate)
                .orElse(null);
    }

    @Override
    public boolean exists() {
        return readRepository.count() > 0;
    }
}
