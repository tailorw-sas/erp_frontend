package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageTradingCompany.ManageTradingCompanyResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import com.kynsoft.finamer.insis.domain.services.IManageTradingCompanyService;
import com.kynsoft.finamer.insis.infrastructure.model.ManageTradingCompany;
import com.kynsoft.finamer.insis.infrastructure.repository.command.ManageTradingCompanyWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ManageTradingCompanyReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ManageTradingCompanyServiceImpl implements IManageTradingCompanyService {

    private final ManageTradingCompanyWriteDataJPARepository writeRepository;
    private final ManageTradingCompanyReadDataJPARepository readRepository;

    public ManageTradingCompanyServiceImpl(ManageTradingCompanyWriteDataJPARepository writeRepository,
                                           ManageTradingCompanyReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ManageTradingCompanyDto dto) {
        ManageTradingCompany manageTradingCompany = new ManageTradingCompany((dto));
        return writeRepository.save(manageTradingCompany).getId();
    }

    @Override
    public void update(ManageTradingCompanyDto dto) {
        ManageTradingCompany manageTradingCompany = new ManageTradingCompany((dto));
        manageTradingCompany.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(manageTradingCompany);
    }

    @Override
    public void delete(ManageTradingCompanyDto dto) {
        try {
            writeRepository.deleteById(dto.getId());
        }catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageTradingCompanyDto findById(UUID id) {
        Optional<ManageTradingCompany> optionalEntity = readRepository.findById(id);
        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_TRADING_COMPANIES_TYPE_NOT_FOUND, new ErrorField("id", "Manage TradingCompany not found.")));
    }

    @Override
    public List<ManageTradingCompanyDto> findAll() {
        return readRepository.findAll().stream()
                .map(ManageTradingCompany::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageTradingCompany> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageTradingCompany> data = readRepository.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageTradingCompany> data) {
        List<ManageTradingCompanyResponse> tradingCompaniesResponse = new ArrayList<>();
        for (ManageTradingCompany p : data.getContent()) {
            tradingCompaniesResponse.add(new ManageTradingCompanyResponse(p.toAggregate()));
        }
        return new PaginatedResponse(tradingCompaniesResponse, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
