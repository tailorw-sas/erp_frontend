package com.tailorw.tcaInnsist.infrastructure.service.redis;

import com.tailorw.tcaInnsist.domain.dto.ManageRateDto;
import com.tailorw.tcaInnsist.domain.services.IManageRateService;
import com.tailorw.tcaInnsist.infrastructure.model.redis.ManageRate;
import com.tailorw.tcaInnsist.infrastructure.repository.redis.ManageRateRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ManageRateServiceImpl implements IManageRateService {
    private final ManageRateRepository manageRateRepository;

    public ManageRateServiceImpl(ManageRateRepository manageRateRepository){
        this.manageRateRepository = manageRateRepository;
    }


    @Override
    public String create(ManageRateDto manageRateDto) {
        ManageRate manageRate = new ManageRate(manageRateDto);
        return manageRateRepository.save(manageRate).getId();
    }

    @Override
    public ManageRateDto findById(String id) {
        Optional<ManageRate> manageRate = manageRateRepository.findById(id);
        if(manageRate.isPresent()){
            return manageRate.get().toAggregate();
        }

        return null;
    }
}
