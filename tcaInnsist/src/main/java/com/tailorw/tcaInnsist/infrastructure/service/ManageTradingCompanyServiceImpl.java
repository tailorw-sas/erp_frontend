package com.tailorw.tcaInnsist.infrastructure.service;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;
import com.tailorw.tcaInnsist.domain.services.IManageTradingCompanyService;
import com.tailorw.tcaInnsist.infrastructure.model.ManageTradingCompany;
import com.tailorw.tcaInnsist.infrastructure.repository.command.ManageTradingCompanyWriteDataJPARepository;
import com.tailorw.tcaInnsist.infrastructure.repository.query.ManageTradingCompanyReadDataJPARepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class ManageTradingCompanyServiceImpl implements IManageTradingCompanyService {

    private final ManageTradingCompanyWriteDataJPARepository writeRepository;
    private final ManageTradingCompanyReadDataJPARepository readRepository;

    @Override
    public UUID create(ManageTradingCompanyDto dto) {
        ManageTradingCompany tradingCompany = new ManageTradingCompany(dto);
        return writeRepository.save(tradingCompany).getId();
    }

    @Override
    public void update(ManageTradingCompanyDto dto) {
        ManageTradingCompany tradingCompany = new ManageTradingCompany(dto);
        writeRepository.save(tradingCompany);
    }

    @Override
    public void delete(UUID id) {
        try{
            writeRepository.deleteById(id);
        }catch (Exception ex){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("ManageTradingCompany_id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public void createMany(List<ManageTradingCompanyDto> list) {
        try{
            writeRepository.deleteAll();

            List<ManageTradingCompany> tradingCompanies = list.stream()
                    .map(ManageTradingCompany::new)
                    .toList();

            writeRepository.saveAll(tradingCompanies);
        }catch (Exception ex){
            Logger.getLogger(ManageTradingCompanyServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ManageTradingCompanyDto getById(UUID id) {
        return readRepository.findById(id)
                .map(ManageTradingCompany::toAggregate)
                .orElse(null);
    }

    @Override
    public boolean exists() {
        return readRepository.count() > 0;
    }
}
