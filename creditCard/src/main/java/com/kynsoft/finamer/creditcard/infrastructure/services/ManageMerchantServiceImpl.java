package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchant;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageMerchantWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageMerchantReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageMerchantServiceImpl implements IManageMerchantService {

    @Autowired
    private ManageMerchantWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageMerchantReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageMerchantDto dto) {
        ManageMerchant data = new ManageMerchant(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageMerchantDto dto) {
        ManageMerchant update = new ManageMerchant(dto);
        update.setUpdateAt(LocalDateTime.now());
        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageMerchantDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageMerchantDto findById(UUID id) {
        Optional<ManageMerchant> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_MERCHANT_NOT_FOUND, new ErrorField("id", "Manager Merchant not found.")));
    }
}
