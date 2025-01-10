package com.tailorw.tcaInnsist.infrastructure.service.redis;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.tailorw.tcaInnsist.domain.dto.ManageConnectionDto;
import com.tailorw.tcaInnsist.domain.services.IManageConnectionService;
import com.tailorw.tcaInnsist.infrastructure.model.redis.ManageConnection;
import com.tailorw.tcaInnsist.infrastructure.repository.redis.ManageConnectionRepository;
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

    private final ManageConnectionRepository connectionRepository;

    @Override
    public UUID create(ManageConnectionDto dto){
        ManageConnection configuration = new ManageConnection(dto);
        return connectionRepository.save(configuration).getId();
    }

    @Override
    public void update(ManageConnectionDto dto) {
        ManageConnection configuration = new ManageConnection(dto);
        connectionRepository.save(configuration);
    }

    @Override
    public void delete(UUID id) {
        try{
            connectionRepository.deleteById(id);
        }catch (Exception ex){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("ManageConnection_id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public void createMany(List<ManageConnectionDto> dtoList) {
        try{
            connectionRepository.deleteAll();

            List<ManageConnection> configurations = dtoList.stream()
                    .map(ManageConnection::new)
                    .collect(Collectors.toList());
            connectionRepository.saveAll(configurations);
        }catch (Exception ex){
            Logger.getLogger(ManageConnectionServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ManageConnectionDto getById(UUID id) {
        return connectionRepository.findById(id)
                .map(ManageConnection::toAggregate)
                .orElse(null);
    }

    @Override
    public boolean exists() {
        return connectionRepository.count() > 0;
    }
}
