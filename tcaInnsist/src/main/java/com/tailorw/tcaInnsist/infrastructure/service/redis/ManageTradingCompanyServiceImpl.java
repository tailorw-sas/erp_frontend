package com.tailorw.tcaInnsist.infrastructure.service.redis;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.tailorw.tcaInnsist.domain.dto.ManageTradingCompanyDto;
import com.tailorw.tcaInnsist.domain.services.IManageTradingCompanyService;
import com.tailorw.tcaInnsist.infrastructure.model.redis.ManageTradingCompany;
import com.tailorw.tcaInnsist.infrastructure.repository.redis.ManageTradingCompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class ManageTradingCompanyServiceImpl implements IManageTradingCompanyService {

    private final ManageTradingCompanyRepository tradingCompanyRepository;

    @Override
    public UUID create(ManageTradingCompanyDto dto) {
        ManageTradingCompany tradingCompany = new ManageTradingCompany(dto);
        return tradingCompanyRepository.save(tradingCompany).getId();
    }

    @Override
    public void update(ManageTradingCompanyDto dto) {
        ManageTradingCompany tradingCompany = new ManageTradingCompany(dto);
        tradingCompanyRepository.save(tradingCompany);
    }

    @Override
    public void delete(UUID id) {
        try{
            tradingCompanyRepository.deleteById(id);
        }catch (Exception ex){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("ManageTradingCompany_id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public void createMany(List<ManageTradingCompanyDto> list) {
        try{
            tradingCompanyRepository.deleteAll();

            List<ManageTradingCompany> tradingCompanies = list.stream()
                    .map(ManageTradingCompany::new)
                    .toList();

            tradingCompanyRepository.saveAll(tradingCompanies);
        }catch (Exception ex){
            Logger.getLogger(ManageTradingCompanyServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ManageTradingCompanyDto getById(UUID id) {
        return tradingCompanyRepository.findById(id)
                .map(ManageTradingCompany::toAggregate)
                .orElse(null);
    }

    @Override
    public boolean exists() {
        return tradingCompanyRepository.count() > 0;
    }
}
