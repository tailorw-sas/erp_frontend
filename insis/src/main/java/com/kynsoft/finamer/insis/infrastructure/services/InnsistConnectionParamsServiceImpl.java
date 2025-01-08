package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.insis.application.query.objectResponse.innsistConnectionParams.InnsistConnectionParamsResponse;
import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import com.kynsoft.finamer.insis.domain.services.IInnsistConnectionParamsService;
import com.kynsoft.finamer.insis.infrastructure.model.InnsistConnectionParams;
import com.kynsoft.finamer.insis.infrastructure.model.ManageTradingCompany;
import com.kynsoft.finamer.insis.infrastructure.repository.command.InnsistConnectionParamsWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.InnsistConnectionParamsReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InnsistConnectionParamsServiceImpl implements IInnsistConnectionParamsService {

    private final InnsistConnectionParamsWriteDataJPARepository writeRepository;
    private final InnsistConnectionParamsReadDataJPARepository readRepository;

    public InnsistConnectionParamsServiceImpl(InnsistConnectionParamsWriteDataJPARepository writeRepository, InnsistConnectionParamsReadDataJPARepository readDataJPARepository){
        this.writeRepository = writeRepository;
        this.readRepository = readDataJPARepository;
    }

    @Override
    public UUID create(InnsistConnectionParamsDto dto) {
        InnsistConnectionParams innsistConnectionParams = new InnsistConnectionParams(dto);
        return writeRepository.save(innsistConnectionParams).getId();
    }

    @Override
    public void update(InnsistConnectionParamsDto dto) {
        InnsistConnectionParams innsistConnectionParams = new InnsistConnectionParams(dto);
        innsistConnectionParams.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(innsistConnectionParams);
    }

    @Override
    public void delete(InnsistConnectionParamsDto dto) {
        try{
            writeRepository.deleteById(dto.getId());
        }catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public InnsistConnectionParamsDto findById(UUID id) {
        Optional<InnsistConnectionParams> innsistConnectionParamsOptional = readRepository.findById(id);
        if(innsistConnectionParamsOptional.isPresent()){
            return innsistConnectionParamsOptional.get().toAggregate();
        }else{
            return null;
        }
    }

    @Override
    public InnsistConnectionParamsDto findByTradingCompany(UUID tradingCompanyId) {
        List<InnsistConnectionParams> connectionParams = readRepository.findByTradingCompanyId(tradingCompanyId);
        System.out.println(connectionParams.size());
        if(connectionParams.size() > 0){
            return connectionParams.stream()
                    .findFirst()
                    .map(InnsistConnectionParams::toAggregate)
                    .get();
        }

        return null;
    }

    @Override
    public List<InnsistConnectionParamsDto> findAll() {
        return readRepository.findAll().stream()
                .map(InnsistConnectionParams::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<InnsistConnectionParams> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<InnsistConnectionParams> data = readRepository.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public boolean hasTradingCompanyAssociation(UUID id) {
        if(readRepository.hasTradingCompanyAssociation(id) > 0){
            return true;
        }
        return false;
    }

    @Override
    public ManageTradingCompanyDto findTradingCompanyAssociated(UUID id) {
        Optional<ManageTradingCompany> manageTradingCompany = readRepository.findTradingCompanyAssociated(id);
        if(manageTradingCompany.isPresent()){
            return manageTradingCompany.get().toAggregate();
        }
        return null;
    }

    private PaginatedResponse getPaginatedResponse(Page<InnsistConnectionParams> data) {
        List<InnsistConnectionParamsResponse> innsistConnectionParamsResponses = new ArrayList<>();
        for (InnsistConnectionParams p : data.getContent()) {
            innsistConnectionParamsResponses.add(new InnsistConnectionParamsResponse(p.toAggregate()));
        }
        return new PaginatedResponse(innsistConnectionParamsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
