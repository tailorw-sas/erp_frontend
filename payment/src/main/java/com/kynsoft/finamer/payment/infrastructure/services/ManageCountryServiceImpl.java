package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageCountryDto;
import com.kynsoft.finamer.payment.domain.services.IManageCountryService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageCountry;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageCountryWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageCountryReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageCountryServiceImpl implements IManageCountryService {

    private final ManageCountryWriteDataJPARepository writeRepository;
    private final ManageCountryReadDataJPARepository readRepository;

    public ManageCountryServiceImpl(ManageCountryWriteDataJPARepository writeRepository,
                                    ManageCountryReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ManageCountryDto dto) {
        ManageCountry data = new ManageCountry(dto);
        return this.writeRepository.save(data).getId();
    }

    @Override
    public void update(ManageCountryDto dto) {
        ManageCountry update = new ManageCountry(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.writeRepository.save(update);
    }

    @Override
    public void delete(ManageCountryDto dto) {
        try {
            this.writeRepository.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageCountryDto findById(UUID id) {
        Optional<ManageCountry> country = this.readRepository.findById(id);
        if(country.isPresent()){
            return country.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_COUNTRY_NOT_FOUND, new ErrorField("id", "Manager County not found: " + id)));
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return readRepository.countByCodeAndNotId(code, id);
    }

    @Override
    public Long countByNameAndNotId(String name, UUID id) {
        return this.readRepository.countByNameAndNotId(name, id);
    }

}
