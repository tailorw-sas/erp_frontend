package com.kynsoft.notification.infrastructure.service;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.notification.application.query.mailjetConfiguration.getById.MailjetConfigurationResponse;
import com.kynsoft.notification.domain.dto.MailjetConfigurationDto;
import com.kynsoft.notification.domain.service.IMailjetConfigurationService;
import com.kynsoft.notification.infrastructure.entity.MailjetConfiguration;
import com.kynsoft.notification.infrastructure.repository.command.MailjetConfigurationWriteDataJPARepository;
import com.kynsoft.notification.infrastructure.repository.query.MailjetConfigurationReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MailjetConfigurationServiceImpl implements IMailjetConfigurationService {

    @Autowired
    private MailjetConfigurationWriteDataJPARepository commandRepository;

    @Autowired
    private MailjetConfigurationReadDataJPARepository queryRepository;

    @Override
    public UUID create(MailjetConfigurationDto object) {
        MailjetConfiguration entity = this.commandRepository.save(new MailjetConfiguration(object));
        return entity.getId();
    }

    @Override
    public void update(MailjetConfiguration object) {
       this.commandRepository.save(object);
    }

    @Override
    public void delete(MailjetConfigurationDto object) {

        MailjetConfiguration delete = new MailjetConfiguration(object);
        delete.setDeleted(Boolean.TRUE);

        this.commandRepository.save(delete);
    }

    @Override
    public MailjetConfigurationDto findById(UUID id) {
        Optional<MailjetConfiguration> mailjetConfiguration = this.queryRepository.findById(id);
        return mailjetConfiguration.map(MailjetConfiguration::toAggregate).orElse(null);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<MailjetConfiguration> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<MailjetConfiguration> data = this.queryRepository.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<MailjetConfiguration> data) {
        List<MailjetConfigurationResponse> response = new ArrayList<>();
        for (MailjetConfiguration p : data.getContent()) {
            response.add(new MailjetConfigurationResponse(p.toAggregate()));
        }
        return new PaginatedResponse(response, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
